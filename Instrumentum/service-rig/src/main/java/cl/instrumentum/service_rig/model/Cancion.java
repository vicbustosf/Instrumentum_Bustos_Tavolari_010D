package cl.instrumentum.service_rig.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cancion")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Entidad que representa una canción dentro de la biblioteca de una banda")
public class Cancion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único de la canción", example = "1")
    private Long id;
    
    @NotBlank(message = "El nombre de la canción es obligatorio")
    @Schema(description = "Nombre o título de la canción", example = "Master of Puppets")
    private String nombre;

    @NotNull(message = "La banda es obligatoria")
    @Schema(description = "Identificador único de la banda a la que pertenece la canción", example = "10")
    private Long bandaId;

    @NotNull(message = "La duración es obligatoria")
    @Positive(message = "La duración debe ser mayor a cero")
    @Schema(description = "Duración total de la canción expresada en segundos", example = "515")
    private Integer duracionSegundos;

    //orphanRemoval para eliminar los equipos asignados si se borra la canción
    @OneToMany(mappedBy = "cancion", 
                cascade = CascadeType.ALL, 
                orphanRemoval = true, 
                fetch = FetchType.EAGER)
    @Schema(description = "Lista de equipos asignados a la canción (su cadena de efectos o Rig)")
    private List<EquipoCancion> equiposAsignados = new ArrayList<>();
}