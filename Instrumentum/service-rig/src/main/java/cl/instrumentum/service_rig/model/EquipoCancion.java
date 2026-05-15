package cl.instrumentum.service_rig.model;
 
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
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
<<<<<<< HEAD
    // Cancion tiene List<EquipoCancion> y EquipoCancion tiene Cancion,
    // Jackson entraba en bucle infinito generando respuestas de miles de líneas.
=======
    // Cancion tiene List<EquipoCancion> y EquipoCancion tiene Cancion, o sea, referencia circular. 
    // Sin @JsonIgnore, al convertir a JSON, Jackson entra en un loop infinito tratando de convertir 
    // Cancion -> EquipoCancion -> Cancion -> EquipoCancion... y así sucesivamente, 
    // lo que resulta en un error.
    
>>>>>>> mi-respaldo
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "cancion_id")
    private Cancion cancion;
 
    private Long equipoId;
    private Integer posicion;
    private String seteoPerillas;
}
 