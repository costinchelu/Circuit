package ro.ase.costin.ecomback.category;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ro.ase.costin.ecomback.category.CategoryRepository;
import ro.ase.costin.ecomback.category.CategoryService;
import ro.ase.costin.ecomcommon.entity.Category;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
@Disabled("Integration tests")
class CategoryServiceTest {

    private static final String DUPLICATE_NAME = "duplicate-name";

    private static final String DUPLICATE_ALIAS = "duplicate-alias";

    @MockBean
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void checkUniqueInNewModeReturnDuplicateName() {
        Integer id = null;
        String name = "Computers";
        String alias = "abc";

        Category category = new Category(id, name, alias);
        when(categoryRepository.findByName(name)).thenReturn(category);
        when(categoryRepository.findByAlias(alias)).thenReturn(null);

        String result = categoryService.checkUnique(id, name, alias);
        assertThat(result).isEqualTo(DUPLICATE_NAME);
    }

    @Test
    void checkUniqueInNewModeReturnDuplicateAlias() {
        Integer id = null;
        String name = "abc";
        String alias = "computers";

        Category category = new Category(id, name, alias);
        when(categoryRepository.findByName(name)).thenReturn(null);
        when(categoryRepository.findByAlias(alias)).thenReturn(category);

        String result = categoryService.checkUnique(id, name, alias);
        assertThat(result).isEqualTo(DUPLICATE_ALIAS);
    }

    @Test
    void checkUniqueInNewModeReturnOK() {
        Integer id = null;
        String name = "abc";
        String alias = "abc";

        Category category = new Category(id, name, alias);
        when(categoryRepository.findByName(name)).thenReturn(null);
        when(categoryRepository.findByAlias(alias)).thenReturn(null);

        String result = categoryService.checkUnique(id, name, alias);
        assertThat(result).isEqualTo("OK");
    }

    @Test
    void testCheckUniqueInEditModeReturnDuplicateName() {
        Integer id = 1;
        String name = "Computers";
        String alias = "abc";

        Category category = new Category(2, name, alias);

        when(categoryRepository.findByName(name)).thenReturn(category);
        when(categoryRepository.findByAlias(alias)).thenReturn(null);

        String result = categoryService.checkUnique(id, name, alias);

        assertThat(result).isEqualTo(DUPLICATE_NAME);
    }

    @Test
    void testCheckUniqueInEditModeReturnDuplicateAlias() {
        Integer id = 1;
        String name = "NameABC";
        String alias = "computers";

        Category category = new Category(2, name, alias);

        when(categoryRepository.findByName(name)).thenReturn(null);
        when(categoryRepository.findByAlias(alias)).thenReturn(category);

        String result = categoryService.checkUnique(id, name, alias);

        assertThat(result).isEqualTo(DUPLICATE_ALIAS);
    }

    @Test
    void testCheckUniqueInEditModeReturnOK() {
        Integer id = 1;
        String name = "NameABC";
        String alias = "computers";

        Category category = new Category(id, name, alias);

        when(categoryRepository.findByName(name)).thenReturn(null);
        when(categoryRepository.findByAlias(alias)).thenReturn(category);

        String result = categoryService.checkUnique(id, name, alias);

        assertThat(result).isEqualTo("OK");
    }
}