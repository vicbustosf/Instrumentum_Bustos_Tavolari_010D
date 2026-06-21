package cl.instrumentum.service_specs.model;
 
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Entity
@Table(name = "especificacion_electronica")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Modelo que representa los parámetros y especificaciones electrónicas de un equipo.")
public class EspecificacionElectronica {

    @Id
    @Column(name = "id_equipo")
    @Schema(description = "ID del equipo electrónico asociado", example = "2")
    private Long idEquipo; // Sin @NotNull para evitar rechazos 400 en el @Valid del Controller

    @NotBlank(message = "El voltaje es obligatorio")
    @Schema(description = "Voltaje de operación del circuito", example = "9V DC")
    private String voltaje;

    @NotNull(message = "El consumo es obligatorio")
    @Positive(message = "El consumo debe ser mayor a cero")
    @Schema(description = "Consumo de corriente medido en miliamperios (mA)", example = "150.0")
    private Double consumo;

    @NotBlank(message = "El tipo de circuito es obligatorio")
    @Column(name = "tipo_circuito")
    @Schema(description = "Clasificación del circuito electrónico interno", example = "Análogo / True Bypass")
    private String tipoCircuito;
}