package ro.ase.costin.ecomfront.customer;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ro.ase.costin.ecomcommon.data.EmailSettingBag;
import ro.ase.costin.ecomcommon.entity.Customer;
import ro.ase.costin.ecomcommon.exception.CustomerNotFoundException;
import ro.ase.costin.ecomfront.setting.SettingService;
import ro.ase.costin.ecomfront.utils.Utility;

@Controller
@AllArgsConstructor
public class ForgotPasswordController {

    private CustomerService customerService;

    private SettingService settingService;

    @GetMapping("/forgot_password")
    public String showRequestForm() {
        return "customer/forgot_password_form";
    }

    @PostMapping("/forgot_password")
    public String processRequestForm(HttpServletRequest request, Model model) {
        String email = request.getParameter("email");
        try {
            String token = customerService.updateResetPasswordToken(email);
            String link = Utility.getSiteURL(request) + "/reset_password?token=" + token;
            sendEmail(link, email);
            model.addAttribute("message", "Ți-am trimis un link de resetare a parolei pe adresa ta de email.");
        } catch (CustomerNotFoundException e) {
            model.addAttribute("error", e.getMessage());
        } catch (UnsupportedEncodingException | MessagingException e) {
            model.addAttribute("error", "Email-ul nu poate fi trimis.");
        }
        return "customer/forgot_password_form";
    }

    private void sendEmail(String link, String email) throws UnsupportedEncodingException, MessagingException {
        EmailSettingBag emailSettings = settingService.getEmailSettings();
        JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);

        String subject = "Acesta este linku-ul tău de resetare a parolei";

        String content = "<p>Salut,</p>"
                + "<p>A fost &#238;nregistrat&#259; o cerere de resetare a parolei tale. "
                + "Apas&#259; pe link-ul de mai jos pentru schimbarea parolei:</p>"
                + "<p><a href=\"" + link + "\">Schimbare parol&#259;</a></p>"
                + "<br>"
                + "<p>Ignor&#259; acest mesaj dac&#259; &#355;i-ai reamintit parola sau dac&#259; "
                + "nu ai f&#259;cut nicio cerere de resetare a sa.</p>"
                + "<br>"
                + "<p>Mul&#355;umim,</p>"
                + "<p>Echipa Circuit Store.</p>";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
        message.setContentLanguage(new String[]{"ro"});
        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText(content, true);
        mailSender.send(message);
    }

    @GetMapping("/reset_password")
    public String showResetForm(String token, Model model) {
        Customer customer = customerService.getByResetPasswordToken(token);
        if (customer != null) {
            model.addAttribute("token", token);
        } else {
            model.addAttribute("pageTitle", "Token invalid");
            model.addAttribute("message", "Token invalid");
            return "message";
        }
        return "customer/reset_password_form";
    }

    @PostMapping("/reset_password")
    public String processResetForm(HttpServletRequest request, Model model) {
        String token = request.getParameter("token");
        String password = request.getParameter("password");

        try {
            customerService.updatePassword(token, password);
            model.addAttribute("pageTitle", "Resetează-ți parola");
            model.addAttribute("title", "Resetează-ți parola");
            model.addAttribute("message", "Parola ta a fost schimbată cu succes!");
        } catch (CustomerNotFoundException e) {
            model.addAttribute("pageTitle", "Token invalid");
            model.addAttribute("message", e.getMessage());
        }
        return "message";
    }
}