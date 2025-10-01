package com.clinica.persistencia;
import java.sql.Connection;
import java.sql.DriverManager;
public class ConexionBD {
    private static final String URL = "jdbc:sqlite:clinica.db";
    
    public static Connection getConnection(){
        try{
            Connection conn = DriverManager.getConnection(URL);
            System.out.println("Conectado a SQLite ");
            return conn;
        }catch(Exception e){
            System.out.println("Error al conectar SQLite: " + e.getLocalizedMessage());
            return null;
        }
    }
    public static void main(String[] args){
        getConnection();
    }
}
