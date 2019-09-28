package guru.springframework.brewery.web.controllers;

import guru.springframework.brewery.domain.BeerOrder;
import guru.springframework.brewery.services.BeerOrderService;
import guru.springframework.brewery.web.model.BeerOrderDto;
import guru.springframework.brewery.web.model.BeerOrderLineDto;
import guru.springframework.brewery.web.model.BeerOrderPagedList;
import guru.springframework.brewery.web.model.OrderStatusEnum;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created By john on Sep, 2019
 **/

@WebMvcTest(BeerOrderController.class)
class BeerOrderControllerTest {

    @MockBean
    private BeerOrderService beerOrderService;

    private BeerOrderDto validBeerOrder;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {


        validBeerOrder = BeerOrderDto.builder().id(UUID.randomUUID())
                .version(1)
                .orderStatus(OrderStatusEnum.NEW)
                .beerOrderLines(List.of(BeerOrderLineDto.builder()
                        .id(UUID.randomUUID())
                        .createdDate(OffsetDateTime.now())
                        .lastModifiedDate(OffsetDateTime.now())
                        .version(1)
                        .orderQuantity(4)
                        .beerId(UUID.randomUUID())
                        .build()))
                .createdDate(OffsetDateTime.now())
                .lastModifiedDate(OffsetDateTime.now())
                .customerRef("xxx001")
                .customerId(UUID.randomUUID()).build();
    }

    @AfterEach
    void tearDown() {
        reset(beerOrderService);
    }


    @Test
    void placeOrder() throws Exception {
        given(beerOrderService.placeOrder(any(UUID.class), any(BeerOrderDto.class))).willReturn(validBeerOrder);

        /*MvcResult mvcResult = mockMvc.perform(post("/api/v1/customers/{customerId}/orders", UUID.randomUUID()))
                .andExpect(status().isCreated()).andReturn();
        System.out.println(mvcResult);*/
    }

    @Test
    void getOrder() throws Exception {
        given(beerOrderService.getOrderById(any(UUID.class), any(UUID.class))).willReturn(validBeerOrder);

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/customers/{customerId}/orders/{orderId}", UUID.randomUUID(), UUID.randomUUID()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(validBeerOrder.getId().toString())))
                .andExpect(jsonPath("$.customerId", is(validBeerOrder.getCustomerId().toString())))
                .andExpect(jsonPath("$.customerRef", is(validBeerOrder.getCustomerRef())))
                .andExpect(jsonPath("$.orderStatus", is("NEW")))
                .andExpect(jsonPath("$.beerOrderLines[0].beerId", is(validBeerOrder.getBeerOrderLines().get(0).getBeerId().toString())))
                .andExpect(jsonPath("$.beerOrderLines[0].orderQuantity", is(4))).andReturn();
        System.out.println(mvcResult);
    }

    @Test
    void pickupOrder() {

    }

    @DisplayName("List Ops - ")
    @Nested
    class ListOperations {


        private BeerOrderPagedList beerOrderPagedList;


        @BeforeEach
        void setUp() {
            beerOrderPagedList = new BeerOrderPagedList(List.of(validBeerOrder,
                    BeerOrderDto.builder()
                            .id(UUID.randomUUID())
                            .version(1)
                            .customerId(UUID.randomUUID())
                            .customerRef("abc1")
                            .createdDate(OffsetDateTime.now().minusDays(2))
                            .lastModifiedDate(OffsetDateTime.now())
                            .orderStatus(OrderStatusEnum.READY)
                            .beerOrderLines(List.of(BeerOrderLineDto.builder()
                                    .id(UUID.randomUUID())
                                    .beerId(UUID.randomUUID())
                                    .orderQuantity(5)
                                    .version(2).build()))
                            .build()), PageRequest.of(1, 2), 2);
        }

        @DisplayName("List Orders")
        @Test
        void listOrders() throws Exception {
            given(beerOrderService.listOrders(any(UUID.class), any(PageRequest.class))).willReturn(beerOrderPagedList);

            mockMvc.perform(get("/api/v1/customers/{customerId}/orders", UUID.randomUUID())).
                    andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                    .andExpect(jsonPath("$.content", hasSize(2)))
                    .andExpect(jsonPath("$.content[0].id", is(validBeerOrder.getId().toString())))
                    .andExpect(jsonPath("$.content[1].id", is(beerOrderPagedList.getContent().get(1).getId().toString())));
        }

    }

}