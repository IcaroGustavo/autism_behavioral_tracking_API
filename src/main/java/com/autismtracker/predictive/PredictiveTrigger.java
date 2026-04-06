package com.autismtracker.predictive;

import com.autismtracker.security.tenant.BaseTenantEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "predictive_triggers", indexes = {
	@Index(name = "idx_trigger_patient_time", columnList = "patient_id, occurred_at")
})
public class PredictiveTrigger extends BaseTenantEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "patient_id", nullable = false, length = 64)
	private String patientId;

	@Column(name = "antecedent_id", length = 64)
	private String antecedentId;

	@Column(name = "behavior_id", length = 64)
	private String behaviorId;

	@Column(name = "consequence_id", length = 64)
	private String consequenceId;

	@Column(name = "sleep_score")
	private Integer sleepScore;

	@Column(name = "diet_category", length = 32)
	private String dietCategory;

	@Column(name = "weather_code", length = 16)
	private String weatherCode;

	@Column(name = "environment_notes", length = 512)
	private String environmentNotes;

	@Column(name = "occurred_at", nullable = false)
	private OffsetDateTime occurredAt = OffsetDateTime.now();

	@Column(name = "risk_level", length = 16)
	private String riskLevel;

	public Long getId() { return id; }
	public String getPatientId() { return patientId; }
	public void setPatientId(String patientId) { this.patientId = patientId; }
	public String getAntecedentId() { return antecedentId; }
	public void setAntecedentId(String antecedentId) { this.antecedentId = antecedentId; }
	public String getBehaviorId() { return behaviorId; }
	public void setBehaviorId(String behaviorId) { this.behaviorId = behaviorId; }
	public String getConsequenceId() { return consequenceId; }
	public void setConsequenceId(String consequenceId) { this.consequenceId = consequenceId; }
	public Integer getSleepScore() { return sleepScore; }
	public void setSleepScore(Integer sleepScore) { this.sleepScore = sleepScore; }
	public String getDietCategory() { return dietCategory; }
	public void setDietCategory(String dietCategory) { this.dietCategory = dietCategory; }
	public String getWeatherCode() { return weatherCode; }
	public void setWeatherCode(String weatherCode) { this.weatherCode = weatherCode; }
	public String getEnvironmentNotes() { return environmentNotes; }
	public void setEnvironmentNotes(String environmentNotes) { this.environmentNotes = environmentNotes; }
	public OffsetDateTime getOccurredAt() { return occurredAt; }
	public void setOccurredAt(OffsetDateTime occurredAt) { this.occurredAt = occurredAt; }
	public String getRiskLevel() { return riskLevel; }
	public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
}

