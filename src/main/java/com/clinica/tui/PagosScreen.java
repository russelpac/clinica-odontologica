package com.clinica.tui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.table.Table;
import com.clinica.managers.PagosManager;
import com.clinica.managers.PacienteManager;
import com.clinica.managers.OdontologoManager;
import com.clinica.modelos.Pagos;
import com.clinica.modelos.Paciente;
import com.clinica.modelos.Odontologo;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Pantalla para listar / ver / editar / anular / eliminar pagos usando Lanterna.
 */
public class PagosScreen {

    private final PagosManager pagosManager;
    private final PacienteManager pacienteManager;
    private final OdontologoManager odontologoManager;

    private Table<String> table;
    private List<String> currentIds;

    public PagosScreen(PagosManager pagosManager,
                       PacienteManager pacienteManager,
                       OdontologoManager odontologoManager) {
        this.pagosManager = pagosManager;
        this.pacienteManager = pacienteManager;
        this.odontologoManager = odontologoManager;
        this.currentIds = new ArrayList<>();
    }

    /** Compatibilidad: muestra lista sin selección */
    public void show(WindowBasedTextGUI textGUI) {
        show(textGUI, null);
    }

    /**
     * Muestra lista de pagos; si selectId != null intenta seleccionar ese pago.
     */
    public void show(WindowBasedTextGUI textGUI, String selectId) {
        final Window window = new BasicWindow("Pagos - Lista");
        Panel panel = new Panel(new GridLayout(1));

        Label instrucciones = new Label("Use ↑/↓ para seleccionar, TAB para ir a botones y Enter para ejecutar.");
        panel.addComponent(instrucciones);

        table = new Table<>("#", "ID", "Paciente", "Fecha", "Monto", "Método", "Estado", "Odontólogo");
        table.setPreferredSize(new TerminalSize(110, 20));
        reloadTable();

        if (selectId != null) {
            int idx = -1;
            for (int i = 0; i < currentIds.size(); i++) {
                if (selectId.equals(currentIds.get(i))) { idx = i; break; }
            }
            if (idx >= 0) {
                try { table.setSelectedRow(idx); table.takeFocus(); } catch (Exception ignored) {}
            }
        } else {
            try {
                if (table.getTableModel().getRowCount() > 0) {
                    table.setSelectedRow(0);
                }
                table.takeFocus();
            } catch (Exception ignored) {}
        }

        panel.addComponent(table.withBorder(Borders.singleLine("Pagos")));

        Panel buttons = new Panel(new GridLayout(2));
        Button btnAcciones = new Button("Acciones", () -> showActionsForSelectedRow(textGUI));
        Button btnCerrar = new Button("Cerrar", window::close);
        buttons.addComponent(btnAcciones);
        buttons.addComponent(btnCerrar);

        panel.addComponent(buttons);
        window.setComponent(panel);
        textGUI.addWindowAndWait(window);
    }

    private void reloadTable() {
        table.getTableModel().clear();
        currentIds.clear();
        List<Pagos> lista = pagosManager.listar();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        int idx = 1;
        for (Pagos p : lista) {
            currentIds.add(p.getID());
            String pacienteNombre = "";
            try {
                Paciente pac = pacienteManager.getById(p.getPacienteID());
                pacienteNombre = pac != null ? (pac.getNombres() + " " + pac.getApellidos()) : "";
            } catch (Exception ignored) {}
            String odontNombre = "";
            try {
                if (p.getOdontologoID() != null) {
                    Odontologo od = odontologoManager.getById(p.getOdontologoID());
                    odontNombre = od != null ? od.getNombre() : "";
                }
            } catch (Exception ignored) {}
            String fecha = p.getFecha() != null ? p.getFecha().format(fmt) : "";
            String monto = p.getMonto() != null ? p.getMonto().toPlainString() : "";
            String metodo = p.getMetodo() != null ? p.getMetodo().toString() : "";
            String estado = p.getEstado() != null ? p.getEstado().toString() : "";

            table.getTableModel().addRow(
                    String.valueOf(idx),
                    shortId(p.getID()),
                    safe(pacienteNombre),
                    fecha,
                    monto,
                    safe(metodo),
                    safe(estado),
                    safe(odontNombre)
            );
            idx++;
        }
        try { table.setSelectedRow(-1); } catch (Exception ignored) {}
    }

