package in.food.restro.authservice.dtos;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {
	private UUID id;
	private String street;
	private String city;
	private String state;
	private String country;
	private String zipCode;
}
