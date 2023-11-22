package ro.ase.costin.ecomfront.checkout.paypal;

import java.util.List;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import ro.ase.costin.ecomcommon.data.PaymentSettingBag;
import ro.ase.costin.ecomfront.setting.SettingService;

@Component
@RequiredArgsConstructor
public class PayPalService {

    private static final String GET_ORDER_API = "/v2/checkout/orders/";

    @NonNull
    private SettingService settingService;

    public boolean validateOrder(String orderId) throws PayPalApiException {
        PayPalOrderResponse orderResponse = getOrderDetails(orderId);
        return orderResponse.validate(orderId);
    }

    private PayPalOrderResponse getOrderDetails(String orderId) throws PayPalApiException {
        ResponseEntity<PayPalOrderResponse> response = makeRequest(orderId);
        HttpStatus statusCode = response.getStatusCode();
        if (!statusCode.equals(HttpStatus.OK)) {
            throwExceptionWithSpecificMessage(statusCode);
        }
        return response.getBody();
    }

    private ResponseEntity<PayPalOrderResponse> makeRequest(String orderId) {
        PaymentSettingBag paymentSettings = settingService.getPaymentSettings();
        String baseURL = paymentSettings.getURL();
        String requestURL = baseURL + GET_ORDER_API + orderId;
        String clientId = paymentSettings.getClientID();
        String clientSecret = paymentSettings.getClientSecret();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.add("Accept-Language", "en_US");
        headers.setBasicAuth(clientId, clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(requestURL, HttpMethod.GET, request, PayPalOrderResponse.class);
    }

    private void throwExceptionWithSpecificMessage(HttpStatus statusCode) throws PayPalApiException {
        String message;
        switch (statusCode) {
            case NOT_FOUND:
                message = "ID comenzii nu a fost găsit"; break;
            case BAD_REQUEST:
                message = "Cerere eronată către API-ul PayPal Checkout"; break;
            case INTERNAL_SERVER_ERROR:
                message = "Eroare server PayPal"; break;
            default:
                message = "PayPal a răspuns cu un cod de eroare";
        }
        throw new PayPalApiException(message);
    }
}

