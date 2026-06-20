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
    private Long idEquipo; // Sin @NotNull para evitar rechazos 400 en el @Valid del Controller

    @NotBlank(message = "El voltaje es obligatorio")
    private String voltaje;

    @NotNull(message = "El consumo es obligatorio")
    @Positive(message = "El consumo debe ser mayor a cero")
    private Double consumo;

    @NotBlank(message = "El tipo de circuito es obligatorio")
    @Column(name = "tipo_circuito")
    private String tipoCircuito;
}