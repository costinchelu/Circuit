package ro.ase.costin.ecomback.review;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ro.ase.costin.ecomback.paging.PagingAndSortingHelper;
import ro.ase.costin.ecomback.paging.PagingAndSortingParam;
import ro.ase.costin.ecomcommon.entity.Review;
import ro.ase.costin.ecomcommon.exception.ReviewNotFoundException;

@Controller
@RequiredArgsConstructor
public class ReviewController {

    private static final String DEFAULT_REDIRECT_URL = "redirect:/reviews/page/1?sortField=reviewTime&sortDir=desc";

    @NonNull
    private ReviewService service;

    @GetMapping("/reviews")
    public String listFirstPage(Model model) {
        return DEFAULT_REDIRECT_URL;
    }

    @GetMapping("/reviews/page/{pageNum}")
    public String listByPage(
            @PagingAndSortingParam(listName = "listReviews", moduleURL = "/reviews") PagingAndSortingHelper helper,
            @PathVariable(name = "pageNum") int pageNum) {

        service.listByPage(pageNum, helper);
        return "reviews/reviews";
    }

    @GetMapping("/reviews/detail/{id}")
    public String viewReview(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        try {
            Review review = service.get(id);
            model.addAttribute("review", review);
            return "reviews/review_detail_modal";
        } catch (ReviewNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
            return DEFAULT_REDIRECT_URL;
        }
    }

    @GetMapping("/reviews/edit/{id}")
    public String editReview(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        try {
            Review review = service.get(id);
            model.addAttribute("review", review);
            model.addAttribute("pageTitle", String.format("Editare recenzie (ID: %d)", id));
            return "reviews/review_form";
        } catch (ReviewNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
            return DEFAULT_REDIRECT_URL;
        }
    }

    @PostMapping("/reviews/save")
    public String saveReview(Review reviewInForm, RedirectAttributes ra) {
        service.save(reviewInForm);
        ra.addFlashAttribute("message", "Recenzia (ID: " + reviewInForm.getId() + ") a fost actualizată.");
        return DEFAULT_REDIRECT_URL;
    }

    @GetMapping("/reviews/delete/{id}")
    public String deleteReview(@PathVariable("id") Integer id, RedirectAttributes ra) {
        try {
            service.delete(id);
            ra.addFlashAttribute("message", "Recenzia (ID: " + id + ") a fost ștearsă.");
        } catch (ReviewNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
        }
        return DEFAULT_REDIRECT_URL;
    }
}
