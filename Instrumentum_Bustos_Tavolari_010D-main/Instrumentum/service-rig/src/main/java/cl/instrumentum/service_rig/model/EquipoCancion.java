package cl.instrumentum.service_rig.model;
 
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Entity
@Table(name = "equipo_cancion")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EquipoCancion {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    // FIX: @JsonIgnore corta la referencia circular.
    // Cancion tiene List<EquipoCancion> y EquipoCancion tiene Cancion,
    // lo que causó un ciclo infinito.
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "cancion_id")
    private Cancion cancion;
 
    @NotNull(message = "El equipo es obligatorio")
    private Long equipoId;

    @NotNull(message = "La posición es obligatoria")
    @Positive(message = "La posición debe ser mayor a cero")
    private Integer posicion;

    @NotBlank(message = "El seteo de perillas es obligatorio")
    private String seteoPerillas;
}
 