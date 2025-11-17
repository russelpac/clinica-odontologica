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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;


public class PagosScreen {

    private final PagosManager pagosManager;
    private final PacienteManager pacienteManager;
    private final OdontologoManager odontologoManager;

    private Table<String> table;
    private final List<String> currentIds = new ArrayList<>();
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public PagosScreen(PagosManager pagosManager,
                       PacienteManager pacienteManager,
                       OdontologoManager odontologoManager) {
        this.pagosManager = pagosManager;
        this.pacienteManager = pacienteManager;
        this.odontologoManager = odontologoManager;
    }

    
    public void show(WindowBasedTextGUI textGUI, String providedSelectedId) {
        final Window w = new BasicWindow("Pagos - Lista");
        Panel root = new Panel(new GridLayout(1));

        
        root.addComponent(new Label("Pagos registrados (use flechas para seleccionar). TAB para botones."));

        
        table = new Table<>("#", "ID", "Paciente", "Fecha", "Monto", "Método", "Estado", "Odontólogo");
        table.setPreferredSize(new TerminalSize(150, 20));
        reloadTable();

        
        if (providedSelectedId != null) {
            for (int i = 0; i < currentIds.size(); i++) {
                if (providedSelectedId.equals(currentIds.get(i))) {
                    try { table.setSelectedRow(i); } catch (Exception ignored) {}
                    break;
                }
            }
        }

        root.addComponent(table.withBorder(Borders.singleLine("Pagos")));

       
        Panel botones = new Panel(new GridLayout(4));
        botones.addComponent(new Button("Ver", () -> onVer(textGUI)));
        botones.addComponent(new Button("Editar", () -> onEditar(textGUI)));
        botones.addComponent(new Button("Anular", () -> onAnular(textGUI)));
        botones.addComponent(new Button("Eliminar (físico)", () -> onEliminarFisico(textGUI)));
        botones.addComponent(new Button("Cerrar", w::close));
        root.addComponent(botones);

        w.setComponent(root);
        w.setHints(Arrays.asList(Window.Hint.CENTERED));
        textGUI.addWindowAndWait(w);
    }

    
    private void reloadTable() {
        table.getTableModel().clear();
        currentIds.clear();
        List<Pagos> lista = pagosManager.listar();
        int idx = 1;
        for (Pagos p : lista) {
            currentIds.add(p.getID());

            
            String pacienteNombre = "";
            try {
                Paciente pac = pacienteManager.getById(p.getPacienteID());
                if (pac != null) pacienteNombre = safe(pac.getNombres()) + " " + safe(pac.getApellidos());
            } catch (Exception ignored) {}

           
            String odontNombre = "";
            try {
                if (p.getOdontologoID() != null) {
                    Odontologo od = odontologoManager.getById(p.getOdontologoID());
                    if (od != null) odontNombre = od.getNombre();
                }
            } catch (Exception ignored) {}

            String fecha = p.getFecha() != null ? p.getFecha().format(fmt) : "";
            String monto = p.getMonto() != null ? p.getMonto().toPlainString() : "";
            String metodo = p.getMetodo() != null ? p.getMetodo().toString() : "";
            String estado = p.getEstado() != null ? p.getEstado().toString() : "";

            table.getTableModel().addRow(
                    String.valueOf(idx),
                    shortId(p.getID()),
                    pacienteNombre,
                    fecha,
                    monto,
                    metodo,
                    estado,
                    odontNombre
            );
            idx++;
        }

        try { table.setSelectedRow(-1); } catch (Exception ignored) {}
    }

    // ---------- ACCIONES ----------

    private void onVer(WindowBasedTextGUI textGUI) {
        int sel = table.getSelectedRow();
        if (sel < 0) { showMsg(textGUI, "Seleccione un pago primero."); return; }
        if (sel >= currentIds.size()) { showMsg(textGUI, "Fila inválida."); return; }
        String id = currentIds.get(sel);
        Pagos p = pagosManager.getById(id);
        if (p == null) { showMsg(textGUI, "Pago no encontrado."); return; }

        
        String pacienteNombre = "";
        Paciente pac = pacienteManager.getById(p.getPacienteID());
        if (pac != null) pacienteNombre = safe(pac.getNombres()) + " " + safe(pac.getApellidos());
        String odontNombre = "";
        if (p.getOdontologoID() != null) {
            Odontologo od = odontologoManager.getById(p.getOdontologoID());
            if (od != null) odontNombre = od.getNombre();
        }

        final Window vw = new BasicWindow("Detalle Pago");
        Panel pn = new Panel(new GridLayout(2));
        pn.addComponent(new Label("ID:")); pn.addComponent(new Label(p.getID()));
        pn.addComponent(new Label("Paciente:")); pn.addComponent(new Label(pacienteNombre));
        pn.addComponent(new Label("Odontólogo:")); pn.addComponent(new Label(odontNombre));
        pn.addComponent(new Label("Fecha:")); pn.addComponent(new Label(p.getFecha() != null ? p.getFecha().format(fmt) : ""));
        pn.addComponent(new Label("Monto:")); pn.addComponent(new Label(p.getMonto() != null ? p.getMonto().toPlainString() : ""));
        pn.addComponent(new Label("Método:")); pn.addComponent(new Label(p.getMetodo() != null ? p.getMetodo().toString() : ""));
        pn.addComponent(new Label("Estado:")); pn.addComponent(new Label(p.getEstado() != null ? p.getEstado().toString() : ""));
        pn.addComponent(new Button("Cerrar", vw::close));
        vw.setComponent(pn);
        textGUI.addWindowAndWait(vw);
    }

