package com.awannada.filmrecommender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
class RecommendationHistoryRepositoryTests {

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private MovieRepository movieRepository;

	@Autowired
	private RecommendationRequestRepository requestRepository;

	@Test
	@Transactional
	void savesAndReloadsRequestWithRankedMovieItems() {
		Movie movie = movieRepository.findAll().get(0);
		RecommendationRequest request = new RecommendationRequest(movie.getGenre(), "A thoughtful mystery");
		request.addItem(movie, 1, "A measured, atmospheric story.");

		RecommendationRequest saved = requestRepository.saveAndFlush(request);
		Long requestId = saved.getId();
		entityManager.clear();

		RecommendationRequest reloaded = requestRepository.findById(requestId).orElseThrow();

		assertNotNull(reloaded.getCreatedAt());
		assertEquals("A thoughtful mystery", reloaded.getPreferences());
		assertEquals(1, reloaded.getItems().size());
		assertEquals(1, reloaded.getItems().get(0).getRank());
		assertEquals("A measured, atmospheric story.", reloaded.getItems().get(0).getReason());
		assertEquals(movie.getId(), reloaded.getItems().get(0).getMovie().getId());
	}
}
