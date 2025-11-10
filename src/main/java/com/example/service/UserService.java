package com.example.service;

import com.example.model.Role;
import com.example.model.User;
import com.example.model.Cliente;
import com.example.repository.UserRepository;
import com.example.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Lazy
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Optional -> orElseThrow
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().name()) // genera ROLE_ADMIN / ROLE_USER / ROLE_CLIENT
                .build();
    }

    // Registrar un nuevo usuario
    public void registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    // Inicializar usuario administrador
    public void initializeAdminUser() {
        // Optional -> isEmpty / isPresent
        if (userRepository.findByUsername("admin").isEmpty()) {
            // Crear cliente por defecto para el administrador
            Cliente adminCliente = new Cliente();
            adminCliente.setNombre("Administrador");
            adminCliente.setApellido("Sistema");
            adminCliente.setEmail("admin@sistema.com");
            adminCliente.setCedula("0000000-0");
            adminCliente.setTelefono("000-000-000");
            adminCliente.setDireccion("Sistema");
            adminCliente.setUsername("admin");
            
            // Guardar el cliente
            clienteRepository.save(adminCliente);
            
            // Crear usuario administrador con cliente asociado
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRole(Role.ADMIN);
            admin.setCliente(adminCliente);
            userRepository.save(admin);
            
            System.out.println("Usuario administrador y cliente por defecto creados con éxito");
        } else {
            System.out.println("Usuario administrador ya existe");
            
            // Verificar si el admin tiene cliente asignado
            User existingAdmin = userRepository.findByUsername("admin").get();
            if (existingAdmin.getCliente() == null) {
                // Si no tiene cliente, crear uno y asignarlo
                Cliente adminCliente = new Cliente();
                adminCliente.setNombre("Administrador");
                adminCliente.setApellido("Sistema");
                adminCliente.setEmail("admin@sistema.com");
                adminCliente.setCedula("0000000-0");
                adminCliente.setTelefono("000-000-000");
                adminCliente.setDireccion("Sistema");
                adminCliente.setUsername("admin");
                
                clienteRepository.save(adminCliente);
                
                existingAdmin.setCliente(adminCliente);
                userRepository.save(existingAdmin);
                
                System.out.println("Cliente por defecto asignado al administrador existente");
            }
        }
    }

    // Listar todos los usuarios
    public List<User> getAllUsers() {
        return userRepository.findAllWithCliente();
    }

    // Actualizar un usuario existente
    public void updateUser(Long id, User user) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        existingUser.setUsername(user.getUsername());
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        existingUser.setRole(user.getRole());
        userRepository.save(existingUser);
    }

    // Eliminar un usuario
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado");
        }
        userRepository.deleteById(id);
    }

    public void changePassword(Long id, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // Verifica credenciales contra AuthenticationManager
    public boolean checkPasswordExists(String username, String password) {
        // Primero verifica que el usuario exista
        if (userRepository.findByUsername(username).isEmpty()) {
            return false;
        }
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            return true;
        } catch (AuthenticationException e) {
            return false;
        }
    }
}
