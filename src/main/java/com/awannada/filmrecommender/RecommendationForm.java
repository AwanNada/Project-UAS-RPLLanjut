package com.awannada.filmrecommender;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RecommendationForm(
		@NotBlank(message = "Choose a genre.") @Size(max = 80) String genre,
		@NotBlank(message = "Enter your preference.") @Size(max = 300, message = "Use at most 300 characters.")
		String preferences) {
}
