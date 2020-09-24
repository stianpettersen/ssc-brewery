package guru.sfg.brewery.web.controllers.api;

import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.repositories.BeerOrderRepository;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.web.controllers.BaseIT;
import guru.sfg.brewery.web.model.BeerStyleEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
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

        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.BeerControllerIT#getStreamNotAdmin")
        void deleteBeerAsAuthenticatedButNotAdmin(String username, String password) throws Exception {
            mockMvc.perform(delete("/api/v1/beer/" + beerToDelete().getId())
                    .with(httpBasic(username, password)))
                    .andExpect(status().isForbidden());
        }

        @Test
        void deleteBeerAsAdmin() throws Exception {
            mockMvc.perform(delete("/api/v1/beer/" + beerToDelete().getId())
                    .with(httpBasic("spring", "guru")))
                    .andExpect(status().is2xxSuccessful());
        }

        @Test
        void deleteBeerNoAuth() throws Exception {
            mockMvc.perform(delete("/api/v1/beer/" + beerToDelete().getId()))
                    .andExpect(status().isUnauthorized());
        }
    }

    @DisplayName("List Beers")
    @Nested
    class ListBeers {
        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.BeerControllerIT#getStreamAllUsers")
        void listBeersAuthenticated(String username, String password) throws Exception {
            mockMvc.perform(get("/api/v1/beer").with(httpBasic(username, password)))
                    .andExpect(status().isOk());
        }

        @Test
        void listBeersNotAuthenticated() throws Exception {
            mockMvc.perform(get("/api/v1/beer"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @DisplayName("Find Beer By Id")
    @Nested
    class FindBeerById {
        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.BeerControllerIT#getStreamAllUsers")
        void findBeerByIdAuthenticated(String username, String password) throws Exception {
            Beer beer = beerRepository.findAll().get(0);
            mockMvc.perform(get("/api/v1/beer/" + beer.getId())
                    .with(httpBasic(username, password)))
                    .andExpect(status().isOk());
        }

        @Test
        void findBeerByIdNotAuthenticated() throws Exception {

            Beer beer = beerRepository.findAll().get(0);
            mockMvc.perform(get("/api/v1/beer/" + beer.getId()))
                    .andExpect(status().isUnauthorized());
        }
    }

    @DisplayName("Find beer by UPC")
    @Nested
    class FindBeerByUpc {
        String id = "0631234200036";

        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.BeerControllerIT#getStreamAllUsers")
        void findBeerByUpcAuthenticated(String username, String password) throws Exception {
            mockMvc.perform(get("/api/v1/beerUpc/" + id)
                    .with(httpBasic(username, password)))
                    .andExpect(status().isOk());
        }

        @Test
        void findBeerByUpcNoAuth() throws Exception {
            mockMvc.perform(get("/api/v1/beerUpc/" + id))
                    .andExpect(status().isUnauthorized());
        }
    }
}
