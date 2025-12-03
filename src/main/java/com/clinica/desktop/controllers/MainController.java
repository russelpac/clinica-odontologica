package com.clinica.desktop.controllers;

import com.clinica.managers.OdontologoManager;
import com.clinica.managers.PacienteManager;
import com.clinica.managers.PagosManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import javafx.stage.Modality;

public class MainController {

    private PacienteManager pacienteManager;
    private OdontologoManager odontologoManager;
    private PagosManager pagosManager;

    private Connection conn;

    
    public void inicializar() {
    try {
        conn = DriverManager.getConnection("jdbc:sqlite:clinica.db");
        conn.createStatement().execute("PRAGMA foreign_keys = ON");

        pacienteManager = new PacienteManager(conn);
        odontologoManager = new OdontologoManager(conn);
        pagosManager = new PagosManager(conn);

        System.out.println("Conexión establecida y managers listos.");

    } catch (Exception e) {
        e.printStackTrace();
        System.err.println("ERROR al inicializar MainController: " + e.getMessage());
    }
}


    
    @FXML
    private void abrirListadoPacientes() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/clinica/desktop/views/ListarPacientesView.fxml"));
            Parent root = loader.load();

           
            ListarPacientesController controller = loader.getController();
            controller.setManager(pacienteManager);

            
            Stage stage = new Stage();
            stage.setTitle("Listado de Pacientes");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("ERROR abriendo listado de pacientes: " + e.getMessage());
        }
    }

    
    @FXML
    private void abrirRegistrarPaciente() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/clinica/desktop/views/RegistrarPacienteView.fxml"));
            Parent root = loader.load();

            // Obtener controlador
            RegistrarPacienteController controller = loader.getController();
            controller.setManager(pacienteManager);

            // Crear ventana
            Stage stage = new Stage();
            stage.setTitle("Registrar Paciente");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("ERROR abriendo registro de paciente: " + e.getMessage());
        }
    }
    @FXML
    private void abrirRegistrarOdontologo() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/clinica/desktop/views/RegistrarOdontologoView.fxml"));
            Parent root = loader.load();

            RegistrarOdontologoController controller = loader.getController();
            controller.setManager(odontologoManager);

            Stage stage = new Stage();
            stage.setTitle("Registrar Odontólogo");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("ERROR abriendo registro de odontólogo: " + e.getMessage());
        }
    }
    @FXML
    private void abrirListadoOdontologos() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/clinica/desktop/views/ListarOdontologosView.fxml"));
            Parent root = loader.load();

            ListarOdontologosController controller = loader.getController();
            controller.setManager(odontologoManager);

            Stage stage = new Stage();
            stage.setTitle("Listado de Odontólogos");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("ERROR abriendo listado de odontólogos: " + e.getMessage());
        }
    }
    @FXML
    private void abrirGenerarInformePDF() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/clinica/desktop/views/GenerarInformeView.fxml"));
            Parent root = loader.load();

            GenerarInformeController controller = loader.getController();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Generar Informe PDF");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }   
    }
    @FXML
    private void abrirRegistrarPago() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                "/com/clinica/desktop/views/RegistrarPagoView.fxml"
            ));
            Parent root = loader.load();

            RegistrarPagoController controller = loader.getController();
            controller.setManagers(pagosManager,pacienteManager,odontologoManager);

            Stage stage = new Stage();
            stage.setTitle("Registrar Pago");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("ERROR abriendo registrar pago: " + e.getMessage());
        }
    }
    @FXML
    private void abrirListadoPagos() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/clinica/desktop/views/ListarPagosView.fxml"));
            Parent root = loader.load();

            ListarPagosController controller = loader.getController();
            controller.setManager(pagosManager);

            Stage stage = new Stage();
            stage.setTitle("Listado de Pagos");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
}





}


