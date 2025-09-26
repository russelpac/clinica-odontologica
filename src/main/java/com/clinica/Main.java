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
       do { //ESTE SERA EL MENU PRINCIPAL
           System.out.println("================================");
           System.out.println("\033[1;34m=========MENU PRINCIPALj=========\033[0m");
           System.out.println("================================");
           System.out.println("=======CLINICA ODONTOLOGIA======");
           System.out.println("================================");
           System.out.println("1. Gestion de Pacientes->");
           //iremos añadiendo la gestion de odontologos, pagos, generar informes en pdf, imprimir expedientes
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
                    menuPagos(sc,pagosManager,pacienteManager);
               }
               default: {
                   System.out.println("Opcion no valida");
                   break;
               }
           }
       }while (opcion != 4);
       sc.close();
    }
    private static void menuPacientes(Scanner sc, PacienteManager pacienteManager){
    int opcion;
    do{
        System.out.println("=================================");
        System.out.println("=======GESTION DE PACIENTES======");
        System.out.println("=================================");
        System.out.println("1. Registrar paciente");
        System.out.println("2. Listar pacientes");
        System.out.println("3. Actualizar paciente");
        System.out.println("4. Eliminar paciente");
        System.out.println("5. <-Volver al menu principal");
        System.out.println("Seleccione una opcion valida: ");
        opcion = sc.nextInt();
        sc.nextLine();
        switch(opcion){
            case 1:{
                registrarPaciente(sc, pacienteManager);
                break;
            }
            case 2:{
                listarPacientes(pacienteManager);
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
                System.out.println("Volviendo al menu principal");
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
        System.out.print("Año de nacimiento: ");
        int anio = sc.nextInt();
        System.out.print("Mes: ");
        int mes = sc.nextInt();
        System.out.print("Dia: ");
        int dia = sc.nextInt();
        sc.nextLine();
        LocalDate fechaNacimiento = LocalDate.of(anio,mes,dia);
        
        Paciente nuevo = new Paciente(
                java.util.UUID.randomUUID().toString(), //ID generado aleatorio
                nombres,
                ci,
                apellidos,
                numero,
                fechaNacimiento,
                "",
                "",
                sexo,
                "",
                "",
                ""
        );
        manager.agregar(nuevo);
    }
    private static void listarPacientes(PacienteManager manager){//al listar el paciente nos devuelve su ID
        System.out.println("--- LISTA DE PACIENTES REGISTRADOS ---");
        for(Paciente p : manager.listar()){
            System.out.println(p.getNombres());
        }
    }
    private static void actualizarPaciente(Scanner sc, PacienteManager manager){
        System.out.print("Ingrese el ID del paciente para actualizar: ");
        String id = sc.nextLine();
        Paciente paciente = manager.getById(id);
        if(paciente == null){
            System.out.println("Paciente no encontrado");
            return;
        }
        //actualizamos el numero de contacto VAMOS A AMPLIAR ESTE CAMPO
        System.out.print("Nuevo numero de contacto: ");
        String nuevoContacto = sc.nextLine();
        paciente.setNumeroContacto(nuevoContacto);
        manager.actualizarPorId(id, paciente);
    }
    private static void eliminarPaciente(Scanner sc, PacienteManager manager){
        System.out.print("Ingrese ID del paciente a eliminar: ");
        String id = sc.nextLine();
        manager.eliminarPorId(id);//revisar
    }
    private static void menuOdontologos(Scanner sc, OdontologoManager odontologoManager){
    int opcion ;
    do{
        System.out.print("==============================");
        System.out.print("======MENU DE ODONTOLOGOS=====");
        System.out.print("==============================");
        System.out.print("1.Ingresar odontologo");
        System.out.print("2. Listar odontologos ");
        System.out.print("3. Actualizar odontologo");
        System.out.print("4. Eliminar odontologo");
        System.out.print("5. <-Volver al menu principal ");
        opcion=sc.nextInt();
        sc.nextLine();
        switch(opcion){
            case 1:{
                registrarOdontologo(sc, odontologoManager);
                break;
            }
            case 2:{
                listarOdontologos(odontologoManager);
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
                System.out.println("Volviendo al menu principal");
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
        java.util.UUID.randomUUID().toString()
    ); 
    manager.agregar(nuevo);
    }
    private static void listarOdontologos(OdontologoManager manager){
    System.out.println("--- LISTA DE PACIENTES REGISTRADOS ---");
    for(Odontologo o : manager.listar()){
        System.out.println(o.getNombre());
    }
    }
    private static void actualizarOdontologo(Scanner sc, OdontologoManager manager){
    System.out.println("Ingrese el ID del paciente para actualizar");
    String id = sc.nextLine();
    Odontologo odontologo = manager.getById(id);
    if(odontologo == null){
        System.out.println("Paciente no encontrado");
        return;
    }
    System.out.print("Nuevo numero de contacto: ");
    String nuevoContacto = sc.nextLine();
    odontologo.setNumero_de_celular(nuevoContacto);
    manager.actualizarPorId(id, odontologo);
    }
    private static void eliminarOdontologo(Scanner sc, OdontologoManager manager){
    System.out.println("Ingrese ID del odontologo a eliminar");
    String id = sc.nextLine();
    manager.eliminarPorId(id);
    }
    private static void menuPagos(Scanner sc, PagosManager pagosManager, PacienteManager pacienteManager){
    int opcion;
    do{
        System.out.println("======================================");
        System.out.println("=========GESTION DE PAGOS=========");
        System.out.println("======================================");
        System.out.println("1. Registrar pago");
        System.out.println("2. Listar pagos");
        System.out.println("3. Actualizar pago");
        System.out.println("4. Anular pago");
        System.out.println("5. Elimianr pago");
        System.out.println("6. <-Volver al menu principal");
        System.out.println("Seleccione una opcion valida");
        opcion = sc.nextInt();
        sc.nextLine();
        switch(opcion){
            case 1:{
                registrarPago(sc, pagosManager, pacienteManager);
                break;
            }
            case 2:{
                listarPagos(pagosManager, pacienteManager);
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
                System.out.println("Volviendo al menu principal");
                break;
            }
            default:{
                System.out.println("Seleccione una opcion valida");
                break;
            }
        }
    }while(opcion != 6);
    }
    private static void registrarPago(Scanner sc, PagosManager pagosManager, PacienteManager pacienteManager){
        System.out.println("---REGISTRANDO UN PAGO---");
        List<Paciente> pacientes = pacienteManager.listar();
        if(pacientes.isEmpty()){
            System.out.println("No hay pacientes registrado. Registre un paciente primero ");
            return;
        }
        
        System.out.println("Seleccione el paciente para el pago: ");
        for(int i = 0; i < pacientes.size(); i++){
            Paciente p = pacientes.get(i);
            System.out.println((i+1)+" . "+p.getNombres()+" "+p.getApellidos()+" (CI: " + p.getCI() + ")");
        
        }
        
        int idx;
        while(true){
            System.out.println("Ingrese el numero del paciente: ");
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
        BigDecimal monto;
        while(true){
            System.out.println("Ingrese el monto (ej. 150.50): ");
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
            System.out.println("Seleccione metodo de pago: ");
            System.out.println("1) EFECTIVO");
            System.out.println("2) TRANSFERENCIA");
            System.out.println("Opcion: ");
            String mline = sc.nextLine().trim();
            if (mline.equals("1")) { metodo = Pagos.MetodoPago.EFECTIVO; break; }
            if (mline.equals("2")) { metodo = Pagos.MetodoPago.TRANSFERENCIA; break; }
            System.out.println("Opcion no valida. Intente de nuevo");
        }
        Pagos.EstadoPago estado = Pagos.EstadoPago.PENDIENTE;
        String pagoID = java.util.UUID.randomUUID().toString();
        LocalDateTime ahora = LocalDateTime.now();
        
        Pagos pago = new Pagos(
                pagoID,
                pacienteSeleccionado.getID(),
                ahora,
                monto,
                metodo,
                estado
        );
        pagosManager.agregar(pago);
        System.out.printf("✅ pago registrado: %s %s - Monto: %s - Metodo: %s%n",
                pacienteSeleccionado.getNombres(),
                pacienteSeleccionado.getApellidos(),
                monto.toPlainString(),
                metodo);
    }
    private static void listarPagos(PagosManager pagosManager, PacienteManager pacienteManager){
        List<Pagos> pagos = pagosManager.listar();
        if(pagos.isEmpty()){
            System.out.println("No hay pagos registrados");
            return;
        }
        System.out.println("---LISTA DE PAGOS---");
        for(Pagos p : pagos){
            Paciente pac = pacienteManager.getById(p.getPacienteID());
            String nombrePaciente = (pac != null) ? pac.getNombres() + " " + pac.getApellidos(): "Paciente no encontrado";
            System.out.printf("ID Pago: %s | Paciente: %s | Fecha: %s | Monto: %s | Metodo: %s | Estado: %s%n",
                    p.getID(),
                    nombrePaciente,
                    p.getFecha().toString(),
                    p.getMonto().toPlainString(),
                    p.getMetodo(),
                    p.getEstado());
        }
    }
    private static void actualizarPago(Scanner sc, PagosManager pagosManager, PacienteManager pacienteManager) {
        System.out.println("\n--- ACTUALIZAR PAGO ---");
        List<Pagos> lista = pagosManager.listar();
        if (lista.isEmpty()) {
            System.out.println("No hay pagos registrados.");
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
        System.out.printf("%d) ID:%s | Paciente:%s | Monto:%s | Metodo:%s | Estado:%s%n",
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
            return;
        }

    // Si es numeeo entonces obtener por índice
        try {
            int opc = Integer.parseInt(entrada);
            if (opc >= 1 && opc <= lista.size()) {
                pagoId = lista.get(opc - 1).getID();
            }else {
                System.out.println("Número fuera de rango.");
                return;
            }
        } catch (NumberFormatException e) {
            pagoId = entrada;
        }

        Pagos pago = pagosManager.getById(pagoId);
        if (pago == null) {
            System.out.println("No se encontró un pago con ese ID.");
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
                    return;
                }
                nuevoMonto = m;
            }catch (Exception ex) {
                System.out.println("Formato de monto inválido. Cancelando actualización.");
                return;
            }
        }
        Pagos.MetodoPago nuevoMetodo = null;
        System.out.println("Método actual: " + pago.getMetodo());
        System.out.println("Opciones método: 1) EFECTIVO  2) TRANSFERENCIA  3) TARJETA");
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
                        return;
                    }
                }
            }
        }

        boolean ok = pagosManager.actualizarPorId(pagoId, nuevoMonto, nuevoMetodo, nuevoEstado);
        if (ok) {
            System.out.println("✅ Pago actualizado correctamente.");
        } else {
            System.out.println("❌ No se pudo actualizar el pago (ID no encontrado o error).");
        }
    }
    private static void anularPago(Scanner sc, PagosManager manager){
        System.out.println("--- ANULAR PAGO ---");
        System.out.println("ingrese el ID del pago para anular: ");
        String id = sc.nextLine();
        Pagos pago = manager.getById(id);
        if(pago == null){
            System.out.println("No existe un pago con ese ID");
            return;
        }
        System.out.println("Sen encontro el siguiente pago: ");
        System.out.println("ID: "+pago.getID() + 
                           "| PacienteID: "+pago.getPacienteID()+
                           "| Monto: "+
                           "| Estado: "+ pago.getEstado());
        System.out.println("¿Desea anular este pago? (S/N)");
        String confirm = sc.nextLine().toUpperCase();
        
        if(confirm.equals("S")){
            boolean exito = manager.eliminadPorId(id);
            if(exito){
                System.out.println("✅ El pago ha sido anulado correctamente.");
            }else {
                System.out.println("⚠️ El pago ya estaba anulado previamente.");
            }
        }else {
            System.out.println("Operación cancelada por el ususario.");
        }
    }
    private static void eliminarPago(Scanner sc, PagosManager manager){
        System.out.println("--- ELIMINAR PAGO ---");
        System.out.println("ingrese el ID del pago a eliminar: ");
        String id = sc.nextLine();
        Pagos pago = manager.getById(id);
        if(pago == null){
            System.out.println("No existe un pago con ese ID");
            return;
        }
        System.out.println("Sen encontro el siguiente pago: ");
        System.out.println("ID: "+pago.getID() + 
                           "| PacienteID: "+pago.getPacienteID()+
                           "| Monto: "+
                           "| Estado: "+ pago.getEstado());
        System.out.println("¿Desea anular este pago? (S/N)");
        String confirm = sc.nextLine().toUpperCase();
        
        if(confirm.equals("S")){
            boolean exito = manager.eliminarFisicoPorId(id);
            if(exito){
                System.out.println("✅ El pago ha sido anulado correctamente.");
            }else {
                System.out.println("⚠️ El pago ya estaba anulado previamente.");
            }
        }else {
            System.out.println("Operación cancelada por el ususario.");
        }
    }
    //metodo para limpiar las consola 
    public static void limpiarConsola(){
        System.out.print("\\033[H\\033[2J");
        System.out.flush();
    }
}

