package com.clinica.interfaces;
import java.util.List;

public interface ICrud<T>{
    void agregar (T obj);
    List<T> listar();
    void actualizar(int id, T obj);
    void eliminar(int id);
}