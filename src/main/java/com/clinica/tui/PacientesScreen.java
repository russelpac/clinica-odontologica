package com.clinica.tui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.table.Table;
import com.clinica.managers.PacienteManager;
import com.clinica.modelos.Paciente;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class PacientesScreen {

    private final PacienteManager pacienteManager;
    private Table<String> table; // tabla mostrada
    private List<String> currentIds; // IDs en el orden mostrado por la tabla

    public PacientesScreen(PacienteManager pacienteManager) {
        this.pacienteManager = pacienteManager;
        this.currentIds = new ArrayList<>();
    }


public void show(WindowBasedTextGUI textGUI) {
    
    show(textGUI, null);
}

/**
 * Versión de show que opcionalmente selecciona una fila por su ID (selectId).
 * Si selectId==null se comporta como antes.
 */
public void show(WindowBasedTextGUI textGUI, String selectId) {
    final Window window = new BasicWindow("Pacientes - Lista");
    Panel panel = new Panel(new GridLayout(1));

    // instrucción de ayuda para el manejo de la TUI
    Label instrucciones = new Label("Use ↑/↓ para seleccionar, TAB para ir a botones y Enter para ejecutar.");
    panel.addComponent(instrucciones);

    // tabla que mostrara las columnas de los pacientes
    table = new Table<>("#", "ID", "Nombre", "CI", "Tel", "Activo");
    table.setPreferredSize(new TerminalSize(90, 20));
    reloadTable(); // rellena currentIds y la tabla

    
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
                table.setSelectedRow(0); // seleccionar la primera por defecto
            }
            table.takeFocus();
        } catch (Exception ignored) {}
    }

    panel.addComponent(table.withBorder(Borders.singleLine("Pacientes")));

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
     * Rellena la tabla y la lista currentIds en el mismo orden.
     */
    private void reloadTable() {
        table.getTableModel().clear();
        currentIds.clear();
        List<Paciente> lista = pacienteManager.listar();
        int idx = 1;
        for (Paciente p : lista) {
            currentIds.add(p.getID()); // guardo el ID real en la misma posición que la fila
            table.getTableModel().addRow(
                    String.valueOf(idx),
                    shortId(p.getID()), 
                    safe(p.getNombres()) + " " + safe(p.getApellidos()),
                    safe(p.getCI()),
                    safe(p.getNumeroContacto()),
                    p.isActivo() ? "Sí" : "No"
            );
            idx++;
        }
        // intentar quitar selección por defecto
        try {
            table.setSelectedRow(-1);
        } catch (Exception ignored) {
        }
    }
   

    // ---------- Acciones ----------

    
    private Optional<String> selectedIdFromTable() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) return Optional.empty();
        if (currentIds == null) return Optional.empty();
        if (selectedRow >= currentIds.size()) return Optional.empty();
        return Optional.ofNullable(currentIds.get(selectedRow));
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
    
