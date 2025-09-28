package com.clinica;
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
public class Main 
{
    public static void main( String[] args )
    {
       Scanner sc = new Scanner(System.in);
       PacienteManager pacienteManager = new PacienteManager();
       OdontologoManager odontologoManager = new OdontologoManager();
       PagosManager pagosManager = new PagosManager();
       int opcion;
       limpiarConsola();
       do { //ESTE SERA EL MENU PRINCIPAL
           System.out.println("================================");
           System.out.println("=========MENU PRINCIPAL=========");
           System.out.println("================================");
           System.out.println("=======CLINICA ODONTOLOGIA======");
           System.out.println("================================");
           System.out.println("1. Gestion de Pacientes->");
           System.out.println("2. Gestion de Odontologos->");
           System.out.println("3. Gestion de Pagos->");
           System.out.println("4. Salir");
           System.out.print("Seleccione una opcion valida: ");
           opcion = sc.nextInt();
           sc.nextLine();
           switch(opcion){
               case 1: {
                   menuPacientes(sc,pacienteManager);
                   break;
               }
               case 2: {
                   menuOdontologos(sc,odontologoManager);
                   break;
               }
               case 3: {
                    menuPagos(sc, pagosManager, pacienteManager, odontologoManager);
                    break;
               }
               case 4:{
                    System.out.println("Saliendo del menú");
                    break;
               }
               default: {
                   System.out.println("Opción no valida");
                   break;
               }
           }
       }while (opcion != 4);
       sc.close();
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
        System.out.println("4. Eliminar paciente");//Aprobado
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
        LocalDateTime fechaConsulta = LocalDateTime.now();//colocar en un formato entendible
        //DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        //fechaConsulta.format(formato)->es un string, al momento de mostraa se mostrara como string
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
        for(Paciente p : manager.listar()){
            System.out.println("Paciente "+p.getID()+" : "+p.getNombres()+" "+p.getApellidos());
        }
        System.out.println("Presione ENTER para continuar...");
        sc.nextLine();
        limpiarConsola();
    }
    private static void actualizarPaciente(Scanner sc, PacienteManager manager){
        limpiarConsola();
        System.out.print("Ingrese el ID del paciente para actualizar: ");
        String id = sc.nextLine();
        Paciente paciente = manager.getById(id);
        if(paciente == null){
            System.out.println("Paciente no encontrado");
            System.out.println("Presione ENTER para continuar");
            sc.nextLine();
            limpiarConsola();
            return;
        }
        System.out.println("IMPORTANTE: Dejar vacío y presionar Enter para no modificar un campo.");
        System.out.print("Actualizando nombres (" + paciente.getNombres()+") : ");
        String nuevosNombres = sc.nextLine();
        if(!nuevosNombres.isEmpty())paciente.setNombres(nuevosNombres);
        
        System.out.print("Actualizando apellidos (" + paciente.getApellidos()+") : ");
        String nuevosApellidos = sc.nextLine();
        if(!nuevosApellidos.isEmpty())paciente.setApellidos(nuevosApellidos);
        
        System.out.print("Actualizando CI (" + paciente.getCI()+") : ");
        String nuevoCI = sc.nextLine();
        if(!nuevoCI.isEmpty())paciente.setCI(nuevoCI);
        
        System.out.print("Actualizando numero de contacto (" + paciente.getContactoEmergencias()+") : ");
        String nuevoContacto = sc.nextLine();
        if(!nuevoContacto.isEmpty())paciente.setNumeroContacto(nuevoContacto);
        
        System.out.print("Actualizando sexo (" + paciente.getSexo()+") : ");
        String nuevoSexo = sc.nextLine();
        if(!nuevoSexo.isEmpty())paciente.setSexo(nuevoSexo);
        System.out.println("Fecha de nacimiento actual: " + paciente.getFechaNacimiento());
        System.out.print("¿Desea actualizar la fecha? (s/n): ");
        String opcion = sc.nextLine();
        if(opcion.equalsIgnoreCase("s")){
            System.out.print("Año de nacimiento actualizado: ");
            int anioNuevo = sc.nextInt();
            System.out.print("Mes de nacimiento actualizado: ");
            int mesNuevo = sc.nextInt();
            System.out.print("Dia de nacimiento actualizado: ");
            int diaNuevo = sc.nextInt();
            sc.nextLine();
            try{
                LocalDate fechaNacimientoNueva = LocalDate.of(anioNuevo,mesNuevo,diaNuevo);
                paciente.setFechaNacimiento(fechaNacimientoNueva);
            }catch(Exception e){
                System.out.println(" Fecha inválida, se conserva la anterior.");
            } 
        }
        
        System.out.print("Actuallizando contacto de emergencias ("+paciente.getContactoEmergencias()+" ): ");
        String contactoNuevo = sc.nextLine();
        if(!contactoNuevo.isEmpty())paciente.setContatoEmergencias(contactoNuevo);
        
        System.out.println("Actuallizando direccion de paciente ("+paciente.getDireccion()+" )");
        String direccionNueva = sc.nextLine();
        if(!direccionNueva.isEmpty())paciente.setDireccion(direccionNueva);
        
        System.out.println("Actuallizando las alergias del paciente ("+paciente.getAlergias()+" )");
        String alergiasNuevas = sc.nextLine();
        if(!alergiasNuevas.isEmpty())paciente.setAlergias(alergiasNuevas);
        
        System.out.println("Actuallizando antecedentes del paciente ("+paciente.getAntecMedicos()+" )");
        String AntecMedicosNuevos = sc.nextLine();
        if(!AntecMedicosNuevos.isEmpty())paciente.setAntecMedicos(AntecMedicosNuevos);
 
        System.out.println("Actuallizando el motivo de consulta ("+paciente.getConsultas()+" )");
        String consultaNueva = sc.nextLine();
        if(!consultaNueva.isEmpty())paciente.setConsultas(consultaNueva);
        
        manager.actualizarPorId(id, paciente);
        System.out.println("Presione ENTER para continuar...");
        sc.nextLine();
        limpiarConsola();
    }
    private static void eliminarPaciente(Scanner sc, PacienteManager manager){
        limpiarConsola();
        System.out.print("Ingrese ID del paciente a eliminar: ");
        String id = sc.nextLine();
        manager.eliminarPorId(id);
        System.out.println("Presione ENTER para continuar...");
        sc.nextLine();
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
        String nombre = sc.nextLine();
        System.out.print("Numero de contacto: ");
        String numero = sc.nextLine();
        System.out.print("Especialidad: ");
        String especialidad = sc.nextLine();
    
        Odontologo nuevo = new Odontologo(
            nombre,
            numero,
            especialidad,
            java.util.UUID.randomUUID().toString().substring(0,8)
        ); 
        manager.agregar(nuevo);
        System.out.println("Presione ENTER para continuar...");
        sc.nextLine();
        limpiarConsola();
    }
    private static void listarOdontologos(Scanner sc,OdontologoManager manager){
        limpiarConsola();
        System.out.println("--- LISTA DE ODONTOLOGOS REGISTRADOS ---");
        for(Odontologo o : manager.listar()){
            System.out.println("Odontologo "+o.getID()+" : "+o.getNombre());
        }
        System.out.println("Ingrese ENTER para continuar...");
        sc.nextLine();
        limpiarConsola();
    }
    private static void actualizarOdontologo(Scanner sc, OdontologoManager manager){
        limpiarConsola();
        System.out.println("Ingrese el ID del odontologo para actualizar");
        String id = sc.nextLine();
        Odontologo odontologo = manager.getById(id);
        if(odontologo == null){
            System.out.println("Odontologo no encontrado");
            System.out.println("Presione ENTER para continuar");
            sc.nextLine();
            limpiarConsola();
            return;
        }
        System.out.println("IMPORTANTE: Dejar vacío y presionar Enter para no modificar un campo.");
        System.out.print("Actualizando nombre( "+odontologo.getNombre()+" ): ");
        String nuevoNombre = sc.nextLine();
        if(!nuevoNombre.isEmpty()) odontologo.setNombre(nuevoNombre);
        
        System.out.print("Actualizando especialidad ( "+odontologo.getEspecialidad()+" ): ");
        String nuevaEspecialidad = sc.nextLine();
        if(!nuevaEspecialidad.isEmpty()) odontologo.setEspecialidad(nuevaEspecialidad);
        
        System.out.print("Actualizando numero de contacto ("+odontologo.getNumero_de_celular()+" ): ");
        String nuevoContacto = sc.nextLine();
        if(!nuevoContacto.isEmpty()) odontologo.setNumero_de_celular(nuevoContacto);
        
        manager.actualizarPorId(id, odontologo);
        System.out.println("Presione ENTER para continuar...");
        sc.nextLine();
        limpiarConsola();
    }
    private static void eliminarOdontologo(Scanner sc, OdontologoManager manager){
        limpiarConsola();
        System.out.println("Ingrese ID del odontologo a eliminar");
        String id = sc.nextLine();
        manager.eliminarPorId(id);
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
                anularPago(sc, pagosManager);
                break;
            }
            case 5:{
                eliminarPago(sc, pagosManager);
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
        for(Pagos p : pagos){
            Paciente pac = pacienteManager.getById(p.getPacienteID());
            String nombrePaciente = (pac != null) ? pac.getNombres() + " " + pac.getApellidos(): "Paciente no encontrado";
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            System.out.printf("ID Pago: %s | Paciente: %s | Fecha: %s | Monto: %s | Metodo: %s | Estado: %s%n",
                    p.getID(),
                    nombrePaciente,
                    p.getFecha().format(formato),
                    p.getMonto().toPlainString(),
                    p.getMetodo(),
                    p.getEstado());
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
            System.out.println("Presione ENTER para continuar....");
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
            if (pac != null) pacienteNombre = pac.getNombres() + " " + pac.getApellidos();
        }
        String idCorto = p.getID().length() > 8 ? p.getID().substring(0, 8) : p.getID();
        System.out.printf("%d: ID:%s | Paciente:%s | Monto:%s | Metodo:%s | Estado:%s%n",
            i + 1,
            idCorto,
            pacienteNombre,
            p.getMonto().toPlainString(),
            p.getMetodo(),
            p.getEstado());
        }
        System.out.print("Ingrese el número del pago o el ID completo del pago que desea actualizar: ");
        String entrada = sc.nextLine().trim();
        String pagoId = null;

