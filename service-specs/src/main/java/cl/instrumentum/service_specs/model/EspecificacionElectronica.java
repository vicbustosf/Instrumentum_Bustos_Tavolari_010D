package cl.instrumentum.service_specs.model;
 
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
public class EspecificacionElectronica {
 
    @Id
    @Column(name = "id_equipo")
    // Quite @NotNull. El id viene del @PathVariable del controlador,
    // no del body. Con @NotNull el validador rechazaba el request con 400.
    private Long idEquipo;
 
    @NotBlank(message = "El voltaje es obligatorio")
    private String voltaje;
 
    @NotNull(message = "El consumo es obligatorio")
    @Positive(message = "El consumo debe ser mayor a cero")
    private Double consumo;

    @NotBlank
    @Column(name = "tipo_circuito")
    private String tipoCircuito;
}
 