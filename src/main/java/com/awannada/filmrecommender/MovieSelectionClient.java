package com.awannada.filmrecommender;

import java.util.List;

public interface MovieSelectionClient {

	MovieSelectionResponse select(String genre, String preferences, List<Movie> candidates);
}
