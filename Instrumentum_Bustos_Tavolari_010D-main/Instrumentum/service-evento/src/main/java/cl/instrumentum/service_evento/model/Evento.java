package cl.instrumentum.service_evento.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "evento")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEvento;

    @NotNull(message = "El id de la banda es obligatorio")
    private Long idBanda;

    @NotBlank(message = "El nombre del evento es obligatorio")
    private String nombre;

    @NotNull(message = "La fecha del evento es obligatoria")
    private LocalDate fecha;

    @NotNull(message = "Las canciones son obligatorias")
    @NotBlank(message = "Las canciones no pueden estar vacías")
    @Pattern(regexp = "^\\d+(,\\d+)*$", message = "Las canciones deben ser IDs numéricos separados por comas (ej: 1,4,7)")
    @Column(name = "canciones", nullable = false)
    private String canciones;
}