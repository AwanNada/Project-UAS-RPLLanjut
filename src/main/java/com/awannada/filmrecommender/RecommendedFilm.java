package com.awannada.filmrecommender;

public record RecommendedFilm(Long movieId, String title, int releaseYear, String genre, String reason) {
}
