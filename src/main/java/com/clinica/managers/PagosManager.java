package com.clinica.managers;
import java.math.BigDecimal;
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
    //metodos aparte
    public Pagos getById(String id){
        for(Pagos p : pagos){
            if(p.getID().equals(id)) return p;
        }
        return null;
    }
    public boolean actualizarPorId(String id, BigDecimal nuevoMonto, Pagos.MetodoPago nuevoMetodo, Pagos.EstadoPago nuevoEstado){
        for(int i = 0; i < pagos.size(); i++){
            Pagos p = pagos.get(i);
            if(p.getID().equals(id)){
                if(nuevoMonto != null) p.setMonto(nuevoMonto);
                if(nuevoMetodo != null)p.setMetodo(nuevoMetodo);
                if(nuevoEstado != null)p.setEstado(nuevoEstado);
                pagos.set(i, p);
                return true;
            }
        }
        return false;
    }
    public boolean eliminadPorId(String id){
        for(int i = 0; i < pagos.size(); i++){
            Pagos p = pagos.get(i);
            if(p.getID().equals(id)){
                if(p.getEstado() == Pagos.EstadoPago.ANULADO){
                //si ya esta anulado no se hace nada, asi para tener historial
                return false;
            }
            p.setEstado(Pagos.EstadoPago.ANULADO);
            pagos.set(i,p);
            return true;
            }
        }
    return false;
    }
    public boolean eliminarFisicoPorId(String id){
        for(int i = 0; i < pagos.size(); i++){
            if(pagos.get(i).getID().equals(id)){
                pagos.remove(i);
                return true;
            }
        }
    return false;
    }
}