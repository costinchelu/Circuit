package ro.ase.costin.ecomback.product;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ro.ase.costin.ecomback.brand.BrandService;
import ro.ase.costin.ecomback.category.CategoryService;
import ro.ase.costin.ecomback.paging.PagingAndSortingHelper;
import ro.ase.costin.ecomback.paging.PagingAndSortingParam;
import ro.ase.costin.ecomback.product.export.ProductCsvExporter;
import ro.ase.costin.ecomback.product.export.ProductPdfExporter;
import ro.ase.costin.ecomcommon.exception.ProductNotFoundException;
import ro.ase.costin.ecomback.security.UserDetailsSecurity;
import ro.ase.costin.ecomback.security.UserType;
import ro.ase.costin.ecomback.utils.FileUploadUtil;
import ro.ase.costin.ecomcommon.entity.Brand;
import ro.ase.costin.ecomcommon.entity.Category;
import ro.ase.costin.ecomcommon.entity.Product;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private static final String DEFAULT_REDIRECT_URL = "redirect:/products/page/1?sortField=name&sortDir=asc&categoryId=0";

    private static final String MESSAGE = "message";

    private static final String PRODUCT_IMAGES_DIR = "product-images/";

    private static final String PRODUCT_FORM = "products/form_product";

    @NonNull
    private ProductService productService;

    @NonNull
    private BrandService brandService;

    @NonNull
    private CategoryService categoryService;

    @GetMapping("/products")
    public String listFirstPage() {
        return DEFAULT_REDIRECT_URL;
    }

    @GetMapping("/products/page/{pageNum}")
    public String listByPage(
            @PagingAndSortingParam(listName = "listProducts", moduleURL = "/products") PagingAndSortingHelper helper,
            @PathVariable(name = "pageNum") int pageNum, Model model, Integer categoryId
    ) {
        productService.listByPage(pageNum, helper, categoryId);
        List<Category> listCategories = categoryService.listCategoriesForForm();

        if (categoryId != null) model.addAttribute("categoryId", categoryId);
        model.addAttribute("listCategories", listCategories);

        return "products/products";
    }

    @GetMapping("/products/new")
    public String newProduct(Model model) {
        List<Brand> brands = brandService.listAll();

        Product product = new Product();
        product.setEnabled(true);
        product.setInStock(true);

        model.addAttribute("product", product);
        model.addAttribute("listBrands", brands);
        model.addAttribute("pageTitle", "Inserare produs");
        model.addAttribute("numberOfExistingExtraImages", 0); // corecție număr poze suplimentare produs

        return PRODUCT_FORM;
    }

    @PostMapping("/products/save")
    public String saveProduct(Product product,
                              RedirectAttributes ra,
                              @RequestParam(value = "fileImage", required = false) MultipartFile mainImageBinary,
                              @RequestParam(value = "extraImage", required = false) MultipartFile[] extraImageBinaries,
                              @RequestParam(name = "detailIDs", required = false) String[] detailIDs,
                              @RequestParam(name = "detailNames", required = false) String[] detailNames,
                              @RequestParam(name = "detailValues", required = false) String[] detailValues,
                              @RequestParam(name = "imageIDs", required = false) String[] imageIDs,
                              @RequestParam(name = "imageNames", required = false) String[] imageNames,
                              @AuthenticationPrincipal UserDetailsSecurity loggedUser)
            throws IOException, ProductNotFoundException {

        if ((!loggedUser.hasRole(UserType.ADMIN.getName()) && !loggedUser.hasRole(UserType.EDITOR.getName()))
                && loggedUser.hasRole(UserType.SALES.getName())) {
            productService.saveProductPrice(product);
            ra.addFlashAttribute(MESSAGE, "Produsul a fost salvat.");
            return DEFAULT_REDIRECT_URL;
        }

        ProductSaveMapper.setMainImageName(mainImageBinary, product);
        ProductSaveMapper.setExistingExtraImageNames(imageIDs, imageNames, product);
        ProductSaveMapper.setNewExtraImageNames(extraImageBinaries, product);
        ProductSaveMapper.setProductDetails(detailIDs, detailNames, detailValues, product);
        Product savedProduct = productService.saveProduct(product);
        ProductSaveMapper.saveUploadedImages(mainImageBinary, extraImageBinaries, savedProduct);
        ProductSaveMapper.cleanExtraImagesNotInDb(product);

        ra.addFlashAttribute(MESSAGE, "Produsul a fost salvat.");
        return DEFAULT_REDIRECT_URL;
    }

    @GetMapping("/products/{id}/enabled/{status}")
    public String updateProductEnabledStatus(@PathVariable("id") Integer id,
                                             @PathVariable("status") boolean enabled,
                                             RedirectAttributes redirectAttributes) {
        productService.updateProductEnabledStatus(id, enabled);
        String status = enabled ? "activat" : "dezactivat";
        String message = "Produsul (ID: " + id + ") a fost " + status;
        redirectAttributes.addFlashAttribute(MESSAGE, message);
        return DEFAULT_REDIRECT_URL;
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable(name = "id") Integer id,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        try {
            productService.delete(id);

            String productExtraImagesDir = PRODUCT_IMAGES_DIR + id + "/extras";
            String productImagesDir = PRODUCT_IMAGES_DIR + id;
            FileUploadUtil.removeDir(productExtraImagesDir);
            FileUploadUtil.removeDir(productImagesDir);

            String message = "Produsul (ID: " + id + ") a fost șters.";
            redirectAttributes.addFlashAttribute(MESSAGE, message);
        } catch (ProductNotFoundException ex) {
            redirectAttributes.addFlashAttribute(MESSAGE, ex.getMessage());
        }
        return DEFAULT_REDIRECT_URL;
    }

    @GetMapping("/products/edit/{id}")
    public String editProduct(@PathVariable("id") Integer id,
                              Model model,
                              RedirectAttributes ra, @AuthenticationPrincipal UserDetailsSecurity loggedUser) {
        try {
            Product productToEdit = productService.get(id);
            List<Brand> brands = brandService.listAll();
            Integer extraImagesCount = productToEdit.getImages().size();

            boolean isReadOnlyForSalesperson = (!loggedUser.hasRole(UserType.ADMIN.getName()) &&
                    !loggedUser.hasRole(UserType.EDITOR.getName()))
                    && loggedUser.hasRole(UserType.SALES.getName());
            model.addAttribute("isReadOnlyForSalesperson", isReadOnlyForSalesperson);
            model.addAttribute("product", productToEdit);
            model.addAttribute("listBrands", brands);
            model.addAttribute("pageTitle", "Editare produs (ID: " + id + ")");
            model.addAttribute("numberOfExistingExtraImages", extraImagesCount);

            return PRODUCT_FORM;

        } catch (ProductNotFoundException e) {
            ra.addFlashAttribute(MESSAGE, e.getMessage());
            return DEFAULT_REDIRECT_URL;
        }
    }

    @GetMapping("/products/detail/{id}")
    public String viewProductDetails(@PathVariable("id") Integer id,
                                     Model model,
                                     RedirectAttributes ra) {
        try {
            Product product = productService.get(id);
            model.addAttribute("product", product);

            return "products/product_details_modal";

        } catch (ProductNotFoundException e) {
            ra.addFlashAttribute(MESSAGE, e.getMessage());

            return DEFAULT_REDIRECT_URL;
        }
    }

    @GetMapping("/products/export/csv")
    public void exportToCSV(HttpServletResponse response) {
        List<Product> products = productService.listAllProducts()
                .stream()
                .sorted(Comparator.comparing(Product::getName))
                .collect(Collectors.toList());
        ProductCsvExporter csvExporter = new ProductCsvExporter();
        csvExporter.exportCsv(products, response);
    }

    @GetMapping("/products/export/pdf")
    public void exportToPDF(HttpServletResponse response) {
        List<Product> products = productService.listAllProducts()
                .stream()
                .sorted(Comparator.comparing(Product::getName))
                .collect(Collectors.toList());
        ProductPdfExporter pdfExporter = new ProductPdfExporter();
        pdfExporter.exportPdf(products, response);
    }
}