package in.food.restro.authservice.service;

import in.food.restro.authservice.dtos.UserDto;

public interface AuthService {

	UserDto registerUser(UserDto userDto);
	
	UserDto authenticateUser(String username, String password);
	
	UserDto loginUser(String username, String password);
	
	UserDto logoutUser(String username);
}
