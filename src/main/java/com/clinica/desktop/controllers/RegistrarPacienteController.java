package com.clinica.desktop.controllers;

import com.clinica.managers.PacienteManager;
import com.clinica.modelos.Paciente;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class RegistrarPacienteController {

    @FXML private TextField txtCI;
    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtNumero;
    @FXML private DatePicker dateNacimiento;

    @FXML private ComboBox<String> comboSexo;

    @FXML private TextField txtAlergias;
    @FXML private TextField txtConsultas;
    @FXML private TextField txtEmergencias;
    @FXML private TextField txtDireccion;
    @FXML private TextArea txtAntecedentes;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    private PacienteManager pacienteManager;

    public void setManager(PacienteManager manager){
        this.pacienteManager = manager;
    }

    @FXML
    public void initialize() {
        comboSexo.getItems().addAll("M", "F", "Otro");

        btnGuardar.setOnAction(e -> guardar());
        btnCancelar.setOnAction(e -> cerrarVentana());
    }

    private void guardar() {

        try {
            LocalDate nacimiento = dateNacimiento.getValue();
            LocalDateTime fechaConsulta = LocalDateTime.now();

            
            String id = UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 8);

            Paciente p = new Paciente(
                    id,
                    txtNombres.getText(),
                    txtCI.getText(),
                    txtApellidos.getText(),
                    txtNumero.getText(),
                    nacimiento,
                    txtAlergias.getText(),
                    txtConsultas.getText(),
                    comboSexo.getValue(),
                    txtEmergencias.getText(),
                    txtDireccion.getText(),
                    txtAntecedentes.getText(),
                    fechaConsulta
            );

            pacienteManager.agregar(p);

            mostrarInfo("Paciente registrado correctamente.\nID: " + id);

            cerrarVentana();

        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarError("Error al registrar paciente:\n" + ex.getMessage());
        }
    }

    private void cerrarVentana() {
        btnCancelar.getScene().getWindow().hide();
    }

    private void mostrarInfo(String msg){
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.showAndWait();
    }

    private void mostrarError(String msg){
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.showAndWait();
    }
}


