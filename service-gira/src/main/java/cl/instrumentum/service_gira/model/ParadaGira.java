package cl.instrumentum.service_gira.model;

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
@Table(name = "parada_gira")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParadaGira {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idParada;

    @NotNull
    private Long idGira;

    @NotNull
    private Long idEvento;

    @NotBlank
    private String ciudad;

    @NotBlank
    private String alojamiento;

    @NotBlank
    private String transporte;
}