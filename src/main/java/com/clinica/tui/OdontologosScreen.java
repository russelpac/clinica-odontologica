package com.clinica.tui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.table.Table;
import com.clinica.managers.OdontologoManager;
import com.clinica.modelos.Odontologo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Pantalla para listar / ver / editar / eliminar odontólogos usando Lanterna.
 * Sigue el mismo patrón que PacientesScreen.
 */
public class OdontologosScreen {

    private final OdontologoManager odontologoManager;
    private Table<String> table;
    private List<String> currentIds;

    public OdontologosScreen(OdontologoManager odontologoManager) {
        this.odontologoManager = odontologoManager;
        this.currentIds = new ArrayList<>();
    }

    /** Compatibilidad: muestra la lista sin seleccionar nada */
    public void show(WindowBasedTextGUI textGUI) {
        show(textGUI, null);
    }

    /**
     * Muestra la lista de odontólogos. Si selectId != null intentará seleccionar ese ID.
     */
    public void show(WindowBasedTextGUI textGUI, String selectId) {
        final Window window = new BasicWindow("Odontólogos - Lista");
        Panel panel = new Panel(new GridLayout(1));

        // Instrucciones
        Label instrucciones = new Label("Use ↑/↓ para seleccionar, TAB para ir a botones y Enter para ejecutar.");
        panel.addComponent(instrucciones);

        // Tabla: índice, id corto, nombre, teléfono, especialidad
        table = new Table<>("#", "ID", "Nombre", "Tel", "Especialidad");
        table.setPreferredSize(new TerminalSize(90, 20));
        reloadTable();

        // Si se solicitó seleccionar un ID, buscar y marcar; si no, intentar dar foco
        if (selectId != null) {
            int idx = -1;
            for (int i = 0; i < currentIds.size(); i++) {
                if (selectId.equals(currentIds.get(i))) { idx = i; break; }
            }
            if (idx >= 0) {
                try {
                    table.setSelectedRow(idx);
                    table.takeFocus();
                } catch (Exception ignored) {}
            }
        } else {
            try {
                if (table.getTableModel().getRowCount() > 0) {
                    table.setSelectedRow(0);
                }
                table.takeFocus();
            } catch (Exception ignored) {}
        }

        panel.addComponent(table.withBorder(Borders.singleLine("Odontólogos")));

        Panel buttons = new Panel(new GridLayout(2));
        Button btnAcciones = new Button("Acciones", () -> showActionsForSelectedRow(textGUI));
        Button btnCerrar = new Button("Cerrar", window::close);
        buttons.addComponent(btnAcciones);
        buttons.addComponent(btnCerrar);

        panel.addComponent(buttons);
        window.setComponent(panel);
        textGUI.addWindowAndWait(window);
    }

    /**
     * Rellena la tabla y currentIds en el mismo orden.
     */
    private void reloadTable() {
        table.getTableModel().clear();
        currentIds.clear();
        List<Odontologo> lista = odontologoManager.listar();
        int idx = 1;
        for (Odontologo o : lista) {
            currentIds.add(o.getID());
            table.getTableModel().addRow(
                    String.valueOf(idx),
                    shortId(o.getID()),
                    safe(o.getNombre()),
                    safe(o.getNumeroCelular()),
                    safe(o.getEspecialidad())
            );
            idx++;
        }
        try { table.setSelectedRow(-1); } catch (Exception ignored) {}
    }

