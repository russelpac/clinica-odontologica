package com.clinica.managers;

import java.util.ArrayList;
import java.util.List;
import com.clinica.modelos.Odontologo;
import com.clinica.interfaces.ICrud;
import java.sql.*;


public class OdontologoManager implements ICrud<Odontologo>{
    private Connection conn;
    public OdontologoManager(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void agregar(Odontologo odontologo) {
        String sql = "INSERT INTO odontologo (ID, nombre, numeroCelular, especialidad) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, odontologo.getID());
            stmt.setString(2, odontologo.getNombre());
            stmt.setString(3, odontologo.getNumeroCelular());
            stmt.setString(4, odontologo.getEspecialidad());
            stmt.executeUpdate();
            System.out.println("Odontólogo agregado correctamente: " + odontologo.getNombre());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Odontologo> listar() {
        List<Odontologo> odontologos = new ArrayList<>();
        String sql = "SELECT * FROM odontologo";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Odontologo o = new Odontologo(
                        
                        rs.getString("nombre"),
                        rs.getString("numeroCelular"),
                        rs.getString("especialidad"),
                        rs.getString("ID")
                );
                odontologos.add(o);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return odontologos;
    }

    @Override
    public void actualizar(int id, Odontologo odontologoActualizado) {
        
        System.out.println("Use actualizarPorId en lugar de actualizar(int, Odontologo).");
    }

    @Override
    public void eliminar(int id) {
        
        System.out.println("Use eliminarPorId en lugar de eliminar(int).");
    }

    //FUNCIONES EXTRAS ADAPTADAS A BD

    public Odontologo getById(String id) {
        String sql = "SELECT * FROM odontologo WHERE ID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Odontologo(
                        
                        rs.getString("nombre"),
                        rs.getString("numeroCelular"),
                        rs.getString("especialidad"),
                        rs.getString("ID")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean actualizarPorId(String id, Odontologo odontologoActualizado) {
        String sql = "UPDATE odontologo SET nombre=?, numeroCelular=?, especialidad=? WHERE ID=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, odontologoActualizado.getNombre());
            stmt.setString(2, odontologoActualizado.getNumeroCelular());
            stmt.setString(3, odontologoActualizado.getEspecialidad());
            stmt.setString(4, id);
            int filas = stmt.executeUpdate();
            return (filas > 0); 
                
        } catch (SQLException e) {
            System.err.println("Error al actualizar odontologo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminarPorId(String id) {
        String sql = "DELETE FROM odontologo WHERE ID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            int filas = stmt.executeUpdate();
            return (filas > 0);
        } catch (SQLException e) {
            System.err.println("Error al eliminar odontologo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}