package cuentas_medicas_cx.service.impl;

import cuentas_medicas_cx.model.dto.external.DinamicaCirugiaDTO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DinamicaRowMapper implements RowMapper<DinamicaCirugiaDTO> {

    @Override
    public DinamicaCirugiaDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        DinamicaCirugiaDTO dto = new DinamicaCirugiaDTO();
        dto.setTipo(rs.getString("TIPO"));
        dto.setPaciente(rs.getString("PACIENTE"));
        dto.setNombres(rs.getString("NOMBRES"));
        dto.setIngreso(rs.getString("INGRESO"));
        dto.setCups(rs.getString("CUPS"));
        dto.setProcedCod(rs.getString("PROCED_COD"));
        dto.setGrupoqxCod(rs.getString("GRUPOQXCOD"));
        dto.setIntervencion(rs.getString("INTERVENCION"));
        dto.setEspecialidad(rs.getString("ESPECIALIDAD"));
        dto.setMedico(rs.getString("MEDICO"));
        dto.setFechaSolicitud(rs.getString("FECHA_SOLICITUD"));
        dto.setFechaCargue(rs.getString("FECHA_CARGUE"));
        dto.setHoraCargue(rs.getString("HORA_CARGUE"));
        dto.setFechaResultado(rs.getString("FECHA_RESULTADO"));
        dto.setRegimen(rs.getString("REGIMEN"));
        dto.setEntidad(rs.getString("ENTIDAD"));
        return dto;
    }
}