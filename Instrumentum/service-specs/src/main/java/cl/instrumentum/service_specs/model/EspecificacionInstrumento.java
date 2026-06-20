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
 
    @Id
    @Column(name = "id_equipo")
    private Long idEquipo;
 
    @NotBlank(message = "El tipo de madera del equipo es obligatorio")
    @Column(name = "tipo_madera")
    private String tipoMadera;
 
    @NotBlank (message = "La configuración de pastillas del equipo es obligatorio")
    @Column(name = "config_pastillas")
    private String configPastillas;
 
    @NotBlank(message = "El calibre de cuerdas del equipo es obligatorio")
    @Column(name = "calibre_cuerdas")
    private String calibreCuerdas;
}