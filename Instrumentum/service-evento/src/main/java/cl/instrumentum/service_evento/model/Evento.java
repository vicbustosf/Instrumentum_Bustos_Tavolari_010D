package cl.instrumentum.service_evento.model;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Entidad que representa un evento musical o concierto dentro del sistema")
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del evento", example = "1")
    private Long idEvento;

    @NotNull(message = "El id de la banda es obligatorio")
    @Schema(description = "Identificador único de la banda que se presentará en el evento", example = "5")
    private Long idBanda;

    @NotBlank(message = "El nombre del evento es obligatorio")
    @Schema(description = "Nombre o título comercial del evento", example = "Santiago Rock Festival 2026")
    private String nombre;

    @NotNull(message = "La fecha del evento es obligatoria")
    @Schema(description = "Fecha programada para la realización del evento", example = "2026-11-15")
    private LocalDate fecha;

    @NotNull(message = "Las canciones son obligatorias")
    @NotBlank(message = "Las canciones no pueden estar vacías")
    @Pattern(regexp = "^\\d+(,\\d+)*$", message = "Las canciones deben ser IDs numéricos separados por comas (ej: 1,4,7)")
    @Column(name = "canciones", nullable = false)
    @Schema(description = "Lista de identificadores de canciones (setlist) separados por comas", example = "101,104,107")
    private String canciones;
}