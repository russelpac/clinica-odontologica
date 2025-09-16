package com.clinica;
import com.clinica.managers.PacienteManager;
import com.clinica.modelos.Paciente;
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
        System.out.println("3. Actualizar pacinte");
        System.out.println("4. Eliminar paciente");
        System.out.println("5. <-Volver al menu principal");
        System.out.println("Seleccione una opcion válida: ");
        opcion = sc.nextInt();
        sc.nextLine();
        switch(opcion){
            case 1:{
            
            }
        
        }
    }while(opcion != 5 );
           
    }
    
 
}

