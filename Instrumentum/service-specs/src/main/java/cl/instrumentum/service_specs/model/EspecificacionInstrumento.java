package cl.instrumentum.service_specs.model;
 
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Modelo que representa las especificaciones físicas y de luthería de un instrumento musical.")
public class EspecificacionInstrumento {
 
    @Id
    @Column(name = "id_equipo")
    @Schema(description = "ID del equipo/instrumento asociado", example = "1")
    private Long idEquipo;
 
    @NotBlank(message = "El tipo de madera del equipo es obligatorio")
    @Column(name = "tipo_madera")
    @Schema(description = "Tipo de madera utilizada en la construcción del instrumento", example = "Caoba")
    private String tipoMadera;
 
    @NotBlank (message = "La configuración de pastillas del equipo es obligatorio")
    @Column(name = "config_pastillas")
    @Schema(description = "Configuración de los micrófonos o pastillas instaladas", example = "HSS (Humbucker-Single-Single)")
    private String configPastillas;
 
    @NotBlank(message = "El calibre de cuerdas del equipo es obligatorio")
    @Column(name = "calibre_cuerdas")
    @Schema(description = "Calibre recomendado o instalado de las cuerdas", example = "0.010 - 0.046")
    private String calibreCuerdas;
}