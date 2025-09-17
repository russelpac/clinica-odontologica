package com.clinica.managers;

import java.util.ArrayList;
import java.util.List;
import com.clinica.modelos.Paciente;
import com.clinica.interfaces.ICrud;

public class PacienteManager implements ICrud<Paciente>{
    private List<Paciente> pacientes = new ArrayList<>();
    @Override
    public void agregar(Paciente paciente){//se esta usando
        pacientes.add(paciente);
        System.out.println("Paciente agragado correctamente: "+paciente.getNombres());
    }
    @Override
    public List<Paciente> listar(){//se esta usando 
        return pacientes;
    }
    @Override
    public void actualizar(int id, Paciente pacienteActualizado ){// no se esta usando 
        if(id >= 0 && id < pacientes.size()){
            pacientes.set(id, pacienteActualizado);
            System.out.println("Paciente Actualizado");
        }else{
            System.out.println("ID no valido, no se pudo actualizar");
        }
    }
    @Override
    public void eliminar(int id){ //no se esta usando 
        if(id >= 0 && id < pacientes.size()){
            pacientes.remove(id);
            System.out.println("Paciente eliminado");
        }else{
            System.out.println("ID no valido, no se pudo eliminar");
    }
  }
    //metodos externos del PacienteManager
    public Paciente getById(String id){
        for (Paciente p : pacientes){
            if (p.getID().equals(id))return p;
        }
        return null;
    }
    public void actualizarPorId(String id, Paciente pacienteActualizado){
        for(int i = 0; i < pacientes.size(); i++){
            if(pacientes.get(i).getID().equals(id)){
                pacientes.set(i, pacienteActualizado);
                System.out.println("Paciente actualizado correctamente. ");
                return; 
            }
        }
        System.out.println("No se encontro paciente con ID: " + id);
    }
    public void eliminarPorId(String id){
        for(int i = 0; i<pacientes.size(); i++){
            if(pacientes.get(i).getID().equals(id)){
                pacientes.remove(i);
                System.out.println("Paciente eliminado correctamente");
                return;
            }
        }
        System.out.println("No se encontro paciente con ID: "+ id);
    }
}