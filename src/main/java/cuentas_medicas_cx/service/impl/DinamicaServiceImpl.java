package cuentas_medicas_cx.service.impl;

import cuentas_medicas_cx.model.dto.external.DinamicaCirugiaDTO;
import cuentas_medicas_cx.service.DinamicaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    private static final String QUERY = """
SELECT
    TIPO,
    PACIENTE,
    INGRESO,
    NOMBRES,
    CUPS,
    PROCED_COD,
    GRUPOQXCOD,
    INTERVENCION,
    ESPECIALIDAD,
    MEDICO,
    FECHA_SOLICITUD,
    FECHA_CARGUE,
    HORA_CARGUE,
    FECHA_RESULTADO,
    REGIMEN,
    ENTIDAD,
    ANESTESIOLOGO,
    AYUDANTE1,
    AYUDANTE2
FROM (

SELECT
    'ENDOSCOPIA' AS TIPO,
    GENPACIEN.PACNUMDOC AS PACIENTE,
    ADNINGRESO.AINCONSEC AS INGRESO,

    RTRIM(GENPACIEN.PACPRINOM) + ' ' +
    RTRIM(GENPACIEN.PACSEGNOM) + ' ' +
    RTRIM(GENPACIEN.PACPRIAPE) + ' ' +
    RTRIM(GENPACIEN.PACSEGAPE) AS NOMBRES,

    GENSERIPS.SIPCODCUP AS CUPS,
    GENSERIPS.SIPCODIGO AS PROCED_COD,

    CAST(X_QXGRUPOQX.GRUPOQZ AS NVARCHAR(200)) AS GRUPOQXCOD,

    GENSERIPS.SIPNOMBRE AS INTERVENCION,
    GENESPECI.GEEDESCRI AS ESPECIALIDAD,
    GENMEDICO.GMENOMCOM AS MEDICO,

    CONVERT(CHAR(16), HCNFOLIO.HCFECFOL, 121) AS FECHA_SOLICITUD,
    CONVERT(CHAR(16), HCNRESPQX.HCRFECCONF, 106) AS FECHA_CARGUE,
    RIGHT(CONVERT(VARCHAR(8), HCNRESPQX.HCRFECCONF, 108), 8) AS HORA_CARGUE,
    CONVERT(CHAR(16), HCNRESPQX.HCRFECRES, 121) AS FECHA_RESULTADO,

    X_CONFAC.CONCEPTO AS REGIMEN,
    GENDETCON.GDENOMBRE AS ENTIDAD,

    CAST(HCMHC50.HCCM22N42 AS NVARCHAR(200)) AS ANESTESIOLOGO,
    CAST(HCMHC50.HCCM10N55 AS NVARCHAR(200)) AS AYUDANTE1,
    CAST(HCMHC50.DESCHCCM22N41 AS NVARCHAR(200)) AS AYUDANTE2

FROM HCNFOLIO
INNER JOIN GENPACIEN
    ON GENPACIEN.OID = HCNFOLIO.GENPACIEN

INNER JOIN ADNINGRESO
    ON ADNINGRESO.OID = HCNFOLIO.ADNINGRESO

INNER JOIN HCNSOLPQX
    ON HCNSOLPQX.HCNFOLIO = HCNFOLIO.OID

INNER JOIN GENSERIPS
    ON GENSERIPS.OID = HCNSOLPQX.GENSERIPS

INNER JOIN HCNRESPQX
    ON HCNSOLPQX.HCNRESPQX = HCNRESPQX.OID

INNER JOIN GENARESER
    ON GENARESER.OID = GENSERIPS.GENARESER1

INNER JOIN GENMEDICO
    ON GENMEDICO.OID = HCNRESPQX.GENMEDICO

INNER JOIN GENTERCER
    ON GENTERCER.OID = GENMEDICO.GENTERCER

INNER JOIN GENESPECI
    ON HCNFOLIO.GENESPECI = GENESPECI.OID

INNER JOIN GENDETCON
    ON GENDETCON.OID = ADNINGRESO.GENDETCON

INNER JOIN X_CONFAC
    ON X_CONFAC.CODIGO = GENDETCON.GDECONFAC

INNER JOIN GENMUNICI
    ON GENMUNICI.OID = GENPACIEN.DGNMUNICIPIO

LEFT JOIN HCMHC50
    ON HCNFOLIO.OID = HCMHC50.OID

LEFT JOIN X_QXGRUPOQX
    ON GENSERIPS.SIPCODIGO = X_QXGRUPOQX.CODIGO

WHERE HCNRESPQX.HCRFECRES >= ?
AND HCNRESPQX.HCRFECRES < ?
AND HCNRESPQX.HCRCONFIR = 1

AND GENSERIPS.SIPCODIGO IN (
'441302','441303','422001','422002','422003',
'893902','893903','18313','18314','18316',
'518501','430102','430103','18319','18320',
'451201','451202','451206','18323','18324',
'492101','18501','18502','452303','452301',
'452305','468501','482701','18510','18511',
'452401','18514','18515','431002','446202',
'881319','881317','521004','521401',
'434101','434102','434104'
)

UNION ALL

SELECT
    'ENDOSCOPIA',

    GENPACIEN.PACNUMDOC,
    ADNINGRESO.AINCONSEC,

    RTRIM(GENPACIEN.PACPRINOM) + ' ' +
    RTRIM(GENPACIEN.PACSEGNOM) + ' ' +
    RTRIM(GENPACIEN.PACPRIAPE) + ' ' +
    RTRIM(GENPACIEN.PACSEGAPE),

    GENSERIPS.SIPCODCUP,
    GENSERIPS.SIPCODIGO,

    CAST('' AS NVARCHAR(200)),

    GENSERIPS.SIPNOMBRE,
    'ENDOSCOPIA',
    GENMEDICO.GMENOMCOM,

    CONVERT(CHAR(16), HCNSOLEXA.HCSFECSOL, 121),
    CONVERT(CHAR(16), HCNRESEXA.HCRFECCONF, 106),
    RIGHT(CONVERT(VARCHAR(8), HCNRESEXA.HCRFECCONF, 108), 8),
    CONVERT(CHAR(16), HCNRESEXA.HCRFECRES, 121),

    X_CONFAC.CONCEPTO,
    GENDETCON.GDENOMBRE,

    CAST(HCMHC50.HCCM22N42 AS NVARCHAR(200)),
    CAST(HCMHC50.HCCM10N55 AS NVARCHAR(200)),
    CAST(HCMHC50.DESCHCCM22N41 AS NVARCHAR(200))

FROM GENPACIEN

INNER JOIN ADNINGRESO
    ON ADNINGRESO.GENPACIEN = GENPACIEN.OID

INNER JOIN HCNSOLEXA
    ON HCNSOLEXA.ADNINGRESO = ADNINGRESO.OID

INNER JOIN GENSERIPS
    ON GENSERIPS.OID = HCNSOLEXA.GENSERIPS

INNER JOIN HCNRESEXA
    ON HCNSOLEXA.HCNRESEXA = HCNRESEXA.OID

INNER JOIN GENARESER
    ON GENARESER.OID = GENSERIPS.GENARESER1

INNER JOIN GENMEDICO
    ON GENMEDICO.OID = HCNRESEXA.GENMEDICO

INNER JOIN GENTERCER
    ON GENTERCER.OID = GENMEDICO.GENTERCER

INNER JOIN GENDETCON
    ON GENDETCON.OID = ADNINGRESO.GENDETCON

INNER JOIN X_CONFAC
    ON X_CONFAC.CODIGO = GENDETCON.GDECONFAC

INNER JOIN GENMUNICI
    ON GENMUNICI.OID = GENPACIEN.DGNMUNICIPIO

INNER JOIN HCNFOLIO
    ON ADNINGRESO.OID = HCNFOLIO.ADNINGRESO
    AND HCNSOLEXA.HCNFOLIO1 = HCNFOLIO.OID
    AND HCNSOLEXA.HCNFOLIO = HCNFOLIO.OID
    AND HCNRESEXA.HCNFOLIO = HCNFOLIO.OID

INNER JOIN HCMHC50
    ON HCNFOLIO.OID = HCMHC50.OID

WHERE HCNRESEXA.HCRFECRES >= ?
AND HCNRESEXA.HCRFECRES < ?
AND HCNRESEXA.HCRCONFIR = 1

AND (
    GENSERIPS.SIPCODIGO IN ('999','21511','21332')
    OR GENSERIPS.SIPCODIGO LIKE '999-%'
)

AND GENMEDICO.GMECODIGO NOT LIKE 'RAD%'

UNION ALL

SELECT
    'CIRUGIA' AS TIPO,
    GENPACIEN.PACNUMDOC AS PACIENTE,
    ADNINGRESO.AINCONSEC AS INGRESO,

    RTRIM(GENPACIEN.PACPRINOM) + ' ' +
    RTRIM(GENPACIEN.PACSEGNOM) + ' ' +
    RTRIM(GENPACIEN.PACPRIAPE) + ' ' +
    RTRIM(GENPACIEN.PACSEGAPE) AS NOMBRES,

    GENSERIPS.SIPCODCUP AS CUPS,
    GENSERIPS.SIPCODIGO AS PROCED_COD,

    CAST(X_QXGRUPOQX.GRUPOQZ AS NVARCHAR(200)) AS GRUPOQXCOD,

    GENSERIPS.SIPNOMBRE AS INTERVENCION,
    GENESPECI.GEEDESCRI AS ESPECIALIDAD,
    GENMEDICO.GMENOMCOM AS MEDICO,

    '' AS FECHA_SOLICITUD,
    CONVERT(CHAR(16), HCNFOLIO.HCFECFOL, 106) AS FECHA_CARGUE,
    RIGHT(CONVERT(VARCHAR(8), HCNFOLIO.HCFECFOL, 108), 8) AS HORA_CARGUE,
    '' AS FECHA_RESULTADO,

    X_CONFAC.CONCEPTO AS REGIMEN,
    GENDETCON.GDENOMBRE AS ENTIDAD,

    CAST(HCMHC50.DESCHCCM22N42 AS NVARCHAR(200)) AS ANESTESIOLOGO,
    HCMHC50.HCCM10N55 AS AYUDANTE1,
    HCMHC50.DESCHCCM22N41 AS AYUDANTE2

FROM HCMHC50
INNER JOIN HCNFOLIO
    ON HCMHC50.HCNFOLIO = HCNFOLIO.OID

INNER JOIN GENPACIEN
    ON HCNFOLIO.GENPACIEN = GENPACIEN.OID

INNER JOIN GENMUNICI
    ON GENMUNICI.OID = GENPACIEN.DGNMUNICIPIO

INNER JOIN HCNTIPHIS
    ON HCNFOLIO.HCNTIPHIS = HCNTIPHIS.OID

INNER JOIN GENESPECI
    ON HCNFOLIO.GENESPECI = GENESPECI.OID

INNER JOIN HCNQXEPAC
    ON HCNFOLIO.OID = HCNQXEPAC.HCNFOLIO

INNER JOIN GENSERIPS
    ON HCNQXEPAC.GENSERIPS = GENSERIPS.OID

INNER JOIN ADNINGRESO
    ON HCNFOLIO.ADNINGRESO = ADNINGRESO.OID

INNER JOIN GENDETCON
    ON GENDETCON.OID = ADNINGRESO.GENDETCON

INNER JOIN GENMEDICO
    ON GENMEDICO.OID = HCNFOLIO.GENMEDICO

INNER JOIN X_CONFAC
    ON X_CONFAC.CODIGO = GENDETCON.GDECONFAC

LEFT JOIN X_QXGRUPOQX
    ON GENSERIPS.SIPCODIGO = X_QXGRUPOQX.CODIGO

WHERE HCNFOLIO.HCFECFOL >= ?
AND HCNFOLIO.HCFECFOL < ?

UNION ALL

SELECT
    'RESCATE ORGANOS' AS TIPO,
    GENPACIEN.PACNUMDOC AS PACIENTE,
    ADNINGRESO.AINCONSEC AS INGRESO,

    RTRIM(GENPACIEN.PACPRINOM) + ' ' +
    RTRIM(GENPACIEN.PACSEGNOM) + ' ' +
    RTRIM(GENPACIEN.PACPRIAPE) + ' ' +
    RTRIM(GENPACIEN.PACSEGAPE) AS NOMBRES,

    GENSERIPS.SIPCODCUP AS CUPS,
    GENSERIPS.SIPCODIGO AS PROCED_COD,

    CAST(X_QXGRUPOQX.GRUPOQZ AS NVARCHAR(200)) AS GRUPOQXCOD,

    GENSERIPS.SIPNOMBRE AS INTERVENCION,
    GENESPECI.GEEDESCRI AS ESPECIALIDAD,
    GENMEDICO.GMENOMCOM AS MEDICO,

    '' AS FECHA_SOLICITUD,
    CONVERT(CHAR(16), HCNFOLIO.HCFECFOL, 106) AS FECHA_CARGUE,
    RIGHT(CONVERT(VARCHAR(8), HCNFOLIO.HCFECFOL, 108), 8) AS HORA_CARGUE,
    '' AS FECHA_RESULTADO,

    X_CONFAC.CONCEPTO AS REGIMEN,
    GENDETCON.GDENOMBRE AS ENTIDAD,

    CAST(HCMHC101.HCCM02N07 AS NVARCHAR(200)) AS ANESTESIOLOGO,
    HCMHC101.HCCM02N03 AS AYUDANTE1,
    '""' AS AYUDANTE2

FROM HCMHC101
INNER JOIN HCNFOLIO
    ON HCMHC101.HCNFOLIO = HCNFOLIO.OID

INNER JOIN GENPACIEN
    ON HCNFOLIO.GENPACIEN = GENPACIEN.OID

INNER JOIN GENMUNICI
    ON GENMUNICI.OID = GENPACIEN.DGNMUNICIPIO

INNER JOIN HCNTIPHIS
    ON HCNFOLIO.HCNTIPHIS = HCNTIPHIS.OID

INNER JOIN GENESPECI
    ON HCNFOLIO.GENESPECI = GENESPECI.OID

INNER JOIN HCNQXEPAC
    ON HCNFOLIO.OID = HCNQXEPAC.HCNFOLIO

INNER JOIN GENSERIPS
    ON HCNQXEPAC.GENSERIPS = GENSERIPS.OID

INNER JOIN ADNINGRESO
    ON HCNFOLIO.ADNINGRESO = ADNINGRESO.OID

INNER JOIN GENDETCON
    ON GENDETCON.OID = ADNINGRESO.GENDETCON

INNER JOIN GENMEDICO
    ON GENMEDICO.OID = HCNFOLIO.GENMEDICO

INNER JOIN X_CONFAC
    ON X_CONFAC.CODIGO = GENDETCON.GDECONFAC

LEFT JOIN X_QXGRUPOQX
    ON GENSERIPS.SIPCODIGO = X_QXGRUPOQX.CODIGO

WHERE HCNFOLIO.HCFECFOL >= ?
AND HCNFOLIO.HCFECFOL < ?

) X

ORDER BY INGRESO, FECHA_CARGUE
""";

    @Override
    public List<DinamicaCirugiaDTO> obtenerCirugiasPorFechas(String fechaInicio, String fechaFin) {
        log.info("Consultando cirugías desde Dinámica entre {} y {}", fechaInicio, fechaFin);
        List<DinamicaCirugiaDTO> resultados = new ArrayList<>();

        String startDate = parsearFecha(fechaInicio);
        String endDate = sumarUnDia(fechaFin);
        log.info("Rango: >= {} AND < {}", startDate, endDate);

        String sql = QUERY.replace("?1", "'" + startDate + "'").replace("?2", "'" + endDate + "'");

        try (Connection conn = getExternalDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(QUERY)) {

            ps.setString(1, startDate);
            ps.setString(2, endDate);

            ps.setString(3, startDate);
            ps.setString(4, endDate);

            ps.setString(5, startDate);
            ps.setString(6, endDate);

            ps.setString(7, startDate);
            ps.setString(8, endDate);

            log.info("Ejecutando consulta dinámica");

            ResultSet rs = ps.executeQuery();

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
                dto.setAnestesiologo(rs.getString("ANESTESIOLOGO"));
                dto.setAyudante1(rs.getString("AYUDANTE1"));
                dto.setAyudante2(rs.getString("AYUDANTE2"));

                resultados.add(dto);
            }

            log.info("Total registros: {}", resultados.size());

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

    private String parsearFecha(String fecha) {
        if (fecha == null || fecha.isEmpty()) return fecha;
        try {
            if (fecha.contains("/")) {
                String[] p = fecha.split("/");
                return p[2] + p[1] + p[0];
            } else if (fecha.contains("-")) {
                LocalDate ld = LocalDate.parse(fecha);
                return ld.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            }
        } catch (Exception e) {
            log.warn("Error al parsear fecha: {}", fecha);
        }
        return fecha;
    }

    private String sumarUnDia(String fecha) {
        try {
            LocalDate ld;
            if (fecha.contains("/")) {
                String[] p = fecha.split("/");
                ld = LocalDate.of(Integer.parseInt(p[2]), Integer.parseInt(p[1]), Integer.parseInt(p[0]));
            } else {
                ld = LocalDate.parse(fecha);
            }
            return ld.plusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        } catch (Exception e) {
            log.warn("Error al sumar día: {}", fecha);
            return fecha;
        }
    }
}
