package cl.instrumentum.service_merchandising.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "producto_merch")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Modelo que representa un producto de merchandising de una banda")
public class ProductoMerch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID autonumérico del producto", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long idProducto;

    @NotNull
    @Schema(description = "ID de la banda dueña del producto", example = "1")
    private Long idBanda;

    @NotBlank
    @Schema(description = "Nombre del producto", example = "Polera Tour 2026")
    private String nombre;
    
    @NotBlank
    @Schema(description = "Tipo de producto", example = "Polera")
    private String tipo; // El tipo debe ser "Polera", "Disco", "Parche" o "Gorro"

    @NotNull
    @Positive // Buscar para que es esto...
    @Schema(description = "Precio unitario del producto", example = "15000.0")
    private Double precio;

    @Schema(description = "Cantidad disponible en inventario", example = "50")
    private Integer stock;
}