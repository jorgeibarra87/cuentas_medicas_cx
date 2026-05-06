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
        select 'ENDOSCOPIA' TIPO,
          GENPACIEN.PACNUMDOC As PACIENTE,
          ADNINGRESO.AINCONSEC As INGRESO,
          RTrim(GENPACIEN.PACPRINOM) + ' ' + RTrim(GENPACIEN.PACSEGNOM) + ' ' +
          RTrim(GENPACIEN.PACPRIAPE) + ' ' + RTrim(GENPACIEN.PACSEGAPE) As NOMBRES,
          GENSERIPS.SIPCODCUP As CUPS,
          GENSERIPS.SIPCODIGO As PROCED_COD,
            X_QXGRUPOQX.GRUPOQZ As GRUPOQXCOD,
          (GENSERIPS.SIPNOMBRE) As INTERVENCION,(geedescri) As ESPECIALIDAD,GMENOMCOM MEDICO,
            convert(char(16),HCNFOLIO.HCFECFOL,121) As FECHA_SOLICITUD,
          convert(char(16),HCRFECCONF,106) AS FECHA_CARGUE,
           RIGHT(CONVERT(DATETIME, HCRFECCONF, 108),8) AS HORA_CARGUE,
          convert(char(16),hcrfecres,121) as fecha_resultado,
            concepto regimen,gdenombre entidad
         from
         hcnfolio
         inner join genpacien on genpacien.oid =  hcnfolio.genpacien
         inner join adningreso on adningreso.oid =  hcnfolio.adningreso
         inner join HCNSOLPQX on HCNSOLPQX.hcnfolio = hcnfolio.oid
         inner join genserips on genserips.oid = HCNSOLPQX.genserips
         INNER join HCNRESPQX on HCNSOLPQX.HCNRESPQX =  HCNRESPQX.oid
        INNER JOIN GENARESER ON GENARESER.OID = genserips.GENARESER1
        INNER JOIN GENMEDICO ON GENMEDICO.OID = HCNRESPQX.GENMEDICO
        inner join gentercer on gentercer.oid = genmedico.gentercer
        Inner Join GENESPECI On HCNFOLIO.GENESPECI = GENESPECI.OID
        INNER JOIN GENDETCON ON GENDETCON.OID = adningreso.GENDETCON
          inner join x_confac on x_confac.codigo = gendetcon.gdeconfac
          inner join genmunici on genmunici.oid = genpacien.DGNMUNICIPIO
        left join X_QXGRUPOQX  On GENSERIPS.SIPCODIGO = X_QXGRUPOQX.CODIGO
          where hcrfecres >= ?1 And hcrfecres < ?2
         And sipcodigo in ('441302','441303','422001','422002','422003','893902','893903','18313','18314','18316','518501','430102','430103','18319',
         '18320','451201','451202','451206','18323','18324','492101','18501','18502','452303','452301','452305','468501','482701','482701',
         '18510','18511','452401','18514','18515','431002',
         '446202','881319','881317','521004','521401','422003','434101','434102','434104')

          union all

         select 'ENDOSCOPIA' TIPO,
            GENPACIEN.PACNUMDOC As PACIENTE,
          ADNINGRESO.AINCONSEC As INGRESO,
          RTrim(GENPACIEN.PACPRINOM) + ' ' + RTrim(GENPACIEN.PACSEGNOM) + ' ' +
          RTrim(GENPACIEN.PACPRIAPE) + ' ' + RTrim(GENPACIEN.PACSEGAPE) As NOMBRES,
          GENSERIPS.SIPCODCUP As CUPS,
          GENSERIPS.SIPCODIGO As PROCED_COD,
              '' As GRUPOQXCOD,
          (GENSERIPS.SIPNOMBRE) As INTERVENCION, 'ENDOSCOPIA' As ESPECIALIDAD,GMENOMCOM MEDICO,
         convert(char(16),HCNSOLEXA.HCSFECSOL,121) As FECHA_SOLICITUD,
         convert(char(16),HCRFECCONF,106) AS FECHA_CARGUE,
         RIGHT(CONVERT(DATETIME, HCRFECCONF, 108),8) AS HORA_CARGUE,
         convert(char(16),hcrfecres,121) as fecha_resultado,
        concepto regimen,gdenombre entidad
         from
          genpacien
         inner join adningreso on adningreso.genpacien  =  genpacien.oid
         inner join HCNSOLexa on HCNSOLexa.adningreso = adningreso.oid
         inner join genserips on genserips.oid = HCNSOLexa.genserips
         INNER join HCNRESexa on HCNSOLexa.HCNRESexa =  HCNRESexa.oid
        INNER JOIN GENARESER ON GENARESER.OID = genserips.GENARESER1
        INNER JOIN GENMEDICO ON GENMEDICO.OID = HCNRESexa.GENMEDICO
        inner join gentercer on gentercer.oid = genmedico.gentercer
         INNER JOIN GENDETCON ON GENDETCON.OID = adningreso.GENDETCON
          inner join x_confac on x_confac.codigo = gendetcon.gdeconfac
          inner join genmunici on genmunici.oid = genpacien.DGNMUNICIPIO
         where hcrfecres >= ?1 And hcrfecres < ?2 AND   (sipcodigo in ('999','21511','21332') or sipcodigo like '999-%')
        And   GENMEDICO.GMECODIGO NOT LIKE  ('RAD%')

        union all

        Select  'CIRUGIA' TIPO,
          GENPACIEN.PACNUMDOC As PACIENTE,
          ADNINGRESO.AINCONSEC As INGRESO,
          RTrim(GENPACIEN.PACPRINOM) + ' ' + RTrim(GENPACIEN.PACSEGNOM) + ' ' +
          RTrim(GENPACIEN.PACPRIAPE) + ' ' + RTrim(GENPACIEN.PACSEGAPE) As NOMBRES,

        GENSERIPS.SIPCODCUP As CUPS,
          GENSERIPS.SIPCODIGO As PROCED_COD,
            X_QXGRUPOQX.GRUPOQZ As GRUPOQXCOD,
          (GENSERIPS.SIPNOMBRE) As INTERVENCION, (geedescri) As ESPECIALIDAD,GMENOMCOM MEDICO,
          '' As FECHA_SOLICITUD,
        convert(char(16),HCNFOLIO.HCFECFOL,106)  AS FECHA_CARGUE,
        RIGHT(CONVERT(DATETIME, HCNFOLIO.HCFECFOL, 108),8) AS HORA_CARGUE,
         '' fecha_resultado,
           concepto regimen,gdenombre entidad
          From HCMHC50
          Inner Join HCNFOLIO On HCMHC50.HCNFOLIO = HCNFOLIO.OID
          Inner Join GENPACIEN On HCNFOLIO.GENPACIEN = GENPACIEN.OID
          inner join genmunici on genmunici.oid = genpacien.DGNMUNICIPIO
          Inner Join HCNTIPHIS On HCNFOLIO.HCNTIPHIS = HCNTIPHIS.OID
          Inner Join GENESPECI On HCNFOLIO.GENESPECI = GENESPECI.OID
          Inner Join HCNQXEPAC On HCNFOLIO.OID = HCNQXEPAC.HCNFOLIO
          Inner Join GENSERIPS On HCNQXEPAC.GENSERIPS = GENSERIPS.OID
          Inner Join ADNINGRESO On HCNFOLIO.ADNINGRESO = ADNINGRESO.OID
          INNER JOIN GENDETCON ON GENDETCON.OID = adningreso.GENDETCON
          INNER JOIN GENMEDICO ON GENMEDICO.OID = HCNFOLIO.GENMEDICO
          inner join x_confac on x_confac.codigo = gendetcon.gdeconfac
          Left Outer Join X_QXGRUPOQX On GENSERIPS.SIPCODIGO = X_QXGRUPOQX.CODIGO
            Where HCNFOLIO.HCFECFOL >= ?1 And HCNFOLIO.HCFECFOL < ?2
        union all

        Select  'RESCATE ORGANOS' TIPO,
          GENPACIEN.PACNUMDOC As PACIENTE,
          ADNINGRESO.AINCONSEC As INGRESO,
          RTrim(GENPACIEN.PACPRINOM) + ' ' + RTrim(GENPACIEN.PACSEGNOM) + ' ' +
          RTrim(GENPACIEN.PACPRIAPE) + ' ' + RTrim(GENPACIEN.PACSEGAPE) As NOMBRES,
          GENSERIPS.SIPCODCUP As CUPS,
          GENSERIPS.SIPCODIGO As PROCED_COD,
            X_QXGRUPOQX.GRUPOQZ As GRUPOQXCOD,
          (GENSERIPS.SIPNOMBRE) As INTERVENCION, (geedescri) As ESPECIALIDAD,GMENOMCOM MEDICO,
          '' As FECHA_SOLICITUD,
        convert(char(16),HCNFOLIO.HCFECFOL,106)  AS FECHA_CARGUE,
        RIGHT(CONVERT(DATETIME, HCNFOLIO.HCFECFOL, 108),8) AS HORA_CARGUE,
         '' fecha_resultado,
           concepto regimen,gdenombre entidad
          From HCMHC101
          Inner Join HCNFOLIO On HCMHC101.HCNFOLIO = HCNFOLIO.OID
          Inner Join GENPACIEN On HCNFOLIO.GENPACIEN = GENPACIEN.OID
          inner join genmunici on genmunici.oid = genpacien.DGNMUNICIPIO
          Inner Join HCNTIPHIS On HCNFOLIO.HCNTIPHIS = HCNTIPHIS.OID
          Inner Join GENESPECI On HCNFOLIO.GENESPECI = GENESPECI.OID
          Inner Join HCNQXEPAC On HCNFOLIO.OID = HCNQXEPAC.HCNFOLIO
          Inner Join GENSERIPS On HCNQXEPAC.GENSERIPS = GENSERIPS.OID
          Inner Join ADNINGRESO On HCNFOLIO.ADNINGRESO = ADNINGRESO.OID
          INNER JOIN GENDETCON ON GENDETCON.OID = adningreso.GENDETCON
          INNER JOIN GENMEDICO ON GENMEDICO.OID = HCNFOLIO.GENMEDICO
          inner join x_confac on x_confac.codigo = gendetcon.gdeconfac
          Left Outer Join X_QXGRUPOQX On GENSERIPS.SIPCODIGO = X_QXGRUPOQX.CODIGO
            Where HCNFOLIO.HCFECFOL >= ?1 And HCNFOLIO.HCFECFOL < ?2
        order by ingreso,fecha_cargue
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
