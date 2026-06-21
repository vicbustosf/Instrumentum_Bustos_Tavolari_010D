package cl.instrumentum.service_merchandising.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "venta_merch")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Modelo que representa una venta de un producto de merchandising")
public class VentaMerch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID autonumérico de la venta", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long idVenta;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_producto")
    @Schema(description = "Producto vendido. Debes enviar el objeto anidado, ej: {\"producto\": {\"idProducto\": 1}}")
    private ProductoMerch producto;
    
    @NotNull
    @Positive
    @Schema(description = "Cantidad de unidades vendidas", example = "2")
    private Integer cantidad;

    @Schema(description = "Monto total recaudado. Se calcula automáticamente (precio del producto x cantidad), no es necesario enviarlo", example = "30000.0", accessMode = Schema.AccessMode.READ_ONLY)
    private Double montoTotal;

    @Schema(description = "ID del evento/concierto donde se realizó la venta. Es opcional, puede ir null si la venta fue fuera de un show", example = "5")
    private Long idEventoOrigen;
}