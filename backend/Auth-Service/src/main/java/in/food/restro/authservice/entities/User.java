package in.food.restro.authservice.entities;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Component;
import in.food.restro.authservice.enums.Provider;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Entity
@Table(name="users")
@Component
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name="user_id")
	private UUID id;
	
	@Column(name="user_name", unique=true)
	private String username;
	
	private String password;
	@Column(name="user_email",unique=true)
	private String email;
	
	private boolean isActive=true;
	@Column(name="mobile", unique=true)
	private String phoneNumber;
	private String dob;
	private String gender;
	private String profileImage;
	
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "user_user_addr",
		joinColumns = @JoinColumn(name = "user_id"),
		inverseJoinColumns = @JoinColumn(name = "user_addr_id")
	)
	private Address address;
	private String firstName;
	private String middleName;
	private String lastName;
	private Instant createdAt=Instant.now();
	private Instant updatedAt=Instant.now();
	private Instant lastLoginAt=Instant.now();
	
	@Enumerated(EnumType.STRING)
	private Provider provider=Provider.LOCAL;
	
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name="user_user_roles",
		joinColumns = @JoinColumn(name="user_id"),
		inverseJoinColumns = @JoinColumn(name="user_role_id")
	)
	@Builder.Default
	private Set<Role> roles=new HashSet<>();
	
	@PrePersist
	protected void oncreate() {
		Instant now=Instant.now();
		if(createdAt==null) {
			createdAt=now;
		}else {
			updatedAt=now;
		}
	}
	
	@PreUpdate
	protected void onUpdate() {
		updatedAt=Instant.now();
	}
}
