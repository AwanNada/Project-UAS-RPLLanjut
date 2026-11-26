package com.awannada.filmrecommender;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MovieRepository extends JpaRepository<Movie, Long> {

	boolean existsByTitleAndReleaseYear(String title, int releaseYear);

	List<Movie> findByGenreIgnoreCaseOrderByTitleAsc(String genre);

	@Query("select distinct movie.genre from Movie movie order by movie.genre")
	List<String> findDistinctGenres();
}
