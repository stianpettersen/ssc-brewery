package guru.sfg.brewery.web.controllers.api;

import guru.sfg.brewery.web.controllers.BaseIT;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class BeerRestControllerIT extends BaseIT {

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
