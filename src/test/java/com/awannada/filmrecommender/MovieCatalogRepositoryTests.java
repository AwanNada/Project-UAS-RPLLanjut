package com.awannada.filmrecommender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class MovieCatalogRepositoryTests {

	@Autowired
	private MovieRepository movieRepository;

	@Autowired
	private MovieCatalogSeeder movieCatalogSeeder;

	@Test
	void seedingTwiceDoesNotCreateDuplicateMovies() {
		movieCatalogSeeder.seedCatalog();
		long countAfterFirstSeed = movieRepository.count();

		movieCatalogSeeder.seedCatalog();

		assertEquals(countAfterFirstSeed, movieRepository.count());
	}

	@Test
	void availableGenresAreDistinct() {
		List<String> genres = movieRepository.findDistinctGenres();

		assertFalse(genres.isEmpty());
		assertEquals(genres.size(), new HashSet<>(genres).size());
	}
}
