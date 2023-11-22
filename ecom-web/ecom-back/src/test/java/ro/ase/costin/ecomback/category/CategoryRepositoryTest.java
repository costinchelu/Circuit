package ro.ase.costin.ecomback.category;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import ro.ase.costin.ecomback.category.CategoryRepository;
import ro.ase.costin.ecomcommon.entity.Category;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = true)
@Disabled("Integration tests")
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository repository;

    @Test
    void creteRootCategoryTest() {
        Category category = new Category("Electronics");
        Category savedCategory = repository.save(category);
        assertThat(savedCategory.getId()).isPositive();
    }

    @Test
    void createSubCategoryTest() {
        Category parent = new Category(7);
        Category iPhone = new Category("iPhone", parent);
//        Category smartphones = new Category("Smartphones", parent);
        repository.saveAll(List.of(iPhone));
    }

    @Test
    void getCategoryTest() {
        Category category = repository.findById(1).get();
        Set<Category> children = category.getChildren();

        assertThat(children.size()).isPositive();
    }

    @Test
    void printHierarchyTest() {
        Iterable<Category> all = repository.findAll();

        for (Category category : all) {
            if (category.getParent() == null) {
                System.out.println(category.getName());

                Set<Category> children = category.getChildren();

                for (Category subCategory : children) {
                    System.out.println("--" + subCategory.getName());
                    printChildren(subCategory, 1);
                }
            }
        }
    }

    private void printChildren(Category parent, int subLevel) {
        int newSubLevel = subLevel + 1;
        Set<Category> children = parent.getChildren();

        for (Category subCategory : children) {
            for (int i = 0; i < newSubLevel; i++) {
                System.out.print("--");
            }
            System.out.println(subCategory.getName());

            printChildren(subCategory, newSubLevel);
        }
    }

    @Test
    void listRootCategoriesTest() {
        List<Category> rootCategories = repository.findRootCategories(Sort.by("name").ascending());
        rootCategories.stream().map(Category::getName).forEach(System.out::println);
    }

    @Test
    void findByNameTest() {
        String name = "Computers";
        Category category = repository.findByName(name);

        assertThat(category).isNotNull();
        assertThat(category.getName()).isEqualTo(name);
    }

    @Test
    void findByAliasTest() {
        String alias = "electronics";
        Category category = repository.findByAlias(alias);

        assertThat(category).isNotNull();
        assertThat(category.getAlias()).isEqualTo(alias);
    }
}