    private Optional<String> selectedIdFromTable() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) return Optional.empty();
        if (currentIds == null) return Optional.empty();
        if (selectedRow >= currentIds.size()) return Optional.empty();
        return Optional.ofNullable(currentIds.get(selectedRow));
    }

    private void showActionsForSelectedRow(WindowBasedTextGUI textGUI) {
        Optional<String> maybeId = selectedIdFromTable();
        if (maybeId.isEmpty()) {
            showMsg(textGUI, "Seleccione un pago primero.");
            return;
        }
        String id = maybeId.get();

        final Window w = new BasicWindow("Acciones - Pago");
        Panel p = new Panel(new GridLayout(1));
        p.addComponent(new Label("ID: " + shortId(id)));
        p.addComponent(new Label("Seleccione la acción:"));

        p.addComponent(new Button("Ver detalle", () -> {
            w.close();
            showDetailById(id, textGUI);
        }));

        p.addComponent(new Button("Editar / Actualizar", () -> {
            w.close();
            editById(id, textGUI);
            reloadTable();
        }));

        p.addComponent(new Button("Anular (marcar como anulado)", () -> {
            w.close();
            // intentar setear estado ANULADO (probar varios nombres comunes)
            Pagos pago = pagosManager.getById(id);
            if (pago == null) { showMsg(textGUI, "Pago no encontrado."); return; }
            Pagos.EstadoPago nuevoEstado = tryParseEstado("ANULADO");
            if (nuevoEstado == null) nuevoEstado = tryParseEstado("CANCELADO");
            if (nuevoEstado == null) nuevoEstado = tryParseEstado("ANULADA");
            if (nuevoEstado == null) {
                showMsg(textGUI, "No se pudo anular: no existe un valor enum ANULADO/CANCELADO en EstadoPago.");
                return;
            }
            boolean ok = pagosManager.actualizarPorId(id, null, null, nuevoEstado);
            if (ok) showMsg(textGUI, "Pago anulado.");
            else showMsg(textGUI, "No se pudo anular el pago.");
            reloadTable();
        }));

p.addComponent(new Button("Eliminar permanentemente", () -> {
    w.close();
    final Window confirm = new BasicWindow("Confirmar eliminación");
    Panel cp = new Panel(new GridLayout(1));
    cp.addComponent(new Label("Eliminar pago permanentemente (irreversible)."));
    cp.addComponent(new Label("¿Desea continuar?"));
    cp.addComponent(new Button("Sí", () -> {
        // --- Diagnóstico / debug antes de borrar ---
        System.out.println("DEBUG PagosScreen: solicitar eliminación");
        Optional<String> maybeIdDbg = selectedIdFromTable();
        if (maybeIdDbg.isEmpty()) {
            showMsg(textGUI, "No se detectó ID seleccionado (debug).");
            confirm.close();
            return;
        }
        String idDbg = maybeIdDbg.get();
        System.out.println("DEBUG PagosScreen: id seleccionado = " + idDbg);

        Pagos before = pagosManager.getById(idDbg);
        System.out.println("DEBUG PagosScreen: pago en manager antes = " + (before == null ? "NULL" : "ENCONTRADO"));

        boolean ok = false;
        try {
            ok = pagosManager.eliminarFisicoPorId(idDbg); // usa el método de eliminación física
        } catch (Exception ex) {
            ex.printStackTrace();
            ok = false;
        }

        // --- Diagnóstico / debug después de borrar ---
        System.out.println("DEBUG PagosScreen: eliminarFisicoPorId returned = " + ok);
        Pagos after = pagosManager.getById(idDbg);
        System.out.println("DEBUG PagosScreen: pago en manager después = " + (after == null ? "NULL" : "ENCONTRADO"));

        if (ok) showMsg(textGUI, "Pago eliminado permanentemente.");
        else showMsg(textGUI, "No se pudo eliminar. Revise la consola para más detalles.");

        confirm.close();
        reloadTable();
    }));
    cp.addComponent(new Button("No", confirm::close));
    confirm.setComponent(cp);
    textGUI.addWindowAndWait(confirm);
}));


        p.addComponent(new Button("Volver", w::close));
        w.setComponent(p);
        textGUI.addWindowAndWait(w);
    }

    private void showDetailById(String id, WindowBasedTextGUI textGUI) {
        Pagos p = pagosManager.getById(id);
        if (p == null) { showMsg(textGUI, "Pago no encontrado."); return; }

        final Window w = new BasicWindow("Detalle Pago");
        Panel pnl = new Panel(new GridLayout(2));
        pnl.addComponent(new Label("ID:"));
        pnl.addComponent(new Label(safe(p.getID())));
        pnl.addComponent(new Label("Paciente ID:"));
        pnl.addComponent(new Label(safe(p.getPacienteID())));
        String pacienteNombre = "";
        try {
            Paciente pac = pacienteManager.getById(p.getPacienteID());
            pacienteNombre = pac != null ? (pac.getNombres() + " " + pac.getApellidos()) : "";
        } catch (Exception ignored) {}
        pnl.addComponent(new Label("Paciente:"));
        pnl.addComponent(new Label(safe(pacienteNombre)));
        pnl.addComponent(new Label("Fecha:"));
        pnl.addComponent(new Label(p.getFecha() != null ? p.getFecha().toString() : ""));
        pnl.addComponent(new Label("Monto:"));
        pnl.addComponent(new Label(p.getMonto() != null ? p.getMonto().toPlainString() : ""));
        pnl.addComponent(new Label("Método:"));
        pnl.addComponent(new Label(p.getMetodo() != null ? p.getMetodo().toString() : ""));
        pnl.addComponent(new Label("Estado:"));
        pnl.addComponent(new Label(p.getEstado() != null ? p.getEstado().toString() : ""));
        pnl.addComponent(new Label("Odontólogo ID:"));
        pnl.addComponent(new Label(safe(p.getOdontologoID())));
        String odontNombre = "";
        try {
            Odontologo od = odontologoManager.getById(p.getOdontologoID());
            odontNombre = od != null ? od.getNombre() : "";
        } catch (Exception ignored) {}
        pnl.addComponent(new Label("Odontólogo:"));
        pnl.addComponent(new Label(safe(odontNombre)));
        pnl.addComponent(new Button("Cerrar", w::close));
        w.setComponent(pnl);
        textGUI.addWindowAndWait(w);
    }

    private void editById(String id, WindowBasedTextGUI textGUI) {
        Pagos p = pagosManager.getById(id);
        if (p == null) { showMsg(textGUI, "Pago no encontrado."); return; }

        final Window w = new BasicWindow("Editar Pago");
        Panel form = new Panel();
        form.setLayoutManager(new GridLayout(2));

        form.addComponent(new Label("Monto:"));
        TextBox txtMonto = new TextBox(new TerminalSize(20, 1), p.getMonto() != null ? p.getMonto().toPlainString() : "");
        form.addComponent(txtMonto);

        form.addComponent(new Label("Método (EFECTIVO ,TRANSFERENCIA):"));
        TextBox txtMetodo = new TextBox(new TerminalSize(20, 1), p.getMetodo() != null ? p.getMetodo().toString() : "");
        form.addComponent(txtMetodo);

        form.addComponent(new Label("Estado (PENDIENTE, PAGADO, ANULADO):"));
        TextBox txtEstado = new TextBox(new TerminalSize(20, 1), p.getEstado() != null ? p.getEstado().toString() : "");
        form.addComponent(txtEstado);

        form.addComponent(new Label("Odontólogo ID (opcional):"));
        TextBox txtOdontId = new TextBox(new TerminalSize(20, 1), p.getOdontologoID() != null ? p.getOdontologoID() : "");
        form.addComponent(txtOdontId);

        Panel actions = new Panel(new GridLayout(3));
        Button btnGuardar = new Button("Guardar", () -> {
            String v;
            BigDecimal nuevoMonto = null;
            v = txtMonto.getText().trim();
            if (!v.isEmpty()) {
                try {
                    nuevoMonto = new BigDecimal(v);
                } catch (Exception ex) {
                    showMsg(textGUI, "Monto inválido.");
                    return;
                }
            }

            Pagos.MetodoPago nuevoMetodo = null;
            v = txtMetodo.getText().trim();
            if (!v.isEmpty()) {
                nuevoMetodo = tryParseMetodo(v);
                if (nuevoMetodo == null) {
                    // no pudo parsear -> informar pero permitir continuar (se puede guardar null para no cambiar)
                    showMsg(textGUI, "Método no reconocido; el campo no será cambiado si deja vacío.");
                }
            }

            Pagos.EstadoPago nuevoEstado = null;
            v = txtEstado.getText().trim();
            if (!v.isEmpty()) {
                nuevoEstado = tryParseEstado(v);
                if (nuevoEstado == null) {
                    showMsg(textGUI, "Estado no reconocido; el campo no será cambiado si deja vacío.");
                }
            }

            String nuevoOdontId = txtOdontId.getText().trim();
            // Llamar al manager (su implementación actual sólo actualiza los campos no nulos)
            boolean ok = pagosManager.actualizarPorId(id, nuevoMonto, nuevoMetodo, nuevoEstado);
            // Si además hay odontologoID que quieras persistir, necesitarías un método adicional en manager
            if (ok) showMsg(textGUI, "Pago actualizado.");
            else showMsg(textGUI, "No se pudo actualizar el pago.");
            w.close();
        });

        Button btnCancelar = new Button("Cancelar", w::close);
        actions.addComponent(btnGuardar);
        actions.addComponent(btnCancelar);

        form.addComponent(actions, GridLayout.createHorizontallyFilledLayoutData(2));
        w.setComponent(form);
        textGUI.addWindowAndWait(w);
    }

    // --- helpers para parsear enums (intenta valueOf ignorando case) ---
    private Pagos.MetodoPago tryParseMetodo(String s) {
        if (s == null || s.isEmpty()) return null;
        try {
            return Pagos.MetodoPago.valueOf(s.toUpperCase());
        } catch (Exception ex) {
            return null;
        }
    }

    private Pagos.EstadoPago tryParseEstado(String s) {
        if (s == null || s.isEmpty()) return null;
        try {
            return Pagos.EstadoPago.valueOf(s.toUpperCase());
        } catch (Exception ex) {
            // probar algunos sinónimos comunes
            String u = s.toUpperCase();
            try {
                if (u.contains("ANUL")) return Pagos.EstadoPago.valueOf("ANULADO");
                if (u.contains("CANCEL")) return Pagos.EstadoPago.valueOf("CANCELADO");
            } catch (Exception ignored) {}
            return null;
        }
    }

    // ---------- Helpers ----------
    private static String safe(String s) { return s == null ? "" : s; }
    private static String shortId(String id) { if (id == null) return ""; return id.length() > 8 ? id.substring(0,8) : id; }

    private void showMsg(WindowBasedTextGUI textGUI, String msg) {
        final Window w = new BasicWindow("Mensaje");
        Panel p = new Panel(new GridLayout(1));
        p.addComponent(new Label(msg));
        p.addComponent(new Button("Cerrar", w::close));
        w.setComponent(p);
        textGUI.addWindowAndWait(w);
    }
}
