package ro.ase.costin.ecomback.product;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.ase.costin.ecomcommon.data.ProductDTO;
import ro.ase.costin.ecomcommon.entity.Product;
import ro.ase.costin.ecomcommon.exception.ProductNotFoundException;

@RestController
@AllArgsConstructor
public class ProductRestController {

    private ProductService productService;

    @PostMapping("/products/check_unique")
    public String checkUnique(Integer id, String name) {
        return productService.checkUniqueName(id, name);
    }

    @GetMapping("/products/get/{id}")
    public ProductDTO getProductInfo(@PathVariable("id") Integer id) throws ProductNotFoundException {
        Product product = productService.get(id);
        return new ProductDTO(product.getName(), product.getMainImagePath(), product.getDiscountPrice(), product.getCost());
    }
}
