package cl.instrumentum.service_merchandising.controller;

import cl.instrumentum.service_merchandising.model.ProductoMerch;
import cl.instrumentum.service_merchandising.model.VentaMerch;
import cl.instrumentum.service_merchandising.service.MerchandisingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v2/merchandising")
@Tag(name = "Merchandising", description = "Gestión del inventario de productos comerciales de la banda y registro de ventas a los fans")
public class MerchandisingController {

    @Autowired
    private MerchandisingService merchandisingService;

// ---------------- CRUD Productos ---------------- \\

// Crear producto
    @Operation(summary = "Crear un nuevo producto de merchandising", description = "Registra un nuevo producto en el inventario de la banda")
    @PostMapping("/productos")
    public ResponseEntity<String> crearProducto(@Valid @RequestBody ProductoMerch producto) {
        try {
            merchandisingService.crearProducto(producto);
            return ResponseEntity.status(HttpStatus.CREATED).body("Producto registrado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

// Listar todos los productos
    @Operation(summary = "Listar todos los productos", description = "Obtiene todos los productos de merchandising registrados en el sistema")
    @GetMapping("/productos/todos")
    public List<ProductoMerch> listarTodosProductos() {
        return merchandisingService.listarTodosProductos();
    }

// Obtener producto por id
    @Operation(summary = "Obtener un producto por ID", description = "Busca un producto específico de merchandising por su ID")
    @GetMapping("/productos/{id}")
    public ResponseEntity<?> obtenerProducto(@PathVariable Long id) {
        return merchandisingService.buscarProductoPorId(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Error: Producto no encontrado"));
    }

// Listar productos por banda
    @Operation(summary = "Listar productos de una banda", description = "Obtiene todos los productos de merchandising asociados al ID de una banda")
    @GetMapping("/productos/banda/{idBanda}")
    public ResponseEntity<?> listarProductosPorBanda(@PathVariable Long idBanda) {
        try {
            return ResponseEntity.ok(merchandisingService.listarProductosPorBanda(idBanda));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

// Actualizar producto
    @Operation(summary = "Actualizar un producto", description = "Modifica los datos de nombre, tipo, precio o stock de un producto usando su ID")
    @PutMapping("/productos/{id}")
    public ResponseEntity<String> actualizarProducto(@PathVariable Long id, @RequestBody ProductoMerch datos) {
        return merchandisingService.buscarProductoPorId(id)
                .map(p -> {
                    p.setNombre(datos.getNombre());
                    p.setTipo(datos.getTipo());
                    p.setPrecio(datos.getPrecio());
                    p.setStock(datos.getStock());
                    merchandisingService.actualizarProducto(p);
                    return ResponseEntity.ok("Producto actualizado exitosamente");
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Producto no encontrado"));
    }

// Eliminar producto
    @Operation(summary = "Eliminar un producto", description = "Borra un producto del inventario, solo si no tiene ventas registradas")
    @DeleteMapping("/productos/{id}")
    public ResponseEntity<String> eliminarProducto(@PathVariable Long id) {
        try {
            merchandisingService.eliminarProducto(id);
            return ResponseEntity.ok("Producto eliminado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

// ---------------- CRUD Ventas ---------------- \\

// Registrar venta
    @Operation(summary = "Registrar una nueva venta", description = "Valida el stock disponible, lo descuenta y calcula el monto total recaudado. Si se envía idEventoOrigen, valida que el evento exista")
    @PostMapping("/ventas")
    public ResponseEntity<String> registrarVenta(@Valid @RequestBody VentaMerch venta) {
        try {
            merchandisingService.registrarVenta(venta);
            return ResponseEntity.status(HttpStatus.CREATED).body("Venta registrada exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

// Listar todas las ventas
    @Operation(summary = "Listar todas las ventas", description = "Obtiene todas las ventas de merchandising registradas en el sistema")
    @GetMapping("/ventas/todas")
    public List<VentaMerch> listarTodasVentas() {
        return merchandisingService.listarTodasVentas();
    }

// Obtener venta por id
    @Operation(summary = "Obtener una venta por ID", description = "Busca una venta específica por su ID")
    @GetMapping("/ventas/{id}")
    public ResponseEntity<?> obtenerVenta(@PathVariable Long id) {
        return merchandisingService.buscarVentaPorId(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Error: Venta no encontrada"));
    }

// Listar ventas por producto
    @Operation(summary = "Listar ventas de un producto", description = "Obtiene el historial de ventas de un producto específico")
    @GetMapping("/ventas/producto/{idProducto}")
    public ResponseEntity<?> listarVentasPorProducto(@PathVariable Long idProducto) {
        try {
            return ResponseEntity.ok(merchandisingService.listarVentasPorProducto(idProducto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

// Listar ventas por evento de origen
    @Operation(summary = "Listar ventas de un evento", description = "Obtiene todas las ventas realizadas en el stand de merchandising de un concierto específico")
    @GetMapping("/ventas/evento/{idEventoOrigen}")
    public List<VentaMerch> listarVentasPorEvento(@PathVariable Long idEventoOrigen) {
        return merchandisingService.listarVentasPorEvento(idEventoOrigen);
    }

// Actualizar venta
    @Operation(summary = "Actualizar una venta", description = "Permite corregir el evento de origen asociado a una venta ya registrada")
    @PutMapping("/ventas/{id}")
    public ResponseEntity<String> actualizarVenta(@PathVariable Long id, @RequestBody VentaMerch datos) {
        return merchandisingService.buscarVentaPorId(id)
                .map(v -> {
                    v.setIdEventoOrigen(datos.getIdEventoOrigen());
                    merchandisingService.actualizarVenta(v);
                    return ResponseEntity.ok("Venta actualizada exitosamente");
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Venta no encontrada"));
    }

// Eliminar venta
    @Operation(summary = "Eliminar una venta", description = "Elimina el registro de venta y reintegra el stock al inventario del producto")
    @DeleteMapping("/ventas/{id}")
    public ResponseEntity<String> eliminarVenta(@PathVariable Long id) {
        try {
            merchandisingService.eliminarVenta(id);
            return ResponseEntity.ok("Venta eliminada y stock reintegrado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}