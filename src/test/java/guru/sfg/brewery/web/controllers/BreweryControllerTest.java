package guru.sfg.brewery.web.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class BreweryControllerTest extends BaseIT {

    @Test
    void listBreweriesAsCustomer() throws Exception {
        mockMvc.perform(get("/brewery/breweries/")
                .with(httpBasic("scott", "tiger")))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void listBreweriesAsAdmin() throws Exception {
        mockMvc.perform(get("/brewery/breweries/")
                .with(httpBasic("spring", "guru")))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void listBreweriesAsUser() throws Exception {
        mockMvc.perform(get("/brewery/breweries/")
                .with(httpBasic("user", "password")))
                .andExpect(status().isForbidden());
    }

    @Test
    void listBreweriesNoAuth() throws Exception {
        mockMvc.perform(get("/brewery/breweries/"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listBreweriesJsonAsCustomer() throws Exception {
        mockMvc.perform(get("/brewery/api/v1/breweries/")
                .with(httpBasic("scott", "tiger")))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void listBreweriesJsonAsAdmin() throws Exception {
        mockMvc.perform(get("/brewery/api/v1/breweries/")
                .with(httpBasic("spring", "guru")))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void listBreweriesJsonAsUser() throws Exception {
        mockMvc.perform(get("/brewery/api/v1/breweries/")
                .with(httpBasic("user", "password")))
                .andExpect(status().isForbidden());
    }

    @Test
    void listBreweriesJsonNoAuth() throws Exception {
        mockMvc.perform(get("/brewery/api/v1/breweries/"))
                .andExpect(status().isUnauthorized());
    }
}
