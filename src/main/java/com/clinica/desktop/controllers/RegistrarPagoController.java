package com.clinica.desktop.controllers;

import com.clinica.managers.OdontologoManager;
import com.clinica.managers.PacienteManager;
import com.clinica.managers.PagosManager;
import com.clinica.modelos.Odontologo;
import com.clinica.modelos.Paciente;
import com.clinica.modelos.Pagos;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class RegistrarPagoController {

    @FXML private TableView<Paciente> tablaPacientes;
    @FXML private TableColumn<Paciente, String> colPacID;
    @FXML private TableColumn<Paciente, String> colPacNombre;

    @FXML private TableView<Odontologo> tablaOdontologos;
    @FXML private TableColumn<Odontologo, String> colOdoID;
    @FXML private TableColumn<Odontologo, String> colOdoNombre;

    @FXML private TextField txtMonto;

    @FXML private RadioButton rbEfectivo;
    @FXML private RadioButton rbTransferencia;
    @FXML private ToggleGroup grupoMetodo;

    private PagosManager pagosManager;
    private PacienteManager pacienteManager;
    private OdontologoManager odontologoManager;

    // ========= SETTERS PARA INYECCIÓN DESDE MAIN =========
    public void setManagers(PagosManager pagosManager,
                            PacienteManager pacienteManager,
                            OdontologoManager odontologoManager) {
        this.pagosManager = pagosManager;
        this.pacienteManager = pacienteManager;
        this.odontologoManager = odontologoManager;

        cargarPacientes();
        cargarOdontologos();
    }

    // ========= CARGA DE TABLAS =========
    private void cargarPacientes() {
        colPacID.setCellValueFactory(new PropertyValueFactory<>("ID"));
        colPacNombre.setCellValueFactory(new PropertyValueFactory<>("nombres"));

        List<Paciente> lista = pacienteManager.listar();
        tablaPacientes.getItems().setAll(lista);
    }

    private void cargarOdontologos() {
        colOdoID.setCellValueFactory(new PropertyValueFactory<>("ID"));
        colOdoNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        List<Odontologo> lista = odontologoManager.listar();
        tablaOdontologos.getItems().setAll(lista);
    }

    // ========= ACCIONES =========

    @FXML
    private void guardarPago() {

        // ───── validar selección de paciente ─────
        Paciente paciente = tablaPacientes.getSelectionModel().getSelectedItem();
        if (paciente == null) {
            mostrarAlerta("Debe seleccionar un paciente.");
            return;
        }

        // ───── validar selección de odontólogo ─────
        Odontologo odontologo = tablaOdontologos.getSelectionModel().getSelectedItem();
        if (odontologo == null) {
            mostrarAlerta("Debe seleccionar un odontólogo.");
            return;
        }

        // ───── validar monto ─────
        BigDecimal monto;
        try {
            String txt = txtMonto.getText().trim().replace(",", ".");
            monto = new BigDecimal(txt);

            if (monto.compareTo(BigDecimal.ZERO) <= 0) {
                mostrarAlerta("El monto debe ser mayor a 0.");
                return;
            }
        } catch (Exception e) {
            mostrarAlerta("Monto inválido. Use formato numérico (ej: 150.50).");
            return;
        }

        // ───── método de pago ─────
        Pagos.MetodoPago metodo;
        if (rbEfectivo.isSelected()) metodo = Pagos.MetodoPago.EFECTIVO;
        else metodo = Pagos.MetodoPago.TRANSFERENCIA;

        // ───── crear pago ─────
        String pagoID = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        LocalDateTime ahora = LocalDateTime.now();

        Pagos pago = new Pagos(
                pagoID,
                paciente.getID(),
                odontologo.getID(),
                ahora,
                monto,
                metodo,
                Pagos.EstadoPago.PENDIENTE
        );

        pagosManager.agregar(pago);

        mostrarAlerta("Pago registrado correctamente.");

        cerrarVentana();
    }

    @FXML
    private void cancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) txtMonto.getScene().getWindow();
        stage.close();
    }

    // ========= Utilidades =========

    private void mostrarAlerta(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Mensaje");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}

