package com.awannada.filmrecommender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(HomeControllerTests.StubConfiguration.class)
@Transactional
class HomeControllerTests {

	@Autowired
	private MockMvc mockMvc;

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
	void homePageShowsGenresAndForm() throws Exception {
		MvcResult result = mockMvc.perform(get("/"))
				.andExpect(status().isOk())
				.andExpect(view().name("index"))
				.andReturn();

		assertTrue(result.getModelAndView().getModel().containsKey("genres"));
		assertTrue(result.getResponse().getContentAsString().contains("Mystery"));
		assertTrue(result.getResponse().getContentAsString().contains("maxlength=\"300\""));
	}

	@Test
	void invalidRequiredOrOversizedInputDoesNotCallModel() throws Exception {
		MvcResult missingGenre = mockMvc.perform(post("/recommendations")
						.param("genre", "")
						.param("preferences", "A clever story."))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/"))
				.andReturn();
		MvcResult missingPreference = mockMvc.perform(post("/recommendations")
						.param("genre", "Mystery")
						.param("preferences", ""))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/"))
				.andReturn();
		MvcResult oversizedPreference = mockMvc.perform(post("/recommendations")
						.param("genre", "Mystery")
						.param("preferences", "x".repeat(301)))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/"))
				.andReturn();

		String genreError = mockMvc.perform(get("/").flashAttrs(missingGenre.getFlashMap()))
				.andReturn().getResponse().getContentAsString();
		String requiredError = mockMvc.perform(get("/").flashAttrs(missingPreference.getFlashMap()))
				.andReturn().getResponse().getContentAsString();
		String sizeError = mockMvc.perform(get("/").flashAttrs(oversizedPreference.getFlashMap()))
				.andReturn().getResponse().getContentAsString();

		assertTrue(genreError.contains("Choose a genre."));
		assertTrue(requiredError.contains("Enter your preference."));
		assertTrue(sizeError.contains("Use at most 300 characters."));
		assertEquals(0, selectionClient.callCount());
		assertEquals(0, requestRepository.count());
	}

	@Test
	void successfulPostRedirectsAndShowsRecommendationsAndHistory() throws Exception {
		Movie expected = movieRepository.findByGenreIgnoreCaseOrderByTitleAsc("Mystery").get(0);
		selectionClient.respond(List.of(new MovieSelection(expected.getId(), "A satisfying puzzle.")));

		MvcResult postResult = mockMvc.perform(post("/recommendations")
						.param("genre", "Mystery")
						.param("preferences", "I like clever investigations."))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/"))
				.andReturn();
		MvcResult page = mockMvc.perform(get("/").flashAttrs(postResult.getFlashMap()))
				.andExpect(status().isOk())
				.andReturn();
		String html = page.getResponse().getContentAsString();

		assertTrue(html.contains(expected.getTitle()));
		assertTrue(html.contains("A satisfying puzzle."));
		assertTrue(html.contains("I like clever investigations."));
		assertEquals(1, selectionClient.callCount());
		assertEquals(1, requestRepository.count());
	}

	@Test
	void noResultsAreVisibleWithoutSavingHistory() throws Exception {
		MvcResult postResult = mockMvc.perform(post("/recommendations")
						.param("genre", "Not in catalog")
						.param("preferences", "Something atmospheric."))
				.andExpect(status().is3xxRedirection())
				.andReturn();
		String html = mockMvc.perform(get("/").flashAttrs(postResult.getFlashMap()))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		assertTrue(html.contains("No matching films were found."));
		assertEquals(0, selectionClient.callCount());
		assertEquals(0, requestRepository.count());
	}

	@Test
	void providerErrorsAreVisibleWithoutSavingHistory() throws Exception {
		selectionClient.failWith(new MovieSelectionException("Provider unavailable."));
		MvcResult postResult = mockMvc.perform(post("/recommendations")
						.param("genre", "Mystery")
						.param("preferences", "A quiet story."))
				.andExpect(status().is3xxRedirection())
				.andReturn();
		String html = mockMvc.perform(get("/").flashAttrs(postResult.getFlashMap()))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		assertTrue(html.contains("Recommendations are temporarily unavailable. Please try again."));
		assertEquals(1, selectionClient.callCount());
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
		private int calls;

		void reset() {
			response = new MovieSelectionResponse(List.of());
			failure = null;
			calls = 0;
		}

		void respond(List<MovieSelection> selections) {
			response = new MovieSelectionResponse(selections);
		}

		void failWith(RuntimeException failure) {
			this.failure = failure;
		}

		int callCount() {
			return calls;
		}

		@Override
		public MovieSelectionResponse select(String genre, String preferences, List<Movie> candidates) {
			calls++;
			if (failure != null) {
				throw failure;
			}
			return response;
		}
	}
}
