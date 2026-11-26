package com.awannada.filmrecommender;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendationRequestRepository extends JpaRepository<RecommendationRequest, Long> {

	@EntityGraph(attributePaths = { "items", "items.movie" })
	List<RecommendationRequest> findAllByOrderByCreatedAtDesc();
}
