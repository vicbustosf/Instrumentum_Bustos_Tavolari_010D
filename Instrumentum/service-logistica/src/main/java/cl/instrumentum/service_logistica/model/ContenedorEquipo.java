package cl.instrumentum.service_logistica.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "contenedor_equipo")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContenedorEquipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "contenedor_id")
    private Contenedor contenedor;

    private Long idEquipo;
}
//Quiza agregarle los shema