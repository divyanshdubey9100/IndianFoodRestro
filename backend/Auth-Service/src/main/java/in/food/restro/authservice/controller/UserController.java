package in.food.restro.authservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.food.restro.authservice.dtos.UserDto;
import in.food.restro.authservice.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
@Slf4j
public class UserController {
	
	private final UserService userService;
	
	@PostMapping("/create")
	public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto){
		log.info("Received request to create user with username: {}", userDto.getUsername());
		UserDto savedUser = userService.createUser(userDto);
		log.info("User created successfully with ID: {}", savedUser.getId());
		return ResponseEntity.ok(savedUser);
	}
}