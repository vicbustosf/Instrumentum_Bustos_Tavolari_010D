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

    @NotNull
    private Long idBanda;

    @NotBlank
    private String nombre;

    @NotNull
    private LocalDate fecha;

    // IDs de canciones separados por coma, ej: "1,4,7"
    private String canciones;
}