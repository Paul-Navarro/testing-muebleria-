package com.example.repository;

import com.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Buscar usuario por username
    Optional<User> findByUsername(String username);

    // Verificar si ya existe un usuario con ese username
    boolean existsByUsername(String username);
    
    // Buscar todos los usuarios con sus datos de cliente cargados
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.cliente")
    List<User> findAllWithCliente();
}
