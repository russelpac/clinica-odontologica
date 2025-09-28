package com.clinica.modelos;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.math.BigDecimal;


public class Pagos implements Serializable{
    private final String ID;
    private final String pacienteID;
    private final String odontologoID;
    private LocalDateTime fecha;
    private BigDecimal monto;
    private MetodoPago metodo;
    private EstadoPago estado;
     
    public enum MetodoPago{EFECTIVO,TRANSFERENCIA}
    public enum EstadoPago{PENDIENTE,PAGADO,ANULADO}

    public Pagos(String ID, String pacienteID, String odontologoID, LocalDateTime fecha, BigDecimal monto, MetodoPago metodo, EstadoPago estado){
        this.ID=ID;
        this.estado=estado;
        this.fecha=fecha;
        this.metodo=metodo;
        this.monto=monto;
        this.pacienteID=pacienteID;
        this.odontologoID=odontologoID;
    }
//setters
    public void setFecha(LocalDateTime fecha){
        this.fecha=fecha;
    }
    public void setMonto(BigDecimal monto){
        this.monto=monto;
    }
    public void setMetodo(MetodoPago metodo){
        this.metodo=metodo;
    }
    public void setEstado(EstadoPago estado){
        this.estado=estado;
    }
    //getters
    public String getID(){
        return ID;
    }
    public String getPacienteID(){
        return pacienteID;
    }
    public LocalDateTime getFecha(){
        return fecha;
    }
    public BigDecimal getMonto(){
        return monto;
    }
    public MetodoPago getMetodo(){
        return metodo;
    }
    public EstadoPago getEstado(){
        return estado;
    }
    public String getOdontologID(){
        return odontologoID;
    }
}