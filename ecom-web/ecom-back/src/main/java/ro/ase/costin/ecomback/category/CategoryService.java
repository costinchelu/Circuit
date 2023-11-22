package ro.ase.costin.ecomback.category;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ro.ase.costin.ecomcommon.data.CategoryPageInfo;
import ro.ase.costin.ecomcommon.exception.CategoryNotFoundException;
import ro.ase.costin.ecomcommon.entity.Category;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    public static final int ROOT_CATEGORIES_PER_PAGE = 1;

    private static final String DUPLICATE_NAME = "duplicate-name";

    private static final String DUPLICATE_ALIAS = "duplicate-alias";

    @NonNull
    private CategoryRepository categoryRepository;

    public List<Category> listAllByPage(CategoryPageInfo pageInfo, int pageNum, String sortDir, String keyword) {
        Sort sort = Sort.by("name");
        if (sortDir.equals("asc")) {
            sort = sort.ascending();
        } else if (sortDir.equals("desc")) {
            sort = sort.descending();
        }

        Pageable pageable = PageRequest.of(pageNum - 1, ROOT_CATEGORIES_PER_PAGE, sort);
        Page<Category> pageCategories;
        if (keyword != null && !keyword.trim().isEmpty()) {
            pageCategories = categoryRepository.search(keyword, pageable);
        } else {
            pageCategories = categoryRepository.findRootCategories(pageable);
        }
        List<Category> rootCategories = pageCategories.getContent();
        pageInfo.setTotalElements(pageCategories.getTotalElements());
        pageInfo.setTotalPages(pageCategories.getTotalPages());

        if (keyword != null && !keyword.trim().isEmpty()) {
            List<Category> searchResult = pageCategories.getContent();
            for (Category category : searchResult) {
                category.setHasChildren(!category.getChildren().isEmpty());
            }
            return searchResult;
        } else {
            return listHierarchicalCategories(rootCategories, sortDir);
        }
    }

    public Category save(Category category) {
        Category parent = category.getParent();
        if (parent != null) {
            String allParentIds = parent.getAllParentIDs() == null ? "-" : parent.getAllParentIDs();
            allParentIds += parent.getId() + "-";
            category.setAllParentIDs(allParentIds);
        }
        return categoryRepository.save(category);
    }

    public String checkUnique(Integer id, String name, String alias) {
        boolean isCreatingNewCategory = (id == null || id == 0);
        Category categoryByName = categoryRepository.findByName(name);
        Category categoryByAlias = categoryRepository.findByAlias(alias);

        if (isCreatingNewCategory) {
            if (categoryByName != null) {
                return DUPLICATE_NAME;
            } else {
                if (categoryByAlias != null) {
                    return DUPLICATE_ALIAS;
                }
            }
        } else {
            if (categoryByName != null && !Objects.equals(categoryByName.getId(), id)) {
                return DUPLICATE_NAME;
            }
            if (categoryByAlias != null && !Objects.equals(categoryByAlias.getId(), id)) {
                return DUPLICATE_ALIAS;
            }
        }
        return "OK";
    }

    private List<Category> listHierarchicalCategories(List<Category> rootCategories, String sortDir) {
        List<Category> hierarchicalCategories = new ArrayList<>();
        for (Category rootCategory : rootCategories) {
            hierarchicalCategories.add(Category.prototypeCategory(rootCategory));

            Set<Category> children = sortSubCategories(rootCategory.getChildren(), sortDir);

            for (Category subCategory : children) {
                String processedName = "- " + subCategory.getName();
                hierarchicalCategories.add(Category.prototypeCategory(subCategory, processedName));

                listSubHierarchicalCategories(hierarchicalCategories, subCategory, 1, sortDir);
            }
        }
        return hierarchicalCategories;
    }

    private void listSubHierarchicalCategories(List<Category> hierarchicalCategories, Category parent, int subLevel, String sortDir) {
        int newSubLevel = subLevel + 1;
        Set<Category> children = sortSubCategories(parent.getChildren(), sortDir);

        for (Category subCategory : children) {
            String processedName = "- ".repeat(Math.max(0, newSubLevel)) + subCategory.getName();
            hierarchicalCategories.add(Category.prototypeCategory(subCategory, processedName));

            listSubHierarchicalCategories(hierarchicalCategories, subCategory, newSubLevel, sortDir);
        }
    }

    public List<Category> listCategoriesForForm() {
        List<Category> categoriesUsedForForm = new ArrayList<>();
        Iterable<Category> fromDB = categoryRepository.findRootCategories(Sort.by("name").ascending());

        for (Category category : fromDB) {
            if (category.getParent() == null) {
                categoriesUsedForForm.add(Category.categoryFactory(category));

                Set<Category> children = sortSubCategories(category.getChildren());

                for (Category subCategory : children) {
                    String processedName = "- " + subCategory.getName();
                    categoriesUsedForForm.add(Category.categoryFactory(subCategory.getId(), processedName));
                    listSubCategoriesForForm(categoriesUsedForForm, subCategory, 1);
                }
            }
        }
        return categoriesUsedForForm;
    }

    private void listSubCategoriesForForm(List<Category> categoriesUsedForDropdown, Category parent, int subLevel) {
        int newSubLevel = subLevel + 1;
        Set<Category> children = sortSubCategories(parent.getChildren());

        for (Category subCategory : children) {
            String processedName = "- ".repeat(Math.max(0, newSubLevel)) + subCategory.getName();
            categoriesUsedForDropdown.add(Category.categoryFactory(subCategory.getId(), processedName));

            listSubCategoriesForForm(categoriesUsedForDropdown, subCategory, newSubLevel);
        }
    }

    public Category get(Integer id) throws CategoryNotFoundException {
        try {
            return categoryRepository.findById(id).orElseThrow();
        } catch (NoSuchElementException | NullPointerException ex) {
            throw new CategoryNotFoundException("Categoria cu ID " + id + " nu a fost găsită.");
        }
    }

    private SortedSet<Category> sortSubCategories(Set<Category> children) {
        return sortSubCategories(children, "asc");
    }

        private SortedSet<Category> sortSubCategories(Set<Category> children, String sortDir) {
        SortedSet<Category> sortedChildren;
        if (sortDir == null || sortDir.trim().isEmpty()) {
            sortedChildren = new TreeSet<>(Comparator.comparing(Category::getName));
        } else {
            if (sortDir.equals("desc")) {
                sortedChildren = new TreeSet<>(Comparator.comparing(Category::getName).reversed());
            } else {
                sortedChildren = new TreeSet<>(Comparator.comparing(Category::getName));
            }
        }
        sortedChildren.addAll(children);
        return sortedChildren;
    }

    public void updateCategoryEnabledStatus(Integer id, boolean enabled) {
        categoryRepository.updateEnabledStatus(id, enabled);
    }

    public void delete(Integer id) throws CategoryNotFoundException {
        Long countById = categoryRepository.countById(id);
        if (countById == null || countById == 0) {
            throw new CategoryNotFoundException("Categoria cu ID " + id + " nu a fost găsită.");
        }

        try {
            categoryRepository.deleteById(id);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CategoryNotFoundException("Categoria nu poate fi ștearsă.");
        }
    }
}
