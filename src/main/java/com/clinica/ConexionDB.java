
package com.clinica;
import java.sql.Connection;
import java.sql.DriverManager;
public class ConexionDB {
    private static final String URL = "jdbc:sqlite:clinica.db";
    
    public static Connection getConnection(){
        try{
            Connection conn = DriverManager.getConnection(URL);
            System.out.println("Conectado a SQLite vamos a dormir");
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
