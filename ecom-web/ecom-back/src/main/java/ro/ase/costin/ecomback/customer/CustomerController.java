package ro.ase.costin.ecomback.customer;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ro.ase.costin.ecomback.user.export.UserCsvExporter;
import ro.ase.costin.ecomcommon.entity.User;
import ro.ase.costin.ecomcommon.exception.CustomerNotFoundException;
import ro.ase.costin.ecomback.paging.PagingAndSortingHelper;
import ro.ase.costin.ecomback.paging.PagingAndSortingParam;
import ro.ase.costin.ecomcommon.entity.Country;
import ro.ase.costin.ecomcommon.entity.Customer;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CustomerController {

    private static final String DEFAULT_REDIRECT_URL = "redirect:/customers/page/1?sortField=lastName&sortDir=asc";

    @NonNull
    private CustomerService customerService;

    @GetMapping("/customers")
    public String listFirstPage() {
        return DEFAULT_REDIRECT_URL;
    }

    @GetMapping("/customers/page/{pageNum}")
    public String listByPage(
            @PagingAndSortingParam(listName = "listCustomers", moduleURL = "/customers") PagingAndSortingHelper helper,
            @PathVariable(name = "pageNum") int pageNum) {

        customerService.listByPage(pageNum, helper);
        return "customers/customers";
    }

    @GetMapping("/customers/{id}/enabled/{status}")
    public String updateCustomerEnabledStatus(@PathVariable("id") Integer id,
                                              @PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) {
        customerService.updateCustomerEnabledStatus(id, enabled);
        String status = enabled ? "activat" : "dezactivat";
        String message = "Clientul (ID: " + id + ") a fost " + status + ".";
        redirectAttributes.addFlashAttribute("message", message);
        return DEFAULT_REDIRECT_URL;
    }

    @GetMapping("/customers/detail/{id}")
    public String viewCustomer(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        try {
            Customer customer = customerService.get(id);
            model.addAttribute("customer", customer);
            return "customers/customer_detail_modal";

        } catch (CustomerNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
            return DEFAULT_REDIRECT_URL;
        }
    }

    @GetMapping("/customers/edit/{id}")
    public String editCustomer(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        try {
            Customer customer = customerService.get(id);
            List<Country> countries = customerService.listAllCountries();

            model.addAttribute("listCountries", countries);
            model.addAttribute("customer", customer);
            model.addAttribute("pageTitle", String.format("Editare client (ID: %d)", id));
            return "customers/form_customer";

        } catch (CustomerNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
            return DEFAULT_REDIRECT_URL;
        }
    }

    @PostMapping("/customers/save")
    public String saveCustomer(Customer customer, RedirectAttributes ra) {
        try {
            customerService.save(customer);
        } catch (CustomerNotFoundException e) {
            log.error(e.getMessage());
        }
        ra.addFlashAttribute("message",
                "Clientul (ID: " + customer.getId() + ") a fost actualizat.");
        return DEFAULT_REDIRECT_URL;
    }

    @GetMapping("/customers/delete/{id}")
    public String deleteCustomer(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            customerService.delete(id);
            ra.addFlashAttribute("message",
                    "Clientul (ID: " + id + ") a fost șters.");

        } catch (CustomerNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
        }
        return DEFAULT_REDIRECT_URL;
    }

    @GetMapping("/customers/export/csv")
    public void exportToCSV(HttpServletResponse response) {
        List<Customer> customers = customerService.listAllCustomers();
        CustomerCsvExporter csvExporter = new CustomerCsvExporter();
        csvExporter.exportCsv(customers, response);
    }
}
