package com.clinica.tui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.clinica.managers.PacienteManager;
import com.clinica.modelos.Paciente;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.UUID;


public class RegistrarPacienteScreen {

    private final PacienteManager pacienteManager;

    public RegistrarPacienteScreen(PacienteManager pacienteManager) {
        this.pacienteManager = pacienteManager;
    }

    public void show(WindowBasedTextGUI textGUI) {
        final Window w = new BasicWindow("Registrar Paciente");
        Panel form = new Panel();
        form.setLayoutManager(new GridLayout(2));

        // Campos básicos
        form.addComponent(new Label("Nombres*:"));
        TextBox txtNombres = new TextBox(new TerminalSize(40, 1));
        form.addComponent(txtNombres);

        form.addComponent(new Label("Apellidos*:"));
        TextBox txtApellidos = new TextBox(new TerminalSize(40, 1));
        form.addComponent(txtApellidos);

        form.addComponent(new Label("CI*:"));
        TextBox txtCI = new TextBox(new TerminalSize(20, 1));
        form.addComponent(txtCI);

        form.addComponent(new Label("Numero de contacto:"));
        TextBox txtNumero = new TextBox(new TerminalSize(20, 1));
        form.addComponent(txtNumero);

        form.addComponent(new Label("Sexo:"));
        TextBox txtSexo = new TextBox(new TerminalSize(10, 1));
        form.addComponent(txtSexo);

        // Fecha (año/mes/dia) - ahora obligatoria y validada estrictamente
        form.addComponent(new Label("Fecha Nac - Año (yyyy) *:"));
        TextBox txtAnio = new TextBox(new TerminalSize(8, 1));
        form.addComponent(txtAnio);

        form.addComponent(new Label("Fecha Nac - Mes (1-12) *:"));
        TextBox txtMes = new TextBox(new TerminalSize(8, 1));
        form.addComponent(txtMes);

        form.addComponent(new Label("Fecha Nac - Día (1-31) *:"));
        TextBox txtDia = new TextBox(new TerminalSize(8, 1));
        form.addComponent(txtDia);

        form.addComponent(new Label("Contacto de emergencias:"));
        TextBox txtContactoEmer = new TextBox(new TerminalSize(30, 1));
        form.addComponent(txtContactoEmer);

        form.addComponent(new Label("Dirección:"));
        TextBox txtDireccion = new TextBox(new TerminalSize(40, 1));
        form.addComponent(txtDireccion);

        form.addComponent(new Label("Alergias:"));
        TextBox txtAlergias = new TextBox(new TerminalSize(40, 1));
        form.addComponent(txtAlergias);

        form.addComponent(new Label("Antecedentes médicos:"));
        TextBox txtAntec = new TextBox(new TerminalSize(40, 1));
        form.addComponent(txtAntec);

        form.addComponent(new Label("Motivo de consulta:"));
        TextBox txtCons = new TextBox(new TerminalSize(40, 1));
        form.addComponent(txtCons);

        // Botones
        Panel actions = new Panel(new GridLayout(3));
        Button btnGuardar = new Button("Guardar", () -> {
            // Validaciones básicas
            String nombres = txtNombres.getText().trim();
            String apellidos = txtApellidos.getText().trim();
            String ci = txtCI.getText().trim();

            if (nombres.isEmpty() || apellidos.isEmpty() || ci.isEmpty()) {
                showMsg(textGUI, "Los campos *Nombres, Apellidos y CI* son obligatorios.");
                return;
            }

            // Fecha obligatoria: los tres campos deben estar llenos
            String anioStr = txtAnio.getText().trim();
            String mesStr = txtMes.getText().trim();
            String diaStr = txtDia.getText().trim();

            if (anioStr.isEmpty() || mesStr.isEmpty() || diaStr.isEmpty()) {
                showMsg(textGUI, "La fecha de nacimiento es obligatoria. Complete Año, Mes y Día.");
                return;
            }

            int anio, mes, dia;
            try {
                anio = Integer.parseInt(anioStr);
                mes = Integer.parseInt(mesStr);
                dia = Integer.parseInt(diaStr);
            } catch (NumberFormatException nfe) {
                showMsg(textGUI, "Año/Mes/Día deben ser números enteros. Revise la fecha.");
                return;
            }

            // comprobaciones rápidas de rango para dar feedback más claro
            if (mes < 1 || mes > 12) {
                showMsg(textGUI, "Mes inválido. Debe estar entre 1 y 12.");
                return;
            }
            if (dia < 1 || dia > 31) {
                showMsg(textGUI, "Día inválido. Debe estar entre 1 y 31.");
                return;
            }

            LocalDate fechaNacimiento;
            try {
                // LocalDate.of validará días por mes (ej. noviembre no tiene 31)
                fechaNacimiento = LocalDate.of(anio, mes, dia);
            } catch (DateTimeException dte) {
                showMsg(textGUI, "Fecha inválida. Revise año/mes/día (ej. noviembre no tiene 31 días).");
                return;
            }

            // No puede ser futura
            LocalDate hoy = LocalDate.now();
            if (fechaNacimiento.isAfter(hoy)) {
                showMsg(textGUI, "Fecha de nacimiento no puede ser en el futuro.");
                return;
            }

            // Limitar edad razonable (ej. <= 150 años)
            int edad = Period.between(fechaNacimiento, hoy).getYears();
            if (edad > 150) {
                showMsg(textGUI, "Edad demasiado grande. Verifique la fecha de nacimiento.");
                return;
            }
            if (edad < 0) {
                showMsg(textGUI, "Fecha de nacimiento inválida.");
                return;
            }

            // Generar ID
            String id = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
            LocalDateTime fechaConsulta = LocalDateTime.now();

            // Crear Paciente
            Paciente nuevo = new Paciente(
                    id,
                    nombres,
                    ci,
                    apellidos,
                    txtNumero.getText().trim(),
                    fechaNacimiento,
                    txtAlergias.getText().trim(),
                    txtCons.getText().trim(),
                    txtSexo.getText().trim(),
                    txtContactoEmer.getText().trim(),
                    txtDireccion.getText().trim(),
                    txtAntec.getText().trim(),
                    fechaConsulta
            );

            try {
                pacienteManager.agregar(nuevo);
                showMsg(textGUI, "Paciente registrado correctamente (ID: " + id + ").");
                new PacientesScreen(pacienteManager).show(textGUI, id);
                w.close();
            } catch (Exception ex) {
                showMsg(textGUI, "Error al registrar paciente: " + ex.getMessage());
            }
        });

        Button btnCancelar = new Button("Cancelar", w::close);
        actions.addComponent(btnGuardar);
        actions.addComponent(btnCancelar);

        form.addComponent(new EmptySpace(), GridLayout.createHorizontallyFilledLayoutData(2));
        form.addComponent(actions, GridLayout.createHorizontallyFilledLayoutData(2));

        w.setComponent(form);
        textGUI.addWindowAndWait(w);
    }
//helper
    private void showMsg(WindowBasedTextGUI textGUI, String msg) {
        final Window w = new BasicWindow("Mensaje");
        Panel p = new Panel(new GridLayout(1));
        p.addComponent(new Label(msg));
        p.addComponent(new Button("Cerrar", w::close));
        w.setComponent(p);
        textGUI.addWindowAndWait(w);
    }
}
