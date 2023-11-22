package ro.ase.costin.ecomfront.shoppingcart;

import javax.servlet.http.HttpServletRequest;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.ase.costin.ecomcommon.entity.Customer;
import ro.ase.costin.ecomcommon.exception.CustomerNotFoundException;
import ro.ase.costin.ecomcommon.exception.ProductNotFoundException;
import ro.ase.costin.ecomfront.customer.CustomerService;
import ro.ase.costin.ecomfront.utils.Utility;

@RestController
@AllArgsConstructor
public class ShoppingCartRestController {

    private ShoppingCartService cartService;

    private CustomerService customerService;

    @PostMapping("/cart/add/{productId}/{quantity}")
    public String addProductToCart(@PathVariable("productId") Integer productId,
                                   @PathVariable("quantity") Integer quantity, HttpServletRequest request) {

        try {
            Customer customer = getAuthenticatedCustomer(request);
            Integer updatedQuantity = cartService.addProductToCart(productId, quantity, customer);
            String unit = "unitate";
            if (updatedQuantity > 1) {
                unit = "unități";
            }
            return updatedQuantity + unit + " ale acestui produs au fost adăugate în coșul de cumpărături.";
        } catch (CustomerNotFoundException ex) {
            return "Trebuie să fi autentificat(ă) ca să poți adăuga acest produs în coșul de cumpărături.";
        } catch (ShoppingCartException ex) {
            return ex.getMessage();
        }
    }

    private Customer getAuthenticatedCustomer(HttpServletRequest request) throws CustomerNotFoundException {
        String email = Utility.getEmailOfAuthenticatedCustomer(request);
        if (email == null) {
            throw new CustomerNotFoundException("Clientul nu este autentificat.");
        }
        return customerService.getCustomerByEmail(email);
    }

    @PostMapping("/cart/update/{productId}/{quantity}")
    public String updateQuantity(@PathVariable("productId") Integer productId,
                                 @PathVariable("quantity") Integer quantity, HttpServletRequest request) {
        try {
            Customer customer = getAuthenticatedCustomer(request);
            float subtotal = cartService.updateQuantity(productId, quantity, customer);

            return String.valueOf(subtotal);
        } catch (CustomerNotFoundException | ProductNotFoundException ex) {
            return "Trebuie să fi autentificat(ă) ca să poți schimba cantitatea pentru acest produs.";
        }
    }

    @DeleteMapping("/cart/remove/{productId}")
    public String removeProduct(@PathVariable("productId") Integer productId, HttpServletRequest request) {
        try {
            Customer customer = getAuthenticatedCustomer(request);
            cartService.removeProduct(productId, customer);
            return "Produsul a fost înlăturat din coșul de cumpărături.";
        } catch (CustomerNotFoundException e) {
            return "Trebuie să fi autentificat(ă) ca să poți înlătura acest produs.";
        }
    }
}
