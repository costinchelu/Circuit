package ro.ase.costin.ecomfront.order;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import ro.ase.costin.ecomcommon.data.OrderReturnRequest;

@SpringBootTest
@AutoConfigureMockMvc
@Disabled("Integration tests")
class OrderRestControllerTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @Test
    @WithUserDetails("circuit.ecom@gmail.com")
    void testSendOrderReturnRequestFailed() throws Exception {
        Integer orderId = 1111;
        OrderReturnRequest returnRequest = new OrderReturnRequest(orderId, "", "");

        String requestURL = "/orders/return";

        mockMvc.perform(post(requestURL)
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(returnRequest)))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @WithUserDetails("costin.chelu@gmail.com")
    void testSendOrderReturnRequestSuccessful() throws Exception {
        Integer orderId = 23;
        String reason = "I bought the wrong items";
        String note = "Please return my money";

        OrderReturnRequest returnRequest = new OrderReturnRequest(orderId, reason, note);

        String requestURL = "/orders/return";

        mockMvc.perform(post(requestURL)
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(returnRequest)))
                .andExpect(status().isOk())
                .andDo(print());
    }
}