    private void onEditar(WindowBasedTextGUI textGUI) {
        int sel = table.getSelectedRow();
        if (sel < 0) { showMsg(textGUI, "Seleccione un pago primero."); return; }
        if (sel >= currentIds.size()) { showMsg(textGUI, "Fila inválida."); return; }
        String id = currentIds.get(sel);
        Pagos p = pagosManager.getById(id);
        if (p == null) { showMsg(textGUI, "Pago no encontrado."); return; }

        final Window w = new BasicWindow("Editar Pago");
        Panel form = new Panel(new GridLayout(2));

        
        

        
        form.addComponent(new Label("Monto:"));
        TextBox txtMonto = new TextBox(new TerminalSize(20,1), p.getMonto() != null ? p.getMonto().toPlainString() : "");
        form.addComponent(txtMonto);

        // Metodo
        form.addComponent(new Label("Método:"));
        ComboBox<String> cmbMetodo = new ComboBox<>();
        cmbMetodo.addItem("EFECTIVO");
        cmbMetodo.addItem("TRANSFERENCIA");
        cmbMetodo.setSelectedItem(p.getMetodo() != null ? p.getMetodo().toString() : "EFECTIVO");
        form.addComponent(cmbMetodo);

        
        form.addComponent(new Label("Estado:"));
        ComboBox<String> cmbEstado = new ComboBox<>();
        for (Pagos.EstadoPago e : Pagos.EstadoPago.values()) cmbEstado.addItem(e.toString());
        cmbEstado.setSelectedItem(p.getEstado() != null ? p.getEstado().toString() : Pagos.EstadoPago.PENDIENTE.toString());
        form.addComponent(cmbEstado);

        
        form.addComponent(new Label("Odontólogo:"));
        String nombreOdActual = "";
        if (p.getOdontologoID() != null) {
            Odontologo odact = odontologoManager.getById(p.getOdontologoID());
            if (odact != null) nombreOdActual = odact.getNombre();
        }
        TextBox txtOd = new TextBox(new TerminalSize(30,1), nombreOdActual);
        txtOd.setReadOnly(true);

        final String[] seleccionadoOdId = new String[1];
        seleccionadoOdId[0] = p.getOdontologoID();

        Button btnCambiarOd = new Button("Cambiar odontólogo", () -> {
            Odontologo elegido = seleccionarOdontologo(textGUI);
            if (elegido != null) {
                seleccionadoOdId[0] = elegido.getID();
                txtOd.setText(elegido.getNombre());
            }
        });

        Panel odontPanel = new Panel(new GridLayout(2));
        odontPanel.addComponent(txtOd);
        odontPanel.addComponent(btnCambiarOd);
        form.addComponent(odontPanel);

        
        Panel acc = new Panel(new GridLayout(2));
        Button btnGuardar = new Button("Guardar", () -> {
            
            BigDecimal monto;
            try {
                String mtext = txtMonto.getText().trim().replace(",", ".");
                monto = new BigDecimal(mtext);
                if (monto.compareTo(BigDecimal.ZERO) <= 0) { showMsg(textGUI, "El monto debe ser mayor a 0."); return; }
            } catch (Exception ex) {
                showMsg(textGUI, "Monto inválido."); return;
            }

            Pagos.MetodoPago metodo = cmbMetodo.getSelectedItem().equals("EFECTIVO")
                    ? Pagos.MetodoPago.EFECTIVO : Pagos.MetodoPago.TRANSFERENCIA;
            Pagos.EstadoPago estado = Pagos.EstadoPago.valueOf(cmbEstado.getSelectedItem());

            
            boolean ok1 = pagosManager.actualizarPorId(p.getID(), monto, metodo, estado);

            
            boolean ok2 = true;
            if (seleccionadoOdId[0] != null && !seleccionadoOdId[0].equals(p.getOdontologoID())) {
                ok2 = pagosManager.actualizarOdontologoPorId(p.getID(), seleccionadoOdId[0]);
            }

            if (ok1 && ok2) {
                showMsg(textGUI, "Pago actualizado correctamente.");
                reloadTable();
                w.close();
            } else {
                showMsg(textGUI, "Ocurrió un error al actualizar el pago.");
            }
        });

        Button btnCancelar = new Button("Cancelar", w::close);
        acc.addComponent(btnGuardar);
        acc.addComponent(btnCancelar);

        form.addComponent(new EmptySpace(TerminalSize.ONE), GridLayout.createHorizontallyFilledLayoutData(2));
        form.addComponent(acc, GridLayout.createHorizontallyFilledLayoutData(2));

        w.setComponent(form);
        w.setHints(Arrays.asList(Window.Hint.CENTERED));
        textGUI.addWindowAndWait(w);
    }

