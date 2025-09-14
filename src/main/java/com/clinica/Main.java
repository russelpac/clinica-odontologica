package com.clinica;
import com.clinica.managers.PacienteManager;
import com.clinica.modelos.Paciente;
public class Main 
{
    public static void main( String[] args )
    {
        PacienteManager manager = new PacienteManager();
        manager.agregar(new Paciente("34","Juan","Perez","","",java.time.LocalDate.of(2000,5,10)," ","","","","",""));
        System.out.println("Lista de pacientes");
        for(Paciente p : manager.listar()){
            System.out.println(p.getNombres()+ " "+p.getApellidos());
        }
    }
}
