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
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * RegistrarPagoScreen usando Table (más compatible que ListBox si hay problemas de classpath).
 */
public class RegistrarPagoScreen {

    private final PagosManager pagosManager;
    private final PacienteManager pacienteManager;
    private final OdontologoManager odontologoManager;

    public RegistrarPagoScreen(PagosManager pagosManager,
                               PacienteManager pacienteManager,
                               OdontologoManager odontologoManager) {
        this.pagosManager = pagosManager;
        this.pacienteManager = pacienteManager;
        this.odontologoManager = odontologoManager;
    }

    public void show(WindowBasedTextGUI textGUI) {
        final Window w = new BasicWindow("Registrar Pago");
        Panel root = new Panel(new GridLayout(1));
        root.setPreferredSize(new TerminalSize(120, 30));

        root.addComponent(new Label("Seleccione paciente y odontólogo con las flechas (↑/↓). Use TAB para ir a botones y Enter para activar."));

        // --- Panel central con dos tablas ---
        Panel center = new Panel(new GridLayout(2));

        // Tabla pacientes (izquierda)
        Table<String> tablePac = new Table<>("#", "ID", "Nombre (CI)");
        tablePac.setPreferredSize(new TerminalSize(56, 12));
        List<Paciente> pacientes = pacienteManager.listar();
        for (int i = 0; i < pacientes.size(); i++) {
            Paciente px = pacientes.get(i);
            tablePac.getTableModel().addRow(
                String.valueOf(i+1),
                shortId(px.getID()),
                safe(px.getNombres()) + " " + safe(px.getApellidos()) + " (" + safe(px.getCI()) + ")"
            );
        }
        if (tablePac.getTableModel().getRowCount() > 0) tablePac.setSelectedRow(0);

        // Tabla odontologos (derecha)
        Table<String> tableOd = new Table<>("#", "ID", "Nombre");
        tableOd.setPreferredSize(new TerminalSize(56, 12));
        List<Odontologo> odontologos = odontologoManager.listar();
        for (int i = 0; i < odontologos.size(); i++) {
            Odontologo ox = odontologos.get(i);
            tableOd.getTableModel().addRow(
                String.valueOf(i+1),
                shortId(ox.getID()),
                safe(ox.getNombre())
            );
        }
        if (tableOd.getTableModel().getRowCount() > 0) tableOd.setSelectedRow(0);

        center.addComponent(new Panel().addComponent(new Label("Pacientes:")).addComponent(tablePac.withBorder(Borders.singleLine("Pacientes"))));
        center.addComponent(new Panel().addComponent(new Label("Odontólogos:")).addComponent(tableOd.withBorder(Borders.singleLine("Odontólogos"))));
        root.addComponent(center);

        // --- Panel inferior: monto, metodo y botones ---
        Panel bottom = new Panel(new GridLayout(2));

        bottom.addComponent(new Label("Monto (ej. 150.50):"));
        final TextBox txtMonto = new TextBox(new TerminalSize(20, 1));
        bottom.addComponent(txtMonto);

        bottom.addComponent(new Label("Método de pago:"));
        final RadioBoxList<String> metodoList = new RadioBoxList<>();
        metodoList.addItem("1) EFECTIVO");
        metodoList.addItem("2) TRANSFERENCIA");
        metodoList.setSelectedIndex(0);
        bottom.addComponent(metodoList);

        // botones
        Panel actions = new Panel(new GridLayout(3));
        Button btnGuardar = new Button("Guardar", () -> {
            // validaciones
            if (tablePac.getTableModel().getRowCount() == 0) { showMsg(textGUI, "No hay pacientes."); return; }
            if (tableOd.getTableModel().getRowCount() == 0) { showMsg(textGUI, "No hay odontólogos."); return; }

            int selPac = tablePac.getSelectedRow();
            int selOd = tableOd.getSelectedRow();
            if (selPac < 0 || selPac >= pacientes.size()) { showMsg(textGUI, "Seleccione un paciente."); return; }
            if (selOd < 0 || selOd >= odontologos.size()) { showMsg(textGUI, "Seleccione un odontólogo."); return; }

            Paciente pacienteSeleccionado = pacientes.get(selPac);
            Odontologo odontologoSeleccionado = odontologos.get(selOd);

            BigDecimal monto;
            try {
                String mStr = txtMonto.getText().trim().replace(",", ".");
                monto = new BigDecimal(mStr);
                if (monto.compareTo(BigDecimal.ZERO) <= 0) { showMsg(textGUI, "El monto debe ser mayor que 0."); return; }
            } catch (Exception ex) {
                showMsg(textGUI, "Monto inválido. Use formato numérico (ej. 150.50)."); return;
            }

            String metodoSel = metodoList.getCheckedItem();
            Pagos.MetodoPago metodo = metodoSel != null && metodoSel.startsWith("1") ? Pagos.MetodoPago.EFECTIVO : Pagos.MetodoPago.TRANSFERENCIA;

            String pagoID = UUID.randomUUID().toString().replace("-", "").substring(0,8);
            LocalDateTime ahora = LocalDateTime.now();

            Pagos pago = new Pagos(
                    pagoID,
                    pacienteSeleccionado.getID(),
                    odontologoSeleccionado.getID(),
                    ahora,
                    monto,
                    metodo,
                    Pagos.EstadoPago.PENDIENTE
            );

            try {
                pagosManager.agregar(pago);
                // abrir lista y seleccionar el nuevo pago
                new PagosScreen(pagosManager, pacienteManager, odontologoManager).show(textGUI, pagoID);
                w.close();
            } catch (Exception ex) {
                showMsg(textGUI, "Error al registrar pago: " + ex.getMessage());
            }
        });

        Button btnCancelar = new Button("Cancelar", w::close);
        actions.addComponent(btnGuardar);
        actions.addComponent(btnCancelar);

        bottom.addComponent(new EmptySpace(TerminalSize.ONE), GridLayout.createHorizontallyFilledLayoutData(2));
        bottom.addComponent(actions, GridLayout.createHorizontallyFilledLayoutData(2));

        root.addComponent(bottom);
        w.setComponent(root);
        textGUI.addWindowAndWait(w);
    }

    // ---------- helpers ----------
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
}

