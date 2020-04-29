package blog.valerioemanuele.learnreactivespring.repository;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import blog.valerioemanuele.learnreactivespring.document.Item;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataMongoTest
@DirtiesContext
@ExtendWith(SpringExtension.class)
public class ItemReactiveRepositoryTest {
    @Autowired
    ItemReactiveRepository itemReactiveRepository; 
    
    List<Item> itemList = List.of(Item.builder().description("Samsung TV").price(400d).build(),
        Item.builder().description("LG TV").price(420d).build(),
        Item.builder().description("Apple Watch").price(299.9).build(),
        Item.builder().description("Beats Headphones").price(149.99).build(),
        Item.builder().id("ABC").description("Bose Headphones").price(149.99).build()
        );
        
    
    @BeforeEach
    public void setUp() {
        itemReactiveRepository.deleteAll()
            .thenMany(Flux.fromIterable(itemList))
            .flatMap(itemReactiveRepository::save)
            .doOnNext(item -> System.out.println("Inserted item is: "+ item))
            .blockLast();
    }
    
    @Test
    public void getAllItems() {
        StepVerifier.create(itemReactiveRepository.findAll())
            .expectSubscription()
            .expectNextCount(5)
            .verifyComplete();
    }
    
    @Test
    public void getItemById() {
        StepVerifier.create(itemReactiveRepository.findById("ABC"))
            .expectSubscription()
            .expectNextMatches(item -> item.getDescription().equals("Bose Headphones"))
            .verifyComplete();
    }
    
    @Test
    public void findItemByDescritption() {
        StepVerifier.create(itemReactiveRepository.findByDescription("Beats Headphones"))
            .expectSubscription()
            .expectNextCount(1)
            .verifyComplete();
    }
    
    @Test
    public void saveItem() {
        Item newItem = Item.builder().id("EDF").description("Google Home Mini").price(30d).build();
        Mono<Item> savedItem = itemReactiveRepository.save(newItem);
        
        StepVerifier.create(savedItem.log("Saved item: "))
            .expectSubscription()
            .expectNextMatches(item -> item.getId() != null && item.getDescription().equals("Google Home Mini"))
            .verifyComplete();
            
    }
    
    @Test
    public void updateItem() {
        double newPrice = 520d;
        Mono<Item> updatedItem = itemReactiveRepository.findByDescription("LG TV")
            .flatMap(item -> {
                item.setPrice(newPrice);
                return itemReactiveRepository.save(item);
            });
        
        StepVerifier.create(updatedItem.log("Updated item: "))
            .expectSubscription()
            .expectNextMatches(item -> item.getPrice().equals(520d))
            .verifyComplete();
    }
    
    @Test
    public void deleteItemById() {
        Mono<Void> deletedItem = itemReactiveRepository.findById("ABC")
            .map(Item::getId)
            .flatMap(id -> {
                return itemReactiveRepository.deleteById(id);
            });
        
        StepVerifier.create(deletedItem.log("Deleted item: "))
            .expectSubscription()
            .verifyComplete();
        
        StepVerifier.create(itemReactiveRepository.findAll())
            .expectSubscription()
            .expectNextCount(4)
            .verifyComplete();
    }
    
    
    @Test
    public void deleteItem() {
        Mono<Void> deletedItem = itemReactiveRepository.findByDescription("LG TV")
            .map(Item::getId)
            .flatMap(id -> {
                return itemReactiveRepository.deleteById(id);
            });
        
        StepVerifier.create(deletedItem.log("Deleted item: "))
            .expectSubscription()
            .verifyComplete();
        
        StepVerifier.create(itemReactiveRepository.findAll())
            .expectSubscription()
            .expectNextCount(4)
            .verifyComplete();
    }
}