        if (entrada.isEmpty()) {
            System.out.println("Entrada vacía. Cancelando actualización.");
            System.out.println("Presione ENTER para continnuar...");
            sc.nextLine();
            limpiarConsola();
            return;
        }

    // Si es numero entonces obtener por índice
        try {
            int opc = Integer.parseInt(entrada);
            if (opc >= 1 && opc <= lista.size()) {
                pagoId = lista.get(opc - 1).getID();
            }else {
                System.out.println("Número fuera de rango.");
                System.out.println("Presione ENTER para continnuar...");
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
            System.out.println("Presione ENTER para continnuar...");
            sc.nextLine();
            limpiarConsola();
            return;
        }

        System.out.println("IMPORTANTE: Dejar vacío y presionar Enter para no modificar un campo.");
        BigDecimal nuevoMonto = null;
        System.out.printf("Monto actual: %s. Nuevo monto: ", pago.getMonto().toPlainString());
        String montoStr = sc.nextLine().trim();
        if (!montoStr.isEmpty()) {
            try {
                montoStr = montoStr.replace(",", ".");
                BigDecimal m = new BigDecimal(montoStr);
                if (m.compareTo(BigDecimal.ZERO) <= 0) {
                    System.out.println("Monto inválido (debe ser > 0). Cancelando actualización.");
                    System.out.println("Presione ENTER para continnuar...");
                    sc.nextLine();
                    limpiarConsola();
                    return;
                }
                nuevoMonto = m;
            }catch (Exception ex) {
                System.out.println("Formato de monto inválido. Cancelando actualización.");
                System.out.println("Presione ENTER para continnuar...");
                sc.nextLine();
                limpiarConsola();
                return;
            }
        }
        Pagos.MetodoPago nuevoMetodo = null;
        System.out.println("Método actual: " + pago.getMetodo());
        System.out.println("Opciones método: 1) EFECTIVO  2) TRANSFERENCIA ");
        System.out.print("Elija nuevo método (o Enter para mantener): ");
        String metodoStr = sc.nextLine().trim();
        if (!metodoStr.isEmpty()) {
            switch (metodoStr) {
                case "1":{ nuevoMetodo = Pagos.MetodoPago.EFECTIVO;
                break;
                }
                case "2":{ nuevoMetodo = Pagos.MetodoPago.TRANSFERENCIA;
                break;
                }
                default :{
                    try {
                        nuevoMetodo = Pagos.MetodoPago.valueOf(metodoStr.toUpperCase());
                    } catch (Exception ex) {
                        System.out.println("Opción de método inválida. Cancelando actualización.");
                        System.out.println("Presione ENTER para continnuar...");
                        sc.nextLine();
                        limpiarConsola();
                        return;
                    }
                }
            }
        }
        Pagos.EstadoPago nuevoEstado = null;
        System.out.println("Estado actual: " + pago.getEstado());
        System.out.println("Opciones estado: 1) PENDIENTE  2) PAGADO  3) ANULADO");
        System.out.print("Elija nuevo estado (o Enter para mantener): ");
        String estadoStr = sc.nextLine().trim();
        if (!estadoStr.isEmpty()) {
            switch (estadoStr) {
                case "1" :{ nuevoEstado = Pagos.EstadoPago.PENDIENTE;
                break;
                }
                case "2" :{ nuevoEstado = Pagos.EstadoPago.PAGADO;
                break;
                }
                case "3" :{ nuevoEstado = Pagos.EstadoPago.ANULADO;
                break;
                }
                default : {
                    try {
                        nuevoEstado = Pagos.EstadoPago.valueOf(estadoStr.toUpperCase());
                    } catch (Exception ex) {
                        System.out.println("Opción de estado inválida. Cancelando actualización.");
                        System.out.println("Presione ENTER para continnuar...");
                        sc.nextLine();
                        limpiarConsola();
                        return;
                    }
                }
            }
        }

        boolean ok = pagosManager.actualizarPorId(pagoId, nuevoMonto, nuevoMetodo, nuevoEstado);
        if (ok) {
            System.out.println(" Pago actualizado correctamente.");
        } else {
            System.out.println(" No se pudo actualizar el pago (ID no encontrado o error).");
        }
        System.out.println("Presione ENTER para continuar...");
        sc.nextLine();
        limpiarConsola();
    }
    private static void anularPago(Scanner sc, PagosManager manager){
        limpiarConsola();
        System.out.println("--- ANULAR PAGO ---");
        System.out.print("ingrese el ID del pago para anular: ");
        String id = sc.nextLine();
        Pagos pago = manager.getById(id);
        if(pago == null){
            System.out.println("No existe un pago con ese ID");
            System.out.println("Presione ENTER para continuar...");
            sc.nextLine();
            limpiarConsola();
            return;
        }
        System.out.println("Se encontro el siguiente pago: ");
        System.out.println("ID: "+pago.getID() + 
                           "| PacienteID: "+pago.getPacienteID()+
                           "| Monto: "+
                           "| Estado: "+ pago.getEstado());
        System.out.println("¿Desea anular este pago? (S/N)");
        String confirm = sc.nextLine().toUpperCase();
        
        if(confirm.equals("S")){
            boolean exito = manager.eliminadPorId(id);
            if(exito){
                System.out.println("El pago ha sido anulado correctamente.");
            }else {
                System.out.println(" El pago ya estaba anulado previamente.");
            }
        }else {
            System.out.println("Operación cancelada por el ususario.");
        }
        System.out.println("Presione ENTER para continuar...");
        sc.nextLine();
        limpiarConsola();
    }
    private static void eliminarPago(Scanner sc, PagosManager manager){
        limpiarConsola();
        System.out.println("--- ELIMINAR PAGO ---");
        System.out.println("ingrese el ID del pago a eliminar: ");
        String id = sc.nextLine();
        Pagos pago = manager.getById(id);
        if(pago == null){
            System.out.println("No existe un pago con ese ID");
            System.out.println("Presione ENTER para continuar...");
            sc.nextLine();
            limpiarConsola();
            return;
        }
        System.out.println("Se encontro el siguiente pago: ");
        System.out.println("ID: "+pago.getID() + 
                           "| PacienteID: "+pago.getPacienteID()+
                           "| Monto: "+
                           "| Estado: "+ pago.getEstado());
        System.out.println("¿Desea eliminar este pago? (S/N)");
        String confirm = sc.nextLine().toUpperCase();
        
        if(confirm.equals("S")){
            boolean exito = manager.eliminarFisicoPorId(id);
            if(exito){
                System.out.println(" El pago ha sido eliminado correctamente.");
            }else {
                System.out.println(" Ocurrio un problema al eliminar el pago.");
            }
        }else {
            System.out.println("Operación cancelada por el ususario.");
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

