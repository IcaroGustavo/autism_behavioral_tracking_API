package com.autismtracker.service;

import com.autismtracker.model.BehaviorEvent;
import com.autismtracker.model.User;
import com.autismtracker.repository.BehaviorEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AnalysisServiceTest {

	private BehaviorEventRepository behaviorEventRepository;
	private AnalysisService analysisService;

	@BeforeEach
	void setUp() {
		behaviorEventRepository = mock(BehaviorEventRepository.class);
		analysisService = new AnalysisService(behaviorEventRepository);
	}

	@Test
	void mostFrequentAntecedentWords_shouldCountIgnoringStopwords_andRespectLimit() {
		
		User user = new User();
		BehaviorEvent e1 = new BehaviorEvent();
		e1.setAntecedent("The loud noise at the mall");
		BehaviorEvent e2 = new BehaviorEvent();
		e2.setAntecedent("Noise and loud music in the car");
		BehaviorEvent e3 = new BehaviorEvent();
		e3.setAntecedent("Car ride was loud");
		when(behaviorEventRepository.findByUser(user)).thenReturn(List.of(e1, e2, e3));

		
		Map<String, Long> result = analysisService.mostFrequentAntecedentWords(user, 2);

		
		assertThat(result).isInstanceOf(LinkedHashMap.class);
		assertThat(result).containsKeys("loud", "noise");
		assertThat(result.keySet().size()).isEqualTo(2);
		assertThat(result.get("loud")).isGreaterThanOrEqualTo(2L);
	}
}

