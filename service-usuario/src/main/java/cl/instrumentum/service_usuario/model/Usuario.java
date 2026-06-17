package cl.instrumentum.service_usuario.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
    @Column(unique = true)
    private String username;

    @NotBlank(message = "El email es obligatorio")
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