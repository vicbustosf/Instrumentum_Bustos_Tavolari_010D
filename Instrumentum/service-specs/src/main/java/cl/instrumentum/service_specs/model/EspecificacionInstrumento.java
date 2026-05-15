package cl.instrumentum.service_specs.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "especificacion_instrumento")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EspecificacionInstrumento {

    //lo mismo que en EspecificacionElectronica,     
    @Id
    @Column(name = "id_equipo")
    @NotNull
    private Long idEquipo;

    @NotBlank
    @Column(name = "tipo_madera")
    private String tipoMadera;

    @NotBlank
    @Column(name = "config_pastillas")
    private String configPastillas;

    @NotBlank
    @Column(name = "calibre_cuerdas")
    private String calibreCuerdas;
}