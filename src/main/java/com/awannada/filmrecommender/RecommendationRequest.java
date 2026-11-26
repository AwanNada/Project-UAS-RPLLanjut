package com.awannada.filmrecommender;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

@Entity
@Table(name = "recommendation_requests")
public class RecommendationRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 80)
	private String genre;

	@Column(nullable = false, length = 300)
	private String preferences;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("rank ASC")
	private List<RecommendationItem> items = new ArrayList<>();

	protected RecommendationRequest() {
	}

	public RecommendationRequest(String genre, String preferences) {
		this.genre = genre;
		this.preferences = preferences;
		this.createdAt = Instant.now();
	}

	public void addItem(Movie movie, int rank, String reason) {
		items.add(new RecommendationItem(this, movie, rank, reason));
	}

	public Long getId() {
		return id;
	}

	public String getGenre() {
		return genre;
	}

	public String getPreferences() {
		return preferences;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public List<RecommendationItem> getItems() {
		return items;
	}
}
