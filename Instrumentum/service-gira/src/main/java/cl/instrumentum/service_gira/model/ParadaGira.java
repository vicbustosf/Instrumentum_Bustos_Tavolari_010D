package cl.instrumentum.service_gira.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "parada_gira")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Entidad que detalla una parada específica dentro del itinerario de una gira.")
public class ParadaGira {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único de la parada logística", example = "105")
    private Long idParada;

    
    // SE REEMPLAZA EL ID PLANO POR RELACIÓN FÍSICA
    
    @ManyToOne
    @JoinColumn(name = "id_gira", nullable = false) // Define la llave foránea física (FK) en MySQL
    @JsonBackReference // Evita la recursión infinita en JSON ocultando la gira completa desde la parada
    @ToString.Exclude // Evita StackOverflow en Lombok
    @EqualsAndHashCode.Exclude // Evita StackOverflow en Lombok
    @Schema(description = "Instancia de la Gira matriz a la que pertenece físicamente esta parada")
    private Gira gira;

    @NotNull(message = "El ID del evento es obligatorio")
    @Schema(description = "ID del concierto/evento asociado (Relación lógica con servicio externo)", example = "500")
    private Long idEvento; // Relación lógica externa (Mantiene el ID plano)

    @NotBlank(message = "La ciudad es obligatoria")
    @Schema(description = "Ciudad donde se realizará la parada", example = "Santiago")
    private String ciudad;

    @NotBlank(message = "El detalle del alojamiento es obligatorio")
    @Schema(description = "Especificaciones sobre el hospedaje de la banda", example = "Hotel Costanera, 5 habitaciones chb")
    private String alojamiento;

    @NotBlank(message = "El detalle del transporte es obligatorio")
    @Schema(description = "Especificaciones sobre el traslado hacia o en la ciudad", example = "Van privada para 8 personas")
    private String transporte;
}