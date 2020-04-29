package blog.valerioemanuele.learnreactivespring.handler;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import blog.valerioemanuele.constants.ItemConstants;
import blog.valerioemanuele.learnreactivespring.document.ItemCapped;
import blog.valerioemanuele.learnreactivespring.repository.ItemCappedReactiveRepository;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@SpringBootTest
@DirtiesContext
@AutoConfigureWebTestClient
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class ItemStreamsHandlerTest {

    @Autowired
    ItemCappedReactiveRepository itemCappedReactiveRepository;
    
    @Autowired
    MongoOperations mongoOperations;
    
    @Autowired
    WebTestClient webTestClient;
    
    @BeforeEach
    public void setUp() {
        mongoOperations.dropCollection(ItemCapped.class);
        mongoOperations.createCollection(ItemCapped.class,
               CollectionOptions.empty().maxDocuments(20).size(50000).capped());
        
        Flux<ItemCapped> itemCappedFlux = Flux.interval(Duration.ofMillis(100))
            .map(i -> ItemCapped.builder().description("Random Item" + i).price(100d+i).build())
            .take(5);
        
        itemCappedReactiveRepository
            .insert(itemCappedFlux)
            .doOnNext(item -> System.out.println("Inserted Item is " + item))
            .blockLast();
    }
    
    @Test
    void testStreamAllItems() {
        Flux<ItemCapped> itemCappedFlux = webTestClient.get().uri(ItemConstants.ITEM_STREAM_FUNCTIONAL_END_POINT_V1)
            .exchange()
            .expectStatus().isOk()
            .returnResult(ItemCapped.class)
            .getResponseBody()
            .take(5);
        
        StepVerifier.create(itemCappedFlux)
            .expectNextCount(5)
            .thenCancel()
            .verify();
    }

}
