package ro.ase.costin.ecomback.brand;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.ase.costin.ecomback.exception.BrandNotFoundException;
import ro.ase.costin.ecomcommon.data.CategoryDTO;
import ro.ase.costin.ecomcommon.entity.Brand;
import ro.ase.costin.ecomcommon.entity.Category;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@RestController
@AllArgsConstructor
public class BrandRestController {

    private BrandService brandService;

    @PostMapping("/brands/check_unique")
    public String checkUnique(Integer id, String name) {
        return brandService.checkUniqueBrandName(id, name);
    }

    @GetMapping("/brands/{id}/categories")
    public List<CategoryDTO> listCategoriesByBrand(@PathVariable(name = "id") Integer brandId) {
        List<CategoryDTO> categoryDTOList = new ArrayList<>();
        try {
            Brand brand = brandService.get(brandId);
            Set<Category> categories = brand.getCategories();
            categories.stream()
                    .map(category -> new CategoryDTO(category.getId(), category.getName()))
                    .sorted(Comparator.comparing(CategoryDTO::getName))
                    .forEach(categoryDTOList::add);
        } catch (BrandNotFoundException e) {
            e.printStackTrace();
        }
        return categoryDTOList;
    }
}
