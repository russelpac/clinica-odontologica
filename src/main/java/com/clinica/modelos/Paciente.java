package com.clinica.modelos;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
//import java.util.UUID;

public class Paciente implements Serializable  {
    private final String ID;//para que no se pueda modificar una vez ya construido
    private String nombres;
    private String CI;
    private String apellidos;
    private String numeroContacto;
    private LocalDate fechaNacimiento;
    private String alergias;
    private String consultas;
    private String sexo;
    private String contactoEmergencias;
    private String direccion;
    private String antecMedicos;
    private Boolean activo = true;
    private final LocalDateTime fechaConsulta;

public Paciente(String ID, String nombres, String CI, String apellidos, String numeroContacto,LocalDate fechaNacimiento, String alergias, String consultas,String sexo,
String contactoEmergencias,String direccion, String antecMedicos,LocalDateTime fechaConsulta) {
    
    this.ID = ID;
    this.nombres = nombres;
    this.CI = CI;
    this.apellidos = apellidos;
    this.numeroContacto = numeroContacto;
    this.fechaNacimiento = fechaNacimiento;
    this.alergias = alergias;
    this.consultas = consultas;
    this.sexo = sexo;
    this.contactoEmergencias = contactoEmergencias;
    this.direccion = direccion;
    this.antecMedicos = antecMedicos;
    this.fechaConsulta = fechaConsulta;
}
//GETERS
public String getID(){
    return ID;
} 
public String getNombres(){
    return nombres;
}
public String getCI(){
    return CI;
}
public String getApellidos(){
    return apellidos;
}
public String getNumeroContacto(){
    return numeroContacto;
}
public LocalDate getFechaNacimiento(){
    return fechaNacimiento;
}
public String getAlergias(){
    return alergias;
}
public String getConsultas(){
    return consultas;
}
public String getSexo(){
    return sexo;
}
public String getContactoEmergencias(){
    return contactoEmergencias;
    
}
public String getDireccion(){
    return direccion;
}
public String getAntecMedicos(){
    return antecMedicos;
}
public Boolean isActivo(){
    return activo;
}
public LocalDateTime getFechaConsulta(){
    return fechaConsulta;
}
//SETERS
public void setNombres(String nombres){
    this.nombres=nombres;
}
public void setCI(String CI){
    this.CI=CI;
}
public void setApellidos(String apellidos){
    this.apellidos=apellidos;
}
public void setNumeroContacto(String numeroContacto){
    this.numeroContacto=numeroContacto;
}
public void setFechaNacimiento(LocalDate fechaNacimiento){
    this.fechaNacimiento=fechaNacimiento;
}
public void setAlergias(String alergias){
    this.alergias=alergias;
}
public void setConsultas(String consultas){
    this.consultas=consultas;
}
public void setSexo(String sexo){
    this.sexo=sexo;
}
public void setContatoEmergencias(String contactoEmergencias){
    this.contactoEmergencias=contactoEmergencias;
}
public void setDireccion(String direccion){
    this.direccion=direccion;
}
public void setAntecMedicos(String antecMedicos){
    this.antecMedicos=antecMedicos;
}
public void setActivo(Boolean activo){
    this.activo=activo;
}
}