package com.awannada.filmrecommender;

import java.util.List;

public record MovieSelectionResponse(List<MovieSelection> selections) {

	public MovieSelectionResponse {
		if (selections != null) {
			selections = List.copyOf(selections);
		}
	}
}
