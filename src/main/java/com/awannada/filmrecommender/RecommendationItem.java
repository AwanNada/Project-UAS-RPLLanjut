package com.awannada.filmrecommender;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "recommendation_items", uniqueConstraints = @UniqueConstraint(
		name = "uk_recommendation_items_request_rank",
		columnNames = { "request_id", "rank" }))
public class RecommendationItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "request_id", nullable = false)
	private RecommendationRequest request;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "movie_id", nullable = false)
	private Movie movie;

	@Column(name = "rank", nullable = false)
	private int rank;

	@Column(nullable = false, length = 500)
	private String reason;

	protected RecommendationItem() {
	}

	RecommendationItem(RecommendationRequest request, Movie movie, int rank, String reason) {
		this.request = request;
		this.movie = movie;
		this.rank = rank;
		this.reason = reason;
	}

	public Long getId() {
		return id;
	}

	public RecommendationRequest getRequest() {
		return request;
	}

	public Movie getMovie() {
		return movie;
	}

	public int getRank() {
		return rank;
	}

	public String getReason() {
		return reason;
	}
}
