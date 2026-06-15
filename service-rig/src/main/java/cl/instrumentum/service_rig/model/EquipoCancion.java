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
    // Cancion tiene List<EquipoCancion> y EquipoCancion tiene Cancion,
    // Jackson entraba en bucle infinito generando respuestas de miles de líneas.
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "cancion_id")
    private Cancion cancion;
 
    private Long equipoId;
    private Integer posicion;
    private String seteoPerillas;
}
 