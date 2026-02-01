package in.food.restro.authservice.dtos;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import in.food.restro.authservice.entities.Address;
import in.food.restro.authservice.entities.Role;
import in.food.restro.authservice.enums.Provider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
	
	private UUID id;
	private String username;
	private String password;
	private String email;
	private boolean isActive=true;
	private String phoneNumber;
	private String dob;
	private String gender;
	private String profileImage;
	private AddressDto address;
	private String firstName;
	private String middleName;
	private String lastName;
	private Instant createdAt=Instant.now();
	private Instant updatedAt=Instant.now();
	private Instant lastLoginAt=Instant.now();
	
	private Provider provider=Provider.LOCAL;
	
	private Set<RoleDto> roles=new HashSet<>();
}
