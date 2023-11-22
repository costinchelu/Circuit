package ro.ase.costin.ecomback.report;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Disabled("Slow tests")
class ReportRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "user1", password = "pass1", authorities = {"Sales"})
    void testGetReportDataByDateRange() throws Exception {
        String startDate = "2022-04-01";
        String endDate = "2022-04-30";
        String requestURL = "/reports/sales_by_date/" + startDate + "/" + endDate;
        mockMvc.perform(get(requestURL)).andExpect(status().isOk()).andDo(print());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/reports/sales_by_date/last_7_days", "/reports/sales_by_date/last_6_months", "/reports/category/last_7_days", "/reports/product/last_7_days"})
    @WithMockUser(username = "user1", password = "pass1", authorities = {"Sales"})
    void testGetReport(String requestURL) throws Exception {
        mockMvc.perform(get(requestURL)).andExpect(status().isOk()).andDo(print());
    }
}