package co.com.api.co.com.api.controller;

import co.com.api.co.com.api.domain.clientes.Cliente;
import co.com.api.co.com.api.domain.clientes.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    // GET - Obtener todos los clientes
    @GetMapping
    public ResponseEntity<List<Cliente>> getAllClientes() {
        try {
            List<Cliente> clientes = clienteRepository.findAll();
            return ResponseEntity.ok(clientes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Obtener cliente por ID
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> getClienteById(@PathVariable Long id) {
        try {
            Optional<Cliente> cliente = clienteRepository.findById(id);
            return cliente.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Obtener cliente por cédula
    @GetMapping("/cedula/{cedula}")
    public ResponseEntity<Cliente> getClienteByCedula(@PathVariable String cedula) {
        try {
            Optional<Cliente> cliente = clienteRepository.findByCedula(cedula);
            return cliente.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Obtener cliente por correo
    @GetMapping("/correo/{correo}")
    public ResponseEntity<Cliente> getClienteByCorreo(@PathVariable String correo) {
        try {
            Optional<Cliente> cliente = clienteRepository.findByCorreo(correo);
            return cliente.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // POST - Crear nuevo cliente
    @PostMapping
    public ResponseEntity<Cliente> createCliente(@RequestBody Cliente cliente) {
        try {
            // Verificar si ya existe un cliente con la misma cédula
            Optional<Cliente> clienteExistente = clienteRepository.findByCedula(cliente.getCedula());
            if (clienteExistente.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }

            // Verificar si ya existe un cliente con el mismo correo
            Optional<Cliente> clienteConCorreo = clienteRepository.findByCorreo(cliente.getCorreo());
            if (clienteConCorreo.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }

            Cliente nuevoCliente = clienteRepository.save(cliente);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCliente);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PUT - Actualizar cliente
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> updateCliente(@PathVariable Long id, @RequestBody Cliente clienteActualizado) {
        try {
            Optional<Cliente> clienteExistente = clienteRepository.findById(id);
            if (clienteExistente.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Cliente cliente = clienteExistente.get();
            cliente.setNombre(clienteActualizado.getNombre());
            cliente.setCedula(clienteActualizado.getCedula());
            cliente.setTelefono(clienteActualizado.getTelefono());
            cliente.setCorreo(clienteActualizado.getCorreo());
            cliente.setDireccion(clienteActualizado.getDireccion());

            Cliente clienteGuardado = clienteRepository.save(cliente);
            return ResponseEntity.ok(clienteGuardado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PATCH - Actualizar datos de contacto del cliente
    @PatchMapping("/{id}/contacto")
    public ResponseEntity<Cliente> updateContacto(@PathVariable Long id, 
                                                 @RequestParam(required = false) String telefono,
                                                 @RequestParam(required = false) String correo,
                                                 @RequestParam(required = false) String direccion) {
        try {
            Optional<Cliente> cliente = clienteRepository.findById(id);
            if (cliente.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Cliente clienteActualizado = cliente.get();
            if (telefono != null) {
                clienteActualizado.setTelefono(telefono);
            }
            if (correo != null) {
                clienteActualizado.setCorreo(correo);
            }
            if (direccion != null) {
                clienteActualizado.setDireccion(direccion);
            }

            Cliente clienteGuardado = clienteRepository.save(clienteActualizado);
            return ResponseEntity.ok(clienteGuardado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // DELETE - Eliminar cliente
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCliente(@PathVariable Long id) {
        try {
            if (!clienteRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }

            clienteRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
