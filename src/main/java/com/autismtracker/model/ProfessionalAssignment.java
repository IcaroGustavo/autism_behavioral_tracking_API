package com.autismtracker.model;

import com.autismtracker.security.tenant.BaseTenantEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "professional_assignments",
	indexes = {
		@Index(name = "idx_professional_parent", columnList = "professional_id,parent_id", unique = true),
		@Index(name = "idx_professional", columnList = "professional_id"),
		@Index(name = "idx_parent", columnList = "parent_id")
	}
)
@Getter
@Setter
@NoArgsConstructor
public class ProfessionalAssignment extends BaseTenantEntity {

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "professional_id", nullable = false)
	private User professional;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "parent_id", nullable = false)
	private User parent;

	@Column(name = "active", nullable = false)
	private boolean active = true;

	@Column(name = "notes", length = 500)
	private String notes;

	@Column(name = "created_at", nullable = false)
	private OffsetDateTime createdAt = OffsetDateTime.now();
}

