package ro.ase.costin.ecomback.brand;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ro.ase.costin.ecomback.exception.BrandNotFoundException;
import ro.ase.costin.ecomback.paging.PagingAndSortingHelper;
import ro.ase.costin.ecomcommon.entity.Brand;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class BrandService {

    private static final String DUPLICATE = "duplicate";

    private static final String EXCEPTION_MESSAGE = "Marcă inexistentă. ID ";

    public static final int BRANDS_PER_PAGE = 10;

    @NonNull
    private BrandRepository brandRepository;

    public List<Brand> listAll() {
        return brandRepository.findAll();
    }

    public void listByPage(int pageNum, PagingAndSortingHelper helper) {
        helper.listEntities(pageNum, BRANDS_PER_PAGE, brandRepository);
    }

    public Brand save(Brand brand) {
        return brandRepository.save(brand);
    }

    public Brand get(Integer id) throws BrandNotFoundException {
        try {
            return brandRepository.findById(id).orElseThrow(() -> new BrandNotFoundException(EXCEPTION_MESSAGE + id));
        } catch (NoSuchElementException ex) {
            throw new BrandNotFoundException(EXCEPTION_MESSAGE + id);
        }
    }

    public void delete(Integer id) throws BrandNotFoundException {
        Long countById = brandRepository.countById(id);

        if (countById == null || countById == 0) {
            throw new BrandNotFoundException(EXCEPTION_MESSAGE + id);
        }
        try {
            brandRepository.deleteById(id);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BrandNotFoundException("Marca nu poate fi ștearsă.");
        }
    }

    public String checkUniqueBrandName(Integer id, String name) {
        boolean isCreatingNew = (id == null || id == 0);
        Brand brandByName = brandRepository.findByName(name);

        if (isCreatingNew) {
            if (brandByName != null) return DUPLICATE;
        } else {
            if (brandByName != null && !Objects.equals(brandByName.getId(), id)) {
                return DUPLICATE;
            }
        }
        return "OK";
    }
}
