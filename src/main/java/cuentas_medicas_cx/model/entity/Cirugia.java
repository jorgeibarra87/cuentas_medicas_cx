package cuentas_medicas_cx.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "cirugias")
@Getter
@Setter
public class Cirugia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TIPO_PROCEDIMIENTO", nullable = false, length = 20)
    private String tipoProcedimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PACIENTE_ID")
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INGRESO_ID")
    private Ingreso ingreso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUPS_ID")
    private CupsProcedimiento cups;

    @Column(name = "PROCED_COD", length = 20)
    private String procedCod;

    @Column(name = "GQX", length = 10)
    private String gqx;

    @Column(name = "INTERVENCION", columnDefinition = "TEXT")
    private String intervencion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ESPECIALIDAD_ID")
    private Especialidad especialidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEDICO_ID")
    private Medico medico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ANESTESIOLOGO_ID")
    private Medico anestesiologo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ENTIDAD_SALUD_ID")
    private EntidadesSalud entidadSalud;

    @Column(name = "AYUDANTE_1", length = 255)
    private String ayudante1;

    @Column(name = "AYUDANTE_2", length = 255)
    private String ayudante2;

    @Column(name = "LIQUIDACION", length = 50)
    private String liquidacion;

    @Column(name = "AUDITORIA_PORCENTAJE", length = 20)
    private String auditoriaPorcentaje;

    @Column(name = "NOVEDAD", length = 100)
    private String novedad;

    @Column(name = "AUTORIZACION", length = 50)
    private String autorizacion;

    @Column(name = "IMAGENES_DX", columnDefinition = "TEXT")
    private String imagenesDx;

    @Column(name = "CAUSA_OBJECION", columnDefinition = "TEXT")
    private String causaObjecion;

    @Column(name = "REV_SUPERVISION", length = 100)
    private String revSupervision;

    @Column(name = "OBSERVACION_AUDITORIA", columnDefinition = "TEXT")
    private String observacionAuditoria;

    @Column(name = "ESTADO_AUDITORIA", length = 20)
    private String estadoAuditoria;

    @Column(name = "REGIMEN", length = 50)
    private String regimen;

    @Column(name = "FECHA_SOLICITUD")
    private String fechaSolicitud;

    @Column(name = "FECHA_CARGUE")
    private String fechaCargue;

    @Column(name = "HORA_CARGUE", length = 10)
    private String horaCargue;

    @Column(name = "FECHA_RESULTADO")
    private String fechaResultado;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt = LocalDateTime.now();
}