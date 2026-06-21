package cl.instrumentum.service_mantenimiento.model;

import java.time.LocalDate;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mantenimiento")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Entidad que representa el registro de un mantenimiento realizado a un equipo médico o de laboratorio")
public class Mantenimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único autoincremental del mantenimiento", example = "1")
    private Long id;

    @NotNull(message = "El equipo es obligatorio")
    @Schema(description = "Identificador único del equipo asociado al mantenimiento", example = "105")
    private Long equipoId;

    @NotNull(message = "La fecha es obligatoria")
    @Schema(description = "Fecha en la que se ejecutó o ejecutará el mantenimiento", example = "2026-06-21")
    private LocalDate fecha;

    @Schema(description = "Detalle o descripción del trabajo realizado en el equipo", example = "Calibración de óptica y limpieza general de filtros.")
    private String description; //No será obligatorio, puede ser un campo vacío.

    @NotNull(message = "El costo es obligatorio")
    @PositiveOrZero(message = "El costo no puede ser negativo")
    @Schema(description = "Costo económico del mantenimiento realizado", example = "45000.00")
    private Double costo;
}