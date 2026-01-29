package in.food.restro.authservice.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import in.food.restro.authservice.entities.User;

public interface UserRepository extends JpaRepository<User, UUID>{
	
	Optional<User> findByUsername(String username);
	
	Optional<User> findByEmail(String email);
	
	Optional<User> findByPhoneNumber(String phoneNumber);
	
	Optional<User> findById(UUID id);
	
	boolean existsByUsername(String username);
	
	boolean existsByEmail(String email);
	
	boolean existsByPhoneNumber(String phoneNumber);
	
	boolean existsById(UUID id);
}
