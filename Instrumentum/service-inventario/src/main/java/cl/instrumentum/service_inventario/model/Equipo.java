package cl.instrumentum.service_inventario.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "equipo")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Representa un equipo musical o electrónico dentro del inventario")
public class Equipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único autogenerado del equipo", example = "1")
    private Long id;

    @NotBlank(message = "El nombre del equipo es obligatorio")
    @Size(max = 50, message = "El nombre no puede superar los 50 caracteres")
    @Column(nullable = false)
    @Schema(description = "Nombre descriptivo del equipo", example = "Guitarra Stratocaster")
    private String nombre;

    @NotBlank(message = "El modelo del equipo es obligatorio")
    @Size(max = 50, message = "El nombre no puede superar los 50 caracteres")
    @Column(nullable = false)
    @Schema(description = "Modelo específico del fabricante", example = "American Professional II")
    private String modelo;

    @NotNull(message = "La marca es obligatoria")
    @ManyToOne
    @JoinColumn(name = "marca_id")
    @Schema(description = "Marca asociada al equipo")
    private Marca marca;

    @NotNull(message = "La categoría es obligatoria")
    @ManyToOne
    @JoinColumn(name = "categoria_id")
    @Schema(description = "Categoría a la que pertenece el equipo")
    private Categoria categoria;

    @NotNull(message = "El propietario es obligatorio")
    @Column(nullable = false)
    @Schema(description = "ID del dueño en el microservicio externo", example = "42")
    private Long propietarioId;

    @NotBlank(message = "El tipo de propietario es obligatorio")
    @Pattern(regexp = "USUARIO|BANDA", message = "El tipo de propietario debe ser USUARIO o BANDA")
    @Column(nullable = false)
    @Schema(description = "Tipo de entidad dueña del equipo", example = "USUARIO")
    private String tipoPropietario;

    @NotBlank(message = "El tipo de equipo es obligatorio")
    @Pattern(regexp = "INSTRUMENTO|ELECTRONICO", message = "El tipo de equipo debe ser INSTRUMENTO o ELECTRONICO")
    @Column(nullable = false)
    @Schema(description = "Clasificación del tipo de equipo", example = "INSTRUMENTO")
    private String tipoEquipo;
}