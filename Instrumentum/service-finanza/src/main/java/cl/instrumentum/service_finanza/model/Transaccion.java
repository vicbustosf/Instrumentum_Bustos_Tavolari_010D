package cl.instrumentum.service_finanza.model;

import java.time.LocalDate;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transaccion")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Entidad que representa una transacción financiera (ingreso o egreso) asociada a una banda musical")
public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único de la transacción", example = "1")
    private Long idTransaccion;

    @NotNull(message = "El id de la banda es obligatoria")
    @Schema(description = "Identificador único de la banda musical a la que pertenece la transacción", example = "10")
    private Long idBanda;

    @NotBlank(message = "Debe ingresar el tipo de movimiento (ingreso o egreso)")
    @Schema(description = "Tipo de movimiento financiero", allowableValues = {"ingreso", "egreso"}, example = "ingreso")
    private String tipoMovimiento;

    @NotNull(message = "El monto es obligatorio")
    @Schema(description = "Monto de la transacción en la moneda local", example = "150000.00")
    private Double monto;
    
    @NotNull(message = "La fecha es obligatoria")
    @Schema(description = "Fecha en la que se realizó el movimiento financiero", example = "2026-06-21")
    private LocalDate fecha;
    
    @NotBlank(message = "La descripción es obligatoria")
    @Schema(description = "Detalle o concepto del movimiento", example = "Pago por concepto de presentación en vivo")
    private String descripcion;
}