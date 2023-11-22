package ro.ase.costin.ecomback.shipping;

import java.util.List;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ro.ase.costin.ecomback.exception.ShippingRateAlreadyExistsException;
import ro.ase.costin.ecomback.exception.ShippingRateNotFoundException;
import ro.ase.costin.ecomback.paging.PagingAndSortingHelper;
import ro.ase.costin.ecomback.paging.PagingAndSortingParam;
import ro.ase.costin.ecomcommon.entity.Country;
import ro.ase.costin.ecomcommon.entity.ShippingRate;

@Controller
@RequiredArgsConstructor
public class ShippingRateController {

    private final String defaultRedirectURL = "redirect:/shipping_rates/page/1?sortField=days&sortDir=asc";
//    private final String defaultRedirectURL = "redirect:/shipping_rates/page/1?sortField=country&sortDir=asc";

    @NonNull
    private ShippingRateService service;

    @GetMapping("/shipping_rates")
    public String listFirstPage() {
        return defaultRedirectURL;
    }

    @GetMapping("/shipping_rates/page/{pageNum}")
    public String listByPage(
            @PagingAndSortingParam(listName = "shippingRates", moduleURL = "/shipping_rates") PagingAndSortingHelper helper,
            @PathVariable(name = "pageNum") int pageNum) {
        service.listByPage(pageNum, helper);
        return "shipping/shipping_rates";
    }

    @GetMapping("/shipping_rates/new")
    public String newRate(Model model) {
        List<Country> listCountries = service.listAllCountries();
        model.addAttribute("rate", new ShippingRate());
        model.addAttribute("listCountries", listCountries);
        model.addAttribute("pageTitle", "Creare tarif livrare");
        return "shipping/shipping_rate_form";
    }

    @PostMapping("/shipping_rates/save")
    public String saveRate(ShippingRate rate, RedirectAttributes ra) {
        try {
            service.save(rate);
            ra.addFlashAttribute("message", "Tariful pentru livrare a fost salvat.");
        } catch (ShippingRateAlreadyExistsException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
        }
        return defaultRedirectURL;
    }

    @GetMapping("/shipping_rates/edit/{id}")
    public String editRate(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes ra) {
        try {
            ShippingRate rate = service.get(id);
            List<Country> listCountries = service.listAllCountries();

            model.addAttribute("listCountries", listCountries);
            model.addAttribute("rate", rate);
            model.addAttribute("pageTitle", "Editare tarif (ID: " + id + ")");

            return "shipping/shipping_rate_form";
        } catch (ShippingRateNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
            return defaultRedirectURL;
        }
    }

    @GetMapping("/shipping_rates/cod/{id}/enabled/{supported}")
    public String updateCODSupport(@PathVariable(name = "id") Integer id,
                                   @PathVariable(name = "supported") Boolean supported,
                                   Model model, RedirectAttributes ra) {
        try {
            service.updateCODSupport(id, supported);
            ra.addFlashAttribute("message", "Setare pentru plata la livrare (ID: " + id + ") a fost actualizată.");
        } catch (ShippingRateNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
        }
        return defaultRedirectURL;
    }

    @GetMapping("/shipping_rates/delete/{id}")
    public String deleteRate(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes ra) {
        try {
            service.delete(id);
            ra.addFlashAttribute("message", "Tariful de livrare (ID: " + id + ") a fost șters.");
        } catch (ShippingRateNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
        }
        return defaultRedirectURL;
    }
}