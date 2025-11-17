package com.clinica;
import com.clinica.reports.Reportes;
import com.clinica.managers.PacienteManager;
import com.clinica.modelos.Paciente;
import com.clinica.managers.OdontologoManager;
import com.clinica.modelos.Odontologo;
import com.clinica.managers.PagosManager;
import com.clinica.modelos.Pagos;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.DateTimeException;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import com.clinica.tui.TuiMain;

public class Main 
{
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String modo = null;
        while (true) {
            System.out.println("Seleccione el modo de ejecución:");
            System.out.println("  1) TUI (interfaz en terminal)");
            System.out.println("  2) Consola (modo texto clásico)");
            System.out.print("Opción (1/2): ");
            String entrada = sc.nextLine().trim();
            if ("1".equals(entrada) || "tui".equalsIgnoreCase(entrada)) { modo = "tui"; break; }
            if ("2".equals(entrada) || "console".equalsIgnoreCase(entrada) || "c".equalsIgnoreCase(entrada)) { modo = "console"; break; }
            System.out.println("Opción no válida. Intente de nuevo.");
        }

        String url = "jdbc:sqlite:clinica.db";

    
        try (Connection conn = DriverManager.getConnection(url)) {
        // activar foreign keys
            try (Statement s = conn.createStatement()) {
            s.execute("PRAGMA foreign_keys = ON");
            }

       
            PacienteManager pacienteManager = new PacienteManager(conn);
            OdontologoManager odontologoManager = new OdontologoManager(conn);
            PagosManager pagosManager = new PagosManager(conn);

            if ("tui".equals(modo)) {
                System.out.println("Iniciando TUI...");
            
                TuiMain tui = new TuiMain(pacienteManager, odontologoManager, pagosManager);
                tui.start();
                System.out.println("TUI cerrada. Saliendo de la aplicación.");
            } else {
            
                int opcion;
                do { // ESTE SERA EL MENU PRINCIPAL
                    limpiarConsola();
                    System.out.println("================================");
                    System.out.println("=========MENU PRINCIPAL=========");
                    System.out.println("================================");
                    System.out.println("=======CLINICA ODONTOLOGIA======");
                    System.out.println("================================");
                    System.out.println("1. Gestion de Pacientes->");
                    System.out.println("2. Gestion de Odontologos->");
                    System.out.println("3. Gestion de Pagos->");
                    System.out.println("4. Generar informe de Pacientes por fecha->");
                    System.out.println("5. Salir");
                    System.out.print("Seleccione una opcion valida: ");
                    opcion = sc.nextInt();
                    sc.nextLine();
                    switch (opcion) {
                        case 1: {
                        
                            menuPacientes(sc, pacienteManager);
                            break;
                        }
                        case 2: {
                            menuOdontologos(sc, odontologoManager);
                            break;
                        }
                        case 3: {
                        
                            menuPagos(sc, pagosManager, pacienteManager, odontologoManager);
                            break;
                        }
                        case 4: {
                            generarInforme(conn, sc);
                            break;
                        }
                        case 5: {
                            System.out.println("Saliendo del menu");
                            break;
                        }
                        default: {
                            System.out.println("Opción no valida");
                            System.out.println("Presione ENTER para continuar");
                            sc.nextLine();
                            limpiarConsola();
                            break;
                        }
                    }
                } while (opcion != 5);
            }

        } catch (Exception e) {
            System.err.println("Error en la aplicación: " + e.getMessage());
            e.printStackTrace();
        } finally {
            sc.close();
        }
    }

    private static void generarInforme(Connection conn,Scanner sc){
        limpiarConsola();
        System.out.println("---GENERAR INFORME DE PAGOS POR PACIENTE---");
        System.out.print("Ingrese fecha inicio (yyyy-MM-dd) o ENTER para todos: ");
        String start = sc.nextLine().trim();
        LocalDate starDate = null;
        if(!start.isEmpty()){
            starDate = LocalDate.parse(start);
        }
        System.out.print("Ingrese fecha fin (yyyy-MM-dd) o ENTER para todos:");
        String end = sc.nextLine().trim();
        LocalDate endDate = null;
        if(!end.isEmpty()){
            endDate = LocalDate.parse(end);
        }
        String rutaSalida = "reports/pagos_por_paciente.pdf";
        try{
            Reportes.generarInformePacientes(conn, rutaSalida);
            System.out.println("Informe generado correctamente en: " + rutaSalida );
        }catch(Exception e){
            System.out.println("Error al generar informe: "+e.getMessage());
            e.printStackTrace(); 
        }
        System.out.println("Presione ENTER para continuar...");
        sc.nextLine();
        limpiarConsola();
    }
    private static void menuPacientes(Scanner sc, PacienteManager pacienteManager){
    int opcion;
    limpiarConsola();
    do{
        System.out.println("=================================");
        System.out.println("=======GESTION DE PACIENTES======");
        System.out.println("=================================");
        System.out.println("1. Registrar paciente");//Aprobado
        System.out.println("2. Listar pacientes");//Aprobado
        System.out.println("3. Actualizar paciente");//Aprobado
        System.out.println("4. Anular paciente");//Aprobado
        System.out.println("5. <-Volver al menu principal");
        System.out.print("Seleccione una opcion valida: ");
        opcion = sc.nextInt();
        sc.nextLine();
        switch(opcion){
            case 1:{
                registrarPaciente(sc, pacienteManager);
                break;
            }
            case 2:{
                listarPacientes(sc,pacienteManager);
                break;
            }
            case 3:{
                actualizarPaciente(sc,pacienteManager);
                break;
            }
            case 4:{
                eliminarPaciente(sc,pacienteManager);
                break;
            }
         
            case 5:{
                limpiarConsola();
                break;
            }
            default:{
                System.out.println("Escoja una opcion valida");
                break;
            }
        
        }
    }while(opcion != 5 );        
    }
    //funciones principales pacientes
    private static void registrarPaciente(Scanner sc, PacienteManager manager){
        limpiarConsola();
        LocalDate fechaNacimiento = null;
        boolean fechaValida= false;
        System.out.println("---INGRESANDO UN PACIENTE---");
        System.out.print("Nombres: ");
        String nombres = sc.nextLine();
        System.out.print("Apellidos: ");
        String apellidos = sc.nextLine();
        System.out.print("CI: ");
        String ci =sc.nextLine();
        System.out.print("Numero de contacto: ");
        String numero = sc.nextLine();
        System.out.print("Sexo: ");
        String sexo = sc.nextLine();
        while(!fechaValida){
            try{
                System.out.print("Año de nacimiento: ");
                int anio = sc.nextInt();
                System.out.print("Mes: ");
                int mes = sc.nextInt();
                System.out.print("Dia: ");
                int dia = sc.nextInt();
                sc.nextLine();
                fechaNacimiento = LocalDate.of(anio,mes,dia);
                fechaValida = true;
            }catch(DateTimeException e){
                System.out.println("Error la fecha ingresada no es válida");
                System.out.println("Presione ENTER para ingresar nuevamente la fecha");
                sc.nextLine();
            }
        }
        System.out.print("Ingrese un contacto de emergencias: ");
        String contactoEmergencias = sc.nextLine();
        System.out.print("Ingrese la direccion del paciente: ");
        String direccion = sc.nextLine();
        System.out.println("===Ingrese las alergias del paciente===");
        String alergias=sc.nextLine();
        System.out.println("===Ingrese antecedentes medicos del paciente(habitos relevantes, mediacamentos actuales, cirugias previas)===");
        String antecMedicos = sc.nextLine();
        System.out.println("===Ingrese el motivo de consulta del paciente===");
        String consultas=sc.nextLine();
        LocalDateTime fechaConsulta = LocalDateTime.now();
        Paciente nuevo = new Paciente(
                java.util.UUID.randomUUID().toString().substring(0,8), //ID generado aleatorio
                nombres,
                ci,
                apellidos,
                numero,
                fechaNacimiento,
                alergias,
                consultas,
                sexo,
                contactoEmergencias,
                direccion,
                antecMedicos,
                fechaConsulta
        );
        manager.agregar(nuevo);
        System.out.println("Presione ENTER para continuar...");
        sc.nextLine();
        limpiarConsola();
    }
    private static void listarPacientes(Scanner sc,PacienteManager manager){//al listar el paciente nos devuelve su ID
        limpiarConsola();
        System.out.println("--- LISTA DE PACIENTES REGISTRADOS ---");
        List<Paciente> lista = manager.listar();
        if (lista.isEmpty()) {
            System.out.println("No hay pacientes registrados.");
        } else {
            int i = 1;
            for (Paciente p : lista) {
                String idCorto = p.getID() != null && p.getID().length() > 8 ? p.getID().substring(0, 8) : p.getID();
                System.out.printf("%d) ID:%s | %s %s | CI:%s | Tel:%s | Activo:%s%n",
                    i++,
                    idCorto,
                    p.getNombres() != null ? p.getNombres() : "",
                    p.getApellidos() != null ? p.getApellidos() : "",
                    p.getCI() != null ? p.getCI() : "",
                    p.getNumeroContacto() != null ? p.getNumeroContacto() : "",
                    p.isActivo() ? "Sí" : "No");
            }
        }
        System.out.println();
        System.out.println("Presione ENTER para continuar...");
        sc.nextLine();
        limpiarConsola();
    }
    private static void actualizarPaciente(Scanner sc, PacienteManager manager) {
        limpiarConsola();
        listarPacientes(sc,manager);
        System.out.print("Ingrese el ID o el número mostrado en la lista del paciente para actualizar: ");
        String entrada = sc.nextLine().trim();
        if (entrada.isEmpty()) {
            System.out.println("Entrada vacía. Cancelando.");
            System.out.println("Presione ENTER para continuar");
            sc.nextLine();
            limpiarConsola();
            return;
        }

        String id = entrada;
        try {
            int idx = Integer.parseInt(entrada);
            List<Paciente> lista = manager.listar();
            if (idx >= 1 && idx <= lista.size()) {
                id = lista.get(idx - 1).getID();
            } else {
                System.out.println("Índice fuera de rango.");
                System.out.println("Presione ENTER para continuar");
                sc.nextLine();
                limpiarConsola();
                return;
            }
        } catch (NumberFormatException ex) {
            
        }

        Paciente paciente = manager.getById(id);
        if (paciente == null) {
            System.out.println("Paciente no encontrado");
            System.out.println("Presione ENTER para continuar");
            sc.nextLine();
            limpiarConsola();
            return;
        }

        System.out.println("IMPORTANTE: Dejar vacío y presionar Enter para no modificar un campo.");

        System.out.print("Actualizando nombres (" + safe(paciente.getNombres()) + "): ");
        String nuevosNombres = sc.nextLine().trim();
        if (!nuevosNombres.isEmpty()) paciente.setNombres(nuevosNombres);

        System.out.print("Actualizando apellidos (" + safe(paciente.getApellidos()) + "): ");
        String nuevosApellidos = sc.nextLine().trim();
        if (!nuevosApellidos.isEmpty()) paciente.setApellidos(nuevosApellidos);

        System.out.print("Actualizando CI (" + safe(paciente.getCI()) + "): ");
        String nuevoCI = sc.nextLine().trim();
        if (!nuevoCI.isEmpty()) paciente.setCI(nuevoCI);

        System.out.print("Actualizando número de contacto (" + safe(paciente.getNumeroContacto()) + "): ");
        String nuevoContacto = sc.nextLine().trim();
        if (!nuevoContacto.isEmpty()) paciente.setNumeroContacto(nuevoContacto);

        System.out.print("Actualizando sexo (" + safe(paciente.getSexo()) + "): ");
        String nuevoSexo = sc.nextLine().trim();
        if (!nuevoSexo.isEmpty()) paciente.setSexo(nuevoSexo);
        System.out.println("Fecha de nacimiento actual: " + (paciente.getFechaNacimiento() != null ? paciente.getFechaNacimiento() : "no definida"));
        System.out.print("¿Desea actualizar la fecha? (s/N): ");
        String opcion = sc.nextLine().trim();
        if (opcion.equalsIgnoreCase("s")) {
            System.out.print("Ingrese la nueva fecha en formato yyyy-MM-dd: ");
            String fechaStr = sc.nextLine().trim();
            if (!fechaStr.isEmpty()) {
                try {
                    LocalDate fechaNacimientoNueva = LocalDate.parse(fechaStr);
                    paciente.setFechaNacimiento(fechaNacimientoNueva);
                } catch (Exception e) {
                    System.out.println("Fecha inválida. Se conserva la anterior.");
                }
            } else {
                System.out.println("Entrada vacía. Se conserva la fecha anterior.");
            }
        }

        System.out.print("Actualizando contacto de emergencias (" + safe(paciente.getContactoEmergencias()) + "): ");
        String contactoNuevo = sc.nextLine().trim();
        if (!contactoNuevo.isEmpty()) {
            paciente.setContatoEmergencias(contactoNuevo);
        }

        System.out.print("Actualizando dirección (" + safe(paciente.getDireccion()) + "): ");
        String direccionNueva = sc.nextLine().trim();
        if (!direccionNueva.isEmpty()) paciente.setDireccion(direccionNueva);

        System.out.print("Actualizando alergias (" + safe(paciente.getAlergias()) + "): ");
        String alergiasNuevas = sc.nextLine().trim();
        if (!alergiasNuevas.isEmpty()) paciente.setAlergias(alergiasNuevas);

        System.out.print("Actualizando antecedentes médicos (" + safe(paciente.getAntecMedicos()) + "): ");
        String AntecMedicosNuevos = sc.nextLine().trim();
        if (!AntecMedicosNuevos.isEmpty()) paciente.setAntecMedicos(AntecMedicosNuevos);

        System.out.print("Actualizando motivo de consulta (" + safe(paciente.getConsultas()) + "): ");
        String consultaNueva = sc.nextLine().trim();
        if (!consultaNueva.isEmpty()) paciente.setConsultas(consultaNueva);

    
        System.out.print("Activo (actual: " + (paciente.isActivo() ? "Sí" : "No") + "). Escriba S para activar, N para desactivar, Enter para mantener: ");
        String activoStr = sc.nextLine().trim();
        if (activoStr.equalsIgnoreCase("S")) paciente.setActivo(true);
        else if (activoStr.equalsIgnoreCase("N")) paciente.setActivo(false);

        // Ejecutar actualización en BD
        boolean ok = manager.actualizarPorId(id, paciente);
        if (ok) System.out.println("Paciente actualizado correctamente.");
        else System.out.println("No se pudo actualizar el paciente (ID no encontrado o error).");

        System.out.println("Presione ENTER para continuar...");
        sc.nextLine();
        limpiarConsola();
    }
    private static String safe(Object o) {
        return o == null ? "" : o.toString();
    }
    private static void eliminarPaciente(Scanner sc, PacienteManager manager){
        limpiarConsola();
        listarPacientes(sc,manager);
        System.out.print("Ingrese ID o número del paciente para anular: ");
        String entrada = sc.nextLine().trim();
        if (entrada.isEmpty()) {
            System.out.println("Entrada vacía. Cancelando.");
            System.out.println("Presione ENTER para continuar...");
            sc.nextLine();
            limpiarConsola();
            return;
        }

        String id = entrada;
        try {
            int idx = Integer.parseInt(entrada);
            List<Paciente> lista = manager.listar();
            if (idx >= 1 && idx <= lista.size()) {
                id = lista.get(idx - 1).getID();
            } else {
                System.out.println("Índice fuera de rango.");
                System.out.println("Presione ENTER para continuar...");
                sc.nextLine();
                limpiarConsola();
                return;
            }
        } catch (NumberFormatException ex) {
        }

        Paciente p = manager.getById(id);
        if (p == null) {
            System.out.println("No se encontró paciente con ese ID.");
            System.out.println("Presione ENTER para continuar...");
            sc.nextLine();
            limpiarConsola();
            return;
        }
        System.out.printf("Paciente: %s %s | CI: %s | ID: %s%n", safe(p.getNombres()), safe(p.getApellidos()), safe(p.getCI()), id);
        System.out.print("¿Confirma anular este paciente? (S/N): ");
        String confirm = sc.nextLine().trim().toUpperCase();
        if (!confirm.equals("S")) {
            System.out.println("Operación cancelada.");
            System.out.println("Presione ENTER para continuar...");
            sc.nextLine();
            limpiarConsola();
            return;
        }
        boolean ok = manager.eliminarPorId(id);
        if (ok) System.out.println("Paciente anulado correctamente.");
        else System.out.println("No se pudo anular el paciente (tal vez ya estaba inactivo o ocurrió un error).");
        System.out.println("Presione ENTER para continuar...");
        sc.nextLine();
        limpiarConsola();
    }
    private static void menuOdontologos(Scanner sc, OdontologoManager odontologoManager){
    int opcion ;
    limpiarConsola();
    do{
        System.out.println("==============================");
        System.out.println("======MENU DE ODONTOLOGOS=====");
        System.out.println("==============================");
        System.out.println("1. Ingresar odontologo");//Aprobado
        System.out.println("2. Listar odontologos ");//Aprobado
        System.out.println("3. Actualizar odontologo");//Aprobado
        System.out.println("4. Eliminar odontologo");//Aprobado
        System.out.println("5. <-Volver al menu principal ");
        System.out.print("Ingrese una opcion valida: ");
        opcion=sc.nextInt();
        sc.nextLine();
        switch(opcion){
            case 1:{
                registrarOdontologo(sc, odontologoManager);
                break;
            }
            case 2:{
                listarOdontologos(sc,odontologoManager);
                break;
            }
            case 3:{
                actualizarOdontologo(sc, odontologoManager);
                break;
            }
            case 4:{
                eliminarOdontologo(sc,odontologoManager);
                break;
            }
            case 5:{
                limpiarConsola();
                break;
            }
            default:{
                System.out.println("Escoja una opcion valida");
                break;
            }
        }
    }while(opcion != 5);
    }
    private static void registrarOdontologo(Scanner sc, OdontologoManager manager){
        limpiarConsola();
        System.out.println("---INGRESANDO UN ODONTOLOGO---");
        System.out.print("Nombre completo: ");
        String nombre = sc.nextLine().trim();
        if(nombre.isEmpty()){
            System.out.println("El nombre no puede estar vacio. Operacion cancelada.");
            return;
        }
        System.out.print("Numero de contacto: ");
        String numero = sc.nextLine().trim();
        System.out.print("Especialidad: ");
        String especialidad = sc.nextLine().trim();
        String id = java.util.UUID.randomUUID().toString().substring(0,8);
    
        Odontologo nuevo = new Odontologo(
            nombre,
            numero,
            especialidad,
            id
        ); 
        manager.agregar(nuevo);
        System.out.println("Presione ENTER para continuar...");
        sc.nextLine();
        limpiarConsola();
    }
    private static void listarOdontologos(Scanner sc,OdontologoManager manager){
        limpiarConsola();
        System.out.println("--- LISTA DE ODONTÓLOGOS REGISTRADOS ---");

        List<Odontologo> lista = manager.listar();
        if (lista.isEmpty()) {
            System.out.println("No hay odontólogos registrados.");
        } else {
            int i = 1;
            for (Odontologo o : lista) {
                String idCorto = o.getID() != null && o.getID().length() > 8 ? o.getID().substring(0, 8) : o.getID();
                System.out.printf("%d) ID:%s | Nombre:%s | Tel:%s | Especialidad:%s%n",
                    i++,
                    idCorto,
                    o.getNombre() != null ? o.getNombre() : "",
                    o.getNumeroCelular() != null ? o.getNumeroCelular() : "",
                    o.getEspecialidad() != null ? o.getEspecialidad() : ""
                );
            }
        }
        System.out.println();
        System.out.println("Presione ENTER para continuar...");
        sc.nextLine();
        limpiarConsola();
    }
    private static void actualizarOdontologo(Scanner sc, OdontologoManager manager){
        limpiarConsola();
        listarOdontologos(sc,manager);
        System.out.print("Ingrese el ID o el número mostrado en la lista del odontólogo para actualizar: ");
        String entrada = sc.nextLine().trim();

        if (entrada.isEmpty()) {
            System.out.println("Entrada vacía. Cancelando.");
            System.out.println("Presione ENTER para continuar");
            sc.nextLine();
            limpiarConsola();
            return;
        }

        String id = entrada;
        try {
            int idx = Integer.parseInt(entrada);
            List<Odontologo> lista = manager.listar();
            if (idx >= 1 && idx <= lista.size()) {
                id = lista.get(idx - 1).getID();
            } else {
                System.out.println("Índice fuera de rango.");
                System.out.println("Presione ENTER para continuar");
                sc.nextLine();
                limpiarConsola();
                return;
            }
        } catch (NumberFormatException ex) {
       
        }

        Odontologo odontologo = manager.getById(id);
        if (odontologo == null) {
            System.out.println("Odontólogo no encontrado");
            System.out.println("Presione ENTER para continuar");
            sc.nextLine();
            limpiarConsola();
            return;
        }

        System.out.println("IMPORTANTE: Dejar vacío y presionar Enter para no modificar un campo.");

        System.out.print("Actualizando nombre (" + safe(odontologo.getNombre()) + "): ");
        String nuevoNombre = sc.nextLine().trim();
        if (!nuevoNombre.isEmpty()) odontologo.setNombre(nuevoNombre);

        System.out.print("Actualizando especialidad (" + safe(odontologo.getEspecialidad()) + "): ");
        String nuevaEspecialidad = sc.nextLine().trim();
        if (!nuevaEspecialidad.isEmpty()) odontologo.setEspecialidad(nuevaEspecialidad);

        System.out.print("Actualizando número de contacto (" + safe(odontologo.getNumeroCelular()) + "): ");
        String nuevoContacto = sc.nextLine().trim();
        if (!nuevoContacto.isEmpty()) odontologo.setNumeroCelular(nuevoContacto);

        boolean ok = manager.actualizarPorId(id, odontologo);
        if (ok) {
            System.out.println("Odontólogo actualizado correctamente.");
        } else {
            System.out.println("No se pudo actualizar el odontólogo (ID no encontrado o error).");
        }

        System.out.println("Presione ENTER para continuar...");
        sc.nextLine();
        limpiarConsola();
    }
    private static void eliminarOdontologo(Scanner sc, OdontologoManager manager){
        limpiarConsola();
        listarOdontologos(sc,manager);
        System.out.println("IMPORTANTE: Si el odontologo a eliminar esta relacionado con un pago se producira un ERROR ");
        System.out.print("Ingrese ID o número del odontólogo : ");
        String entrada = sc.nextLine().trim();

        if (entrada.isEmpty()) {
            System.out.println("Entrada vacía. Cancelando.");
            System.out.println("Presione ENTER para continuar...");
            sc.nextLine();
            limpiarConsola();
            return;
        }

        String id = entrada;
        try {
            int idx = Integer.parseInt(entrada);
            List<Odontologo> lista = manager.listar();
            if (idx >= 1 && idx <= lista.size()) {
                id = lista.get(idx - 1).getID();
            } else {
                System.out.println("Índice fuera de rango.");
                System.out.println("Presione ENTER para continuar...");
                sc.nextLine();
                limpiarConsola();
                return;
            }
        } catch (NumberFormatException ex) {
        
        }

        Odontologo o = manager.getById(id);
        if (o == null) {
            System.out.println("No se encontró odontólogo con ese ID.");
            System.out.println("Presione ENTER para continuar...");
            sc.nextLine();
            limpiarConsola();
            return;
        }

        System.out.printf("Odontólogo: %s | Tel: %s | Especialidad: %s | ID: %s%n",
                safe(o.getNombre()), safe(o.getNumeroCelular()), safe(o.getEspecialidad()), id);

        System.out.print("¿Confirma eliminar este odontólogo? (S/N): ");
        String confirm = sc.nextLine().trim().toUpperCase();
        if (!confirm.equals("S")) {
            System.out.println("Operación cancelada.");
            System.out.println("Presione ENTER para continuar...");
            sc.nextLine();
            limpiarConsola();
            return;
        }

        boolean ok = manager.eliminarPorId(id);
        if (ok) {
            System.out.println("Odontólogo eliminado correctamente.");
        } else {
            System.out.println("ERROR:No se pudo eliminar el odontologo, pues esta relacionado con un pago, elimine primero el pago");
        }

        System.out.println("Presione ENTER para continuar...");
        sc.nextLine();
        limpiarConsola();
    }
    private static void menuPagos(Scanner sc, PagosManager pagosManager, PacienteManager pacienteManager, OdontologoManager odontologoManager){
    limpiarConsola();
    int opcion;
    do{
        System.out.println("======================================");
        System.out.println("===========GESTION DE PAGOS===========");
        System.out.println("======================================");
        System.out.println("1. Registrar pago");//Aprobado
        System.out.println("2. Listar pagos");//Aprobado
        System.out.println("3. Actualizar pago");//Aprobado
        System.out.println("4. Anular pago");//Aprobado
        System.out.println("5. Eliminar pago");//Aprobado
        System.out.println("6. <-Volver al menu principal");
        System.out.print("Seleccione una opcion valida: ");
        opcion = sc.nextInt();
        sc.nextLine();
        switch(opcion){
            case 1:{
                registrarPago(sc, pagosManager, pacienteManager, odontologoManager);
                break;
            }
            case 2:{
                listarPagos(sc,pagosManager, pacienteManager);
                break;
            }
            case 3:{
                actualizarPago(sc, pagosManager, pacienteManager);
                break;
            }
            case 4:{
                anularPago(sc, pagosManager, pacienteManager);
                break;
            }
            case 5:{
                eliminarPago(sc, pagosManager,pacienteManager);
                break;
            }
            case 6:{
                limpiarConsola();
                break;
            }
            default:{
                System.out.println("Seleccione una opcion valida");
                break;
            }
        }
    }while(opcion != 6);
    }
    private static void registrarPago(Scanner sc, PagosManager pagosManager, PacienteManager pacienteManager, OdontologoManager odontologoManager){
        limpiarConsola();
        System.out.println("---REGISTRANDO UN PAGO---");
        List<Paciente> pacientes = pacienteManager.listar();
        List<Odontologo> odontologos = odontologoManager.listar();
        if(pacientes.isEmpty()){
            System.out.println("No hay pacientes registrado. Registre un paciente primero ");
            System.out.println("Presione ENTER para continuar...");
            sc.nextLine();
            limpiarConsola();
            return;
        }
        if(odontologos.isEmpty()){
            System.out.println("No hay odontologos registrados. Registre un odontologo primero ");
            System.out.println("Presione ENTER para continuar...");
            sc.nextLine();
            limpiarConsola();
            return;
        }
        System.out.println("Seleccione el paciente para el pago ");
        for(int i = 0; i < pacientes.size(); i++){
            Paciente p = pacientes.get(i);
            System.out.println((i+1)+" . "+p.getNombres()+" "+p.getApellidos()+" (CI: " + p.getCI() + ")");
        }
        
        int idx;
        while(true){
            System.out.print("Ingrese el numero del paciente: ");
            String line = sc.nextLine().trim();
            try{
                idx = Integer.parseInt(line);
                if(idx < 1 || idx > pacientes.size()){
                    System.out.println("Opcion fuera del rango. Intente nuevamente. ");
                    continue;
                }
                break;
            }catch(NumberFormatException ex){
                System.out.println("Entrada invalida. Ingrese un numero");
            }
        }
        Paciente pacienteSeleccionado = pacientes.get(idx - 1);
        System.out.println("Seleccione el odontologo que atendió al paciente ");
        for(int i = 0; i < odontologos.size(); i++){
            Odontologo o = odontologos.get(i);
            System.out.println((i+1)+" . "+o.getNombre()+" (ID: "+o.getID()+" )" );
        }
        while(true){
            System.out.print("Ingrese el número del odontólogo: ");
            String line = sc.nextLine().trim();
            try{
                idx = Integer.parseInt(line);
                if(idx < 1 || idx > odontologos.size()){
                    System.out.println("Opcion fuera del rango. Intente nuevamente. ");
                    continue;
                }
                break;
            }catch(NumberFormatException ex){
                System.out.println("Entrada invalida. Ingrese un número");
            }
        }
        Odontologo odontologoSeleccionado = odontologos.get( idx - 1);
        BigDecimal monto;
        while(true){
            System.out.print("Ingrese el monto (ej. 150.50): ");
            String montoStr = sc.nextLine().trim();
            try{
                montoStr = montoStr.replace(",",".");
                monto = new BigDecimal(montoStr);
                if(monto.compareTo(BigDecimal.ZERO) <= 0){
                    System.out.println("El monto debe ser mayor a 0");
                    continue;
                }
                break;
            }catch(NumberFormatException | ArithmeticException ex){
                System.out.println("Monto invalido. Intente de nuevo");
            }
        }
        Pagos.MetodoPago metodo;
        while(true){
            System.out.println("Seleccione metodo de pago");
            System.out.println("1) EFECTIVO");
            System.out.println("2) TRANSFERENCIA");
            System.out.print("Opcion: ");
            String mline = sc.nextLine().trim();
            if (mline.equals("1")) { metodo = Pagos.MetodoPago.EFECTIVO; break; }
            if (mline.equals("2")) { metodo = Pagos.MetodoPago.TRANSFERENCIA; break; }
            System.out.println("Opcion no valida. Intente de nuevo");
        }
        Pagos.EstadoPago estado = Pagos.EstadoPago.PENDIENTE;
        String pagoID = java.util.UUID.randomUUID().toString().substring(0,8);
        LocalDateTime ahora = LocalDateTime.now();
        
        Pagos pago = new Pagos(
                pagoID,
                pacienteSeleccionado.getID(),
                odontologoSeleccionado.getID(),
                ahora,
                monto,
                metodo,
                estado
        );
        pagosManager.agregar(pago);
        System.out.printf("Pago registrado: %s %s - Monto: %s - Metodo: %s%n",
                pacienteSeleccionado.getNombres(),
                pacienteSeleccionado.getApellidos(),
                monto.toPlainString(),
                metodo);
        System.out.println("Presione ENTER para continuar");
        sc.nextLine();
        limpiarConsola();
    }
    private static void listarPagos(Scanner sc, PagosManager pagosManager, PacienteManager pacienteManager){
        limpiarConsola();
        List<Pagos> pagos = pagosManager.listar();
        if(pagos.isEmpty()){
            System.out.println("No hay pagos registrados");
            System.out.println("Presione ENTER para continuar...");
            sc.nextLine();
            limpiarConsola();
            return;
        }

        System.out.println("---LISTA DE PAGOS---");
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        for(Pagos p : pagos){
            Paciente pac = pacienteManager.getById(p.getPacienteID());
            String nombrePaciente = (pac != null) ? safe(pac.getNombres()) + " " + safe(pac.getApellidos())
                                             : ("Paciente no encontrado (ID: " + safe(p.getPacienteID()) + ")");
            String fechaStr;
            if (p.getFecha() != null) {
                try {
                    fechaStr = p.getFecha().format(formato);
                } catch (Exception ex) {
                    fechaStr = p.getFecha().toString();
                }
            } else {
                fechaStr = "no definida";
            }

            String montoStr = p.getMonto() != null ? p.getMonto().toPlainString() : "no definido";
            String metodoStr = p.getMetodo() != null ? p.getMetodo().name() : "no definido";
            String estadoStr = p.getEstado() != null ? p.getEstado().name() : "no definido";

            System.out.printf("ID Pago: %s | Paciente: %s | Fecha: %s | Monto: %s | Metodo: %s | Estado: %s%n",
                    safe(p.getID()),
                    nombrePaciente,
                    fechaStr,
                    montoStr,
                    metodoStr,
                    estadoStr);
        }

        System.out.println("Presione ENTER para continuar...");
        sc.nextLine();
        limpiarConsola();
    }
    private static void actualizarPago(Scanner sc, PagosManager pagosManager, PacienteManager pacienteManager) {
        limpiarConsola();
        System.out.println("--- ACTUALIZAR PAGO ---");
        List<Pagos> lista = pagosManager.listar();
        if (lista.isEmpty()) {
            System.out.println("No hay pagos registrados.");
            System.out.println("Presione ENTER para continuar...");
            sc.nextLine();
            limpiarConsola();
            return;
        }

        System.out.println("Pagos registrados:");
        for (int i = 0; i < lista.size(); i++) {
            Pagos p = lista.get(i);
            String pacienteNombre = "Paciente no encontrado";
            if (pacienteManager != null) {
                Paciente pac = pacienteManager.getById(p.getPacienteID());
                if (pac != null) pacienteNombre = safe(pac.getNombres()) + " " + safe(pac.getApellidos());
            }
            String id = safe(p.getID());
            String idCorto = id.length() > 8 ? id.substring(0, 8) : id;
            String montoStrList = p.getMonto() != null ? p.getMonto().toPlainString() : "no definido";
            String metodoStrList = p.getMetodo() != null ? p.getMetodo().name() : "no definido";
            String estadoStrList = p.getEstado() != null ? p.getEstado().name() : "no definido";

            System.out.printf("%d: ID:%s | Paciente:%s | Monto:%s | Metodo:%s | Estado:%s%n",
                i + 1,
                idCorto,
                pacienteNombre,
                montoStrList,
                metodoStrList,
                estadoStrList);
        }

        System.out.print("Ingrese el número del pago o el ID completo del pago que desea actualizar: ");
        String entrada = sc.nextLine().trim();
        String pagoId = null;

        if (entrada.isEmpty()) {
            System.out.println("Entrada vacía. Cancelando actualización.");
            System.out.println("Presione ENTER para continuar...");
            sc.nextLine();
            limpiarConsola();
            return;
        }

    // Si es número entonces obtener por índice
        try {
            int opc = Integer.parseInt(entrada);
            if (opc >= 1 && opc <= lista.size()) {
                pagoId = lista.get(opc - 1).getID();
            } else {
                System.out.println("Número fuera de rango.");
                System.out.println("Presione ENTER para continuar...");
                sc.nextLine();
                limpiarConsola();
                return;
            }
        } catch (NumberFormatException e) {
            pagoId = entrada;
        }

        Pagos pago = pagosManager.getById(pagoId);
        if (pago == null) {
            System.out.println("No se encontró un pago con ese ID.");
            System.out.println("Presione ENTER para continuar...");
            sc.nextLine();
            limpiarConsola();
            return;
        }

        System.out.println("IMPORTANTE: Dejar vacío y presionar Enter para no modificar un campo.");

    // Monto
        BigDecimal nuevoMonto = null;
        System.out.printf("Monto actual: %s. Nuevo monto: ", pago.getMonto() != null ? pago.getMonto().toPlainString() : "no definido");
        String montoStr = sc.nextLine().trim();
        if (!montoStr.isEmpty()) {
            try {
                montoStr = montoStr.replace(",", ".");
                BigDecimal m = new BigDecimal(montoStr);
                if (m.compareTo(BigDecimal.ZERO) <= 0) {
                    System.out.println("Monto inválido (debe ser > 0). Cancelando actualización.");
                    System.out.println("Presione ENTER para continuar...");
                    sc.nextLine();
                    limpiarConsola();
                    return;
                }
                nuevoMonto = m;
            } catch (Exception ex) {
                System.out.println("Formato de monto inválido. Cancelando actualización.");
                System.out.println("Presione ENTER para continuar...");
                sc.nextLine();
                limpiarConsola();
                return;
            }
        }

    // Método
        Pagos.MetodoPago nuevoMetodo = null;
        System.out.println("Método actual: " + (pago.getMetodo() != null ? pago.getMetodo() : "no definido"));
        System.out.println("Opciones método: 1) EFECTIVO  2) TRANSFERENCIA ");
        System.out.print("Elija nuevo método (o Enter para mantener): ");
        String metodoStr = sc.nextLine().trim();
        if (!metodoStr.isEmpty()) {
            switch (metodoStr) {
                case "1": nuevoMetodo = Pagos.MetodoPago.EFECTIVO; break;
                case "2": nuevoMetodo = Pagos.MetodoPago.TRANSFERENCIA; break;
                default:
                    try {
                        nuevoMetodo = Pagos.MetodoPago.valueOf(metodoStr.toUpperCase());
                    } catch (Exception ex) {
                        System.out.println("Opción de método inválida. Cancelando actualización.");
                        System.out.println("Presione ENTER para continuar...");
                        sc.nextLine();
                        limpiarConsola();
                        return;
                    }
            }
        }

    // Estado
        Pagos.EstadoPago nuevoEstado = null;
        System.out.println("Estado actual: " + (pago.getEstado() != null ? pago.getEstado() : "no definido"));
        System.out.println("Opciones estado: 1) PENDIENTE  2) PAGADO  3) ANULADO");
        System.out.print("Elija nuevo estado (o Enter para mantener): ");
        String estadoStr = sc.nextLine().trim();
        if (!estadoStr.isEmpty()) {
            switch (estadoStr) {
                case "1": nuevoEstado = Pagos.EstadoPago.PENDIENTE; break;
                case "2": nuevoEstado = Pagos.EstadoPago.PAGADO; break;
                case "3": nuevoEstado = Pagos.EstadoPago.ANULADO; break;
                default:
                    try {
                        nuevoEstado = Pagos.EstadoPago.valueOf(estadoStr.toUpperCase());
                    } catch (Exception ex) {
                        System.out.println("Opción de estado inválida. Cancelando actualización.");
                        System.out.println("Presione ENTER para continuar...");
                        sc.nextLine();
                        limpiarConsola();
                        return;
                    }
            }
        }

        boolean ok = pagosManager.actualizarPorId(pagoId, nuevoMonto, nuevoMetodo, nuevoEstado);
        if (ok) {
            System.out.println("Pago actualizado correctamente.");
        } else {
            System.out.println("No se pudo actualizar el pago (ID no encontrado o error).");
        }

        System.out.println("Presione ENTER para continuar...");
        sc.nextLine();
        limpiarConsola();
    }
    private static void anularPago(Scanner sc, PagosManager manager, PacienteManager managerPaciente){
        limpiarConsola();
        listarPagos(sc,manager,managerPaciente);
        System.out.println("--- ANULAR PAGO ---");
        System.out.print("Ingrese el ID del pago para anular: ");
        String id = sc.nextLine().trim();
        if (id.isEmpty()) {
            System.out.println("Entrada vacía. Cancelando.");
            System.out.println("Presione ENTER para continuar...");
            sc.nextLine();
            limpiarConsola();
            return;
        }

        Pagos pago = manager.getById(id);
        if(pago == null){
            System.out.println("No existe un pago con ese ID");
            System.out.println("Presione ENTER para continuar...");
            sc.nextLine();
            limpiarConsola();
            return;
        }

        System.out.println("Se encontró el siguiente pago:");
        String montoStr = pago.getMonto() != null ? pago.getMonto().toPlainString() : "no definido";
        String pacienteId = pago.getPacienteID() != null ? pago.getPacienteID() : "no definido";
        String odontologoId = pago.getOdontologoID() != null ? pago.getOdontologoID() : "no definido";
        System.out.println("ID: " + safe(pago.getID()) +
                       " | PacienteID: " + safe(pacienteId) +
                       " | OdontologoID: " + safe(odontologoId) +
                       " | Monto: " + montoStr +
                       " | Estado: " + safe(pago.getEstado()));

        System.out.print("¿Desea anular este pago? (S/N): ");
        String confirm = sc.nextLine().trim().toUpperCase();

        if(confirm.equals("S")){
        
            boolean exito = manager.eliminarPorId(id);
            if(exito){
                System.out.println("El pago ha sido anulado correctamente.");
            } else {
                System.out.println("El pago ya estaba anulado previamente o ocurrió un error.");
            }
        } else {
            System.out.println("Operación cancelada por el usuario.");
        }

        System.out.println("Presione ENTER para continuar...");
        sc.nextLine();
        limpiarConsola();
    }
    private static void eliminarPago(Scanner sc, PagosManager manager, PacienteManager managerPaciente){
        limpiarConsola();
        listarPagos(sc,manager,managerPaciente);
        System.out.println("--- ELIMINAR PAGO ---");
        System.out.print("Ingrese el ID del pago: ");
        String entrada = sc.nextLine().trim();

        if (entrada.isEmpty()) {
            System.out.println("Entrada vacía. Cancelando.");
            System.out.println("Presione ENTER para continuar...");
            sc.nextLine();
            limpiarConsola();
            return;
        }

        String id = entrada;
        try {
            int idx = Integer.parseInt(entrada);
            List<Pagos> lista = manager.listar();
            if (idx >= 1 && idx <= lista.size()) {
                id = lista.get(idx - 1).getID();
            } else {
                System.out.println("Número fuera de rango.");
                System.out.println("Presione ENTER para continuar...");
                sc.nextLine();
                limpiarConsola();
                return;
            }
        } catch (NumberFormatException ex) {
        }

        Pagos pago = manager.getById(id);
        if(pago == null){
            System.out.println("No existe un pago con ese ID");
            System.out.println("Presione ENTER para continuar...");
            sc.nextLine();
            limpiarConsola();
            return;
        }

        System.out.println("Se encontró el siguiente pago:");
        String montoStr = pago.getMonto() != null ? pago.getMonto().toPlainString() : "no definido";
        String pacienteId = pago.getPacienteID() != null ? pago.getPacienteID() : "no definido";
        String odontologoId = pago.getOdontologoID() != null ? pago.getOdontologoID() : "no definido";
        System.out.println("ID: " + safe(pago.getID()) +
                       " | PacienteID: " + safe(pacienteId) +
                       " | OdontologoID: " + safe(odontologoId) +
                       " | Monto: " + montoStr +
                       " | Estado: " + safe(pago.getEstado()));

        System.out.print("¿Desea eliminar este pago? (S/N): ");
        String confirm = sc.nextLine().trim().toUpperCase();

        if(confirm.equals("S")){
            boolean exito = manager.eliminarFisicoPorId(id);
            if(exito){
                System.out.println("El pago ha sido eliminado correctamente.");
            } else {
                System.out.println("Ocurrió un problema al eliminar el pago.");
            }
        } else {
            System.out.println("Operación cancelada por el usuario.");
        }

        System.out.println("Presione ENTER para continuar...");
        sc.nextLine();
        limpiarConsola();
    }
    //metodo para limpiar las consola 
    private static void limpiarConsola(){
        try{
            String sistema = System.getProperty("os.name");
            if(sistema.contains("Windows")){
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            }else{
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            }
        
        }catch (Exception e){
            System.out.println("No se limpio la consola");
        }
    }
}

