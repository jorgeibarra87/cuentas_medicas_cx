package cuentas_medicas_cx.service.impl;

import cuentas_medicas_cx.model.dto.external.DinamicaCirugiaDTO;
import cuentas_medicas_cx.service.DinamicaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DinamicaServiceImpl implements DinamicaService {

    @Value("${spring.datasource.external.jdbc-url}")
    private String externalUrl;

    @Value("${spring.datasource.external.username}")
    private String externalUser;

    @Value("${spring.datasource.external.password}")
    private String externalPassword;

    private DataSource getExternalDataSource() {
        SQLServerDataSource ds = new SQLServerDataSource();
        ds.setURL(externalUrl);
        ds.setUser(externalUser);
        ds.setPassword(externalPassword);
        return ds;
    }

    private static final String QUERY_ENDOSCOPIA_1 = """
        SELECT 'ENDOSCOPIA' AS TIPO,
            GENPACIEN.PACNUMDOC AS PACIENTE,
            ADNINGRESO.AINCONSEC AS INGRESO,
            RTRIM(ISNULL(GENPACIEN.PACPRINOM, '')) + ' ' + RTRIM(ISNULL(GENPACIEN.PACSEGNOM, '')) + ' ' +
            RTRIM(ISNULL(GENPACIEN.PACPRIAPE, '')) + ' ' + RTRIM(ISNULL(GENPACIEN.PACSEGAPE, '')) AS NOMBRES,
            GENSERIPS.SIPCODCUP AS CUPS,
            GENSERIPS.SIPCODIGO AS PROCED_COD,
            ISNULL(X_QXGRUPOQX.GRUPOQZ, '') AS GRUPOQXCOD,
            GENSERIPS.SIPNOMBRE AS INTERVENCION,
            ISNULL(GENESPECI.geedescri, 'ENDOSCOPIA') AS ESPECIALIDAD,
            GMENOMCOM AS MEDICO,
            CONVERT(VARCHAR(16), HCNFOLIO.HCFECFOL, 121) AS FECHA_SOLICITUD,
            CONVERT(VARCHAR(10), HCRFECCONF, 126) AS FECHA_CARGUE,
            RIGHT(CONVERT(VARCHAR(8), HCRFECCONF, 108), 8) AS HORA_CARGUE,
            CONVERT(VARCHAR(16), hcrfecres, 121) AS FECHA_RESULTADO,
            x_confac.CONCEPTO AS REGIMEN,
            gdenombre AS ENTIDAD
        FROM hcnfolio
        INNER JOIN genpacien ON genpacien.oid = hcnfolio.genpacien
        INNER JOIN adningreso ON adningreso.oid = hcnfolio.adningreso
        INNER JOIN HCNSOLPQX ON HCNSOLPQX.hcnfolio = hcnfolio.oid
        INNER JOIN genserips ON genserips.oid = HCNSOLPQX.genserips
        INNER JOIN HCNRESPQX ON HCNSOLPQX.HCNRESPQX = HCNRESPQX.oid
        INNER JOIN GENARESER ON GENARESER.OID = genserips.GENARESER1
        INNER JOIN GENMEDICO ON GENMEDICO.OID = HCNRESPQX.GENMEDICO
        INNER JOIN gentercer ON gentercer.oid = genmedico.gentercer
        INNER JOIN GENESPECI ON HCNFOLIO.GENESPECI = GENESPECI.OID
        INNER JOIN GENDETCON ON GENDETCON.OID = adningreso.GENDETCON
        INNER JOIN x_confac ON x_confac.codigo = gendetcon.gdeconfac
        LEFT JOIN X_QXGRUPOQX ON GENSERIPS.SIPCODIGO = X_QXGRUPOQX.CODIGO
        WHERE hcrfecres BETWEEN ?1 AND ?2
          AND HCRCONFIR = 1
          AND sipcodigo IN ('441302','441303','422001','422002','422003','893902','893903','18313','18314','18316','518501','430102','430103','18319',
          '18320','451201','451202','451206','18323','18324','492101','18501','18502','452303','452301','452305','468501','482701','482701',
          '18510','18511','452401','18514','18515','431002','446202','881319','881317','521004','521401','422003','434101','434102','434104')
        """;

    private static final String QUERY_ENDOSCOPIA_2 = """
        SELECT 'ENDOSCOPIA' AS TIPO,
            GENPACIEN.PACNUMDOC AS PACIENTE,
            ADNINGRESO.AINCONSEC AS INGRESO,
            RTRIM(ISNULL(GENPACIEN.PACPRINOM, '')) + ' ' + RTRIM(ISNULL(GENPACIEN.PACSEGNOM, '')) + ' ' +
            RTRIM(ISNULL(GENPACIEN.PACPRIAPE, '')) + ' ' + RTRIM(ISNULL(GENPACIEN.PACSEGAPE, '')) AS NOMBRES,
            GENSERIPS.SIPCODCUP AS CUPS,
            GENSERIPS.SIPCODIGO AS PROCED_COD,
            '' AS GRUPOQXCOD,
            GENSERIPS.SIPNOMBRE AS INTERVENCION,
            'ENDOSCOPIA' AS ESPECIALIDAD,
            GMENOMCOM AS MEDICO,
            CONVERT(VARCHAR(16), HCNSOLEXA.HCSFECSOL, 121) AS FECHA_SOLICITUD,
            CONVERT(VARCHAR(10), HCRFECCONF, 126) AS FECHA_CARGUE,
            RIGHT(CONVERT(VARCHAR(8), HCRFECCONF, 108), 8) AS HORA_CARGUE,
            CONVERT(VARCHAR(16), hcrfecres, 121) AS FECHA_RESULTADO,
            x_confac.CONCEPTO AS REGIMEN,
            gdenombre AS ENTIDAD
        FROM genpacien
        INNER JOIN adningreso ON adningreso.genpacien = genpacien.oid
        INNER JOIN HCNSOLEXA ON HCNSOLEXA.adningreso = adningreso.oid
        INNER JOIN genserips ON genserips.oid = HCNSOLEXA.genserips
        INNER JOIN HCNRESEXA ON HCNSOLEXA.HCNRESEXA = HCNRESEXA.oid
        INNER JOIN GENARESER ON GENARESER.OID = genserips.GENARESER1
        INNER JOIN GENMEDICO ON GENMEDICO.OID = HCNRESEXA.GENMEDICO
        INNER JOIN gentercer ON gentercer.oid = genmedico.gentercer
        INNER JOIN GENDETCON ON GENDETCON.OID = adningreso.GENDETCON
        INNER JOIN x_confac ON x_confac.codigo = gendetcon.gdeconfac
        WHERE hcrfecres BETWEEN ?1 AND ?2
          AND HCRCONFIR = 1
          AND (sipcodigo IN ('999','21511','21332') OR sipcodigo LIKE '999-%')
          AND GENMEDICO.GMECODIGO NOT LIKE 'RAD%'
        """;

    private static final String QUERY_CIRUGIA = """
        SELECT 'CIRUGIA' AS TIPO,
            GENPACIEN.PACNUMDOC AS PACIENTE,
            ADNINGRESO.AINCONSEC AS INGRESO,
            RTRIM(ISNULL(GENPACIEN.PACPRINOM, '')) + ' ' + RTRIM(ISNULL(GENPACIEN.PACSEGNOM, '')) + ' ' +
            RTRIM(ISNULL(GENPACIEN.PACPRIAPE, '')) + ' ' + RTRIM(ISNULL(GENPACIEN.PACSEGAPE, '')) AS NOMBRES,
            GENSERIPS.SIPCODCUP AS CUPS,
            GENSERIPS.SIPCODIGO AS PROCED_COD,
            ISNULL(X_QXGRUPOQX.GRUPOQZ, '') AS GRUPOQXCOD,
            GENSERIPS.SIPNOMBRE AS INTERVENCION,
            ISNULL(GENESPECI.geedescri, '') AS ESPECIALIDAD,
            GMENOMCOM AS MEDICO,
            '' AS FECHA_SOLICITUD,
            CONVERT(VARCHAR(10), HCNFOLIO.HCFECFOL, 126) AS FECHA_CARGUE,
            RIGHT(CONVERT(VARCHAR(8), HCNFOLIO.HCFECFOL, 108), 8) AS HORA_CARGUE,
            '' AS FECHA_RESULTADO,
            x_confac.CONCEPTO AS REGIMEN,
            gdenombre AS ENTIDAD
        FROM HCMHC50
        INNER JOIN HCNFOLIO ON HCMHC50.HCNFOLIO = HCNFOLIO.OID
        INNER JOIN GENPACIEN ON HCNFOLIO.GENPACIEN = GENPACIEN.OID
        INNER JOIN HCNTIPHIS ON HCNFOLIO.HCNTIPHIS = HCNTIPHIS.OID
        INNER JOIN GENESPECI ON HCNFOLIO.GENESPECI = GENESPECI.OID
        INNER JOIN HCNQXEPAC ON HCNFOLIO.OID = HCNQXEPAC.HCNFOLIO
        INNER JOIN GENSERIPS ON HCNQXEPAC.GENSERIPS = GENSERIPS.OID
        INNER JOIN ADNINGRESO ON HCNFOLIO.ADNINGRESO = ADNINGRESO.OID
        INNER JOIN GENDETCON ON GENDETCON.OID = adningreso.GENDETCON
        INNER JOIN GENMEDICO ON GENMEDICO.OID = HCNFOLIO.GENMEDICO
        INNER JOIN x_confac ON x_confac.codigo = gendetcon.gdeconfac
        LEFT JOIN X_QXGRUPOQX ON GENSERIPS.SIPCODIGO = X_QXGRUPOQX.CODIGO
        WHERE HCNFOLIO.HCFECFOL BETWEEN ?1 AND ?2
        """;

    private static final String QUERY_RESCATE = """
        SELECT 'RESCATE ORGANOS' AS TIPO,
            GENPACIEN.PACNUMDOC AS PACIENTE,
            ADNINGRESO.AINCONSEC AS INGRESO,
            RTRIM(ISNULL(GENPACIEN.PACPRINOM, '')) + ' ' + RTRIM(ISNULL(GENPACIEN.PACSEGNOM, '')) + ' ' +
            RTRIM(ISNULL(GENPACIEN.PACPRIAPE, '')) + ' ' + RTRIM(ISNULL(GENPACIEN.PACSEGAPE, '')) AS NOMBRES,
            GENSERIPS.SIPCODCUP AS CUPS,
            GENSERIPS.SIPCODIGO AS PROCED_COD,
            ISNULL(X_QXGRUPOQX.GRUPOQZ, '') AS GRUPOQXCOD,
            GENSERIPS.SIPNOMBRE AS INTERVENCION,
            ISNULL(GENESPECI.geedescri, '') AS ESPECIALIDAD,
            GMENOMCOM AS MEDICO,
            '' AS FECHA_SOLICITUD,
            CONVERT(VARCHAR(10), HCNFOLIO.HCFECFOL, 126) AS FECHA_CARGUE,
            RIGHT(CONVERT(VARCHAR(8), HCNFOLIO.HCFECFOL, 108), 8) AS HORA_CARGUE,
            '' AS FECHA_RESULTADO,
            x_confac.CONCEPTO AS REGIMEN,
            gdenombre AS ENTIDAD
        FROM HCMHC101
        INNER JOIN HCNFOLIO ON HCMHC101.HCNFOLIO = HCNFOLIO.OID
        INNER JOIN GENPACIEN ON HCNFOLIO.GENPACIEN = GENPACIEN.OID
        INNER JOIN HCNTIPHIS ON HCNFOLIO.HCNTIPHIS = HCNTIPHIS.OID
        INNER JOIN GENESPECI ON HCNFOLIO.GENESPECI = GENESPECI.OID
        INNER JOIN HCNQXEPAC ON HCNFOLIO.OID = HCNQXEPAC.HCNFOLIO
        INNER JOIN GENSERIPS ON HCNQXEPAC.GENSERIPS = GENSERIPS.OID
        INNER JOIN ADNINGRESO ON HCNFOLIO.ADNINGRESO = ADNINGRESO.OID
        INNER JOIN GENDETCON ON GENDETCON.OID = adningreso.GENDETCON
        INNER JOIN GENMEDICO ON GENMEDICO.OID = HCNFOLIO.GENMEDICO
        INNER JOIN x_confac ON x_confac.codigo = gendetcon.gdeconfac
        LEFT JOIN X_QXGRUPOQX ON GENSERIPS.SIPCODIGO = X_QXGRUPOQX.CODIGO
        WHERE HCNFOLIO.HCFECFOL BETWEEN ?1 AND ?2
        """;

    @Override
    public List<DinamicaCirugiaDTO> obtenerCirugiasPorFechas(String fechaInicio, String fechaFin) {
        log.info("Consultando cirugías desde Dinámica entre {} y {}", fechaInicio, fechaFin);
        List<DinamicaCirugiaDTO> resultados = new ArrayList<>();
        
        // Convertir formato dd/mm/yyyy a formato SQL Server
        String[] partsInicio = fechaInicio.split("/");
        String[] partsFin = fechaFin.split("/");
        
        String fechaInicioSQL = partsInicio[2] + "-" + partsInicio[1] + "-" + partsInicio[0];
        String fechaFinSQL = partsFin[2] + "-" + partsFin[1] + "-" + partsFin[0];
        
        String sql = QUERY_ENDOSCOPIA_1.replace("?1", "'" + fechaInicioSQL + "'").replace("?2", "'" + fechaFinSQL + "'") + " UNION ALL " 
                   + QUERY_ENDOSCOPIA_2.replace("?1", "'" + fechaInicioSQL + "'").replace("?2", "'" + fechaFinSQL + "'") + " UNION ALL " 
                   + QUERY_CIRUGIA.replace("?1", "'" + fechaInicioSQL + "'").replace("?2", "'" + fechaFinSQL + "'") + " UNION ALL " 
                   + QUERY_RESCATE.replace("?1", "'" + fechaInicioSQL + "'").replace("?2", "'" + fechaFinSQL + "'")
                   + " ORDER BY Ingreso, Fecha_Cargue";
        
        try (Connection conn = getExternalDataSource().getConnection();
             Statement stmt = conn.createStatement()) {
            
            log.info("Ejecutando SQL: {}", sql);
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
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
                resultados.add(dto);
            }
            log.info("Se encontraron {} registros", resultados.size());
            
        } catch (Exception e) {
            log.error("Error al consultar Dinámica", e);
            throw new RuntimeException("Error al consultar Dinámica: " + e.getMessage(), e);
        }
        
        return resultados;
    }

    @Override
    public List<DinamicaCirugiaDTO> obtenerCirugiasPorFechas(String fechaInicio) {
        return obtenerCirugiasPorFechas(fechaInicio, fechaInicio);
    }
}