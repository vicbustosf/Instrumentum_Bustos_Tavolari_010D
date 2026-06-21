package cl.instrumentum.service_rig.model;
 
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Entidad intermedia que asocia un equipo musical a una canción con su configuración")
public class EquipoCancion {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único de la asignación", example = "100")
    private Long id;
 
    // FIX: @JsonIgnore corta la referencia circular.
    // Cancion tiene List<EquipoCancion> y EquipoCancion tiene Cancion,
    // lo que causó un ciclo infinito.
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "cancion_id")
    @Schema(description = "Canción a la cual se le asigna el equipo")
    private Cancion cancion;
 
    @NotNull(message = "El equipo es obligatorio")
    @Schema(description = "Identificador único del equipo proveniente del inventario", example = "55")
    private Long equipoId;

    @NotNull(message = "La posición es obligatoria")
    @Positive(message = "La posición debe ser mayor a cero")
    @Schema(description = "Posición del equipo en la cadena de efectos de la canción", example = "1")
    private Integer posicion;

    @NotBlank(message = "El seteo de perillas es obligatorio")
    @Schema(description = "Configuración o parámetros específicos de las perillas del equipo", example = "Gain: 7, Treble: 6, Middle: 5, Bass: 8")
    private String seteoPerillas;
}