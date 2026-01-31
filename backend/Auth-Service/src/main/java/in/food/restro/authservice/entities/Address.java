package in.food.restro.authservice.entities;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Entity
@Table(name="user_addr")
public class Address {
	@Id
	@Column(name="user_addr_id")
	private UUID id;
	private String street;
	private String city;
	private String state;
	private String country;
	private String zipCode;


}
