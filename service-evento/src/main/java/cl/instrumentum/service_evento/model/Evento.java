package cl.instrumentum.service_evento.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "La fecha del evento no puede quedar en blanco")
    private LocalDate fecha;

    // IDs de canciones separados por coma, ej: "1,4,7"
    @NotBlank(message = "Deben especificarse las id de las canciones para el evento")
    private String canciones;
}