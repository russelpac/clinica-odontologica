package com.clinica.managers;

import java.util.ArrayList;
import java.util.List;
import com.clinica.modelos.Pagos;
import com.clinica.interfaces.ICrud;

public class PagosManager implements ICrud<Pagos>{
    private List<Pagos> pagos = new ArrayList<>();
    @Override
    public void agregar(Pagos pago){
        pagos.add(pago);
        System.out.println("Pago agregado correctamente: "+pago.getID());
    }
    @Override
    public List<Pagos> listar(){
        return pagos;
    }
    @Override
    public void actualizar(int id, Pagos pagoActualizado ){
        if(id >= 0 && id < pagos.size()){
            pagos.set(id, pagoActualizado);
            System.out.println("Pago Actualizado");
        }else{
            System.out.println("id no valido, no se pudo actualizar");
        }
    }
    @Override
    public void eliminar(int id){
        if(id >= 0 && id < pagos.size()){
            pagos.remove(id);
            System.out.println("Pago eliminado");
        }else{
            System.out.println("id no valido, no se pudo eliminar");
    }
  }
}