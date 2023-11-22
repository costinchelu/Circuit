package ro.ase.costin.ecomback.order;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ro.ase.costin.ecomback.paging.PagingAndSortingHelper;
import ro.ase.costin.ecomback.paging.PagingAndSortingParam;
import ro.ase.costin.ecomback.product.ProductService;

@Controller
@AllArgsConstructor
public class ProductSearchController {

    private ProductService productService;

    @GetMapping("/orders/search_product")
    public String showSearchProductPage() {
        return "orders/search_product";
    }

    @PostMapping("/orders/search_product")
    public String searchProducts(String keyword) {
        if (!isAlphaNumericOrSpace(keyword)) {
            try {
                keyword = keyword.replaceAll("[^A-Za-z0-9 ]", "");
            } catch (NullPointerException e) {
                keyword = "";
            }
        }
        return "redirect:/orders/search_product/page/1?sortField=name&sortDir=asc&keyword=" + keyword;
    }

    @GetMapping("/orders/search_product/page/{pageNum}")
    public String searchProductsByPage(
            @PagingAndSortingParam(listName = "listProducts", moduleURL = "/orders/search_product") PagingAndSortingHelper helper,
            @PathVariable(name = "pageNum") int pageNum) {
        productService.searchProducts(pageNum, helper);
        return "orders/search_product";
    }

    private boolean isAlphaNumericOrSpace(String s) {
        return s != null && s.matches("^[a-zA-Z0-9 ]*$");
    }
}
