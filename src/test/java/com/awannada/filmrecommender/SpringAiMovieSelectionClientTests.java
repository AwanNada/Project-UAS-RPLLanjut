package com.awannada.filmrecommender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;

class SpringAiMovieSelectionClientTests {

	@Test
	void parsesStructuredSelectionsAndSendsOnlyProvidedCandidates() {
		ChatModel chatModel = mock(ChatModel.class);
		when(chatModel.call(any(Prompt.class))).thenReturn(chatResponse(
				"{\"selections\":[{\"movieId\":42,\"reason\":\"A thoughtful mystery.\"}]}"));
		when(chatModel.getOptions()).thenReturn(ChatOptions.builder().build());
		SpringAiMovieSelectionClient client = new SpringAiMovieSelectionClient(ChatClient.builder(chatModel));
		Movie candidate = new Movie("Candidate film", 2020, "Mystery", "A candidate synopsis.");
		Movie excluded = new Movie("Excluded film", 2021, "Comedy", "Not in this prompt.");

		MovieSelectionResponse response = client.select("Mystery", "I like clever stories.", List.of(candidate));

		assertEquals(42L, response.selections().get(0).movieId());
		assertEquals("A thoughtful mystery.", response.selections().get(0).reason());
		ArgumentCaptor<Prompt> promptCaptor = ArgumentCaptor.forClass(Prompt.class);
		verify(chatModel).call(promptCaptor.capture());
		String promptText = promptCaptor.getValue().getInstructions().stream()
				.map(message -> message.getText())
				.collect(Collectors.joining("\n"));
		assertTrue(promptText.contains("Candidate film"));
		assertFalse(promptText.contains(excluded.getTitle()));
	}

	@Test
	void wrapsMalformedModelOutput() {
		ChatModel chatModel = mock(ChatModel.class);
		when(chatModel.call(any(Prompt.class))).thenReturn(chatResponse("not valid JSON"));
		when(chatModel.getOptions()).thenReturn(ChatOptions.builder().build());
		SpringAiMovieSelectionClient client = new SpringAiMovieSelectionClient(ChatClient.builder(chatModel));

		assertThrows(MovieSelectionException.class, () -> client.select(
				"Mystery", "A preference.", List.of(new Movie("Candidate", 2020, "Mystery", "Synopsis."))));
	}

	@Test
	void wrapsProviderFailures() {
		ChatModel chatModel = mock(ChatModel.class);
		when(chatModel.call(any(Prompt.class))).thenThrow(new IllegalStateException("offline"));
		when(chatModel.getOptions()).thenReturn(ChatOptions.builder().build());
		SpringAiMovieSelectionClient client = new SpringAiMovieSelectionClient(ChatClient.builder(chatModel));

		assertThrows(MovieSelectionException.class, () -> client.select(
				"Mystery", "A preference.", List.of(new Movie("Candidate", 2020, "Mystery", "Synopsis."))));
	}

	private ChatResponse chatResponse(String content) {
		return new ChatResponse(List.of(new Generation(new AssistantMessage(content))));
	}
}
