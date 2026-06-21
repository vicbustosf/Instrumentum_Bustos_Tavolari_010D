package cl.instrumentum.service_gira.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "gira")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Entidad que representa una gira musical programada por una banda.")
public class Gira {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único de la gira en la base de datos", example = "1")
    private Long idGira;

    @NotNull(message = "El ID de la banda es obligatorio")
    @Schema(description = "ID de la banda dueña de la gira (Relación lógica con servicio externo)", example = "10")
    private Long idBanda; // Relación lógica externa (Mantiene el ID plano)

    @NotBlank(message = "El nombre de la gira es obligatorio")
    @Schema(description = "Nombre oficial o comercial de la gira", example = "Latinoamérica Despierta 2026")
    private String nombreGira;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @Schema(description = "Fecha en la que inicia el itinerario de la gira", example = "2026-07-01")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    @Schema(description = "Fecha de término de la gira", example = "2026-08-15")
    private LocalDate fechaFin;

//      RELACIÓN FÍSICA (1:N) HACIA PARADAS
//   ==========================================

    // cascade = CascadeType.ALL: Si eliminas una Gira, borra automáticamente sus paradas en la bd.
    // orphanRemoval = true: Si remueves una parada de esta lista, se elimina físicamente de la base de datos.
    @OneToMany(mappedBy = "gira", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // Evita la recursión infinita al serializar a JSON
    @ToString.Exclude // Evita StackOverflow en Lombok(stackoverflow quiere decir que se llama a si mismo  infinitamente, en este caso porque Gira tiene una lista de ParadaGira, y cada ParadaGira tiene una referencia a Gira)
    @EqualsAndHashCode.Exclude // lo mismo que arriba, pero para los métodos equals y hashcode de Lombok
    @Schema(description = "Listado de paradas logísticas asociadas a esta gira")
    private List<ParadaGira> paradas = new ArrayList<>();

    @AssertTrue(message = "La fecha de fin debe ser posterior o igual a la fecha de inicio")
    public boolean isFechasValidas() {
        if (fechaInicio == null || fechaFin == null) {
            return true; 
        }
        return !fechaFin.isBefore(fechaInicio);
    }
}

    /*La anotación @AssertTrue es parte de la librería de validación estándar de Java (Bean Validation).
     Cuando tú le pones @Valid en tu Controller al recibir el JSON, Spring Boot escanea la clase Gira completa. 
     No solo revisa los @NotNull o @NotBlank que están en las variables; también busca cualquier 
     método que devuelva un booleano (que empiece con is o get) y que tenga la anotación @AssertTrue.
     Si ese método devuelve false, Spring detiene la petición y 
     lanza el error igual que si hubieras dejado un campo en blanco. 
     Ponerlo en el modelo es la forma más limpia y estándar de hacer "validaciones cruzadas"
     (comparar dos campos de la misma clase) sin tener que ensuciar tu Controller o tu Service 
     con montones de if / else.
     
     - Respuesta de Gemini tras preguntar si era correcto insertar metodos en el model, y que hacia AssertTrue*/
