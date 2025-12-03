package com.clinica.desktop.controllers;

import com.clinica.managers.PacienteManager;
import com.clinica.modelos.Paciente;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.scene.layout.HBox;

import java.util.Optional;

public class ListarPacientesController {

    @FXML private TableView<Paciente> tablaPacientes;

    @FXML private TableColumn<Paciente, String> colID;
    @FXML private TableColumn<Paciente, String> colNombres;
    @FXML private TableColumn<Paciente, String> colApellidos;
    @FXML private TableColumn<Paciente, String> colCI;
    @FXML private TableColumn<Paciente, String> colSexo;
    @FXML private TableColumn<Paciente, String> colContacto;
    @FXML private TableColumn<Paciente, Boolean> colActivo;

    // columna de acciones (editar / eliminar)
    @FXML private TableColumn<Paciente, Void> colAcciones;

    private PacienteManager manager;

    public void setManager(PacienteManager manager) {
        this.manager = manager;
        cargarPacientes();
    }

    @FXML
    public void initialize() {
        // vinculación simple (si prefieres usar PropertyValueFactory)
        colID.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getID()));
        colNombres.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNombres()));
        colApellidos.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getApellidos()));
        colCI.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getCI()));
        colSexo.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getSexo()));
        colContacto.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNumeroContacto()));
        colActivo.setCellValueFactory(c -> new javafx.beans.property.SimpleBooleanProperty(c.getValue().isActivo()));

        // configurar la columna de acciones (compatible con Java 8)
        colAcciones.setCellFactory(new Callback<TableColumn<Paciente, Void>, TableCell<Paciente, Void>>() {
            @Override
            public TableCell<Paciente, Void> call(final TableColumn<Paciente, Void> param) {
                return new TableCell<Paciente, Void>() {
                    private final Button btnEdit = new Button("Editar");
                    private final Button btnEliminar = new Button("Eliminar");
                    private final HBox pane = new HBox(8, btnEdit, btnEliminar);

                    {
                        btnEdit.setOnAction(e -> {
                            int idx = getIndex();
                            if (idx >= 0 && idx < getTableView().getItems().size()) {
                                Paciente p = getTableView().getItems().get(idx);
                                abrirEditarPaciente(p.getID());
                            }
                        });

                        btnEliminar.setOnAction(e -> {
                            int idx = getIndex();
                            if (idx >= 0 && idx < getTableView().getItems().size()) {
                                Paciente p = getTableView().getItems().get(idx);
                                eliminarPaciente(p.getID());
                            }
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(pane);
                        }
                    }
                };
            }
        });
    }

    private void cargarPacientes() {
        if (manager == null) return;

        tablaPacientes.setItems(
                FXCollections.observableArrayList(manager.listar())
        );
    }

    private void abrirEditarPaciente(String id) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/clinica/desktop/views/EditarPacienteView.fxml"));
            Parent root = loader.load();

            com.clinica.desktop.controllers.EditarPacienteController controller = loader.getController();
            controller.setPacienteManager(manager);
            controller.cargarPaciente(id);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Editar Paciente");
            stage.showAndWait();

            // refrescar la lista al volver
            cargarPacientes();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir el editor: " + e.getMessage());
        }
    }

    private void eliminarPaciente(String id) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Desea anular (marcar como inactivo) al paciente con ID: " + id + " ?");

        Optional<ButtonType> resp = confirm.showAndWait();
        if (resp.isPresent() && resp.get() == ButtonType.OK) {
            try {
                boolean ok = manager.eliminarPorId(id);
                if (ok) {
                    mostrarAlerta("Éxito", "Paciente marcado como inactivo.");
                } else {
                    mostrarAlerta("Error", "No se pudo anular el paciente.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                mostrarAlerta("Error", "Excepción al anular paciente: " + ex.getMessage());
            }
            // refrescar tabla
            cargarPacientes();
        }
    }

    private void mostrarAlerta(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    // método que se usaba desde el FXML si tienes botón "Volver"
    @FXML
    private void volver() {
        // cerrar ventana o acción de volver; si esta escena fue abierta como Stage independiente:
        Stage stage = (Stage) tablaPacientes.getScene().getWindow();
        stage.close();
    }
}





