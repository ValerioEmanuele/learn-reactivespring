package blog.valerioemanuele.learnreactivespring.initialize;

import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

import blog.valerioemanuele.learnreactivespring.document.Item;
import blog.valerioemanuele.learnreactivespring.document.ItemCapped;
import blog.valerioemanuele.learnreactivespring.repository.ItemCappedReactiveRepository;
import blog.valerioemanuele.learnreactivespring.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Component
@Profile("!test")
@Slf4j
public class ItemDataInitializer implements CommandLineRunner{

    @Autowired
    ItemReactiveRepository itemReactiveRepository;
    
    @Autowired
    ItemCappedReactiveRepository itemCappedReactiveRepository;
    
    @Autowired
    MongoOperations mongoOperations;
    
    @Override
    public void run(String... args) throws Exception {
        initialDataSetup();
        createCappedCollection();
        dataSetUpForCappedCollection();
    }
    
    private void createCappedCollection() {
        mongoOperations.dropCollection(ItemCapped.class);
        mongoOperations.createCollection(ItemCapped.class, CollectionOptions.empty().maxDocuments(20).size(50000).capped());
    }

    public List<Item> data(){
        return List.of(Item.builder().id("ABC").description("Samsung TV").price(399.99).build(), 
            Item.builder().description("LG TV").price(329.99).build(),
            Item.builder().description("Apple Watch").price(349.99).build(),
            Item.builder().description("Beats HeadPhones").price(19.99).build()
            );
    }

    private void dataSetUpForCappedCollection() {
        Flux<ItemCapped> itemCappedFlux = Flux.interval(Duration.ofSeconds(1))
            .map(i -> ItemCapped.builder().description("Random Item" + i).price(100d+i).build());
        
        itemCappedReactiveRepository
            .insert(itemCappedFlux)
            .subscribe(item -> log.info("Inserted Item is " + item));
    }
    
    private void initialDataSetup() {
        itemReactiveRepository
            .deleteAll()
            .thenMany(Flux.fromIterable(data()))
            .flatMap(itemReactiveRepository::save)
            .thenMany(itemReactiveRepository.findAll())
            .subscribe(item -> System.out.println("Item inserted from CommandLineRunner: "  + item));
    }

}
