package com.autismtracker.service;

import com.autismtracker.model.BehaviorEvent;
import com.autismtracker.model.User;
import com.autismtracker.repository.BehaviorEventRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class AnalysisService {
	private static final Set<String> STOP_WORDS = Set.of(
		"the","a","an","and","or","but","if","then","when","at","by","for","with","about","against",
		"between","into","through","during","before","after","above","below","to","from","up","down",
		"in","out","on","off","over","under","again","further","here","there","all","any","both","each",
		"few","more","most","other","some","such","no","nor","not","only","own","same","so","than","too",
		"very","can","will","just"
	);
	private static final Pattern SPLIT_PATTERN = Pattern.compile("[^a-zA-Z]+");

	private final BehaviorEventRepository behaviorEventRepository;

	public AnalysisService(BehaviorEventRepository behaviorEventRepository) {
		this.behaviorEventRepository = behaviorEventRepository;
	}

	public Map<String, Long> mostFrequentAntecedentWords(User user, int limit) {
		List<BehaviorEvent> events = behaviorEventRepository.findByUser(user);
		Map<String, Long> freq = new HashMap<>();
		for (BehaviorEvent e : events) {
			if (e.getAntecedent() == null) continue;
			String[] tokens = SPLIT_PATTERN.split(e.getAntecedent().toLowerCase(Locale.ROOT));
			for (String t : tokens) {
				if (t.isBlank()) continue;
				if (STOP_WORDS.contains(t)) continue;
				freq.put(t, freq.getOrDefault(t, 0L) + 1);
			}
		}
		return freq.entrySet().stream()
			.sorted(Map.Entry.<String, Long>comparingByValue().reversed())
			.limit(limit)
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
				(a,b) -> a, LinkedHashMap::new));
	}
}

