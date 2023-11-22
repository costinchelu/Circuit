package ro.ase.costin.ecomback.category;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ro.ase.costin.ecomcommon.data.CategoryPageInfo;
import ro.ase.costin.ecomcommon.exception.CategoryNotFoundException;
import ro.ase.costin.ecomback.utils.FileUploadUtil;
import ro.ase.costin.ecomcommon.entity.Category;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
public class CategoryController {

    private static final String REDIRECT = "redirect:/categories";

    @NonNull
    private CategoryService categoryService;

    @GetMapping("/categories")
    public String listFirstPage(String sortDir, Model model) {
        return listAllByPage(1, sortDir, null, model);
    }

    @GetMapping("/categories/page/{pageNum}")
    public String listAllByPage(@PathVariable(name = "pageNum") int pageNum, String sortDir, String keyword, Model model) {
        if (sortDir == null || sortDir.trim().isEmpty()) {
            sortDir = "asc";
        }
        CategoryPageInfo pageInfo = new CategoryPageInfo();
        List<Category> categories = categoryService.listAllByPage(pageInfo, pageNum, sortDir, keyword);

        long startCount = (long) (pageNum - 1) * CategoryService.ROOT_CATEGORIES_PER_PAGE + 1;
        long endCount = startCount + CategoryService.ROOT_CATEGORIES_PER_PAGE - 1;
        if (endCount > pageInfo.getTotalElements()) {
            endCount = pageInfo.getTotalElements();
        }

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        model.addAttribute("totalPages", pageInfo.getTotalPages());
        model.addAttribute("totalItems", pageInfo.getTotalElements());
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("sortField", "name");
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("listCategories", categories);
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("moduleURL", "/categories");
        return "categories/categories";
    }

    @GetMapping("/categories/new")
    public String newCategory(Model model) {
        List<Category> categoriesForDropdown = categoryService.listCategoriesForForm();
        model.addAttribute("category", new Category());
        model.addAttribute("pageTitle", "Creare categorie");
        model.addAttribute("listCategories", categoriesForDropdown);
        return "categories/form_category";
    }

    @PostMapping("/categories/save")
    public String saveCategory(Category category,
                               @RequestParam("fileImage") MultipartFile multipartFile,
                               RedirectAttributes redirectAttributes) throws IOException {

        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            category.setImage(fileName);

            Category savedCategory = categoryService.save(category);
            String uploadDir = "category-images/" + savedCategory.getId();

            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } else {
            categoryService.save(category);
        }

        redirectAttributes.addFlashAttribute("message", "Categoria a fost salvată.");
        return REDIRECT;
    }

    @GetMapping("/categories/edit/{id}")
    public String editCategory(@PathVariable(name = "id") Integer id,
                               Model model, RedirectAttributes redirectAttributes ) {
        try {
            Category category = categoryService.get(id);
            List<Category> categories = categoryService.listCategoriesForForm();

            model.addAttribute("category", category);
            model.addAttribute("listCategories", categories);
            model.addAttribute("pageTitle", "Editare categorie (ID: " + id + ")");

            return "categories/form_category";
        } catch (CategoryNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            return REDIRECT;
        }
    }

    @GetMapping("/categories/{id}/enabled/{status}")
    public String updateCategoryEnabledStatus(
            @PathVariable("id") Integer id,
            @PathVariable("status") boolean enabled,
            RedirectAttributes redirectAttributes) {

        categoryService.updateCategoryEnabledStatus(id, enabled);
        String status = enabled ? "activată" : "dezactivată";
        String message = "Categoria cu ID " + id + " a fost " + status + ".";
        redirectAttributes.addFlashAttribute("message", message);
        return REDIRECT;
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            categoryService.delete(id);
            String categoryDir = "category-images/" + id;
            FileUploadUtil.removeDir(categoryDir);

            redirectAttributes.addFlashAttribute("message",
                    "Categoria cu ID " + id + " a fost ștearsă.");
        } catch (Exception ex) {
            if (ex.getMessage().contains("SQL")) {
                redirectAttributes.addFlashAttribute("message", "Categoria nu poate fi ștearsă.");
            } else {
                redirectAttributes.addFlashAttribute("message", ex.getMessage());
            }
        }

        return REDIRECT;
    }

    @GetMapping("/categories/export/csv")
    public void exportToCSV(HttpServletResponse response) {
        List<Category> listCategories = categoryService.listCategoriesForForm();
        CategoryCsvExporter exporter = new CategoryCsvExporter();
        exporter.exportCsv(listCategories, response);
    }
}
