package com.autismtracker.wearables;

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
@Table(name = "physiological_metrics", indexes = {
	@Index(name = "idx_metric_patient_time", columnList = "patient_id, recorded_at"),
	@Index(name = "idx_metric_tenant_time", columnList = "tenant_id, recorded_at")
})
public class PhysiologicalMetric extends BaseTenantEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "patient_id", nullable = false, length = 64)
	private String patientId;

	@Column(name = "sensor", nullable = false, length = 32)
	private String sensor;

	@Column(name = "value", nullable = false)
	private Double value;

	@Column(name = "unit", nullable = false, length = 16)
	private String unit;

	@Column(name = "recorded_at", nullable = false)
	private OffsetDateTime recordedAt;

	@Column(name = "source", length = 64)
	private String source;

	@Column(name = "quality", length = 16)
	private String quality;

	public Long getId() { return id; }
	public String getPatientId() { return patientId; }
	public void setPatientId(String patientId) { this.patientId = patientId; }
	public String getSensor() { return sensor; }
	public void setSensor(String sensor) { this.sensor = sensor; }
	public Double getValue() { return value; }
	public void setValue(Double value) { this.value = value; }
	public String getUnit() { return unit; }
	public void setUnit(String unit) { this.unit = unit; }
	public OffsetDateTime getRecordedAt() { return recordedAt; }
	public void setRecordedAt(OffsetDateTime recordedAt) { this.recordedAt = recordedAt; }
	public String getSource() { return source; }
	public void setSource(String source) { this.source = source; }
	public String getQuality() { return quality; }
	public void setQuality(String quality) { this.quality = quality; }
}

