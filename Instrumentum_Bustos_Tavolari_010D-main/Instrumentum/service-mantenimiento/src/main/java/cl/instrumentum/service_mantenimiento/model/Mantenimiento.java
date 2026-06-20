package cl.instrumentum.service_mantenimiento.model;

import java.time.LocalDate;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mantenimiento")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Mantenimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El equipo es obligatorio")
    private Long equipoId;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    //No será obligatorio, puede ser un campo vacío.
    private String descripcion;

    @NotNull(message = "El costo es obligatorio")
    @PositiveOrZero(message = "El costo no puede ser negativo")
    private Double costo;
}