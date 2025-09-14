package com.clinica.modelos;
import java.io.Serializable;
//import java.util.UUID;

public class Odontologo implements Serializable{
    private String nombre;
    private String numeroCelular;
    private String especialidad;
    private String ID;
    

public Odontologo(String nombre, String numero_de_celular,String especialidad, String ID){
    this.nombre = nombre;
    this.numeroCelular = numero_de_celular;
    this.especialidad = especialidad;
    this.ID = ID;
}
//GETTERS
public String getNombre(){
    return nombre;
}
public String getNumero_de_celular(){
    return numeroCelular;
}
public String getEspecialidad(){
    return especialidad;
}
public String getID(){
    return ID;
}
//SETERS
public void setNombre(String nombre){
    this.nombre = nombre;
}
public void setNumero_de_celular(String numeroCelular){
    this.numeroCelular = numeroCelular;
}
public void setEspecialidad(String especialidad){
    this.especialidad = especialidad;
}
public void setID(String ID){
    this.ID=ID;
}
}