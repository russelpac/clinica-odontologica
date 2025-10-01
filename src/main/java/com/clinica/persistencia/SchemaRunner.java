package com.clinica.persistencia;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Statement;
import java.sql.Connection;
public class SchemaRunner {
    public static void runSchema() throws Exception{
        try(Connection conn = ConexionBD.getConnection()){
            conn.setAutoCommit(false);
            try(InputStream in = SchemaRunner.class.getResourceAsStream("/sql/schema.sql")){
                if(in == null){
                    throw new FileNotFoundException("No se encontro /sql/schema.sql en resources");
                }
                String sql = new String(in.readAllBytes(), StandardCharsets.UTF_8);
                String[] statements = sql.split(";");
                try(Statement stmt = conn.createStatement()){
                    for(String raw : statements){
                        String s = raw.trim();
                        if(s.isEmpty()) continue;
                        String[] lines = s.split("\\r?\\n");
                        StringBuilder sb = new StringBuilder();
                        for(String line : lines){
                            String trimmed = line.trim();
                            if(trimmed.startsWith("--")) continue;
                            sb.append(line).append("\n");
                        }
                        String toExec = sb.toString().trim();
                        if(toExec.isEmpty())continue;
                        stmt.execute(toExec);
                    }
                }
                conn.commit();
                System.out.println("Schema ejecutado correctamente.");
            }catch(Exception e){
            
                conn.rollback();
                throw e;
            }
        }
    }
    public static void main (String[] args){
        try{
            runSchema();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
