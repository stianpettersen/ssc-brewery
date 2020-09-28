/*
 *  Copyright 2020 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package guru.sfg.brewery.bootstrap;

import guru.sfg.brewery.domain.*;
import guru.sfg.brewery.domain.security.Authority;
import guru.sfg.brewery.domain.security.Role;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.*;
import guru.sfg.brewery.repositories.security.AuthorityRepository;
import guru.sfg.brewery.repositories.security.RoleRepository;
import guru.sfg.brewery.repositories.security.UserRepository;
import guru.sfg.brewery.web.model.BeerStyleEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


/**
 * Created by jt on 2019-01-26.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class DefaultBreweryLoader implements CommandLineRunner {

    public static final String TASTING_ROOM = "Tasting Room";

    public static final String ST_PETE_DISTRIBUTING = "St Pete Distributing";
    public static final String DUNEDIN_DISTRIBUTING = "Dunedin Distributing";
    public static final String KEY_WEST_DISTRIBUTORS = "Key West Distributors";

    public static final String BEER_1_UPC = "0631234200036";
    public static final String BEER_2_UPC = "0631234300019";
    public static final String BEER_3_UPC = "0083783375213";

    private final BreweryRepository breweryRepo;
    private final BeerRepository beerRepo;
    private final BeerInventoryRepository beerInventoryRepo;
    private final BeerOrderRepository beerOrderRepo;
    private final CustomerRepository customerRepo;
    private final AuthorityRepository authorityRepo;
    private final RoleRepository roleRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        loadSecurityData();
        loadBreweryData();
        loadTastingRoomData();
        loadCustomerData();
    }

    private void loadCustomerData() {
        Role customerRole = roleRepo.findByName("CUSTOMER").orElseThrow();

        // Create customers
        Customer stPeteCustomer = customerRepo.save(Customer.builder()
                .customerName(ST_PETE_DISTRIBUTING)
                .apiKey(UUID.randomUUID())
                .build());

        Customer dunedinCustomer = customerRepo.save(Customer.builder()
                .customerName(DUNEDIN_DISTRIBUTING)
                .apiKey(UUID.randomUUID())
                .build());

        Customer keyWestCustomer = customerRepo.save(Customer.builder()
                .customerName(KEY_WEST_DISTRIBUTORS)
                .apiKey(UUID.randomUUID())
                .build());

        // Create users
        User stPeteUser = userRepo.save(User.builder()
                .username("stpete")
                .password(passwordEncoder.encode("password"))
                .customer(stPeteCustomer)
                .role(customerRole)
                .build());

        User dunedinUser = userRepo.save(User.builder()
                .username("dunedin")
                .password(passwordEncoder.encode("password"))
                .customer(dunedinCustomer)
                .role(customerRole)
                .build());

        User keyWestUser = userRepo.save(User.builder()
                .username("keywest")
                .password(passwordEncoder.encode("password"))
                .customer(keyWestCustomer)
                .role(customerRole)
                .build());

        createOrder(stPeteCustomer);
        createOrder(dunedinCustomer);
        createOrder(keyWestCustomer);

        log.debug("Orders Loaded {}", beerOrderRepo.count());
    }

    private BeerOrder createOrder(Customer customer) {
        return beerOrderRepo.save(BeerOrder.builder()
                .customer(customer)
                .orderStatus(OrderStatusEnum.NEW)
                .beerOrderLines(Set.of(BeerOrderLine.builder()
                        .beer(beerRepo.findByUpc(BEER_1_UPC))
                        .orderQuantity(2)
                        .build()))
                .build());
    }

    private void loadTastingRoomData() {
        Customer tastingRoom = Customer.builder()
                .customerName(TASTING_ROOM)
                .apiKey(UUID.randomUUID())
                .build();

        customerRepo.save(tastingRoom);

        beerRepo.findAll().forEach(beer -> {
            beerOrderRepo.save(BeerOrder.builder()
                    .customer(tastingRoom)
                    .orderStatus(OrderStatusEnum.NEW)
                    .beerOrderLines(Set.of(BeerOrderLine.builder()
                            .beer(beer)
                            .orderQuantity(2)
                            .build()))
                    .build());
        });
    }

    private void loadBreweryData() {
        if (breweryRepo.count() == 0) {
            breweryRepo.save(Brewery
                    .builder()
                    .breweryName("Cage Brewing")
                    .build());

            Beer mangoBobs = Beer.builder()
                    .beerName("Mango Bobs")
                    .beerStyle(BeerStyleEnum.IPA)
                    .minOnHand(12)
                    .quantityToBrew(200)
                    .upc(BEER_1_UPC)
                    .build();

            beerRepo.save(mangoBobs);
            beerInventoryRepo.save(BeerInventory.builder()
                    .beer(mangoBobs)
                    .quantityOnHand(500)
                    .build());

            Beer galaxyCat = Beer.builder()
                    .beerName("Galaxy Cat")
                    .beerStyle(BeerStyleEnum.PALE_ALE)
                    .minOnHand(12)
                    .quantityToBrew(200)
                    .upc(BEER_2_UPC)
                    .build();

            beerRepo.save(galaxyCat);
            beerInventoryRepo.save(BeerInventory.builder()
                    .beer(galaxyCat)
                    .quantityOnHand(500)
                    .build());

            Beer pinball = Beer.builder()
                    .beerName("Pinball Porter")
                    .beerStyle(BeerStyleEnum.PORTER)
                    .minOnHand(12)
                    .quantityToBrew(200)
                    .upc(BEER_3_UPC)
                    .build();

            beerRepo.save(pinball);
            beerInventoryRepo.save(BeerInventory.builder()
                    .beer(pinball)
                    .quantityOnHand(500)
                    .build());
        }
    }

    private void loadSecurityData() {
        // Beer auths
        Authority createBeer = authorityRepo.save(Authority.builder().permission("beer.create").build());
        Authority readBeer = authorityRepo.save(Authority.builder().permission("beer.read").build());
        Authority updateBeer = authorityRepo.save(Authority.builder().permission("beer.update").build());
        Authority deleteBeer = authorityRepo.save(Authority.builder().permission("beer.delete").build());

        // Customer auths
        Authority createCustomer = authorityRepo.save(Authority.builder().permission("customer.create").build());
        Authority readCustomer = authorityRepo.save(Authority.builder().permission("customer.read").build());
        Authority updateCustomer = authorityRepo.save(Authority.builder().permission("customer.update").build());
        Authority deleteCustomer = authorityRepo.save(Authority.builder().permission("customer.delete").build());

        // Brewery auths
        Authority createBrewery = authorityRepo.save(Authority.builder().permission("brewery.create").build());
        Authority readBrewery = authorityRepo.save(Authority.builder().permission("brewery.read").build());
        Authority updateBrewery = authorityRepo.save(Authority.builder().permission("brewery.update").build());
        Authority deleteBrewery = authorityRepo.save(Authority.builder().permission("brewery.delete").build());

        // Order auths
        Authority createOrder = authorityRepo.save(Authority.builder().permission("order.create").build());
        Authority readOrder = authorityRepo.save(Authority.builder().permission("order.read").build());
        Authority updateOrder = authorityRepo.save(Authority.builder().permission("order.update").build());
        Authority deleteOrder = authorityRepo.save(Authority.builder().permission("order.delete").build());
        Authority createOrderCustomer = authorityRepo.save(Authority.builder().permission("customer.order.create").build());
        Authority readOrderCustomer = authorityRepo.save(Authority.builder().permission("customer.order.read").build());
        Authority updateOrderCustomer = authorityRepo.save(Authority.builder().permission("customer.order.update").build());
        Authority deleteOrderCustomer = authorityRepo.save(Authority.builder().permission("customer.order.delete").build());

        Role adminRole = roleRepo.save(Role.builder().name("ADMIN").build());
        Role customerRole = roleRepo.save(Role.builder().name("CUSTOMER").build());
        Role userRole = roleRepo.save(Role.builder().name("USER").build());

        adminRole.setAuthorities(new HashSet<>(Set.of(createBeer, readBeer, updateBeer, deleteBeer,
                createCustomer, readCustomer, updateCustomer, deleteCustomer,
                createBrewery, readBrewery, updateBrewery, deleteBrewery,
                createOrder, readOrder, updateOrder, deleteOrder)));
        customerRole.setAuthorities(new HashSet<>(Set.of(readBeer, readCustomer, readBrewery,
                createOrderCustomer, readOrderCustomer, updateOrderCustomer, deleteOrderCustomer)));
        userRole.setAuthorities(new HashSet<>(Set.of(readBeer)));

        roleRepo.saveAll(Arrays.asList(adminRole, customerRole, userRole));

        userRepo.save(User.builder()
                .username("spring")
                .password(passwordEncoder.encode("guru"))
                .role(adminRole)
                .build());

        userRepo.save(User.builder()
                .username("user")
                .password(passwordEncoder.encode("password"))
                .role(userRole)
                .build());

        userRepo.save(User.builder()
                .username("scott")
                .password(passwordEncoder.encode("tiger"))
                .role(customerRole)
                .build());

        log.info("Users loaded: {}", userRepo.count());
    }
}
