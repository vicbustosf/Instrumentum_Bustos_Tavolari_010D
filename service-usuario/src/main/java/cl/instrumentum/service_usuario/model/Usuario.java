package cl.instrumentum.service_usuario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuario")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUser;

    @NotBlank(message = "El username es obligatorio")
    @Size(max = 30, message = "El username no puede superar los 30 caracteres")
    @Column(unique = true)
    private String username;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El correo electrónico no tiene un formato válido")
    private String email;

    @NotBlank(message = "El rol es obligatorio")
    @Pattern(
        regexp = "Musico|Tech",
        message = "El rol debe ser Musico o Tech"
    )
    private String rol;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_banda")
    private Banda banda;

    
}