    private void onAnular(WindowBasedTextGUI textGUI) {
        int sel = table.getSelectedRow();
        if (sel < 0) { showMsg(textGUI, "Seleccione un pago primero."); return; }
        if (sel >= currentIds.size()) { showMsg(textGUI, "Fila inválida."); return; }
        String id = currentIds.get(sel);
        Pagos p = pagosManager.getById(id);
        if (p == null) { showMsg(textGUI, "Pago no encontrado."); return; }

        final Window conf = new BasicWindow("Confirmar anulación");
        Panel pn = new Panel(new GridLayout(1));
        pn.addComponent(new Label("¿Marcar pago como ANULADO? ID: " + p.getID()));
        pn.addComponent(new Button("Sí", () -> {
            boolean ok = pagosManager.eliminarPorId(p.getID()); 
            if (ok) showMsg(textGUI, "Pago anulado.");
            else showMsg(textGUI, "No se pudo anular.");
            conf.close();
            reloadTable();
        }));
        pn.addComponent(new Button("No", conf::close));
        conf.setComponent(pn);
        textGUI.addWindowAndWait(conf);
    }

    private void onEliminarFisico(WindowBasedTextGUI textGUI) {
        int sel = table.getSelectedRow();
        if (sel < 0) { showMsg(textGUI, "Seleccione un pago primero."); return; }
        if (sel >= currentIds.size()) { showMsg(textGUI, "Fila inválida."); return; }
        String id = currentIds.get(sel);
        Pagos p = pagosManager.getById(id);
        if (p == null) { showMsg(textGUI, "Pago no encontrado."); return; }

        final Window conf = new BasicWindow("Confirmar eliminación física");
        Panel pn = new Panel(new GridLayout(1));
        pn.addComponent(new Label("¿Eliminar pago PERMANENTEMENTE? ID: " + p.getID()));
        pn.addComponent(new Button("Sí", () -> {
            boolean ok = pagosManager.eliminarFisicoPorId(p.getID());
            if (ok) showMsg(textGUI, "Pago eliminado definitivamente.");
            else showMsg(textGUI, "No se pudo eliminar.");
            conf.close();
            reloadTable();
        }));
        pn.addComponent(new Button("No", conf::close));
        conf.setComponent(pn);
        textGUI.addWindowAndWait(conf);
    }

   
    private Odontologo seleccionarOdontologo(WindowBasedTextGUI textGUI) {
        final Window selWin = new BasicWindow("Seleccionar Odontólogo");
        Panel p = new Panel(new GridLayout(1));

        Table<String> t = new Table<>("#", "ID", "Nombre");
        List<Odontologo> list = odontologoManager.listar();
        for (int i = 0; i < list.size(); i++) {
            Odontologo o = list.get(i);
            t.getTableModel().addRow(String.valueOf(i+1), shortId(o.getID()), safe(o.getNombre()));
        }
        if (t.getTableModel().getRowCount() > 0) t.setSelectedRow(0);

        p.addComponent(t.withBorder(Borders.singleLine("Odontólogos")));

        final Odontologo[] resultado = new Odontologo[1];
        Panel actions = new Panel(new GridLayout(2));
        actions.addComponent(new Button("Seleccionar", () -> {
            int sel = t.getSelectedRow();
            if (sel < 0 || sel >= list.size()) { showMsg(textGUI, "Seleccione un odontólogo."); return; }
            resultado[0] = list.get(sel);
            selWin.close();
        }));
        actions.addComponent(new Button("Cancelar", selWin::close));
        p.addComponent(actions);

        selWin.setComponent(p);
        selWin.setHints(Arrays.asList(Window.Hint.CENTERED));
        textGUI.addWindowAndWait(selWin);
        return resultado[0];
    }

    // ---------- Helpers ----------
    private static String safe(String s) { return s == null ? "" : s; }
    private static String shortId(String id) { if (id == null) return ""; return id.length() > 8 ? id.substring(0,8) : id; }

    private void showMsg(WindowBasedTextGUI textGUI, String msg) {
        final Window m = new BasicWindow("Mensaje");
        Panel p = new Panel(new GridLayout(1));
        p.addComponent(new Label(msg));
        p.addComponent(new Button("Cerrar", m::close));
        m.setComponent(p);
        textGUI.addWindowAndWait(m);
    }
public static void showScreen(WindowBasedTextGUI textGUI,
                              PagosManager pagosManager,
                              PacienteManager pacienteManager,
                              OdontologoManager odontologoManager) {

    new PagosScreen(pagosManager, pacienteManager, odontologoManager)
            .show(textGUI, null);   // null = no queremos preseleccionar ningún pago
}



}
