package ro.ase.costin.ecomfront.address;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ro.ase.costin.ecomcommon.entity.Address;
import ro.ase.costin.ecomcommon.entity.Country;
import ro.ase.costin.ecomcommon.entity.Customer;
import ro.ase.costin.ecomfront.customer.CustomerService;
import ro.ase.costin.ecomfront.utils.ControllerHelper;


@Controller
@AllArgsConstructor
public class AddressController {

    private AddressService addressService;

    private CustomerService customerService;

    private ControllerHelper controllerHelper;

    @GetMapping("/address_book")
    public String showAddressBook(Model model, HttpServletRequest request) {
        Customer customer = controllerHelper.getAuthenticatedCustomer(request);
        List<Address> listAddresses = addressService.listAddressBook(customer);

        boolean usePrimaryAddressAsDefault = listAddresses.stream().noneMatch(Address::isDefaultForShipping);

        model.addAttribute("listAddresses", listAddresses);
        model.addAttribute("customer", customer);
        model.addAttribute("usePrimaryAddressAsDefault", usePrimaryAddressAsDefault);
        return "address/addresses";
    }

    @GetMapping("/address_book/new")
    public String newAddress(Model model) {
        List<Country> listCountries = customerService.listAllCountries();
        model.addAttribute("listCountries", listCountries);
        model.addAttribute("address", new Address());
        model.addAttribute("pageTitle", "Adaugați o nouă adresă");
        return "address/address_form";
    }

    @PostMapping("/address_book/save")
    public String saveAddress(Address address, HttpServletRequest request, RedirectAttributes ra) {
        Customer customer = controllerHelper.getAuthenticatedCustomer(request);
        address.setCustomer(customer);
        addressService.save(address);
        String redirectOption = request.getParameter("redirect");
        String redirectURL = "redirect:/address_book";
        if ("checkout".equals(redirectOption)) {
            redirectURL += "?redirect=checkout";
        }
        ra.addFlashAttribute("message", "Adresa a fost salvată.");
        return redirectURL;
    }

    @GetMapping("/address_book/edit/{id}")
    public String editAddress(@PathVariable("id") Integer addressId, Model model, HttpServletRequest request) {
        Customer customer = controllerHelper.getAuthenticatedCustomer(request);
        List<Country> listCountries = customerService.listAllCountries();
        Address address = addressService.get(addressId, customer.getId());
        model.addAttribute("address", address);
        model.addAttribute("listCountries", listCountries);
        model.addAttribute("pageTitle", "Editare adresă (ID: " + addressId + ")");
        return "address/address_form";
    }

    @GetMapping("/address_book/delete/{id}")
    public String deleteAddress(@PathVariable("id") Integer addressId, RedirectAttributes ra, HttpServletRequest request) {
        Customer customer = controllerHelper.getAuthenticatedCustomer(request);
        addressService.delete(addressId, customer.getId());
        ra.addFlashAttribute("message", "Adresa a fost ștearsă.");
        return "redirect:/address_book";
    }

    @GetMapping("/address_book/default/{id}")
    public String setDefaultAddress(@PathVariable("id") Integer addressId, HttpServletRequest request) {
        Customer customer = controllerHelper.getAuthenticatedCustomer(request);
        addressService.setDefaultAddress(addressId, customer.getId());
        String redirectOption = request.getParameter("redirect");
        String redirectURL = "redirect:/address_book";

        if ("cart".equals(redirectOption)) {
            redirectURL = "redirect:/cart";
        } else if ("checkout".equals(redirectOption)) {
            redirectURL = "redirect:/checkout";
        }
        return redirectURL;
    }
}
