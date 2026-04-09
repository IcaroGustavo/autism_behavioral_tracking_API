package com.autismtracker.model;

import com.autismtracker.security.tenant.BaseTenantEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "professional_profiles")
@Getter
@Setter
@NoArgsConstructor
public class ProfessionalProfile extends BaseTenantEntity {

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
	private UUID id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private User user;

	@Enumerated(EnumType.STRING)
	@Column(name = "specialty", nullable = false, length = 40)
	private ProfessionalSpecialty specialty;

	@Column(name = "registration_number", length = 64)
	private String registrationNumber;

}

