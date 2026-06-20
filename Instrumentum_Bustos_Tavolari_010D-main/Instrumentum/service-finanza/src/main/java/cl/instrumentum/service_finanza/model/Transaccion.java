package cl.instrumentum.service_finanza.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transaccion")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTransaccion;

    @NotNull(message = "El id de la banda es obligatoria")
    private Long idBanda;

    @NotBlank(message = "Debe ingresar el tipo de movimiento (ingreso o egreso)")
    private String tipoMovimiento;

    @NotNull(message = "El monto es obligatorio")

    private Double monto;
    
    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;
    
    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;
}
