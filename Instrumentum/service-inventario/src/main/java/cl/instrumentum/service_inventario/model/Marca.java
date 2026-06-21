package cl.instrumentum.service_inventario.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "marca")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Fabricante o marca de los equipos del inventario")
public class Marca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único de la marca", example = "1")
    private Long id;

    @NotBlank(message = "El nombre de la marca es obligatorio")
    @Size(max = 50, message = "El nombre no puede superar los 50 caracteres")
    @Column(nullable = false)
    @Schema(description = "Nombre del fabricante", example = "Fender")
    private String nombre;
}