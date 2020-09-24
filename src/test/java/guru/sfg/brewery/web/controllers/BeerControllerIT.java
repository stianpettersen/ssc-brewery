package guru.sfg.brewery.web.controllers;

import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.repositories.BeerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class BeerControllerIT extends BaseIT {

    @Autowired
    BeerRepository beerRepository;

    @DisplayName("Init New Form")
    @Nested
    class InitNewForm {
        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.BeerControllerIT#getStreamAllUsers")
        void initCreationFormAuth(String username, String password) throws Exception {
            mockMvc.perform(get("/beers/new").with(httpBasic(username, password)))
                    .andExpect(status().isOk())
                    .andExpect(view().name("beers/createBeer"))
                    .andExpect(model().attributeExists("beer"));
        }

        @Test
        void initCreationFormNoAuth() throws Exception {
            mockMvc.perform(get("/beers/new"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @DisplayName("Find beer form")
    @Nested
    class FindForm {

        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.BeerControllerIT#getStreamAllUsers")
        void findBeersForm(String username, String password) throws Exception {
            mockMvc.perform(get("/beers/find")
                    .with(httpBasic(username, password)))
                    .andExpect(status().isOk())
                    .andExpect(view().name("beers/findBeers"))
                    .andExpect(model().attributeExists("beer"));
        }

        @Test
        void findBeersFormAnonymous() throws Exception {
            mockMvc.perform(get("/beers/find").with(anonymous()))
                    .andExpect(status().isUnauthorized());
        }
    }
}
