package in.food.restro.authservice.service;

import in.food.restro.authservice.dtos.UserDto;

public interface UserService {
	UserDto createUser(UserDto userDto);
	
	UserDto getUserById(String id);
	
	UserDto getUserByUsername(String username);
	
	UserDto getUserByEmail(String email);
	
	UserDto getUserByPhoneNumber(String phoneNumber);
	
	UserDto updateUser(String id, UserDto userDto);
	
	UserDto deleteUser(String id);
	
	UserDto assignRoleToUser(String userId, String roleId);
	
	UserDto removeRoleFromUser(String userId, String roleId);
	
	UserDto activateUser(String id);
	
	UserDto deactivateUser(String id);
	
	UserDto changeUserPassword(String id, String newPassword);
	
	boolean isUsernameExists(String username);
	
	boolean isEmailExists(String email);
	
	boolean isPhoneNumberExists(String phoneNumber);
	
	boolean isUserIdExists(String id);
	
	void updateLastLoginAt(String id);
	
	Iterable<UserDto> getAllUsers(int pageNumber, int pageSize, String sortBy, String sortDir);
	
	UserDto getUserRoleDetailsById(String id);
}
