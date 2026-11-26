package com.awannada.filmrecommender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

class FileBackedHistoryRestartTests {

	@Test
	void savedHistorySurvivesApplicationContextRestart(@TempDir Path tempDir) {
		String databaseUrl = "jdbc:h2:file:" + tempDir.resolve("film-history").toAbsolutePath()
				.toString().replace('\\', '/') + ";DB_CLOSE_ON_EXIT=FALSE";
		Long requestId;

		try (ConfigurableApplicationContext context = startApplication(databaseUrl)) {
			MovieRepository movies = context.getBean(MovieRepository.class);
			RecommendationRequestRepository requests = context.getBean(RecommendationRequestRepository.class);
			Movie movie = movies.findAll().get(0);
			RecommendationRequest request = new RecommendationRequest(movie.getGenre(), "Persistent history check");
			request.addItem(movie, 1, "Saved before restart.");
			requestId = requests.saveAndFlush(request).getId();
		}

		try (ConfigurableApplicationContext context = startApplication(databaseUrl)) {
			RecommendationRequest reloaded = context.getBean(RecommendationRequestRepository.class)
					.findAllByOrderByCreatedAtDesc().get(0);

			assertEquals(requestId, reloaded.getId());
			assertNotNull(reloaded.getCreatedAt());
			assertEquals("Persistent history check", reloaded.getPreferences());
			assertEquals("Saved before restart.", reloaded.getItems().get(0).getReason());
			assertEquals(1, reloaded.getItems().get(0).getRank());
		}
	}

	private ConfigurableApplicationContext startApplication(String databaseUrl) {
		return new SpringApplicationBuilder(FilmRecommenderApplication.class)
				.profiles("test")
				.run(
						"--spring.datasource.url=" + databaseUrl,
						"--spring.jpa.hibernate.ddl-auto=update",
						"--spring.main.web-application-type=none");
	}
}
