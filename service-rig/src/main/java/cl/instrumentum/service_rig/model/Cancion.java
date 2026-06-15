package cl.instrumentum.service_rig.model;

import jakarta.persistence.*;
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
    
    private String nombre;
    private Long bandaId;
    private Integer duracionSegundos;

    //orphanRemoval para eliminar los equipos asignados si se borra la canción
    @OneToMany(mappedBy = "cancion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<EquipoCancion> equiposAsignados = new ArrayList<>();
}