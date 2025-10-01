package com.clinica.managers;

import java.util.ArrayList;
import java.util.List;
import com.clinica.modelos.Paciente;
import com.clinica.interfaces.ICrud;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class PacienteManager implements ICrud<Paciente>{
    private static final DateTimeFormatter formatoDate = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter formatoDateTime= DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private final Connection conn;
    public PacienteManager(Connection conn){
        this.conn = conn;
    }
    @Override
    public void agregar(Paciente paciente){//se esta usando
        String sql = "INSERT INTO paciente (ID,nombres,CI,apellidos,numeroContacto,fechaNacimiento,alergias,consultas,sexo,contactoEmergencias,direccion,antecMedicos,activo,fechaConsulta) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, paciente.getID());
            stmt.setString(2, paciente.getNombres());
            stmt.setString(3, paciente.getCI());
            stmt.setString(4, paciente.getApellidos());
            stmt.setString(5, paciente.getNumeroContacto());
            
            if (paciente.getFechaNacimiento() != null) {
                stmt.setString(6, paciente.getFechaNacimiento().format(formatoDate));
            } else {
                stmt.setNull(6, Types.VARCHAR);
            }
            stmt.setString(7, paciente.getAlergias());
            stmt.setString(8, paciente.getConsultas());
            stmt.setString(9, paciente.getSexo());
            stmt.setString(10, paciente.getContactoEmergencias());
            stmt.setString(11, paciente.getDireccion());
            stmt.setString(12, paciente.getAntecMedicos());
            stmt.setInt(13, paciente.isActivo() ? 1 : 0);
            if (paciente.getFechaConsulta() != null) {
                stmt.setString(14, paciente.getFechaConsulta().format(formatoDateTime));
            } else {
                stmt.setNull(14, Types.VARCHAR);
            }
            stmt.executeUpdate();
            System.out.println("Paciente agregado correctamente: "+paciente.getNombres());
        
        }catch(SQLException e){
            System.out.println("Error al insertar paciente: "+ e.getMessage());
            e.printStackTrace();
        }
        
    }
    @Override
    public List<Paciente> listar() {
        List<Paciente> resultado = new ArrayList<>();
        String sql = "SELECT * FROM paciente ORDER BY nombres, apellidos";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Paciente p = mapResultSetToPaciente(rs);
                resultado.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar pacientes: " + e.getMessage());
            e.printStackTrace();
        }
        return resultado;
    }
    @Override
    public void actualizar(int id, Paciente pacienteActualizado ){// no se esta usando 
        throw new UnsupportedOperationException("Este manager usa actualizarPorId(String, Paciente).");
    }
    @Override
    public void eliminar(int id){ //no se esta usando 
        throw new UnsupportedOperationException("Este manager usa eliminarPorId(String).");
  }
    //metodos externos del PacienteManager
    public Paciente getById(String id){//se esta usando
    String sql = "SELECT * FROM paciente WHERE ID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPaciente(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en getById: " + e.getMessage());
            e.printStackTrace();
        }
        return null;  
    }
    public boolean actualizarPorId(String id, Paciente pacienteActualizado){//se esta usando
        String sql = "UPDATE paciente SET nombres=?, CI=?, apellidos=?, numeroContacto=?, fechaNacimiento=?, alergias=?, consultas=?, sexo=?, contactoEmergencias=?, direccion=?, antecMedicos=?, activo=?, fechaConsulta=? WHERE ID=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, pacienteActualizado.getNombres());
            stmt.setString(2, pacienteActualizado.getCI());
            stmt.setString(3, pacienteActualizado.getApellidos());
            stmt.setString(4, pacienteActualizado.getNumeroContacto());
            if (pacienteActualizado.getFechaNacimiento() != null) {
                stmt.setString(5, pacienteActualizado.getFechaNacimiento().toString());
            } else {
                stmt.setNull(5, Types.VARCHAR);
            }
            stmt.setString(6, pacienteActualizado.getAlergias());
            stmt.setString(7, pacienteActualizado.getConsultas());
            stmt.setString(8, pacienteActualizado.getSexo());
            stmt.setString(9, pacienteActualizado.getContactoEmergencias());
            stmt.setString(10, pacienteActualizado.getDireccion());
            stmt.setString(11, pacienteActualizado.getAntecMedicos());
            stmt.setInt(12, (pacienteActualizado.isActivo() ? 1 : 0));
            if (pacienteActualizado.getFechaConsulta() != null) {
                stmt.setString(13, pacienteActualizado.getFechaConsulta().toString());
            } else {
                stmt.setNull(13, Types.VARCHAR);
            }
            stmt.setString(14, id);

            int filas = stmt.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar paciente: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    public boolean eliminarPorId(String id){//se esta usando
        String sql = "UPDATE paciente SET activo = 0 WHERE ID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            int filas = stmt.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar/anular paciente: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    public boolean eliminarFisicoPorId(String id){
        String sql = "DELETE FROM paciente WHERE ID=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            int filas = stmt.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar físicamente el paciente: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
   private Paciente mapResultSetToPaciente(ResultSet rs) throws SQLException {
        String id = rs.getString("ID");
        String nombres = rs.getString("nombres");
        String ci = rs.getString("CI");
        String apellidos = rs.getString("apellidos");
        String numeroContacto = rs.getString("numeroContacto");

        
        LocalDate fechaNacimiento = null;
        String fn = rs.getString("fechaNacimiento");
        if (fn != null && !fn.isBlank()) {
            try {
                fechaNacimiento = LocalDate.parse(fn,formatoDate);
            } catch (Exception ex) {
                try{
                    fechaNacimiento = LocalDate.parse(fn);
                } catch (Exception ex2){
                    fechaNacimiento = null;
                }
            }
        }

        String alergias = rs.getString("alergias");
        String consultas = rs.getString("consultas");
        String sexo = rs.getString("sexo");
        String contactoEmergencias = rs.getString("contactoEmergencias");
        String direccion = rs.getString("direccion");
        String antecMedicos = rs.getString("antecMedicos");
        boolean activo = rs.getInt("activo") == 1;

       
        LocalDateTime fechaConsulta = null;
        String fc = rs.getString("fechaConsulta");
        if (fc != null && !fc.isBlank()) {
            try {
                fechaConsulta = LocalDateTime.parse(fc,formatoDateTime);
            } catch (Exception ex) {
                try {
                    
                    LocalDate d = LocalDate.parse(fc);
                    fechaConsulta = d.atStartOfDay();
                } catch (Exception ex2) {
                    try{
                        fechaConsulta = LocalDateTime.parse(fc);
                    } catch (Exception ex3){
                        fechaConsulta = null;
                    }
                }
            }
        }
        Paciente p = new Paciente(
                id,
                nombres,
                ci,
                apellidos,
                numeroContacto,
                fechaNacimiento,
                alergias,
                consultas,
                sexo,
                contactoEmergencias,
                direccion,
                antecMedicos,
                fechaConsulta
        );
       
            
            p.setActivo(activo);
            if (fechaConsulta != null) p.setFechaConsulta(fechaConsulta);
        

        return p;
    }
}