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

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "daily_logs")
@Getter
@Setter
@NoArgsConstructor
public class DailyLog {

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID")
	@Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
	private UUID id;

	@Column(name = "date", nullable = false)
	private LocalDate date;

	@Column(name = "sleep_hours")
	private Integer sleepHours;

	@Enumerated(EnumType.STRING)
	@Column(name = "diet_quality", length = 20)
	private DietQuality dietQuality;

	@Column(name = "notes", length = 2000)
	private String notes;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;
}

