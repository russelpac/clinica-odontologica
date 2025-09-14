package com.clinica.managers;

import java.util.ArrayList;
import java.util.List;
import com.clinica.modelos.Odontologo;
import com.clinica.interfaces.ICrud;

public class OdontologoManager implements ICrud<Odontologo>{
    private List<Odontologo> odontologos = new ArrayList<>();
    @Override
    public void agregar(Odontologo odontologo){
        odontologos.add(odontologo);
        System.out.println("odontologo agregado correctamente: "+odontologo.getNombre());
    }
    @Override
    public List<Odontologo> listar(){
        return odontologos;
    }
    @Override
    public void actualizar(int id, Odontologo odontologoActualizado ){
        if(id >= 0 && id < odontologos.size()){
            odontologos.set(id, odontologoActualizado);
            System.out.println("Odontologo Actualizado");
        }else{
            System.out.println("id no valido, no se pudo actualizar");
        }
    }
    @Override
    public void eliminar(int id){
        if(id >= 0 && id < odontologos.size()){
            odontologos.remove(id);
            System.out.println("Odontologo eliminado");
        }else{
            System.out.println("id no valido, no se pudo eliminar");
    }
  }
}