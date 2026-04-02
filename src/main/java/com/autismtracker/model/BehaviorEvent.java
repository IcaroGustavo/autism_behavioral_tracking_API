package com.autismtracker.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "behavior_events")
@Getter
@Setter
@NoArgsConstructor
public class BehaviorEvent {

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID")
	@Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
	private UUID id;

	@Column(name = "event_date_time", nullable = false)
	private LocalDateTime eventDateTime;

	@Enumerated(EnumType.STRING)
	@Column(name = "intensity", length = 20)
	private EventIntensity intensity;

	@Column(name = "duration_minutes")
	private Integer durationMinutes;

	@Column(name = "antecedent", length = 2000)
	private String antecedent;

	@Column(name = "behavior", length = 2000)
	private String behavior;

	@Column(name = "consequence", length = 2000)
	private String consequence;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;
}

