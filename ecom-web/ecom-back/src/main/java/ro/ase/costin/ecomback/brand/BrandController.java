package ro.ase.costin.ecomback.brand;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ro.ase.costin.ecomback.category.CategoryService;
import ro.ase.costin.ecomback.exception.BrandNotFoundException;
import ro.ase.costin.ecomback.paging.PagingAndSortingHelper;
import ro.ase.costin.ecomback.paging.PagingAndSortingParam;
import ro.ase.costin.ecomback.utils.FileUploadUtil;
import ro.ase.costin.ecomcommon.entity.Brand;
import ro.ase.costin.ecomcommon.entity.Category;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class BrandController {

    private static final String DEFAULT_REDIRECT_URL = "redirect:/brands/page/1?sortField=name&sortDir=asc";

    @NonNull
    private BrandService brandService;

    @NonNull
    private CategoryService categoryService;

    @GetMapping("/brands")
    public String listFirstPage() {
        return DEFAULT_REDIRECT_URL;
    }

    @GetMapping("/brands/page/{pageNum}")
    public String listByPage(
            @PagingAndSortingParam(listName = "listBrands", moduleURL = "/brands") PagingAndSortingHelper helper,
            @PathVariable(name = "pageNum") int pageNum) {
        brandService.listByPage(pageNum, helper);
        return "brands/brands";
    }

    @GetMapping("/brands/new")
    public String newBrand(Model model) {
        List<Category> categories = categoryService.listCategoriesForForm();

        model.addAttribute("listCategories", categories);
        model.addAttribute("brand", new Brand());
        model.addAttribute("pageTitle", "Creare marcă");
        return "brands/form_brand";
    }

    @SneakyThrows(IOException.class)
    @PostMapping("/brands/save")
    public String saveBrand(Brand brand,
                            @RequestParam("fileImage") MultipartFile multipartFile,
                            RedirectAttributes redirectAttributes) {

        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            brand.setLogo(fileName);

            Brand savedBrand = brandService.save(brand);
            String uploadDir = "brand-logos/" + savedBrand.getId();

            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

        } else {
            brandService.save(brand);
        }
        redirectAttributes.addFlashAttribute("message", "Marca a fost salvată.");
        return DEFAULT_REDIRECT_URL;
    }

    @GetMapping("/brands/edit/{id}")
    public String editBrand(@PathVariable(name = "id") Integer id,
                            Model model, RedirectAttributes ra) {
        try {
            Brand brand = brandService.get(id);
            List<Category> categories = categoryService.listCategoriesForForm();

            model.addAttribute("brand", brand);
            model.addAttribute("listCategories", categories);
            model.addAttribute("pageTitle", "Editare marcă (ID: " + id + ")");
            return "brands/form_brand";
        } catch (BrandNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
            return DEFAULT_REDIRECT_URL;
        }
    }

    @GetMapping("/brands/delete/{id}")
    public String deleteBrand(@PathVariable(name = "id") Integer id,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        try {
            brandService.delete(id);
            String brandDir = "brand-logos/" + id;
            FileUploadUtil.removeDir(brandDir);

            redirectAttributes.addFlashAttribute("message",
                    "Marca cu ID " + id + " a fost ștearsă.");
        } catch (BrandNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }

        return DEFAULT_REDIRECT_URL;
    }

    @GetMapping("/brands/export/csv")
    public void exportToCSV(HttpServletResponse response) {
        List<Brand> brands = brandService.listAll()
                .stream()
                .sorted(Comparator.comparing(Brand::getName))
                .collect(Collectors.toList());
        setBrandCategories(brands);
        BrandsCsvExporter csvExporter = new BrandsCsvExporter();
        csvExporter.exportCsv(brands, response);
    }

    private void setBrandCategories(List<Brand> brands) {
        brands.forEach(brand -> {
            try {
                brand.setCategories(brandService.get(brand.getId()).getCategories());
            } catch (BrandNotFoundException e) {
                e.printStackTrace();
            }
        });
    }
}
