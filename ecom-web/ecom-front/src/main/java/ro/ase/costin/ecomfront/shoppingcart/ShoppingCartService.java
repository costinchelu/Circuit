package ro.ase.costin.ecomfront.shoppingcart;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ro.ase.costin.ecomcommon.entity.CartItem;
import ro.ase.costin.ecomcommon.entity.Customer;
import ro.ase.costin.ecomcommon.entity.Product;
import ro.ase.costin.ecomcommon.exception.ProductNotFoundException;
import ro.ase.costin.ecomfront.product.ProductRepository;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ShoppingCartService {

    private static final int MAX_QUANTITY = 20;

    @NonNull
    private CartItemRepository cartItemRepository;

    @NonNull
    private ProductRepository productRepository;

    public Integer addProductToCart(Integer productId, Integer quantity, Customer customer) throws ShoppingCartException {
        Integer updatedQuantity = quantity;
        Product product = new Product(productId);
        CartItem cartItem = cartItemRepository.findByCustomerAndProduct(customer, product);

        if (cartItem != null) {
            updatedQuantity = cartItem.getQuantity() + quantity;
            if (updatedQuantity > MAX_QUANTITY) {
                throw new ShoppingCartException("Nu puteți adăuga " + quantity + " unitate(unități) deoarece sunt deja "
                        + cartItem.getQuantity() + " unități din acest produs în coșul de cumpărături. Cantitatea maxim permisă este "
                        + MAX_QUANTITY + ".");
            }
        } else {
            cartItem = new CartItem();
            cartItem.setCustomer(customer);
            cartItem.setProduct(product);
        }

        cartItem.setQuantity(updatedQuantity);
        cartItemRepository.save(cartItem);
        return updatedQuantity;
    }

    public List<CartItem> listCartItems(Customer customer) {
        return cartItemRepository.findByCustomer(customer);
    }

    public float updateQuantity(Integer productId, Integer quantity, Customer customer) throws ProductNotFoundException {
        cartItemRepository.updateQuantity(quantity, customer.getId(), productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Cannot find the product with id " + productId));
        return product.getDiscountPrice() * quantity;
    }

    public void removeProduct(Integer productId, Customer customer) {
        cartItemRepository.deleteByCustomerAndProduct(customer.getId(), productId);
    }

    public void deleteByCustomer(Customer customer) {
        cartItemRepository.deleteByCustomer(customer.getId());
    }
}
