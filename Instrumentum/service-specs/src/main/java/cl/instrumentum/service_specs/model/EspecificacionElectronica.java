package cl.instrumentum.service_specs.model;
 
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
 
    @NotNull
    private String voltaje;
 
    @NotNull
    private Double consumo;
 
    @NotBlank
    @Column(name = "tipo_circuito")
    private String tipoCircuito;
}
 