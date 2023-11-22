package ro.ase.costin.ecomback.brand;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ro.ase.costin.ecomback.brand.BrandRepository;
import ro.ase.costin.ecomback.brand.BrandService;
import ro.ase.costin.ecomcommon.entity.Brand;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
@Rollback(value = true)
@Disabled("Integration tests")
class BrandServiceTest {

    @MockBean
    private BrandRepository brandRepository;

    @InjectMocks
    private BrandService brandService;

    @Test
    void testCheckUniqueInNewModeReturnDuplicate() {
        Integer id = null;
        String name = "Acer";
        Brand brand = new Brand(name);

        when(brandRepository.findByName(name)).thenReturn(brand);

        String result = brandService.checkUniqueBrandName(id, name);
        assertThat(result).isEqualTo("Duplicate");
    }

    @Test
    void testCheckUniqueInNewModeReturnOK() {
        Integer id = null;
        String name = "AMD";

        when(brandRepository.findByName(name)).thenReturn(null);

        String result = brandService.checkUniqueBrandName(id, name);
        assertThat(result).isEqualTo("OK");
    }

    @Test
    void testCheckUniqueInEditModeReturnDuplicate() {
        Integer id = 1;
        String name = "Canon";
        Brand brand = new Brand(id, name);

        when(brandRepository.findByName(name)).thenReturn(brand);

        String result = brandService.checkUniqueBrandName(2, "Canon");
        assertThat(result).isEqualTo("Duplicate");
    }

    @Test
    void testCheckUniqueInEditModeReturnOK() {
        Integer id = 1;
        String name = "Acer";
        Brand brand = new Brand(id, name);

        when(brandRepository.findByName(name)).thenReturn(brand);

        String result = brandService.checkUniqueBrandName(id, "Acer Ltd");
        assertThat(result).isEqualTo("OK");
    }
}