package cl.instrumentum.service_rig.model;

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

    @ManyToOne
    @JoinColumn(name = "cancion_id")

    private Cancion cancion;
    private Long equipoId;
    private Integer posicion;
    private String seteoPerillas;
}