package com.clinica.managers;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import com.clinica.modelos.Pagos;
import com.clinica.interfaces.ICrud;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PagosManager implements ICrud<Pagos>{
    private static final DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private final Connection conn;
    public PagosManager (Connection conn){
        this.conn = conn;
    }
    
    @Override
    public void agregar(Pagos pago){
        String sql = "INSERT INTO pago (ID, pacienteID, odontologoID, fecha, monto, metodo, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, pago.getID());
            stmt.setString(2, pago.getPacienteID());
            stmt.setString(3, pago.getOdontologoID());
            
            if (pago.getFecha() != null) {
                stmt.setString(4, pago.getFecha().format(formato)); 
            } else {
                stmt.setNull(4, Types.VARCHAR);
            }
            stmt.setBigDecimal(5, pago.getMonto());
            stmt.setString(6, pago.getMetodo().name());
            stmt.setString(7, pago.getEstado().name());
            stmt.executeUpdate();
            System.out.println("Pago agregado correctamente, ID del pago: " + pago.getID());
        } catch (SQLException e) {
            System.err.println("Error al insertar pago: " + e.getMessage());
            e.printStackTrace();
        }    
    }
    @Override
    public List<Pagos> listar(){
        List<Pagos> pagos = new ArrayList<>();
        String sql = "SELECT * FROM pago ORDER BY fecha DESC";
        try (Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                pagos.add(mapResultSetToPago(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar pagos: " + e.getMessage());
            e.printStackTrace();
        }
        return pagos;
    }
    @Override
    public void actualizar(int id, Pagos pagoActualizado ){
        System.out.println("Use actualizarPorId en lugar de actualizar(int, Pagos).");
    }
    @Override
    public void eliminar(int id){
        System.out.println("Use eliminarPorId en lugar de eliminar(int).");
    }
    //metodos aparte
    public Pagos getById(String id){
       String sql = "SELECT * FROM pago WHERE ID = ?";
       try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPago(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en getById: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    public boolean actualizarPorId(String id, BigDecimal nuevoMonto, Pagos.MetodoPago nuevoMetodo, Pagos.EstadoPago nuevoEstado){
        String selectSql = "SELECT monto, metodo, estado FROM pago WHERE ID = ?";
        String updateSql = "UPDATE pago SET monto = ?, metodo = ?, estado = ? WHERE ID = ?";

        try (PreparedStatement sel = conn.prepareStatement(selectSql)) {
            sel.setString(1, id);
            try (ResultSet rs = sel.executeQuery()) {
                if (!rs.next()) {
                
                    return false;
                }

            
                BigDecimal montoActual = rs.getBigDecimal("monto");
                String metodoActualStr = rs.getString("metodo");
                String estadoActualStr = rs.getString("estado");

            
                Pagos.MetodoPago metodoActual = null;
                Pagos.EstadoPago estadoActual = null;
                try {
                    if (metodoActualStr != null) metodoActual = Pagos.MetodoPago.valueOf(metodoActualStr);
                } catch (IllegalArgumentException ignored) { /* valor extraño en BD */ }
                try {
                    if (estadoActualStr != null) estadoActual = Pagos.EstadoPago.valueOf(estadoActualStr);
                } catch (IllegalArgumentException ignored) { /* valor extraño en BD */ }

            
                BigDecimal montoParaGuardar = (nuevoMonto != null) ? nuevoMonto : montoActual;
                String metodoParaGuardar = (nuevoMetodo != null) ? nuevoMetodo.name()
                        : (metodoActual != null ? metodoActual.name() : null);
                String estadoParaGuardar = (nuevoEstado != null) ? nuevoEstado.name()
                        : (estadoActual != null ? estadoActual.name() : null);

            
                try (PreparedStatement upd = conn.prepareStatement(updateSql)) {
                    if (montoParaGuardar != null) upd.setBigDecimal(1, montoParaGuardar);
                    else upd.setNull(1, Types.NUMERIC);

                    if (metodoParaGuardar != null) upd.setString(2, metodoParaGuardar);
                    else upd.setNull(2, Types.VARCHAR);

                    if (estadoParaGuardar != null) upd.setString(3, estadoParaGuardar);
                    else upd.setNull(3, Types.VARCHAR);

                    upd.setString(4, id);

                    int filas = upd.executeUpdate();
                    return filas > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al actualizar pago: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

    }
    public boolean eliminarPorId(String id){
        String sql = "UPDATE pago SET estado=? WHERE ID=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, Pagos.EstadoPago.ANULADO.name());
            stmt.setString(2, id);
            int filas = stmt.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            System.err.println("Error al anular pago: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    public boolean eliminarFisicoPorId(String id){
        String sql = "DELETE FROM pago WHERE ID=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            int filas = stmt.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar físicamente el pago: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    private Pagos mapResultSetToPago(ResultSet rs) throws SQLException {
        String id = rs.getString("ID");
        String pacienteID = rs.getString("pacienteID");
        String odontologoID = rs.getString("odontologoID");

    
        LocalDateTime fecha = null;
        String fechaStr = rs.getString("fecha");
        if (fechaStr != null && !fechaStr.isBlank()) {
            try {
                fecha = LocalDateTime.parse(fechaStr,formato); 
            } catch (Exception e) {
                try {
                    
                    LocalDate d = LocalDate.parse(fechaStr);
                    fecha = d.atStartOfDay();
                } catch (Exception ignored) {
                    fecha = null; 
                }
            }
        }

        BigDecimal monto = rs.getBigDecimal("monto");
        Pagos.MetodoPago metodo = Pagos.MetodoPago.valueOf(rs.getString("metodo"));
        Pagos.EstadoPago estado = Pagos.EstadoPago.valueOf(rs.getString("estado"));

        return new Pagos(
                id,
                pacienteID,
                odontologoID,
                fecha,   
                monto,
                metodo,
                estado
        );
    }
    //metodo agregado para la TUI
    public boolean actualizarOdontologoPorId(String pagoId, String nuevoOdontologoId) {
    String sql = "UPDATE pago SET odontologoID = ? WHERE ID = ?";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        if (nuevoOdontologoId != null) ps.setString(1, nuevoOdontologoId);
        else ps.setNull(1, Types.VARCHAR);
        ps.setString(2, pagoId);
        int filas = ps.executeUpdate();
        return filas > 0;
    } catch (SQLException e) {
        System.err.println("Error al actualizar odontólogo del pago: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
    }

}