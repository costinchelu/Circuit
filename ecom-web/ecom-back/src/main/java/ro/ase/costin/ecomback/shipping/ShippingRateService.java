package ro.ase.costin.ecomback.shipping;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ro.ase.costin.ecomback.exception.ShippingRateAlreadyExistsException;
import ro.ase.costin.ecomback.exception.ShippingRateNotFoundException;
import ro.ase.costin.ecomback.paging.PagingAndSortingHelper;
import ro.ase.costin.ecomback.product.ProductRepository;
import ro.ase.costin.ecomback.setting.repository.CountryRepository;
import ro.ase.costin.ecomcommon.entity.Country;
import ro.ase.costin.ecomcommon.entity.Product;
import ro.ase.costin.ecomcommon.entity.ShippingRate;
import ro.ase.costin.ecomcommon.exception.ProductNotFoundException;

@Service
@Transactional
@AllArgsConstructor
public class ShippingRateService {

    public static final int RATES_PER_PAGE = 10;

    private static final int DIM_DIVISOR = 5000;

    private static final String ERROR_MESSAGE = "Tariful pentru livrare nu a fost găsit - ID: ";

    private ShippingRateRepository shippingRateRepository;

    private CountryRepository countryRepository;

    private ProductRepository productRepository;

    public void listByPage(int pageNum, PagingAndSortingHelper helper) {
        helper.listEntities(pageNum, RATES_PER_PAGE, shippingRateRepository);
    }

    public List<Country> listAllCountries() {
        return countryRepository.findAllByOrderByNameAsc();
    }

    public void save(ShippingRate rateInForm) throws ShippingRateAlreadyExistsException {
        ShippingRate rateInDB = shippingRateRepository.findByCountryAndState(rateInForm.getCountry().getId(), rateInForm.getState());
        boolean foundExistingRateInNewMode = rateInForm.getId() == null && rateInDB != null;
        boolean foundDifferentExistingRateInEditMode =
                        rateInForm.getId() != null &&
                        rateInDB != null &&
                        !rateInDB.getId().equals(rateInForm.getId());

        if (foundExistingRateInNewMode || foundDifferentExistingRateInEditMode) {
            throw new ShippingRateAlreadyExistsException("Tarif pentru livrare existent pentru destinația "
                    + rateInForm.getCountry().getName() + ", " + rateInForm.getState());
        }
        shippingRateRepository.save(rateInForm);
    }

    public ShippingRate get(Integer id) throws ShippingRateNotFoundException {
        try {
            return shippingRateRepository.findById(id).orElseThrow(NoSuchElementException::new);
        } catch (NoSuchElementException ex) {
            throw new ShippingRateNotFoundException(ERROR_MESSAGE + id);
        }
    }

    public void updateCODSupport(Integer id, boolean codSupported) throws ShippingRateNotFoundException {
        Long count = shippingRateRepository.countById(id);
        if (count == null || count == 0) {
            throw new ShippingRateNotFoundException(ERROR_MESSAGE + id);
        }
        shippingRateRepository.updateCODSupport(id, codSupported);
    }

    public void delete(Integer id) throws ShippingRateNotFoundException {
        Long count = shippingRateRepository.countById(id);
        if (count == null || count == 0) {
            throw new ShippingRateNotFoundException(ERROR_MESSAGE + id);
        }
        shippingRateRepository.deleteById(id);
    }

    public float calculateShippingCost(Integer productId, Integer countryId, String state)
            throws ShippingRateNotFoundException, ProductNotFoundException {
        ShippingRate shippingRate = shippingRateRepository.findByCountryAndState(countryId, state);

        if (shippingRate == null) {
            throw new ShippingRateNotFoundException("Nu a fost găsit un tarif de livrare pentru această destinație. Acesta va trebui introdus.");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Produsul nu a fost găsit (ID: " + productId + ")."));

        float dimensions = (product.getLength() * product.getWidth() * product.getHeight()) / DIM_DIVISOR;
        float packageSize = Math.max(product.getWeight(), dimensions);
        return packageSize * shippingRate.getRate();
    }
}