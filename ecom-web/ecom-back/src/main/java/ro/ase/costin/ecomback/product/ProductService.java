package ro.ase.costin.ecomback.product;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ro.ase.costin.ecomback.paging.PagingAndSortingHelper;
import ro.ase.costin.ecomcommon.exception.ProductNotFoundException;
import ro.ase.costin.ecomcommon.entity.Product;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

    public static final int PRODUCTS_PER_PAGE = 8;

    private static final String EXCEPTION_MESSAGE = "Produsul nu există - ID: ";

    @NonNull
    private ProductRepository productRepository;

    public List<Product> listAllProducts() {
        return (List<Product>) productRepository.findAll();
    }

    public void listByPage(int pageNum, PagingAndSortingHelper helper, Integer categoryId) {
        Pageable pageable = helper.createPageable(PRODUCTS_PER_PAGE, pageNum);
        String keyword = helper.getKeyword();
        Page<Product> page;

        if (keyword != null && !keyword.isEmpty()) {
            if (categoryId != null && categoryId > 0) {
                String categoryIdMatch = "-" + categoryId + "-";
                page = productRepository.searchInCategory(categoryId, categoryIdMatch, keyword, pageable);
            } else {
                page = productRepository.findAll(keyword, pageable);
            }
        } else {
            if (categoryId != null && categoryId > 0) {
                String categoryIdMatch = "-" + categoryId + "-";
                page = productRepository.findAllInCategory(categoryId, categoryIdMatch, pageable);
            } else {
                page = productRepository.findAll(pageable);
            }
        }

        helper.updateModelAttributes(pageNum, page);
    }

    public Product saveProduct(Product product) {
        if (product.getId() == null) {
            product.setCreatedTime(new Date());
        } else {
            Optional<Product> existingProductData = productRepository.findById(product.getId());
            existingProductData.ifPresent(p -> product.setCreatedTime(p.getCreatedTime()));
        }
        product.setUpdatedTime(new Date());

        if (product.getAlias() == null || product.getAlias().trim().isEmpty()) {
            String defaultAlias = product.getName().replaceAll(" ", "-");
            product.setAlias(defaultAlias);
        } else {
            product.setAlias(product.getAlias().replaceAll(" ", "-"));
        }

        Product updatedProduct = productRepository.save(product);
        productRepository.updateReviewCountAndAverageRating(updatedProduct.getId());
        return updatedProduct;
    }

    public void saveProductPrice(Product formProduct) throws ProductNotFoundException {
        Product dbProduct = productRepository.findById(formProduct.getId())
                .orElseThrow(() -> new ProductNotFoundException(EXCEPTION_MESSAGE + formProduct.getId()));
        dbProduct.setCost(formProduct.getCost());
        dbProduct.setPrice(formProduct.getPrice());
        dbProduct.setDiscountPercent(formProduct.getDiscountPercent());
        productRepository.save(dbProduct);
    }

    public String checkUniqueName(Integer id, String name) {
        boolean isANewProduct = (id == null || id == 0);
        Product productByName = productRepository.findByName(name);

        if (isANewProduct) {
            if (productByName != null) return "Duplicate";
        } else {
            if (productByName != null && !Objects.equals(productByName.getId(), id)) {
                return "Duplicate";
            }
        }
        return "OK";
    }

    public void updateProductEnabledStatus(Integer id, boolean enabled) {
        productRepository.updateEnabledStatus(id, enabled);
    }

    public void delete(Integer id) throws ProductNotFoundException {
        Long countById = productRepository.countById(id);

        if (countById == null || countById == 0) {
            throw new ProductNotFoundException(EXCEPTION_MESSAGE + id);
        }
        productRepository.deleteById(id);
    }

    public Product get(Integer id) throws ProductNotFoundException {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(EXCEPTION_MESSAGE + id));
    }
    public void searchProducts(int pageNum, PagingAndSortingHelper helper) {
        Pageable pageable = helper.createPageable(PRODUCTS_PER_PAGE, pageNum);
        String keyword = helper.getKeyword();
        Page<Product> page = productRepository.searchProductsByName(keyword, pageable);
        helper.updateModelAttributes(pageNum, page);
    }
}
