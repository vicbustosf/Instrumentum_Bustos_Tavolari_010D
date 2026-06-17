package cl.instrumentum.service_logistica.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "contenedor")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Modelo que representa un contenedor de transporte logístico")
public class Contenedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID autonumérico del contenedor", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long idContenedor;

    @NotNull
    @Schema(description = "ID de la banda musical dueña del contenedor", example = "10")
    private Long idBanda;

    @NotBlank
    @Schema(description = "Nombre descriptivo de la caja de transporte", example = "Flightcase de Guitarras Principales")
    private String nombreCaja;

    @Schema(description = "Peso estimado del contenedor en kilogramos", example = "25.4")
    private Double peso;

    @OneToMany(mappedBy = "contenedor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Schema(description = "Lista de asociaciones de equipos guardados dentro de este contenedor")
    private List<ContenedorEquipo> equipos = new ArrayList<>();
}