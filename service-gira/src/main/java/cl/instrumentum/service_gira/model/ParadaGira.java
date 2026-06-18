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

    @NotNull(message = "El ID de la gira es obligatorio")
    private Long idGira;

    @NotNull(message = "El ID del evento es obligatorio")
    private Long idEvento;

    @NotBlank(message = "La ciudad es obligatoria")
    private String ciudad;

    @NotBlank(message = "El detalle del alojamiento es obligatorio")
    private String alojamiento;

    @NotBlank(message = "El detalle del transporte es obligatorio")
    private String transporte;
}