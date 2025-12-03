package com.clinica.desktop.controllers;

import com.clinica.managers.OdontologoManager;
import com.clinica.modelos.Odontologo;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.UUID;

public class RegistrarOdontologoController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtNumero;
    @FXML private TextField txtEspecialidad;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    private OdontologoManager odontologoManager;

    public void setManager(OdontologoManager manager){
        this.odontologoManager = manager;
    }

    @FXML
    public void initialize() {
        btnGuardar.setOnAction(e -> guardar());
        btnCancelar.setOnAction(e -> cerrar());
    }

    private void guardar() {
        try {
            String id = UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 8);

            Odontologo o = new Odontologo(
                    txtNombre.getText(),
                    txtNumero.getText(),
                    txtEspecialidad.getText(),
                    id
            );

            odontologoManager.agregar(o);

            mostrarInfo("Odontólogo registrado correctamente.\nID: " + id);
            cerrar();

        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarError("Error al registrar odontólogo:\n" + ex.getMessage());
        }
    }

    private void cerrar() {
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
