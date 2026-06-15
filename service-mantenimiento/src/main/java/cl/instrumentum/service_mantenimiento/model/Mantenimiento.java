package cl.instrumentum.service_mantenimiento.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "mantenimiento")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Mantenimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long equipoId;
    private LocalDate fecha;
    private String descripcion;
    private Double costo;
}