package com.clinica;
import com.clinica.managers.PacienteManager;
import com.clinica.modelos.Paciente;
import java.time.LocalDate;
import java.util.Scanner;
public class Main 
{
    public static void main( String[] args )
    {
       Scanner sc = new Scanner(System.in);
       PacienteManager pacienteManager = new PacienteManager();
       
       int opcion;
       do { //ESTE SERA EL MENU PRINCIPAL
           System.out.println("================================");
           System.out.println("=========MENU PRINCIPAL=========");
           System.out.println("================================");
           System.out.println("=======CLINICA ODONTOLOGIA======");
           System.out.println("================================");
           System.out.println("1. Gestion de Pacientes->");
           //iremos añadiendo la gestion de odontologos, pagos, generar informes en pdf, imprimir expedientes
           System.out.println("2. Salir");
           System.out.print("Seleccione una opcion valida: ");
           opcion = sc.nextInt();
           sc.nextLine();
           switch(opcion){
               case 1: {
                   menuPacientes(sc,pacienteManager);
                   break;
               }
               case 2: {
                   System.out.println("Saliendo del Sistema");
                   break;
               }
               default: {
                   System.out.println("Opcion no valida");
                   break;
               }
           }
       }while (opcion != 2);
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
        System.out.println("Seleccione una opcion válida: ");
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
        System.out.print("Ingrese ID del apciente a eliminar: ");
        String id = sc.nextLine();
        manager.eliminarPorId(id);//revisar
    }
}

