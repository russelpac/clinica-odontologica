package com.clinica.desktop.controllers;

import com.clinica.managers.PagosManager;
import com.clinica.modelos.Pagos;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public class ListarPagosController {

    @FXML private TableView<Pagos> tablaPagos;
    @FXML private TableColumn<Pagos, String> colID;
    @FXML private TableColumn<Pagos, String> colPaciente;
    @FXML private TableColumn<Pagos, String> colOdontologo;
    @FXML private TableColumn<Pagos, String> colFecha;
    @FXML private TableColumn<Pagos, BigDecimal> colMonto;
    @FXML private TableColumn<Pagos, String> colMetodo;
    @FXML private TableColumn<Pagos, String> colEstado;

    private PagosManager pagosManager;

    // ========= Setter para ser llamado desde Main =========
    public void setManager(PagosManager pagosManager) {
        this.pagosManager = pagosManager;
        cargarPagos();
    }

    // ========= LISTAR =========
    private void cargarPagos() {
        colID.setCellValueFactory(new PropertyValueFactory<>("ID"));
        colPaciente.setCellValueFactory(new PropertyValueFactory<>("pacienteID"));
        colOdontologo.setCellValueFactory(new PropertyValueFactory<>("odontologoID"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colMonto.setCellValueFactory(new PropertyValueFactory<>("monto"));
        colMetodo.setCellValueFactory(new PropertyValueFactory<>("metodo"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        List<Pagos> lista = pagosManager.listar();
        tablaPagos.getItems().setAll(lista);
    }

    // ========= EDITAR =========
    @FXML
    private void editarPago() {
        Pagos seleccionado = tablaPagos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Debe seleccionar un pago.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/clinica/desktop/views/EditarPagoView.fxml"));
            Parent root = loader.load();

            EditarPagoController controller = loader.getController();
            controller.setManagers(pagosManager, seleccionado);

            Stage stage = new Stage();
            stage.setTitle("Editar Pago");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            cargarPagos(); 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ========= ANULAR PAGO =========
    @FXML
    private void anularPago() {
        Pagos seleccionado = tablaPagos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Debe seleccionar un pago.");
            return;
        }

        boolean ok = pagosManager.eliminarPorId(seleccionado.getID());

        mostrarAlerta(ok ? "Pago anulado." : "Error al anular.");
        cargarPagos();
    }

    // ========= ALERTA =========
    private void mostrarAlerta(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}


