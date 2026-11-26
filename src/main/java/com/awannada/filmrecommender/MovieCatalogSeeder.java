package com.awannada.filmrecommender;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class MovieCatalogSeeder implements CommandLineRunner {

	private final MovieRepository movieRepository;

	public MovieCatalogSeeder(MovieRepository movieRepository) {
		this.movieRepository = movieRepository;
	}

	@Override
	public void run(String... args) {
		seedCatalog();
	}

	public void seedCatalog() {
		for (Movie movie : curatedMovies()) {
			if (!movieRepository.existsByTitleAndReleaseYear(movie.getTitle(), movie.getReleaseYear())) {
				movieRepository.save(movie);
			}
		}
	}

	private List<Movie> curatedMovies() {
		return List.of(
				new Movie("The Dark Knight", 2008, "Action",
						"A masked hero confronts a rising threat that puts a city and its sense of order at risk."),
				new Movie("Mad Max: Fury Road", 2015, "Action",
						"A relentless journey across a harsh landscape tests survival, trust, and freedom."),
				new Movie("Spirited Away", 2001, "Animation",
						"A young traveler navigates a mysterious spirit world while searching for a way home."),
				new Movie("Coco", 2017, "Animation",
						"A curious child discovers how music, family, and memory connect across generations."),
				new Movie("Groundhog Day", 1993, "Comedy",
						"A cynical broadcaster repeatedly relives one day and slowly changes how he treats others."),
				new Movie("The Grand Budapest Hotel", 2014, "Comedy",
						"A hotel concierge and his apprentice become entangled in an elegant caper."),
				new Movie("Hidden Figures", 2016, "Drama",
						"Three mathematicians use their expertise to help a pioneering space program succeed."),
				new Movie("The Shawshank Redemption", 1994, "Drama",
						"A long friendship offers hope and resilience inside a prison system."),
				new Movie("Knives Out", 2019, "Mystery",
						"A sharp investigator untangles conflicting accounts after a wealthy family gathering."),
				new Movie("The Prestige", 2006, "Mystery",
						"Two rival performers pursue an obsession that blurs illusion and personal cost."),
				new Movie("Before Sunrise", 1995, "Romance",
						"Two travelers spend one evening talking and exploring a city together."),
				new Movie("Pride & Prejudice", 2005, "Romance",
						"A spirited young woman and a reserved landowner reconsider first impressions."),
				new Movie("Arrival", 2016, "Science Fiction",
						"A linguist works to communicate with visitors whose arrival challenges familiar ideas of time."),
				new Movie("Interstellar", 2014, "Science Fiction",
						"Explorers travel beyond Earth in search of a future for humanity.")
		);
	}
}
