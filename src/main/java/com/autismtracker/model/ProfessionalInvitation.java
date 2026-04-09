package com.autismtracker.model;

import com.autismtracker.security.tenant.BaseTenantEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "professional_invitations",
	indexes = {
		@Index(name = "idx_invitee_email", columnList = "professional_email"),
		@Index(name = "idx_inviter_parent", columnList = "inviter_parent_id")
	}
)
@Getter
@Setter
@NoArgsConstructor
public class ProfessionalInvitation extends BaseTenantEntity {

	public enum Status {
		PENDING,
		ACCEPTED,
		REJECTED,
		EXPIRED
	}

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "inviter_parent_id", nullable = false)
	private User inviterParent;

	@Column(name = "professional_email", nullable = false, length = 255)
	private String professionalEmail;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	private Status status = Status.PENDING;

	@Column(name = "message", length = 500)
	private String message;

}

