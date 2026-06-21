package cl.instrumentum.service_usuario.model;

import java.time.LocalDate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "banda")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Modelo que representa a una banda musical dentro del sistema")
public class Banda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único de la banda", example = "1")
    private Long idBanda;

    @NotBlank(message = "El nombre de la banda es obligatorio")
    @Schema(description = "Nombre de la banda musical", example = "Los Prisioneros")
    private String nombre;

    @NotNull(message = "La fecha de registro es obligatoria")
    @Schema(description = "Fecha en que se registró la banda en el sistema", example = "2026-06-21")
    private LocalDate fechaRegistro;
}