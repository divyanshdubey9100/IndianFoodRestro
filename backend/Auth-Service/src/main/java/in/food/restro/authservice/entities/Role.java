package in.food.restro.authservice.entities;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
@Table(name="user_roles")
public class Role {
	@Id
	@Column(name="user_role_id")
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	
	@Column(name="User_role_name",unique = true, nullable=false)
	private String name;
	
	
}
