package cl.instrumentum.service_usuario.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "banda")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Banda {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idBanda;

    private String nombre;
    private LocalDate fechaRegistro;
}