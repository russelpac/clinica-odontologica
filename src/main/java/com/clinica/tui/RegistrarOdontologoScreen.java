package com.clinica.tui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.clinica.managers.OdontologoManager;
import com.clinica.modelos.Odontologo;

import java.util.UUID;


public class RegistrarOdontologoScreen {

    private final OdontologoManager odontologoManager;

    public RegistrarOdontologoScreen(OdontologoManager odontologoManager) {
        this.odontologoManager = odontologoManager;
    }

    public void show(WindowBasedTextGUI textGUI) {
        final Window w = new BasicWindow("Registrar Odontólogo");
        Panel form = new Panel();
        form.setLayoutManager(new GridLayout(2));

        form.addComponent(new Label("Nombre completo*:"));
        final TextBox txtNombre = new TextBox(new TerminalSize(40, 1));
        form.addComponent(txtNombre);

        form.addComponent(new Label("Número de contacto:"));
        final TextBox txtNumero = new TextBox(new TerminalSize(20, 1));
        form.addComponent(txtNumero);

        form.addComponent(new Label("Especialidad:"));
        final TextBox txtEspecialidad = new TextBox(new TerminalSize(30, 1));
        form.addComponent(txtEspecialidad);

        // Acciones
        Panel actions = new Panel(new GridLayout(3));
        Button btnGuardar = new Button("Guardar", () -> {
            String nombre = txtNombre.getText().trim();
            String numero = txtNumero.getText().trim();
            String especialidad = txtEspecialidad.getText().trim();

            if (nombre.isEmpty()) {
                showMsg(textGUI, "El nombre no puede estar vacío. Operación cancelada.");
                return;
            }

            String id = UUID.randomUUID().toString().replace("-", "").substring(0, 8);

            
            Odontologo nuevo = new Odontologo(
                    nombre,
                    numero,
                    especialidad,
                    id
            );

            try {
                odontologoManager.agregar(nuevo);
                // Abrir la lista y seleccionar el nuevo odontólogo
                new OdontologosScreen(odontologoManager).show(textGUI, id);
                w.close();
            } catch (Exception ex) {
                showMsg(textGUI, "Error al registrar odontólogo: " + ex.getMessage());
            }
        });

        Button btnCancelar = new Button("Cancelar", w::close);
        actions.addComponent(btnGuardar);
        actions.addComponent(btnCancelar);

        form.addComponent(new EmptySpace(), GridLayout.createHorizontallyFilledLayoutData(2));
        form.addComponent(actions, GridLayout.createHorizontallyFilledLayoutData(2));

        w.setComponent(form);
        textGUI.addWindowAndWait(w);
    }
//helpers
    private void showMsg(WindowBasedTextGUI textGUI, String msg) {
        final Window w = new BasicWindow("Mensaje");
        Panel p = new Panel(new GridLayout(1));
        p.addComponent(new Label(msg));
        p.addComponent(new Button("Cerrar", w::close));
        w.setComponent(p);
        textGUI.addWindowAndWait(w);
    }
}
