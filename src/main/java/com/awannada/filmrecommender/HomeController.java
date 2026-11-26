package com.awannada.filmrecommender;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController {

	private final MovieRepository movieRepository;
	private final RecommendationRequestRepository requestRepository;
	private final MovieRecommendationService recommendationService;

	public HomeController(
			MovieRepository movieRepository,
			RecommendationRequestRepository requestRepository,
			MovieRecommendationService recommendationService) {
		this.movieRepository = movieRepository;
		this.requestRepository = requestRepository;
		this.recommendationService = recommendationService;
	}

	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("genres", movieRepository.findDistinctGenres());
		model.addAttribute("history", requestRepository.findAllByOrderByCreatedAtDesc());
		if (!model.containsAttribute("recommendationForm")) {
			model.addAttribute("recommendationForm", new RecommendationForm("", ""));
		}
		return "index";
	}

	@PostMapping("/recommendations")
	public String recommend(
			@Valid @ModelAttribute("recommendationForm") RecommendationForm form,
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("recommendationForm", form);
			redirectAttributes.addFlashAttribute(
					BindingResult.MODEL_KEY_PREFIX + "recommendationForm", bindingResult);
			return "redirect:/";
		}

		redirectAttributes.addFlashAttribute("recommendationOutcome", recommendationService.recommend(form));
		return "redirect:/";
	}
}