    /**
     * Devuelve el ID real de la fila seleccionada (si existe).
     */
    private Optional<String> selectedIdFromTable() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) return Optional.empty();
        if (currentIds == null) return Optional.empty();
        if (selectedRow >= currentIds.size()) return Optional.empty();
        return Optional.ofNullable(currentIds.get(selectedRow));
    }

    /**
     * Muestra submenú con acciones para la fila seleccionada.
     */
    private void showActionsForSelectedRow(WindowBasedTextGUI textGUI) {
        Optional<String> maybeId = selectedIdFromTable();
        if (maybeId.isEmpty()) {
            showMsg(textGUI, "Seleccione un odontólogo primero.");
            return;
        }
        String id = maybeId.get();

        final Window w = new BasicWindow("Acciones - Odontólogo");
        Panel p = new Panel(new GridLayout(1));
        p.addComponent(new Label("ID: " + shortId(id)));
        p.addComponent(new Label("Seleccione la acción:"));

        p.addComponent(new Button("Ver detalle", () -> {
            w.close();
            showDetailById(id, textGUI);
        }));

        p.addComponent(new Button("Editar", () -> {
            w.close();
            editById(id, textGUI);
            reloadTable();
        }));

        p.addComponent(new Button("Eliminar", () -> {
            w.close();
            final Window confirm = new BasicWindow("Confirmar eliminación");
            Panel cp = new Panel(new GridLayout(1));
            Odontologo od = odontologoManager.getById(id);
            cp.addComponent(new Label("Eliminar odontólogo: " + safe(od != null ? od.getNombre() : "")));
            cp.addComponent(new Label("¿Desea continuar?"));
            cp.addComponent(new Button("Sí", () -> {
                odontologoManager.eliminarPorId(id);
                showMsg(textGUI, "Odontólogo eliminado/anulado.");
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

    /**
     * Muestra detalle por ID.
     */
    private void showDetailById(String id, WindowBasedTextGUI textGUI) {
        Odontologo o = odontologoManager.getById(id);
        if (o == null) { showMsg(textGUI, "Odontólogo no encontrado."); return; }

        final Window w = new BasicWindow("Detalle Odontólogo");
        Panel pnl = new Panel(new GridLayout(2));
        pnl.addComponent(new Label("ID:"));
        pnl.addComponent(new Label(safe(o.getID())));
        pnl.addComponent(new Label("Nombre:"));
        pnl.addComponent(new Label(safe(o.getNombre())));
        pnl.addComponent(new Label("Teléfono:"));
        pnl.addComponent(new Label(safe(o.getNumeroCelular())));
        pnl.addComponent(new Label("Especialidad:"));
        pnl.addComponent(new Label(safe(o.getEspecialidad())));
        pnl.addComponent(new Button("Cerrar", w::close));
        w.setComponent(pnl);
        textGUI.addWindowAndWait(w);
    }

    /**
     * Abre editor por ID.
     */
    private void editById(String id, WindowBasedTextGUI textGUI) {
        Odontologo o = odontologoManager.getById(id);
        if (o == null) { showMsg(textGUI, "Odontólogo no encontrado."); return; }

        final Window w = new BasicWindow("Editar Odontólogo");
        Panel form = new Panel();
        form.setLayoutManager(new GridLayout(2));

        form.addComponent(new Label("Nombre:"));
        TextBox txtNombre = new TextBox(new TerminalSize(40, 1), safe(o.getNombre()));
        form.addComponent(txtNombre);

        form.addComponent(new Label("Teléfono:"));
        TextBox txtTel = new TextBox(new TerminalSize(20, 1), safe(o.getNumeroCelular()));
        form.addComponent(txtTel);

        form.addComponent(new Label("Especialidad:"));
        TextBox txtEsp = new TextBox(new TerminalSize(30, 1), safe(o.getEspecialidad()));
        form.addComponent(txtEsp);

        Panel actions = new Panel(new GridLayout(3));
        Button btnGuardar = new Button("Guardar", () -> {
            String v;
            v = txtNombre.getText().trim(); if (!v.isEmpty()) o.setNombre(v);
            v = txtTel.getText().trim(); if (!v.isEmpty()) o.setNumeroCelular(v);
            v = txtEsp.getText().trim(); if (!v.isEmpty()) o.setEspecialidad(v);

            odontologoManager.actualizarPorId(id, o); // en tu manager esto es void
            showMsg(textGUI, "Odontólogo actualizado.");
            w.close();
        });
        Button btnCancelar = new Button("Cancelar", w::close);
        actions.addComponent(btnGuardar);
        actions.addComponent(btnCancelar);

        form.addComponent(actions, GridLayout.createHorizontallyFilledLayoutData(2));
        w.setComponent(form);
        textGUI.addWindowAndWait(w);
    }

    /** Helpers */
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
