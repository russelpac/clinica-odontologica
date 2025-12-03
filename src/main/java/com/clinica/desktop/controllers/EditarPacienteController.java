package com.clinica.desktop.controllers;

import com.clinica.managers.PacienteManager;
import com.clinica.modelos.Paciente;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class EditarPacienteController {

    @FXML private Label lblId;
    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtCI;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtFechaNacimiento;
    @FXML private TextField txtAlergias;
    @FXML private TextField txtAntecedentes;
    @FXML private TextField txtConsultas;
    @FXML private TextField txtActivo;

    private PacienteManager pacienteManager;
    private Paciente pacienteOriginal;

    private String pacienteId;

    // ---------------------- Inicialización --------------------------

    public void setPacienteManager(PacienteManager pm) {
        this.pacienteManager = pm;
    }

    public void cargarPaciente(String id) {
        this.pacienteId = id;

        pacienteOriginal = pacienteManager.getById(id);
        if (pacienteOriginal == null) {
            mostrarAlerta("Error", "No se encontró el paciente.");
            cerrar();
            return;
        }

        lblId.setText(id);
        txtNombres.setText(pacienteOriginal.getNombres());
        txtApellidos.setText(pacienteOriginal.getApellidos());
        txtCI.setText(pacienteOriginal.getCI());
        txtTelefono.setText(pacienteOriginal.getNumeroContacto());
        txtFechaNacimiento.setText(
                pacienteOriginal.getFechaNacimiento() != null ?
                pacienteOriginal.getFechaNacimiento().toString() : ""
        );
        txtAlergias.setText(pacienteOriginal.getAlergias());
        txtAntecedentes.setText(pacienteOriginal.getAntecMedicos());
        txtConsultas.setText(pacienteOriginal.getConsultas());
        txtActivo.setText(pacienteOriginal.isActivo() ? "S" : "N");
    }

    // ------------------ Acciones -------------------------

    @FXML
    private void guardarCambios() {

        Paciente p = pacienteOriginal;

        // si campo no está vacío, se actualiza; si está vacío no cambia
        if (!txtNombres.getText().trim().isEmpty())
            p.setNombres(txtNombres.getText().trim());

        if (!txtApellidos.getText().trim().isEmpty())
            p.setApellidos(txtApellidos.getText().trim());

        if (!txtCI.getText().trim().isEmpty())
            p.setCI(txtCI.getText().trim());

        if (!txtTelefono.getText().trim().isEmpty())
            p.setNumeroContacto(txtTelefono.getText().trim());

        if (!txtFechaNacimiento.getText().trim().isEmpty()) {
            try {
                p.setFechaNacimiento(LocalDate.parse(txtFechaNacimiento.getText().trim()));
            } catch (Exception ignored) {}
        }

        if (!txtAlergias.getText().trim().isEmpty())
            p.setAlergias(txtAlergias.getText().trim());

        if (!txtAntecedentes.getText().trim().isEmpty())
            p.setAntecMedicos(txtAntecedentes.getText().trim());

        if (!txtConsultas.getText().trim().isEmpty())
            p.setConsultas(txtConsultas.getText().trim());

        if (!txtActivo.getText().trim().isEmpty()) {
            String v = txtActivo.getText().trim();
            if (v.equalsIgnoreCase("S")) p.setActivo(true);
            else if (v.equalsIgnoreCase("N")) p.setActivo(false);
        }

        boolean ok = pacienteManager.actualizarPorId(pacienteId, p);

        if (ok) {
            mostrarAlerta("Éxito", "Paciente actualizado correctamente.");
            cerrar();
        } else {
            mostrarAlerta("Error", "No se pudo actualizar el paciente.");
        }
    }

    @FXML
    private void cancelar() {
        cerrar();
    }

    private void cerrar() {
        Stage stage = (Stage) lblId.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
