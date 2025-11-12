package com.clinica.tui;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.clinica.managers.OdontologoManager;
import com.clinica.managers.PagosManager;
import com.clinica.managers.PacienteManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import com.clinica.reports.Reportes;
import com.googlecode.lanterna.TerminalSize;

/**
 * TuiMain actualizado para recibir los managers de la aplicación.
 * Las pantallas (listar, crear, editar) deben usar estos managers para la persistencia.
 */
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
            mainPanel.addComponent(new Label("=== MENU PRINCIPAL ==="));

            mainPanel.addComponent(new Button("Pacientes", () -> showPacientesMenu(textGUI)));
            mainPanel.addComponent(new Button("Odontólogos", () -> showOdontologosMenu(textGUI)));
            mainPanel.addComponent(new Button("Pagos", () -> showPagosMenu(textGUI)));
            mainPanel.addComponent(new Button("Reportes (PDF)", () -> showGenerarInforme(textGUI)));
            mainPanel.addComponent(new Button("Salir", window::close));

            window.setComponent(mainPanel);
            textGUI.addWindowAndWait(window);
        }
    }

    // --- Submenús (stubs): reemplaza con implementaciones reales ---
private void showPacientesMenu(WindowBasedTextGUI textGUI) {
    final Window w = new BasicWindow("Pacientes");
    Panel p = new Panel(new GridLayout(1));

    // Registrar paciente (nuevo)
    p.addComponent(new Button("️ Registrar paciente", () -> {
        // abre el formulario de registro
        new RegistrarPacienteScreen(pacienteManager).show(textGUI);
    }));

    // Gestion de los pacientes registrados (lista ya existente)
    p.addComponent(new Button(" Gestión de los pacientes registrados", () -> {
        new PacientesScreen(pacienteManager).show(textGUI);
    }));

    p.addComponent(new Button("Cerrar", w::close));
    w.setComponent(p);
    textGUI.addWindowAndWait(w);
}


    private void showOdontologosMenu(WindowBasedTextGUI textGUI) {
        final Window w = new BasicWindow("Odontólogos");
        Panel p = new Panel(new GridLayout(1));

    
        p.addComponent(new Button("Registrar odontólogo", () -> {
            new RegistrarOdontologoScreen(odontologoManager).show(textGUI);
        }));

    // Gestión / lista
        p.addComponent(new Button("Gestión de los odontólogos registrados", () -> {
            new OdontologosScreen(odontologoManager).show(textGUI);
        }));

        p.addComponent(new Button("Cerrar", w::close));
        w.setComponent(p);
        textGUI.addWindowAndWait(w);
    }


    private void showPagosMenu(WindowBasedTextGUI textGUI) {
        final Window w = new BasicWindow("Pagos");
        Panel p = new Panel(new GridLayout(1));

        // Registrar pago (abrirá el formulario que permitirá elegir paciente, monto, metodo, etc.)
        p.addComponent(new Button("Registrar pago", () -> {
            // abrir formulario de registro (crearemos RegistrarPagoScreen)
            new RegistrarPagoScreen(pagosManager, pacienteManager, odontologoManager).show(textGUI);
        }));

    // Gestión / lista de pagos
        p.addComponent(new Button("Gestión de los pagos registrados", () -> {
        // abrir pantalla que lista pagos y sus acciones
            new PagosScreen(pagosManager, pacienteManager, odontologoManager).show(textGUI);
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

    /* (Opcional: si quieres pedir fecha inicio/fin en TUI, agrega campos aquí)
    form.addComponent(new Label("Fecha inicio (yyyy-MM-dd) o vacío:"));
    TextBox txtStart = new TextBox(new TerminalSize(20,1));
    form.addComponent(txtStart);
    form.addComponent(new Label("Fecha fin (yyyy-MM-dd) o vacío:"));
    TextBox txtEnd = new TextBox(new TerminalSize(20,1));
    form.addComponent(txtEnd);*/

    Panel actions = new Panel(new GridLayout(3));
    Button btnGenerar = new Button("Generar", () -> {
        String out = txtPath.getText().trim();
        if (out.isEmpty()) {
            out = "reports/pacientes.pdf";
        }

        // Intentar generar el informe abriendo una conexión (misma DB que usa la app)
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:clinica.db")) {
            // habilitar FK por si la conexión la necesita (buena práctica)
            try (Statement s = conn.createStatement()) {
                s.execute("PRAGMA foreign_keys = ON");
            }
            // Llamada a tu clase Reportes
            Reportes.generarInformePacientes(conn, out);
            showInfo(textGUI, "Informe generado correctamente:\n" + out);
            w.close();
        } catch (Exception ex) {
            // mostrar error
            showInfo(textGUI, "Error al generar informe:\n" + ex.getMessage());
            ex.printStackTrace();
        }
    });
    Button btnCancelar = new Button("Cancelar", w::close);
    actions.addComponent(btnGenerar);
    actions.addComponent(btnCancelar);

    // layout
    form.addComponent(new EmptySpace(TerminalSize.ONE), GridLayout.createHorizontallyFilledLayoutData(2));
    form.addComponent(actions, GridLayout.createHorizontallyFilledLayoutData(2));

    w.setComponent(form);
    textGUI.addWindowAndWait(w);
}

// helper interno (si no tienes uno en TuiMain)

    private void showInfo(WindowBasedTextGUI textGUI, String msg) {
        final Window w = new BasicWindow("Info");
        Panel p = new Panel();
        p.addComponent(new Label(msg));
        p.addComponent(new Button("Cerrar", w::close));
        w.setComponent(p);
        textGUI.addWindowAndWait(w);
    }

   
}
