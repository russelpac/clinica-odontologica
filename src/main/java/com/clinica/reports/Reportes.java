package com.clinica.reports;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Reportes {

    /**
     * Genera un PDF con todos los pacientes (una fila por paciente).
     * @param conn conexión JDBC ya abierta
     * @param outputPath ruta de salida del PDF (ej: "reports/pacientes.pdf")
     * @throws Exception en caso de fallo (SQL o IO)
     */
    public static void generarInformePacientes(Connection conn, String outputPath) throws Exception {
        // Consulta que obtiene columnas relevantes; ajústala a tu esquema si nombres difieren
        String sql = "SELECT ID, nombres, apellidos, CI, numeroContacto, fechaNacimiento, alergias, activo FROM paciente ORDER BY nombres, apellidos";

        // Crear carpeta contenedora si no existe
        File outFile = new File(outputPath);
        File parent = outFile.getParentFile();
        if (parent != null && !parent.exists()) parent.mkdirs();

        // Documento PDF
        Document document = new Document(PageSize.A4.rotate(), 36, 36, 54, 36); // landscape opcional
        try (FileOutputStream fos = new FileOutputStream(outFile);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            PdfWriter.getInstance(document, fos);
            document.open();

            // Título
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Paragraph title = new Paragraph("Informe de Pacientes", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(12);
            document.add(title);

            // Info de generación
            Font metaFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Paragraph meta = new Paragraph("Generado: " + java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), metaFont);
            meta.setAlignment(Element.ALIGN_RIGHT);
            meta.setSpacingAfter(8);
            document.add(meta);

            // Tabla: ajusta columnas según lo que quieres mostrar
            PdfPTable table = new PdfPTable(new float[]{2f, 3f, 3f, 2f, 2f, 2f, 4f, 1f});
            table.setWidthPercentage(100f);
            // Encabezados
            Font head = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
            table.addCell(new Phrase("ID", head));
            table.addCell(new Phrase("Nombres", head));
            table.addCell(new Phrase("Apellidos", head));
            table.addCell(new Phrase("CI", head));
            table.addCell(new Phrase("Teléfono", head));
            table.addCell(new Phrase("FNac", head));
            table.addCell(new Phrase("Alergias", head));
            table.addCell(new Phrase("Activo", head));

            // Datos
            Font rowFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            DateTimeFormatter df = DateTimeFormatter.ISO_LOCAL_DATE;

            while (rs.next()) {
                String id = rs.getString("ID");
                String nombres = rs.getString("nombres");
                String apellidos = rs.getString("apellidos");
                String ci = rs.getString("CI");
                String telefono = rs.getString("numeroContacto");
                String fnStr = rs.getString("fechaNacimiento");
                String fechaFormateada = "";
                if (fnStr != null && !fnStr.isBlank()) {
                    try {
                        // asumimos formato ISO yyyy-MM-dd
                        LocalDate d = LocalDate.parse(fnStr);
                        fechaFormateada = d.format(df);
                    } catch (Exception ex) {
                        fechaFormateada = fnStr;
                    }
                }
                String alergias = rs.getString("alergias");
                boolean activo = rs.getInt("activo") == 1;

                table.addCell(new Phrase(shortId(id), rowFont));
                table.addCell(new Phrase(nullSafe(nombres), rowFont));
                table.addCell(new Phrase(nullSafe(apellidos), rowFont));
                table.addCell(new Phrase(nullSafe(ci), rowFont));
                table.addCell(new Phrase(nullSafe(telefono), rowFont));
                table.addCell(new Phrase(fechaFormateada, rowFont));
                table.addCell(new Phrase(nullSafe(alergias), rowFont));
                table.addCell(new Phrase(activo ? "Sí" : "No", rowFont));
            }

            document.add(table);
            document.close();
        } catch (Exception e) {
            if (document.isOpen()) document.close();
            throw e;
        }
    }

    private static String nullSafe(String s) {
        return s == null ? "" : s;
    }

    private static String shortId(String id) {
        if (id == null) return "";
        return id.length() > 8 ? id.substring(0, 8) : id;
    }
}