private void showActionsForSelectedRow(WindowBasedTextGUI textGUI) {
    Optional<String> maybeId = selectedIdFromTable();
    if (maybeId.isEmpty()) {
        showMsg(textGUI, "Seleccione un paciente primero.");
        return;
    }
    String id = maybeId.get();

    final Window w = new BasicWindow("Acciones - Paciente");
    Panel p = new Panel(new GridLayout(1));
    p.addComponent(new Label("ID: " + shortId(id)));
    p.addComponent(new Label("Seleccione la acción:"));

    // Ver
    p.addComponent(new Button("Ver detalle", () -> {
        w.close();
        showDetailById(id, textGUI);
    }));

    // Editar
    p.addComponent(new Button("Editar", () -> {
        w.close();
        editById(id, textGUI);
        // recargar tabla al volver del editor
        reloadTable();
    }));

    // Eliminar (con confirmación)
    p.addComponent(new Button("Eliminar", () -> {
        w.close();
        final Window confirm = new BasicWindow("Confirmar eliminación");
        Panel cp = new Panel(new GridLayout(1));
        Paciente pto = pacienteManager.getById(id);
        cp.addComponent(new Label("Anular paciente: " + safe(pto.getNombres()) + " " + safe(pto.getApellidos())));
        cp.addComponent(new Label("¿Desea continuar? (Se marcará como inactivo)"));
        cp.addComponent(new Button("Sí", () -> {
            boolean ok = pacienteManager.eliminarPorId(id);
            if (ok) showMsg(textGUI, "Paciente anulado.");
            else showMsg(textGUI, "No se pudo anular.");
            confirm.close();
            // recargar tabla luego del confirm
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

/** Muestra detalle de paciente por ID (extraído de la lógica antigua) */
private void showDetailById(String id, WindowBasedTextGUI textGUI) {
    Paciente p = pacienteManager.getById(id);
    if (p == null) { showMsg(textGUI, "Paciente no encontrado."); return; }

    final Window w = new BasicWindow("Detalle Paciente");
    Panel pnl = new Panel(new GridLayout(2));
    pnl.addComponent(new Label("ID:"));
    pnl.addComponent(new Label(safe(p.getID())));
    pnl.addComponent(new Label("Nombres:"));
    pnl.addComponent(new Label(safe(p.getNombres())));
    pnl.addComponent(new Label("Apellidos:"));
    pnl.addComponent(new Label(safe(p.getApellidos())));
    pnl.addComponent(new Label("CI:"));
    pnl.addComponent(new Label(safe(p.getCI())));
    pnl.addComponent(new Label("Teléfono:"));
    pnl.addComponent(new Label(safe(p.getNumeroContacto())));
    pnl.addComponent(new Label("Fecha Nac:"));
    pnl.addComponent(new Label(p.getFechaNacimiento() != null ? p.getFechaNacimiento().toString() : ""));
    pnl.addComponent(new Label("Alergias:"));
    pnl.addComponent(new Label(safe(p.getAlergias())));
    pnl.addComponent(new Label("Antecedentes:"));
    pnl.addComponent(new Label(safe(p.getAntecMedicos())));
    pnl.addComponent(new Label("Consultas:"));
    pnl.addComponent(new Label(safe(p.getConsultas())));
    pnl.addComponent(new Label("Activo:"));
    pnl.addComponent(new Label(p.isActivo() ? "Sí" : "No"));
    pnl.addComponent(new Button("Cerrar", w::close));
    w.setComponent(pnl);
    textGUI.addWindowAndWait(w);
}

/**
 * Abre el editor por ID (usa la misma UI que onEditar, pero operando por ID).
 * Mantiene comportamiento: si deja campo vacío no cambia.
 */
private void editById(String id, WindowBasedTextGUI textGUI) {
    Paciente paciente = pacienteManager.getById(id);
    if (paciente == null) { showMsg(textGUI, "Paciente no encontrado."); return; }

    final Window w = new BasicWindow("Editar Paciente");
    Panel form = new Panel();
    form.setLayoutManager(new GridLayout(2));

    form.addComponent(new Label("Nombres:"));
    TextBox txtNombres = new TextBox(new TerminalSize(40, 1), safe(paciente.getNombres()));
    form.addComponent(txtNombres);

    form.addComponent(new Label("Apellidos:"));
    TextBox txtApellidos = new TextBox(new TerminalSize(40, 1), safe(paciente.getApellidos()));
    form.addComponent(txtApellidos);

    form.addComponent(new Label("CI:"));
    TextBox txtCI = new TextBox(new TerminalSize(20, 1), safe(paciente.getCI()));
    form.addComponent(txtCI);

    form.addComponent(new Label("Teléfono:"));
    TextBox txtTel = new TextBox(new TerminalSize(20, 1), safe(paciente.getNumeroContacto()));
    form.addComponent(txtTel);

    form.addComponent(new Label("Fecha Nac (yyyy-MM-dd):"));
    TextBox txtFN = new TextBox(new TerminalSize(20, 1),
            paciente.getFechaNacimiento() != null ? paciente.getFechaNacimiento().toString() : "");
    form.addComponent(txtFN);

    form.addComponent(new Label("Alergias:"));
    TextBox txtAlergias = new TextBox(new TerminalSize(40, 1), safe(paciente.getAlergias()));
    form.addComponent(txtAlergias);

    form.addComponent(new Label("Antecedentes:"));
    TextBox txtAntec = new TextBox(new TerminalSize(40, 1), safe(paciente.getAntecMedicos()));
    form.addComponent(txtAntec);

    form.addComponent(new Label("Consultas:"));
    TextBox txtCons = new TextBox(new TerminalSize(40, 1), safe(paciente.getConsultas()));
    form.addComponent(txtCons);

    form.addComponent(new Label("Activo (S/N):"));
    TextBox txtActivo = new TextBox(new TerminalSize(5, 1), paciente.isActivo() ? "S" : "N");
    form.addComponent(txtActivo);

    Panel actions = new Panel(new GridLayout(3));
    Button btnGuardar = new Button("Guardar", () -> {
        String v;
        v = txtNombres.getText().trim();
        if (!v.isEmpty()) paciente.setNombres(v);
        v = txtApellidos.getText().trim();
        if (!v.isEmpty()) paciente.setApellidos(v);
        v = txtCI.getText().trim();
        if (!v.isEmpty()) paciente.setCI(v);
        v = txtTel.getText().trim();
        if (!v.isEmpty()) paciente.setNumeroContacto(v);
        v = txtFN.getText().trim();
        if (!v.isEmpty()) {
            try { paciente.setFechaNacimiento(LocalDate.parse(v)); } catch (Exception ex) { /* ignorar */ }
        }
        v = txtAlergias.getText().trim();
        if (!v.isEmpty()) paciente.setAlergias(v);
        v = txtAntec.getText().trim();
        if (!v.isEmpty()) paciente.setAntecMedicos(v);
        v = txtCons.getText().trim();
        if (!v.isEmpty()) paciente.setConsultas(v);
        v = txtActivo.getText().trim();
        if (v.equalsIgnoreCase("S")) paciente.setActivo(true);
        else if (v.equalsIgnoreCase("N")) paciente.setActivo(false);

        boolean ok = pacienteManager.actualizarPorId(id, paciente);
        if (ok) showMsg(textGUI, "Paciente actualizado.");
        else showMsg(textGUI, "Error al actualizar.");
        w.close();
    });
    Button btnCancelar = new Button("Cancelar", w::close);
    actions.addComponent(btnGuardar);
    actions.addComponent(btnCancelar);

    form.addComponent(actions, GridLayout.createHorizontallyFilledLayoutData(2));

    w.setComponent(form);
    textGUI.addWindowAndWait(w);
}

/** Eliminar por id  */
private void deleteById(String id, WindowBasedTextGUI textGUI) {
    boolean ok = pacienteManager.eliminarPorId(id);
    if (ok) showMsg(textGUI, "Paciente anulado.");
    else showMsg(textGUI, "No se pudo anular.");
    reloadTable();
}

}

