package cl.instrumentum.service_usuario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuario")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Modelo que representa a un usuario del sistema (Músico o Técnico)")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del usuario", example = "10")
    private Long idUser;

    @NotBlank(message = "El username es obligatorio")
    @Size(max = 30, message = "El username no puede superar los 30 caracteres")
    @Column(unique = true)
    @Schema(description = "Nombre de usuario único para la cuenta", example = "jorge_gonzalez")
    private String username;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El correo electrónico no tiene un formato válido")
    @Schema(description = "Correo electrónico de contacto", example = "jorge@instrumentum.cl")
    private String email;

    @NotBlank(message = "El rol es obligatorio")
    @Pattern(
        regexp = "Musico|Tech",
        message = "El rol debe ser Musico o Tech"
    )
    @Schema(description = "Rol asignado dentro del ecosistema", allowableValues = {"Musico", "Tech"}, example = "Musico")
    private String rol;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_banda")
    @Schema(description = "Banda musical a la cual pertenece el usuario")
    private Banda banda;
    

}