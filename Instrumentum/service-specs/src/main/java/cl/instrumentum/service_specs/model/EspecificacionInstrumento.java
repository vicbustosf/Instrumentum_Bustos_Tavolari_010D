package cl.instrumentum.service_specs.model;
 
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Entity
@Table(name = "especificacion_instrumento")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EspecificacionInstrumento {
 
    @Id
    @Column(name = "id_equipo")
    // FIX: se quitó @NotNull. El id viene del @PathVariable del controlador,
    // no del body. Con @NotNull el validador rechazaba el request con 400.
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