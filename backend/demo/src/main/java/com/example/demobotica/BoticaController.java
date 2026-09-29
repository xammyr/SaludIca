package com.example.demobotica;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/botica")
@CrossOrigin(origins = "*") // Permite la conexión desde el frontend
public class BoticaController {

    // Base de datos estática
    private static final Map<String, Usuario> USUARIOS = new HashMap<>();
    private static final List<Producto> INVENTARIO = new ArrayList<>();

    static {
        // Usuarios (username, password, rol)
        USUARIOS.put("admin", new Usuario("admin", "admin123", "ADMIN"));
        USUARIOS.put("caja1", new Usuario("caja1", "caja123", "CAJERO"));

        // Inventario inicial
        INVENTARIO.add(new Producto(1, "Paracetamol 500mg", 2.50));
        INVENTARIO.add(new Producto(2, "Amoxicilina 500mg", 5.00));
        INVENTARIO.add(new Producto(3, "Ibuprofeno 400mg", 3.20));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Usuario user = USUARIOS.get(request.username());
        
        if (user != null && user.password().equals(request.password())) {
            // Asignar tiempo de sesión según el rol
            int minutosSesion = user.rol().equals("ADMIN") ? 5 : 10;
            long expiracionMs = System.currentTimeMillis() + (minutosSesion * 60 * 1000);
            
            // Simulación de token
            String mockToken = Base64.getEncoder().encodeToString((user.username() + ":" + expiracionMs).getBytes());
            
            return ResponseEntity.ok(new LoginResponse(user.username(), user.rol(), mockToken, expiracionMs));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
    }

    @GetMapping("/productos")
    public ResponseEntity<List<Producto>> getProductos() {
        return ResponseEntity.ok(INVENTARIO);
    }

    // Registros (Records) para manejar los datos
    public record Usuario(String username, String password, String rol) {}
    public record Producto(int id, String nombre, double precio) {}
    public record LoginRequest(String username, String password) {}
    public record LoginResponse(String username, String rol, String token, long expiracionMs) {}
}