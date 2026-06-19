package cl.instrumentum.service_gira.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
public class ParadaGira {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idParada;

    
    // SE REEMPLAZA EL ID PLANO POR RELACIÓN FÍSICA
    
    @ManyToOne
    @JoinColumn(name = "id_gira", nullable = false) // Define la llave foránea física (FK) en MySQL
    @JsonBackReference // Evita la recursión infinita en JSON ocultando la gira completa desde la parada
    @ToString.Exclude // Evita StackOverflow en Lombok
    @EqualsAndHashCode.Exclude // Evita StackOverflow en Lombok
    private Gira gira;

    @NotNull(message = "El ID del evento es obligatorio")
    private Long idEvento; // Relación lógica externa (Mantiene el ID plano)

    @NotBlank(message = "La ciudad es obligatoria")
    private String ciudad;

    @NotBlank(message = "El detalle del alojamiento es obligatorio")
    private String alojamiento;

    @NotBlank(message = "El detalle del transporte es obligatorio")
    private String transporte;
}