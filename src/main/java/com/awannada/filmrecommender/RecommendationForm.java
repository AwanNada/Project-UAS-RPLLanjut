package com.awannada.filmrecommender;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RecommendationForm(
		@NotBlank @Size(max = 80) String genre,
		@NotBlank @Size(max = 300) String preferences) {
}
