package guru.springframework.brewery.web.controllers;

import guru.springframework.brewery.domain.Customer;
import guru.springframework.brewery.repositories.CustomerRepository;
import guru.springframework.brewery.web.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created By john on Sep, 2019
 **/

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BeerOrderControllerIT {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private CustomerRepository customerRepository;

    private Customer customer;

    @BeforeEach
    void setUp() {
         customer = customerRepository.findAll().stream().findAny().get();
        System.out.println(customer);
    }

    @Test
    void listOrders() {
        BeerOrderPagedList beerOrderPagedList =  testRestTemplate.getForObject("/api/v1/customers/{customerId}/orders", BeerOrderPagedList.class, customer.getId().toString());
        assertThat(beerOrderPagedList.getContent()).hasSize(1);
        assertThat(beerOrderPagedList.getContent().get(0)).isNotNull();
        assertThat(beerOrderPagedList.getContent().get(0).getCustomerRef()).isEqualTo("testOrder1");
        assertThat(beerOrderPagedList.getContent().get(0).getOrderStatus()).isEqualTo(OrderStatusEnum.NEW);
    }
}
