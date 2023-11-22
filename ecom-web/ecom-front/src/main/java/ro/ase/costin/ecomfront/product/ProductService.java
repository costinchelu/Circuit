package ro.ase.costin.ecomfront.product;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ro.ase.costin.ecomcommon.entity.Product;
import ro.ase.costin.ecomcommon.exception.ProductNotFoundException;

import java.util.NoSuchElementException;


@Service
@RequiredArgsConstructor
public class ProductService {

    public static final int PRODUCTS_PER_PAGE = 12;

    public static final int SEARCH_RESULTS_PER_PAGE = 12;

    @NonNull
    private ProductRepository productRepository;

    public Page<Product> listProductsByCategory(int pageNum, Integer categoryId) {
        String categoryIdMatch = "-" + categoryId + "-";
        Pageable pageable = PageRequest.of(pageNum - 1, PRODUCTS_PER_PAGE);
        return productRepository.listByCategory(categoryId, categoryIdMatch, pageable);
    }

    public Product getProduct(String alias) throws ProductNotFoundException {
        Product product = productRepository.findByAlias(alias);
        if (product == null) {
            throw new ProductNotFoundException("Nu a fost găsit produsul (alias: " + alias + " ).");
        }
        return product;
    }

    public Product getProduct(Integer id) throws ProductNotFoundException {
        try {
            return productRepository.findById(id).orElseThrow();
        } catch (NoSuchElementException ex) {
            throw new ProductNotFoundException("Nu a fost găsit produsul (ID: " + id + ").");
        }
    }

    public Page<Product> search(String keyword, int pageNum) {
        Pageable pageable = PageRequest.of(pageNum - 1, SEARCH_RESULTS_PER_PAGE);
        return productRepository.search(keyword, pageable);
    }
}
