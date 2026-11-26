package com.awannada.filmrecommender;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "movies", uniqueConstraints = @UniqueConstraint(
		name = "uk_movies_title_release_year",
		columnNames = { "title", "release_year" }))
public class Movie {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 160)
	private String title;

	@Column(name = "release_year", nullable = false)
	private int releaseYear;

	@Column(nullable = false, length = 80)
	private String genre;

	@Column(nullable = false, length = 1200)
	private String synopsis;

	protected Movie() {
	}

	public Movie(String title, int releaseYear, String genre, String synopsis) {
		this.title = title;
		this.releaseYear = releaseYear;
		this.genre = genre;
		this.synopsis = synopsis;
	}

	public Long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public int getReleaseYear() {
		return releaseYear;
	}

	public String getGenre() {
		return genre;
	}

	public String getSynopsis() {
		return synopsis;
	}
}
