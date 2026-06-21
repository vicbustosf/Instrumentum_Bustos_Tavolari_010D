package cl.instrumentum.service_inventario.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "categoria")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Categorías de clasificación para organizar el inventario")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único de la categoría", example = "1")
    private Long id;

    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(max = 20, message = "El nombre no puede superar los 20 caracteres")
    @Column(nullable = false)
    @Schema(description = "Nombre de la categoría de equipos", example = "Cuerdas")
    private String nombre;
}