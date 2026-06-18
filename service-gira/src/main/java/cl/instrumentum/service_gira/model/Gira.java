package cl.instrumentum.service_gira.model;

import java.time.LocalDate;

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
@Table(name = "gira")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Gira {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idGira;

    @NotNull(message = "El ID de la banda es obligatorio")
    private Long idBanda;

    @NotBlank(message = "El nombre de la gira es obligatorio")
    private String nombreGira;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate fechaFin;
}