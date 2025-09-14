package com.clinica.modelos;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.math.BigDecimal;
//import java.util.UUID;

public class Pagos implements Serializable{
    private final String ID;
    private final String pacienteID;
    private LocalDateTime fecha;
    private BigDecimal monto;
    private MetodoPago metodo;
    private EstadoPago estado;
     
    public enum MetodoPago{EFECTIVO,TRANSFERENCIA}
    public enum EstadoPago{PENDIENTE,PAGADO,ANULADO}
//Constructor
public Pagos(String ID, String pacienteID, LocalDateTime fecha, BigDecimal monto, MetodoPago metodo, EstadoPago estado){
    this.ID=ID;
    this.estado=estado;
    this.fecha=fecha;
    this.metodo=metodo;
    this.monto=monto;
    this.pacienteID=pacienteID;
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
}