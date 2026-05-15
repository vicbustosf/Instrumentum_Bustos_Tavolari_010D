package cl.instrumentum.service_inventario.model;

import jakarta.persistence.*;
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

    
    private String nombre;
    private String modelo;


    @ManyToOne
    @JoinColumn(name = "marca_id") //FK a tabla marca
    private Marca marca;

    @ManyToOne
    @JoinColumn(name = "categoria_id")//FK a tabla categoria
    private Categoria categoria;

    private Long propietarioId;
    private String tipoPropietario; // "USUARIO" o "BANDA"
    private String tipoEquipo;      // "INSTRUMENTO" o "ELECTRONICO"
}