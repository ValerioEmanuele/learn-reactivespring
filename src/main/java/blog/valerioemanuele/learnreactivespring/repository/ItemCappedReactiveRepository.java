package blog.valerioemanuele.learnreactivespring.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;

import blog.valerioemanuele.learnreactivespring.document.ItemCapped;
import reactor.core.publisher.Flux;

public interface ItemCappedReactiveRepository extends ReactiveMongoRepository<ItemCapped, String>{
    @Tailable
    Flux<ItemCapped> findItemsBy(); 
}
