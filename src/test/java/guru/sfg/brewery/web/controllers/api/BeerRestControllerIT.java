package guru.sfg.brewery.web.controllers.api;

import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.repositories.BeerOrderRepository;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.web.controllers.BaseIT;
import guru.sfg.brewery.web.model.BeerStyleEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class BeerRestControllerIT extends BaseIT {

    @Autowired
    BeerRepository beerRepository;
    @Autowired
    BeerOrderRepository beerOrderRepository;

    @DisplayName("Delete Tests")
    @Nested
    class DeleteTests {

        public Beer beerToDelete() {
            Random random = new Random();

            return beerRepository.saveAndFlush(Beer.builder()
                    .beerName("Delete Me Beer")
                    .beerStyle(BeerStyleEnum.IPA)
                    .minOnHand(12)
                    .quantityToBrew(200)
                    .upc(String.valueOf(random.nextInt(99999999)))
                    .build());
        }

        @Test
        void deleteBeerHttpBasicAdmin() throws Exception {
            mockMvc.perform(delete("/api/v1/beer/" + beerToDelete().getId())
                    .with(httpBasic("spring", "guru")))
                    .andExpect(status().is2xxSuccessful());
        }

        @Test
        void deleteBeerHttpBasicUser() throws Exception {
            mockMvc.perform(delete("/api/v1/beer/" + beerToDelete().getId())
                    .with(httpBasic("user", "password")))
                    .andExpect(status().isForbidden());
        }

        @Test
        void deleteBeerHttpBasicCustomer() throws Exception {
            mockMvc.perform(delete("/api/v1/beer/" + beerToDelete().getId())
                    .with(httpBasic("scott", "tiger")))
                    .andExpect(status().isForbidden());
        }

        @Test
        void deleteBeerNoAuth() throws Exception {
            mockMvc.perform(delete("/api/v1/beer/" + beerToDelete().getId()))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Test
    void findBeers() throws Exception {
        mockMvc.perform(get("/api/v1/beer"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void findBeerByUpcAsAdmin() throws Exception {
        String id = "0631234200036";
        mockMvc.perform(get("/api/v1/beerUpc/" + id)
        .with(httpBasic("spring", "guru")))
                .andExpect(status().isOk());
    }

    @Test
    void findBeerByUpcAsCustomer() throws Exception {
        String id = "0631234200036";
        mockMvc.perform(get("/api/v1/beerUpc/" + id)
        .with(httpBasic("scott", "tiger")))
                .andExpect(status().isOk());
    }

    @Test
    void findBeerByUpcAsUser() throws Exception {
        String id = "0631234200036";
        mockMvc.perform(get("/api/v1/beerUpc/" + id)
        .with(httpBasic("user", "password")))
                .andExpect(status().isOk());
    }

    @Test
    void findBeerByUpcNoAuth() throws Exception {
        String id = "0631234200036";
        mockMvc.perform(get("/api/v1/beerUpc/" + id))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void findBeerByIdAsAdmin() throws Exception {
        Beer beer = beerRepository.findAll().get(0);

        mockMvc.perform(get("/api/v1/beer/" + beer.getId())
                .with(httpBasic("spring", "guru")))
                .andExpect(status().isOk());
    }

    @Test
    void findBeerByIdAsCustomer() throws Exception {
        Beer beer = beerRepository.findAll().get(0);

        mockMvc.perform(get("/api/v1/beer/" + beer.getId())
                .with(httpBasic("scott", "tiger")))
                .andExpect(status().isOk());
    }

    @Test
    void findBeerByIdAsUser() throws Exception {
        Beer beer = beerRepository.findAll().get(0);

        mockMvc.perform(get("/api/v1/beer/" + beer.getId())
                .with(httpBasic("user", "password")))
                .andExpect(status().isOk());
    }

    @Test
    void findBeerByIdNoAuth() throws Exception {
        Beer beer = beerRepository.findAll().get(0);

        mockMvc.perform(get("/api/v1/beer/" + beer.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void findBeerAsAdmin() throws Exception {
        mockMvc.perform(get("/beers").param("beerName", "")
                .with(httpBasic("spring", "guru")))
                .andExpect(status().isOk());
    }

    @Test
    void findBeerAsCustomer() throws Exception {
        mockMvc.perform(get("/beers").param("beerName", "")
                .with(httpBasic("scott", "tiger")))
                .andExpect(status().isOk());
    }

    @Test
    void findBeerAsUser() throws Exception {
        mockMvc.perform(get("/beers").param("beerName", "")
                .with(httpBasic("user", "password")))
                .andExpect(status().isOk());
    }

    @Test
    void findBeerNoAuth() throws Exception {
        mockMvc.perform(get("/beers").param("beerName", ""))
                .andExpect(status().isUnauthorized());
    }
}
