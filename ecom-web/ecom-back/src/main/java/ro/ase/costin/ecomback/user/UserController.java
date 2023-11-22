package ro.ase.costin.ecomback.user;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ro.ase.costin.ecomback.exception.UserNotFoundException;
import ro.ase.costin.ecomback.paging.PagingAndSortingHelper;
import ro.ase.costin.ecomback.paging.PagingAndSortingParam;
import ro.ase.costin.ecomback.user.export.UserCsvExporter;
import ro.ase.costin.ecomback.user.export.UserExcelExporter;
import ro.ase.costin.ecomback.user.export.UserPdfExporter;
import ro.ase.costin.ecomback.utils.FileUploadUtil;
import ro.ase.costin.ecomcommon.entity.Role;
import ro.ase.costin.ecomcommon.entity.User;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private static final String DEFAULT_REDIRECT_URL = "redirect:/users/page/1?sortField=lastName&sortDir=asc";

    @NonNull
    private UserService userService;

    @GetMapping("/users")
    public String listFirstPage() {
        return DEFAULT_REDIRECT_URL;
    }

    @GetMapping("/users/page/{pageNum}")
    public String listByPage(
            @PagingAndSortingParam(listName = "listUsers", moduleURL = "/users") PagingAndSortingHelper helper,
            @PathVariable(name = "pageNum") int pageNum) {

        userService.listAllByPage(pageNum, helper);
        return "users/users";
    }

    @GetMapping("/users/new")
    public String newUser(Model model) {
        List<Role> allRoles = userService.listAllRoles();
        User user = new User();
        user.setEnabled(true);
        model.addAttribute("user", user);
        model.addAttribute("roleList", allRoles);
        model.addAttribute("pageTitle", "Creare utilizator");
        return "users/form_user";
    }

    @PostMapping("/users/save")
    public String saveUser(User user,
                           RedirectAttributes redirectAttributes,
                           @RequestParam("image") MultipartFile multipartFile) throws IOException {

        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            user.setPhoto(fileName);
            User savedUser = userService.save(user);

            String uploadDir = "user-photos/" + savedUser.getId();
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } else {
            if (user.getPhoto().isEmpty()) {
                user.setPhoto(null);
            }
            userService.save(user);
        }
        redirectAttributes.addFlashAttribute("message", "Utilizatorul a fost salvat.");

        return getRedirectedUrlToAffectedUser(user);
    }

    private String getRedirectedUrlToAffectedUser(User user) {
        String firstPartOfEmail = user.getEmail().split("@")[0];
        return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword=" + firstPartOfEmail;
    }

    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable(name = "id") Integer userId,
                           Model model,
                           RedirectAttributes redirectAttributes) {

        try {
            User user = userService.get(userId);
            List<Role> allRoles = userService.listAllRoles();
            model.addAttribute("user", user);
            model.addAttribute("roleList", allRoles);
            model.addAttribute("pageTitle", "Editare detalii pentru " + user.getFirstName() + " "
                    + user.getLastName());
            return "users/form_user";
        } catch (UserNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            return DEFAULT_REDIRECT_URL;
        }
    }

    @GetMapping("/users/{id}/enabled/{status}")
    public String updateUserEnabledStatus(@PathVariable("id") Integer id,
                                          @PathVariable("status") boolean enabled,
                                          RedirectAttributes redirectAttributes) {
        try {
            userService.updateUserEnabledStatus(id, enabled);
            String status = enabled ? "activat" : "dezactivat";
            String message = "Utilizatorul (ID: " + id + ") a fost " + status;
            redirectAttributes.addFlashAttribute("message", message);
        } catch (UserNotFoundException ex) {
            if (!redirectAttributes.containsAttribute("message")) {
                redirectAttributes.addFlashAttribute("message", "Nu se poate modifica starea utilizatorului.");
            }
        }
        return DEFAULT_REDIRECT_URL;
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable(name = "id") Integer id,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        try {
            userService.delete(id);
            String userDir = "user-photos/" + id;
            FileUploadUtil.removeDir(userDir);

            redirectAttributes.addFlashAttribute("message", "Utilizatorul (ID: " + id + ") a fost șters.");
        } catch (UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return DEFAULT_REDIRECT_URL;
    }

    @GetMapping("/users/export/csv")
    public void exportToCSV(HttpServletResponse response) {
        List<User> users = userService.listAllUsers()
                .stream()
                .sorted(Comparator.comparing(User::getLastName))
                .collect(Collectors.toList());
        UserCsvExporter csvExporter = new UserCsvExporter();
        csvExporter.exportCsv(users, response);
    }

    @GetMapping("/users/export/excel")
    public void exportToXLSX(HttpServletResponse response) throws IOException {
        List<User> users = userService.listAllUsers()
                .stream()
                .sorted(Comparator.comparing(User::getLastName))
                .collect(Collectors.toList());
        UserExcelExporter xlsxExporter = new UserExcelExporter();
        xlsxExporter.exportXlsx(users, response);
    }

    @GetMapping("/users/export/pdf")
    public void exportToPDF(HttpServletResponse response) {
        List<User> users = userService.listAllUsers()
                .stream()
                .sorted(Comparator.comparing(User::getLastName))
                .collect(Collectors.toList());
        UserPdfExporter pdfExporter = new UserPdfExporter();
        pdfExporter.exportPdf(users, response);
    }
}
