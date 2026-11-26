package com.awannada.filmrecommender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MovieRecommendationService {

	private static final Logger LOGGER = LoggerFactory.getLogger(MovieRecommendationService.class);
	private static final int MAX_RECOMMENDATIONS = 3;
	private static final int MAX_REASON_LENGTH = 500;

	private final MovieRepository movieRepository;
	private final RecommendationRequestRepository requestRepository;
	private final MovieSelectionClient selectionClient;

	public MovieRecommendationService(
			MovieRepository movieRepository,
			RecommendationRequestRepository requestRepository,
			MovieSelectionClient selectionClient) {
		this.movieRepository = movieRepository;
		this.requestRepository = requestRepository;
		this.selectionClient = selectionClient;
	}

	public RecommendationOutcome recommend(RecommendationForm form) {
		List<Movie> candidates = movieRepository.findByGenreIgnoreCaseOrderByTitleAsc(form.genre());
		if (candidates.isEmpty()) {
			return RecommendationOutcome.noResults();
		}

		try {
			MovieSelectionResponse response = selectionClient.select(form.genre(), form.preferences(), candidates);
			List<MovieSelection> selections = validate(response, candidates);
			if (selections.isEmpty()) {
				return RecommendationOutcome.noResults();
			}

			Map<Long, Movie> candidatesById = new HashMap<>();
			for (Movie movie : candidates) {
				candidatesById.put(movie.getId(), movie);
			}

			RecommendationRequest request = new RecommendationRequest(form.genre(), form.preferences());
			List<RecommendedFilm> recommendations = new ArrayList<>();
			for (int index = 0; index < selections.size(); index++) {
				MovieSelection selection = selections.get(index);
				Movie movie = candidatesById.get(selection.movieId());
				String reason = selection.reason().trim();
				request.addItem(movie, index + 1, reason);
				recommendations.add(new RecommendedFilm(
						movie.getId(), movie.getTitle(), movie.getReleaseYear(), movie.getGenre(), reason));
			}

			requestRepository.saveAndFlush(request);
			return RecommendationOutcome.success(recommendations);
		}
		catch (MovieSelectionException exception) {
			LOGGER.warn("Movie selection failed; no recommendation request was saved.", exception);
			return RecommendationOutcome.error();
		}
	}

	private List<MovieSelection> validate(MovieSelectionResponse response, List<Movie> candidates) {
		if (response == null || response.selections() == null) {
			throw new MovieSelectionException("The AI returned a malformed selection response.");
		}
		List<MovieSelection> selections = response.selections();
		if (selections.size() > MAX_RECOMMENDATIONS) {
			throw new MovieSelectionException("The AI returned too many film selections.");
		}

		Map<Long, Movie> candidatesById = new HashMap<>();
		for (Movie movie : candidates) {
			candidatesById.put(movie.getId(), movie);
		}

		Set<Long> selectedIds = new HashSet<>();
		for (MovieSelection selection : selections) {
			if (selection == null || selection.movieId() == null
					|| selection.reason() == null || selection.reason().isBlank()
					|| selection.reason().length() > MAX_REASON_LENGTH) {
				throw new MovieSelectionException("The AI returned a malformed film selection.");
			}
			if (!candidatesById.containsKey(selection.movieId())) {
				throw new MovieSelectionException("The AI selected a film outside the candidate catalog.");
			}
			if (!selectedIds.add(selection.movieId())) {
				throw new MovieSelectionException("The AI returned a duplicate film selection.");
			}
		}
		return selections;
	}
}
