package com.clinica.managers;

import java.util.ArrayList;
import java.util.List;
import com.clinica.modelos.Paciente;
import com.clinica.interfaces.ICrud;

public class PacienteManager implements ICrud<Paciente>{
    private List<Paciente> pacientes = new ArrayList<>();
    @Override
    public void agregar(Paciente paciente){
        pacientes.add(paciente);
        System.out.println("Paciente agragado correctamente: "+paciente.getNombres());
    }
    @Override
    public List<Paciente> listar(){
        return pacientes;
    }
    @Override
    public void actualizar(int ID, Paciente pacienteActualizado ){
        if(ID >= 0 && ID < pacientes.size()){
            pacientes.set(ID, pacienteActualizado);
            System.out.println("Paciente Actualizado");
        }else{
            System.out.println("ID no valido, no se pudo actualizar");
        }
    }
    @Override
    public void eliminar(int ID){
        if(ID >= 0 && ID < pacientes.size()){
            pacientes.remove(ID);
            System.out.println("Paciente eliminado");
        }else{
            System.out.println("ID no valido, no se pudo eliminar");
    }
  }
}