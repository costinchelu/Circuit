package ro.ase.costin.ecomfront.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ro.ase.costin.ecomcommon.entity.Category;
import ro.ase.costin.ecomfront.category.CategoryService;

import java.util.List;

@Controller
@AllArgsConstructor
public class MainController {

    private CategoryService categoryService;

    @GetMapping("")
    public String viewHomePage(Model model) {
        List<Category> noChildrenCategories = categoryService.getNoChildrenCategories();
        model.addAttribute("listCategories", noChildrenCategories);
        return "index";
    }

    @GetMapping("/login")
    public String viewLoginPage() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "login";
        }
        return "redirect:/";
    }

    @GetMapping("/notification")
    public String viewNotificationPage() {
        return "confidentiality";
    }

    @GetMapping("/about")
    public String viewAboutPage() {
        return "about";
    }
}
