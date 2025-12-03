package com.clinica.desktop.controllers;

import com.clinica.managers.OdontologoManager;
import com.clinica.modelos.Odontologo;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class EditarOdontologoController {

    @FXML private Label lblId;

    @FXML private TextField txtNombre;
    @FXML private TextField txtCelular;
    @FXML private TextField txtEspecialidad;

    private OdontologoManager manager;
    private Odontologo original;
    private String odontologoId;

    public void setManager(OdontologoManager m) {
        this.manager = m;
    }

    public void cargarOdontologo(String id) {
        odontologoId = id;

        original = manager.getById(id);
        if (original == null) {
            alert("Error", "No se encontró el odontólogo");
            cerrar();
            return;
        }

        lblId.setText(id);
        txtNombre.setText(original.getNombre());
        txtCelular.setText(original.getNumeroCelular());
        txtEspecialidad.setText(original.getEspecialidad());
    }

    @FXML
    private void guardarCambios() {

        if (!txtNombre.getText().trim().isEmpty())
            original.setNombre(txtNombre.getText().trim());

        if (!txtCelular.getText().trim().isEmpty())
            original.setNumeroCelular(txtCelular.getText().trim());

        if (!txtEspecialidad.getText().trim().isEmpty())
            original.setEspecialidad(txtEspecialidad.getText().trim());

        boolean ok = manager.actualizarPorId(odontologoId, original);

        if (ok) {
            alert("Éxito", "Odontólogo actualizado.");
            cerrar();
        } else {
            alert("Error", "No se pudo actualizar.");
        }
    }

    @FXML
    private void cancelar() {
        cerrar();
    }

    private void cerrar() {
        Stage s = (Stage) lblId.getScene().getWindow();
        s.close();
    }

    private void alert(String t, String m) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(t);
        a.setHeaderText(null);
        a.setContentText(m);
        a.showAndWait();
    }
}

