package cl.instrumentum.service_inventario.model;

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
public class Equipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del equipo es obligatorio")
    @Size(max = 50, message = "El nombre no puede superar los 50 caracteres")
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "El modelo del equipo es obligatorio")
    @Size(max = 50, message = "El nombre no puede superar los 50 caracteres")
    @Column(nullable = false)
    private String modelo;

    @NotNull(message = "La marca es obligatoria")
    @ManyToOne
    @JoinColumn(name = "marca_id")
    private Marca marca;

    @NotNull(message = "La categoría es obligatoria")
    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @NotNull(message = "El propietario es obligatorio")
    @Column(nullable = false)
    private Long propietarioId;

    @NotBlank(message = "El tipo de propietario es obligatorio")
    @Pattern(
        regexp = "USUARIO|BANDA",
        message = "El tipo de propietario debe ser USUARIO o BANDA"
    )
    @Column(nullable = false)
    private String tipoPropietario;

    @NotBlank(message = "El tipo de equipo es obligatorio")
    @Pattern(
        regexp = "INSTRUMENTO|ELECTRONICO",
        message = "El tipo de equipo debe ser INSTRUMENTO o ELECTRONICO"
    )
    @Column(nullable = false)
    private String tipoEquipo;
}