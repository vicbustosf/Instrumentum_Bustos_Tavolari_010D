package cl.instrumentum.service_rig.model;

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
public class Cancion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El nombre de la canción es obligatorio")
    private String nombre;

    @NotNull(message = "La banda es obligatoria")
    private Long bandaId;

    @NotNull(message = "La duración es obligatoria")
    @Positive(message = "La duración debe ser mayor a cero")
    private Integer duracionSegundos;


    //orphanRemoval para eliminar los equipos asignados si se borra la canción
    @OneToMany(mappedBy = "cancion", 
                cascade = CascadeType.ALL, 
                orphanRemoval = true, 
                fetch = FetchType.EAGER)
    private List<EquipoCancion> equiposAsignados = new ArrayList<>();
}