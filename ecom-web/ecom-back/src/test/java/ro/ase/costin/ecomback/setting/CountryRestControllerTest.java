package ro.ase.costin.ecomback.setting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ro.ase.costin.ecomback.setting.repository.CountryRepository;
import ro.ase.costin.ecomcommon.entity.Country;

@SpringBootTest
@AutoConfigureMockMvc
@Disabled("Integration tests")
class CountryRestControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    CountryRepository countryRepository;

    private static final String USERNAME = "costin@java.ro";

    private static final String PASSWORD = "something";

    private static final String ROLES = "ADMIN";

    @Test
    @Order(1)
    @WithMockUser(username = USERNAME, password = PASSWORD, roles = ROLES)
    void testListCountries() throws Exception {
        String url = "/countries/list";

        MvcResult result = mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        Country[] countries = objectMapper.readValue(jsonResponse, Country[].class);

        assertThat(countries).hasSizeGreaterThan(0);
    }

    @Test
    @WithMockUser(username = USERNAME, password = PASSWORD, roles = ROLES)
    @Order(2)
    @Disabled(value = "no need for creation of new entry in DB")
    void testCreateCountry() throws JsonProcessingException, Exception {
        String url = "/countries/save";
        String countryName = "Atlantis";
        String countryCode = "AT";
        Country country = new Country(countryName, countryCode);

        MvcResult result = mockMvc.perform(post(url).contentType("application/json")
                        .content(objectMapper.writeValueAsString(country))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        Integer countryId = Integer.parseInt(response);

        Optional<Country> countryOptional = countryRepository.findById(countryId);
        Country savedCountry = countryOptional.orElseGet(Country::new);
        assertThat(savedCountry.getName()).isEqualTo(countryName);
    }

    @Test
    @WithMockUser(username = USERNAME, password = PASSWORD, roles = ROLES)
    @Order(4)
    @Disabled(value = "no need for deleting entries from DB")
    void testDeleteCountry() throws Exception {
        Integer countryId = 9999;
        String url = "/countries/delete/" + countryId;
        mockMvc.perform(delete(url)).andExpect(status().isOk());

        Optional<Country> findById = countryRepository.findById(countryId);
        assertThat(findById).isNotPresent();
    }

    @Test
    @WithMockUser(username = USERNAME, password = PASSWORD, roles = ROLES)
    @Disabled(value = "no need for updating DB")
    @Order(3)
    void testUpdateCountry() throws JsonProcessingException, Exception {
        String url = "/countries/save";
        Integer countryId = 9999;
        String countryName = "Wakanda";
        String countryCode = "WK";
        Country country = new Country(countryId, countryName, countryCode);

        mockMvc.perform(post(url).contentType("application/json")
                        .content(objectMapper.writeValueAsString(country))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(countryId)));

        Optional<Country> findById = countryRepository.findById(countryId);
        Country savedCountry = findById.orElseGet(Country::new);
        assertThat(savedCountry.getName()).isEqualTo(countryName);
    }
}