package com.clinica.desktop;


import com.clinica.desktop.controllers.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;



public class ClinicaDesktopApp extends Application {

@Override
public void start(Stage stage) throws Exception {

    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/clinica/desktop/views/MainView.fxml"));
    Parent root = loader.load();

    MainController controller = loader.getController();
    controller.inicializar();  

    stage.setScene(new Scene(root, 900, 600));
    stage.setTitle("Clínica Odontológica");
    stage.show();
}

    public static void main(String[] args) {
        launch(args);
    }
}


