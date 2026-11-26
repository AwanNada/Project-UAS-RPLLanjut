package com.awannada.filmrecommender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Import(MovieRecommendationServiceTests.StubConfiguration.class)
@Transactional
class MovieRecommendationServiceTests {

	@Autowired
	private MovieRecommendationService recommendationService;

	@Autowired
	private MovieRepository movieRepository;

	@Autowired
	private RecommendationRequestRepository requestRepository;

	@Autowired
	private StubMovieSelectionClient selectionClient;

	@BeforeEach
	void resetTestState() {
		requestRepository.deleteAll();
		selectionClient.reset();
	}

	@Test
	void savesAndReturnsRecommendationsFromMatchingGenreCandidates() {
		Movie expected = movieRepository.findByGenreIgnoreCaseOrderByTitleAsc("Mystery").get(0);
		selectionClient.respond(List.of(new MovieSelection(expected.getId(), "A layered mystery.")));

		RecommendationOutcome outcome = recommendationService.recommend(
				new RecommendationForm("Mystery", "I like a clever investigation."));

		assertEquals(RecommendationOutcome.Status.SUCCESS, outcome.status());
		assertEquals(1, requestRepository.count());
		assertEquals(expected.getId(), outcome.recommendations().get(0).movieId());
		assertEquals("A layered mystery.", outcome.recommendations().get(0).reason());
		assertFalse(selectionClient.lastCandidates().isEmpty());
		assertTrue(selectionClient.lastCandidates().stream().allMatch(movie -> movie.getGenre().equals("Mystery")));
	}

	@Test
	void emptyGenreDoesNotCallSelectorOrSaveRequest() {
		RecommendationOutcome outcome = recommendationService.recommend(
				new RecommendationForm("Not in catalog", "Something atmospheric."));

		assertEquals(RecommendationOutcome.Status.NO_RESULTS, outcome.status());
		assertEquals(0, selectionClient.callCount());
		assertEquals(0, requestRepository.count());
	}

	@Test
	void unknownMovieIdIsRejectedWithoutSaving() {
		selectionClient.respond(List.of(new MovieSelection(Long.MAX_VALUE, "Unknown title.")));

		assertFailedWithoutSaving();
	}

	@Test
	void duplicateMovieIdsAreRejectedWithoutSaving() {
		Long movieId = movieRepository.findByGenreIgnoreCaseOrderByTitleAsc("Mystery").get(0).getId();
		selectionClient.respond(List.of(
				new MovieSelection(movieId, "First reason."),
				new MovieSelection(movieId, "Second reason.")));

		assertFailedWithoutSaving();
	}

	@Test
	void moreThanThreeSelectionsAreRejectedWithoutSaving() {
		List<Movie> candidates = IntStream.range(0, 4)
				.mapToObj(index -> movieRepository.save(
						new Movie("Overflow film " + index, 2000 + index, "Overflow", "A test candidate.")))
				.toList();
		selectionClient.respond(candidates.stream()
				.map(movie -> new MovieSelection(movie.getId(), "Test reason."))
				.toList());

		RecommendationOutcome outcome = recommendationService.recommend(
				new RecommendationForm("Overflow", "A test preference."));

		assertEquals(RecommendationOutcome.Status.ERROR, outcome.status());
		assertEquals(0, requestRepository.count());
	}

	@Test
	void malformedSelectionResponseIsRejectedWithoutSaving() {
		selectionClient.respond(null);

		assertFailedWithoutSaving();
	}

	@Test
	void providerFailureIsVisibleAndDoesNotSaveRequest() {
		selectionClient.failWith(new MovieSelectionException("Provider unavailable."));

		RecommendationOutcome outcome = recommendationService.recommend(
				new RecommendationForm("Mystery", "A quiet story."));

		assertEquals(RecommendationOutcome.Status.ERROR, outcome.status());
		assertNotNull(outcome.message());
		assertFalse(outcome.message().isBlank());
		assertEquals(0, requestRepository.count());
	}

	private void assertFailedWithoutSaving() {
		RecommendationOutcome outcome = recommendationService.recommend(
				new RecommendationForm("Mystery", "A clever story."));

		assertEquals(RecommendationOutcome.Status.ERROR, outcome.status());
		assertNotNull(outcome.message());
		assertFalse(outcome.message().isBlank());
		assertEquals(0, requestRepository.count());
	}

	@TestConfiguration(proxyBeanMethods = false)
	static class StubConfiguration {

		@Bean
		@Primary
		StubMovieSelectionClient stubMovieSelectionClient() {
			return new StubMovieSelectionClient();
		}
	}

	static class StubMovieSelectionClient implements MovieSelectionClient {

		private MovieSelectionResponse response = new MovieSelectionResponse(List.of());
		private RuntimeException failure;
		private List<Movie> lastCandidates = List.of();
		private int calls;

		void reset() {
			response = new MovieSelectionResponse(List.of());
			failure = null;
			lastCandidates = List.of();
			calls = 0;
		}

		void respond(List<MovieSelection> selections) {
			response = selections == null ? null : new MovieSelectionResponse(selections);
		}

		void failWith(RuntimeException failure) {
			this.failure = failure;
		}

		int callCount() {
			return calls;
		}

		List<Movie> lastCandidates() {
			return lastCandidates;
		}

		@Override
		public MovieSelectionResponse select(String genre, String preferences, List<Movie> candidates) {
			calls++;
			lastCandidates = List.copyOf(candidates);
			if (failure != null) {
				throw failure;
			}
			return response;
		}
	}
}
