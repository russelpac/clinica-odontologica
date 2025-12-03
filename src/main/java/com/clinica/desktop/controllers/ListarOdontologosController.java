package com.clinica.desktop.controllers;

import com.clinica.managers.OdontologoManager;
import com.clinica.modelos.Odontologo;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.Optional;

public class ListarOdontologosController {

    @FXML private TableView<Odontologo> tablaOdontologos;

    @FXML private TableColumn<Odontologo, String> colID;
    @FXML private TableColumn<Odontologo, String> colNombre;
    @FXML private TableColumn<Odontologo, String> colCelular;
    @FXML private TableColumn<Odontologo, String> colEspecialidad;

    @FXML private TableColumn<Odontologo, Void> colAcciones;

    private OdontologoManager manager;

    public void setManager(OdontologoManager m) {
        this.manager = m;
        cargar();
    }

    @FXML
    public void initialize() {
        colID.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getID()));
        colNombre.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNombre()));
        colCelular.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNumeroCelular()));
        colEspecialidad.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getEspecialidad()));

        // ACCIONES
        colAcciones.setCellFactory(new Callback<TableColumn<Odontologo, Void>, TableCell<Odontologo, Void>>() {
            @Override
            public TableCell<Odontologo, Void> call(TableColumn<Odontologo, Void> param) {
                return new TableCell<Odontologo, Void>() {

                    private final Button btnEdit = new Button("Editar");
                    private final Button btnEliminar = new Button("Eliminar");
                    private final HBox pane = new HBox(8, btnEdit, btnEliminar);

                    {
                        btnEdit.setOnAction(e -> {
                            int idx = getIndex();
                            if (idx >= 0 && idx < tablaOdontologos.getItems().size()) {
                                Odontologo o = tablaOdontologos.getItems().get(idx);
                                abrirEditar(o.getID());
                            }
                        });

                        btnEliminar.setOnAction(e -> {
                            int idx = getIndex();
                            if (idx >= 0 && idx < tablaOdontologos.getItems().size()) {
                                Odontologo o = tablaOdontologos.getItems().get(idx);
                                eliminar(o.getID());
                            }
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : pane);
                    }
                };
            }
        });
    }

    private void cargar() {
        if (manager == null) return;
        tablaOdontologos.setItems(FXCollections.observableArrayList(manager.listar()));
    }

    private void abrirEditar(String id) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/clinica/desktop/views/EditarOdontologoView.fxml"));
            Parent root = loader.load();

            EditarOdontologoController controller = loader.getController();
            controller.setManager(manager);
            controller.cargarOdontologo(id);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Editar Odontólogo");
            stage.showAndWait();

            cargar();
        } catch (Exception e) {
            e.printStackTrace();
            mostrar("Error", "No se pudo abrir el editor: " + e.getMessage());
        }
    }

    private void eliminar(String id) {

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setContentText("¿Eliminar odontólogo con ID: " + id + "?");

        Optional<ButtonType> r = confirm.showAndWait();

        if (r.isPresent() && r.get() == ButtonType.OK) {
            boolean ok = manager.eliminarPorId(id);
            if (ok) mostrar("Éxito", "Odontólogo eliminado");
            else mostrar("Error", "No se pudo eliminar");
            cargar();
        }
    }

    @FXML
    private void volver() {
        Stage s = (Stage) tablaOdontologos.getScene().getWindow();
        s.close();
    }

    private void mostrar(String t, String m) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(t);
        a.setHeaderText(null);
        a.setContentText(m);
        a.showAndWait();
    }
}


