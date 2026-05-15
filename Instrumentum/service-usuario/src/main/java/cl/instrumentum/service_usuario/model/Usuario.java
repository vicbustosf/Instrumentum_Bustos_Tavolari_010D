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

    //No puede haber dos usuarios con el mismo username
    @Column(unique = true)
    private String username;

    private String email;

    // Valida que el rol sea uno de los valores permitidos
    @Pattern(regexp = "Musico|Tech")
    private String rol;

    // Relación con Banda (muchos usuarios pueden pertenecer a una banda)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_banda")
    private Banda banda;
}