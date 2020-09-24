package guru.sfg.brewery.web.controllers.api;

import guru.sfg.brewery.web.controllers.BaseIT;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class BeerRestControllerIT extends BaseIT {

    String id = UUID.randomUUID().toString();

/*    @Test
    void deleteBeerBadCredentialsUrl() throws Exception {
        mockMvc.perform(
                delete("/api/v1/beer/" + id)
                        .param("apiKey", "spring")
                        .param("apiSecret", "wrongpwd"))
                .andExpect(status().isUnauthorized()
                );
    }

    @Test
    void deleteBeerUrl() throws Exception {
        mockMvc.perform(
                delete("/api/v1/beer/" + id)
                        .param("apiKey", "spring")
                        .param("apiSecret", "guru"))
                .andExpect(status().isOk());

    }

    @Test
    void deleteBeerBadCredentials() throws Exception {
        mockMvc.perform(
                delete("/api/v1/beer/" + id)
                        .header("Api-Key", "spring")
                        .header("Api-Secret", "wrongpwd"))
                .andExpect(status().isUnauthorized()
                );
    }

    @Test
    void deleteBeer() throws Exception {
        mockMvc.perform(
                delete("/api/v1/beer/" + id)
                        .header("Api-Key", "spring")
                        .header("Api-Secret", "guru"))
                .andExpect(status().isOk());

    }*/

    @Test
    void deleteBeerHttpBasicAdmin() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/" + id)
                .with(httpBasic("spring", "guru")))
                .andExpect(status().is2xxSuccessful());

    }

    @Test
    void deleteBeerHttpBasicUser() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/" + id)
                .with(httpBasic("user", "password")))
                .andExpect(status().isForbidden());

    }

    @Test
    void deleteBeerHttpBasicCustomer() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/" + id)
                .with(httpBasic("scott", "tiger")))
                .andExpect(status().isForbidden());

    }

    @Test
    void deleteBeerNoAuth() throws Exception {
        mockMvc.perform(
                delete("/api/v1/beer/" + id))
                .andExpect(status().isUnauthorized());

    }

    @Test
    void findBeers() throws Exception {
        mockMvc.perform(get("/api/v1/beer"))
                .andExpect(status().isOk());
    }

    @Test
    void findBeerById() throws Exception {
        UUID id = UUID.randomUUID();
        System.out.println(id.toString());
        mockMvc.perform(get("/api/v1/beer/" + id.toString()))
                .andExpect(status().isOk());
    }

    @Test
    void findBeerByUpc() throws Exception {
        String id = "0631234200036";
        mockMvc.perform(get("/api/v1/beerUpc/" + id))
                .andExpect(status().isOk());
    }
}
