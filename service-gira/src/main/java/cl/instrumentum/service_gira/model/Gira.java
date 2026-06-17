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

    @NotNull
    private Long idBanda;

    @NotBlank
    private String nombreGira;

    @NotNull
    private LocalDate fechaInicio;

    @NotNull
    private LocalDate fechaFin;
}