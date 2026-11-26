package com.awannada.filmrecommender;

import java.util.List;

public record RecommendationOutcome(Status status, List<RecommendedFilm> recommendations, String message) {

	public RecommendationOutcome {
		recommendations = List.copyOf(recommendations);
	}

	public static RecommendationOutcome success(List<RecommendedFilm> recommendations) {
		return new RecommendationOutcome(Status.SUCCESS, recommendations, "");
	}

	public static RecommendationOutcome noResults() {
		return new RecommendationOutcome(Status.NO_RESULTS, List.of(), "No matching films were found.");
	}

	public static RecommendationOutcome error() {
		return new RecommendationOutcome(
				Status.ERROR, List.of(), "Recommendations are temporarily unavailable. Please try again.");
	}

	public enum Status {
		SUCCESS,
		NO_RESULTS,
		ERROR
	}
}
