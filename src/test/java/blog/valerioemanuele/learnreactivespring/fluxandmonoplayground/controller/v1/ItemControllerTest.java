package blog.valerioemanuele.learnreactivespring.fluxandmonoplayground.controller.v1;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import blog.valerioemanuele.constants.ItemConstants;
import blog.valerioemanuele.learnreactivespring.document.Item;
import blog.valerioemanuele.learnreactivespring.repository.ItemReactiveRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
@DirtiesContext
@AutoConfigureWebTestClient
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class ItemControllerTest {

    @Autowired
    WebTestClient webTestClient;
    
    @Autowired
    ItemReactiveRepository itemReactiveRepository;
    
    public List<Item> data(){
        return List.of(Item.builder().id("ABC").description("Samsung TV").price(399.99).build(), 
            Item.builder().description("LG TV").price(329.99).build(),
            Item.builder().description("Apple Watch").price(349.99).build(),
            Item.builder().description("Beats HeadPhones").price(19.99).build()
            );
    }
    
    @BeforeEach
    public void setUp() {
        itemReactiveRepository.deleteAll()
            .thenMany(Flux.fromIterable(data()))
            .flatMap(itemReactiveRepository::save)
            .doOnNext(item -> System.out.println("Inserted item is: " + item))
            .blockLast();
    }
    
    @Test
    void getAllItems() {
        webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBodyList(Item.class)
            .hasSize(4);
    }
    
    @Test
    void getAllItems_approach2() {
        webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBodyList(Item.class)
            .hasSize(4)
            .consumeWith(response -> {
                List<Item> items = response.getResponseBody();
                items.forEach(item -> assertTrue(item.getId() != null));
            });
    }
    
    @Test
    void getAllItems_approach3() {
        Flux<Item> itemsFlux = webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .returnResult(Item.class)
            .getResponseBody();
        
        StepVerifier.create(itemsFlux.log("value from network:"))
            .expectNextCount(4)
            .verifyComplete();
    }
    
    @Test
    void getItemById() {
        webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1.concat("/{id}"), "ABC")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.price", 149.99);
    }

    @Test
    void getItemById_notFound() {
        webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1.concat("/{id}"), "XYZ")
            .exchange()
            .expectStatus().isNotFound();
    }
    
    @Test
    void createItem() {
        Item item = Item.builder().description("Iphone X").price(999.99).build();
        webTestClient.put().uri(ItemConstants.ITEM_END_POINT_V1)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(item), Item.class)
            .exchange()
            .expectStatus().isCreated()
            .expectBody()
            .jsonPath("$.id").isNotEmpty()
            .jsonPath("$.description").isEqualTo("Iphone X")
            .jsonPath("$.price").isEqualTo(999.99);
    }
    
    @Test
    void deleteItem() {
        webTestClient.delete().uri(ItemConstants.ITEM_END_POINT_V1.concat("/{id}"), "ABC")
            .exchange()
            .expectStatus().isOk()
            .expectBody(Void.class)
            ;
    }
    
    @Test
    void updateItem() {
        double newPrice = 129.99;
        Item item = Item.builder().description("Beats HeadPhones").price(newPrice).build();
        
        webTestClient.post().uri(ItemConstants.ITEM_END_POINT_V1.concat("/{id}"), "ABC")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(item), Item.class)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.description").isEqualTo("Beats HeadPhones")
            .jsonPath("$.price").isEqualTo(newPrice);
    }
    
    @Test
    void updateItem_notFound() {
        double newPrice = 129.99;
        Item item = Item.builder().description("Beats HeadPhones").price(newPrice).build();
        
        webTestClient.post().uri(ItemConstants.ITEM_END_POINT_V1.concat("/{id}"), "ZAY")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(item), Item.class)
            .exchange()
            .expectStatus().isNotFound();
    }
    
    @Test
    public void runtimeException() {
        webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1+"/runtimeException")
            .exchange()
            .expectStatus().is5xxServerError()
            .expectBody(String.class)
            .isEqualTo("RuntimeException Occurred");
    }
}
