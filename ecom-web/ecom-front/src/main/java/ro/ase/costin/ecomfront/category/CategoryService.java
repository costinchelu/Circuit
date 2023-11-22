package ro.ase.costin.ecomfront.category;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ro.ase.costin.ecomcommon.entity.Category;
import ro.ase.costin.ecomcommon.exception.CategoryNotFoundException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CategoryService {

    private CategoryRepository categoryRepository;

    public List<Category> getNoChildrenCategories() {
        return categoryRepository.findAllByEnabledIsTrueOrderByName().stream()
                .filter(category -> category.getChildren() == null || category.getChildren().isEmpty())
                .sorted(Comparator.comparing(Category::getName))
                .collect(Collectors.toList());
    }

    public Category getCategory(String alias) throws CategoryNotFoundException {
        Category category = categoryRepository.findByAliasEnabled(alias);
        if (category == null) {
            throw new CategoryNotFoundException("Categoria cu alias: " + alias + " nu a fost găsită.");
        }
        return category;
    }

    public List<Category> getCategoryParents(Category child) {
        List<Category> parents = new ArrayList<>();
        Category parentCategory = child.getParent();
        while (parentCategory != null) {
            parents.add(0, parentCategory);
            parentCategory = parentCategory.getParent();
        }
        parents.add(child);
        return parents;
    }
}
