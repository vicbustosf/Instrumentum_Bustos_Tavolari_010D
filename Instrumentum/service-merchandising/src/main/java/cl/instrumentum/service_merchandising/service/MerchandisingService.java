package cl.instrumentum.service_merchandising.service;

import cl.instrumentum.service_merchandising.model.ProductoMerch;
import cl.instrumentum.service_merchandising.model.VentaMerch;
import cl.instrumentum.service_merchandising.repository.ProductoMerchRepository;
import cl.instrumentum.service_merchandising.repository.VentaMerchRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.util.Optional;

@Service
public class MerchandisingService {

    @Autowired
    private ProductoMerchRepository productoMerchRepository;

    @Autowired
    private VentaMerchRepository ventaMerchRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

// ---------------- Validar banda y evento ---------------- \\

    private void validarBanda(Long idBanda) {
        try {
            webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8081/api/v2/bandas/" + idBanda)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (org.springframework.web.reactive.function.client.WebClientResponseException.NotFound ex) {
            throw new RuntimeException("Error: Banda no encontrada");
        } catch (Exception ex) {
            throw new RuntimeException("Error: No se pudo validar la banda");
        }
    }

    // Ajustar el puerto/ruta al real del microservicio service-eventos cuando exista
    private void validarEvento(Long idEvento) {
        try {
            webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8086/api/v2/eventos/" + idEvento)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (org.springframework.web.reactive.function.client.WebClientResponseException.NotFound ex) {
            throw new RuntimeException("Error: Evento no encontrado");
        } catch (Exception ex) {
            throw new RuntimeException("Error: No se pudo validar el evento");
        }
    }

// ---------------- CRUD Producto_merch ---------------- \\

// Crear producto
    public ProductoMerch crearProducto(ProductoMerch producto) {
        if (producto.getIdBanda() == null)
            throw new RuntimeException("Error: El ID de banda es obligatorio para crear un producto");
        validarBanda(producto.getIdBanda());
        if (producto.getStock() == null) producto.setStock(0);
        return productoMerchRepository.save(producto);
    }

// Listar todos los productos
    public List<ProductoMerch> listarTodosProductos() {
        return productoMerchRepository.findAll();
    }

// Buscar producto por id
    public Optional<ProductoMerch> buscarProductoPorId(Long id) {
        return productoMerchRepository.findById(id);
    }

// Listar productos por banda
    public List<ProductoMerch> listarProductosPorBanda(Long idBanda) {
        validarBanda(idBanda);
        return productoMerchRepository.findByIdBanda(idBanda);
    }

// Actualizar producto
    public ProductoMerch actualizarProducto(ProductoMerch producto) {
        return productoMerchRepository.save(producto);
    }

// Eliminar producto
    @Transactional
    public void eliminarProducto(Long id) {
        ProductoMerch producto = productoMerchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: Producto no encontrado"));

        List<VentaMerch> ventas = ventaMerchRepository.findByProducto(producto);
        if (!ventas.isEmpty())
            throw new RuntimeException("Error: No se puede eliminar el producto porque tiene ventas registradas");

        productoMerchRepository.delete(producto);
    }

// ---------------- CRUD Venta_merch ---------------- \\

// Registrar venta
    @Transactional
    public VentaMerch registrarVenta(VentaMerch venta) {
        if (venta.getProducto() == null || venta.getProducto().getIdProducto() == null)
            throw new RuntimeException("Error: El producto es obligatorio para registrar una venta");

        ProductoMerch producto = productoMerchRepository.findById(venta.getProducto().getIdProducto())
                .orElseThrow(() -> new RuntimeException("Error: Producto no encontrado"));

        if (venta.getCantidad() == null || venta.getCantidad() <= 0)
            throw new RuntimeException("Error: La cantidad debe ser mayor a 0");

        if (producto.getStock() == null || producto.getStock() < venta.getCantidad())
            throw new RuntimeException("Error: Stock insuficiente para realizar la venta");

        if (venta.getIdEventoOrigen() != null) {
            validarEvento(venta.getIdEventoOrigen());
        }

        producto.setStock(producto.getStock() - venta.getCantidad());
        productoMerchRepository.save(producto);

        venta.setProducto(producto);
        venta.setMontoTotal(producto.getPrecio() * venta.getCantidad());
        return ventaMerchRepository.save(venta);
    }

// Listar todas las ventas
    public List<VentaMerch> listarTodasVentas() {
        return ventaMerchRepository.findAll();
    }

// Buscar venta por id
    public Optional<VentaMerch> buscarVentaPorId(Long id) {
        return ventaMerchRepository.findById(id);
    }

// Listar ventas por producto
    public List<VentaMerch> listarVentasPorProducto(Long idProducto) {
        ProductoMerch producto = productoMerchRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Error: Producto no encontrado"));
        return ventaMerchRepository.findByProducto(producto);
    }

// Listar ventas por evento de origen
    public List<VentaMerch> listarVentasPorEvento(Long idEventoOrigen) {
        return ventaMerchRepository.findByIdEventoOrigen(idEventoOrigen);
    }

// Actualizar venta (solo permite corregir el evento de origen asociado)
    public VentaMerch actualizarVenta(VentaMerch venta) {
        return ventaMerchRepository.save(venta);
    }

// Eliminar venta
    @Transactional
    public void eliminarVenta(Long id) {
        VentaMerch venta = ventaMerchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: Venta no encontrada"));

        ProductoMerch producto = venta.getProducto();
        producto.setStock(producto.getStock() + venta.getCantidad());
        productoMerchRepository.save(producto);

        ventaMerchRepository.delete(venta);
    }
}
//

/* metodo por hacer: Permite saber qué se vendió, cuánto dinero recaudó y opcionalmente asociar esa venta al concierto específico donde se instaló el stand de mercancía.*/