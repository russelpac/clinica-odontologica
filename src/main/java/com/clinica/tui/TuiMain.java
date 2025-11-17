package com.clinica.tui;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.clinica.managers.OdontologoManager;
import com.clinica.managers.PagosManager;
import com.clinica.managers.PacienteManager;
import com.clinica.tui.PagosScreen;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import com.clinica.reports.Reportes;
import com.googlecode.lanterna.TerminalSize;


public class TuiMain {

    private final PacienteManager pacienteManager;
    private final OdontologoManager odontologoManager;
    private final PagosManager pagosManager;
    public TuiMain(PacienteManager pacienteManager,
                   OdontologoManager odontologoManager,
                   PagosManager pagosManager) {
        this.pacienteManager = pacienteManager;
        this.odontologoManager = odontologoManager;
        this.pagosManager = pagosManager;
    }

    public void start() throws Exception {
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        try (Screen screen = terminalFactory.createScreen()) {
            screen.startScreen();
            final WindowBasedTextGUI textGUI = new MultiWindowTextGUI(screen);
            final Window window = new BasicWindow("Clínica Odontológica ");
            Panel mainPanel = new Panel();
            mainPanel.setLayoutManager(new GridLayout(1));
            mainPanel.addComponent(new Label("=== MENÚ PRINCIPAL ==="));
            mainPanel.addComponent(new Button("Pacientes", () -> showPacientesMenu(textGUI)));
            mainPanel.addComponent(new Button("Odontólogos", () -> showOdontologosMenu(textGUI)));
            mainPanel.addComponent(new Button("Pagos", () -> showPagosMenu(textGUI)));
            mainPanel.addComponent(new Button("Generar reportes (PDF)", () -> showGenerarInforme(textGUI)));
            mainPanel.addComponent(new Button("Salir de la aplicación ", window::close));
            window.setComponent(mainPanel);
            textGUI.addWindowAndWait(window);
        }
    }

 
    private void showPacientesMenu(WindowBasedTextGUI textGUI) {
        final Window w = new BasicWindow("Menú de Pacientes");
        Panel p = new Panel(new GridLayout(1));
        p.addComponent(new Button("️ Registrar paciente", () -> {
            new RegistrarPacienteScreen(pacienteManager).show(textGUI);
        }));
        p.addComponent(new Button(" Gestión de los pacientes registrados", () -> {
            new PacientesScreen(pacienteManager).show(textGUI);
        }));
        p.addComponent(new Button("Cerrar", w::close));
        w.setComponent(p);
        textGUI.addWindowAndWait(w);
    }


    private void showOdontologosMenu(WindowBasedTextGUI textGUI) {
        final Window w = new BasicWindow("Menú de Odontólogos");
        Panel p = new Panel(new GridLayout(1));
        p.addComponent(new Button("Registrar odontólogo", () -> {
            new RegistrarOdontologoScreen(odontologoManager).show(textGUI);
        }));
        p.addComponent(new Button("Gestión de los odontólogos registrados", () -> {
            new OdontologosScreen(odontologoManager).show(textGUI);
        }));
        p.addComponent(new Button("Cerrar", w::close));
        w.setComponent(p);
        textGUI.addWindowAndWait(w);
    }


    private void showPagosMenu(WindowBasedTextGUI textGUI) {
        final Window w = new BasicWindow("Menú de Pagos");
        Panel p = new Panel(new GridLayout(1));
        p.addComponent(new Button("Registrar pago", () -> {   
            new RegistrarPagoScreen(pagosManager, pacienteManager, odontologoManager).show(textGUI);
        }));   
       p.addComponent(new Button("Gestión de los pagos registrados", () -> {
        PagosScreen.showScreen(textGUI, pagosManager, pacienteManager, odontologoManager);
       }));

        p.addComponent(new Button("Cerrar", w::close));
        w.setComponent(p);
        textGUI.addWindowAndWait(w);
    }


    private void showGenerarInforme(WindowBasedTextGUI textGUI) {
        final Window w = new BasicWindow("Generar informe de pacientes (PDF)");
        Panel form = new Panel(new GridLayout(2));
        form.addComponent(new Label("Ruta de salida (archivo PDF):"));
        TextBox txtPath = new TextBox(new TerminalSize(50, 1), "reports/pacientes.pdf");
        form.addComponent(txtPath);
        Panel actions = new Panel(new GridLayout(3));
        Button btnGenerar = new Button("Generar", () -> {
        String out = txtPath.getText().trim();
        if (out.isEmpty()) {
            out = "reports/pacientes.pdf";
        }
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:clinica.db")) {         
            try (Statement s = conn.createStatement()) {
                s.execute("PRAGMA foreign_keys = ON");
            }          
            Reportes.generarInformePacientes(conn, out);
            showInfo(textGUI, "Informe generado correctamente:\n" + out);
            w.close();
        }catch (Exception ex) {           
            showInfo(textGUI, "Error al generar informe:\n" + ex.getMessage());
            ex.printStackTrace();
        }
        });
        Button btnCancelar = new Button("Cancelar", w::close);
        actions.addComponent(btnGenerar);
        actions.addComponent(btnCancelar);
        form.addComponent(new EmptySpace(TerminalSize.ONE), GridLayout.createHorizontallyFilledLayoutData(2));
        form.addComponent(actions, GridLayout.createHorizontallyFilledLayoutData(2));
        w.setComponent(form);
        textGUI.addWindowAndWait(w);
    }



    private void showInfo(WindowBasedTextGUI textGUI, String msg) {
        final Window w = new BasicWindow("Info");
        Panel p = new Panel();
        p.addComponent(new Label(msg));
        p.addComponent(new Button("Cerrar", w::close));
        w.setComponent(p);
        textGUI.addWindowAndWait(w);
    }

}
