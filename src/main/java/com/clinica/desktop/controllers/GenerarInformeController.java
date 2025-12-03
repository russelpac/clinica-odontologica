package com.clinica.desktop.controllers;

import com.clinica.reports.Reportes;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class GenerarInformeController {

    @FXML private TextField txtRuta;

    @FXML
    private void generarPDF() {

        String ruta = txtRuta.getText().trim();
        if (ruta.isEmpty()) ruta = "reports/pacientes.pdf";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:clinica.db")) {

            // Activar llaves foráneas igual que en TUI
            try (Statement st = conn.createStatement()) {
                st.execute("PRAGMA foreign_keys = ON");
            }

            // Generar el PDF usando tu clase ya existente
            Reportes.generarInformePacientes(conn, ruta);

            show("Éxito", "Informe generado correctamente:\n" + ruta);
            cerrar();

        } catch (Exception ex) {
            ex.printStackTrace();
            show("Error", "No se pudo generar el informe:\n" + ex.getMessage());
        }
    }

    @FXML
    private void cancelar() {
        cerrar();
    }

    private void cerrar() {
        Stage s = (Stage) txtRuta.getScene().getWindow();
        s.close();
    }

    private void show(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
