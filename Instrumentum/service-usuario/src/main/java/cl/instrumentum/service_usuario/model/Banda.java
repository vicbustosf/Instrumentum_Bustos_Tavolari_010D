package cl.instrumentum.service_usuario.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "banda")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Banda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idBanda;

    @NotBlank(message = "El nombre de la banda es obligatorio")
    private String nombre;

    @NotNull(message = "La fecha de registro es obligatoria")
    private LocalDate fechaRegistro;
}