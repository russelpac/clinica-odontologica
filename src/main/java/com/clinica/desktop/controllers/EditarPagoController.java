package com.clinica.desktop.controllers;

import com.clinica.managers.PagosManager;
import com.clinica.modelos.Pagos;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.math.BigDecimal;

public class EditarPagoController {

    @FXML private TextField txtID;
    @FXML private TextField txtPaciente;
    @FXML private TextField txtOdontologo;
    @FXML private TextField txtMonto;

    @FXML private ComboBox<Pagos.MetodoPago> cbMetodo;
    @FXML private ComboBox<Pagos.EstadoPago> cbEstado;

    private PagosManager pagosManager;
    private Pagos pago;

    public void setManagers(PagosManager pagosManager, Pagos pago) {
        this.pagosManager = pagosManager;
        this.pago = pago;
        cargarDatos();
    }

    private void cargarDatos() {
        txtID.setText(pago.getID());
        txtPaciente.setText(pago.getPacienteID());
        txtOdontologo.setText(pago.getOdontologoID());

        txtMonto.setText(pago.getMonto().toString());

        cbMetodo.getItems().setAll(Pagos.MetodoPago.values());
        cbMetodo.setValue(pago.getMetodo());

        cbEstado.getItems().setAll(Pagos.EstadoPago.values());
        cbEstado.setValue(pago.getEstado());
    }

    @FXML
    private void guardarCambios() {

        BigDecimal nuevoMonto;
        try {
            nuevoMonto = new BigDecimal(txtMonto.getText());
        } catch (Exception e) {
            mostrar("Monto inválido.");
            return;
        }

        Pagos.MetodoPago nuevoMetodo = cbMetodo.getValue();
        Pagos.EstadoPago nuevoEstado = cbEstado.getValue();

        boolean ok = pagosManager.actualizarPorId(
                pago.getID(),
                nuevoMonto,
                nuevoMetodo,
                nuevoEstado
        );

        mostrar(ok ? "Pago actualizado correctamente." : "Error al actualizar.");

        cerrar();
    }

    @FXML
    private void cancelar() {
        cerrar();
    }

    private void cerrar() {
        Stage stage = (Stage) txtMonto.getScene().getWindow();
        stage.close();
    }

    private void mostrar(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
