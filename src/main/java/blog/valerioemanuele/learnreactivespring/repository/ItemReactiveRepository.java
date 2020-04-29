package blog.valerioemanuele.learnreactivespring.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import blog.valerioemanuele.learnreactivespring.document.Item;
import reactor.core.publisher.Mono;

public interface ItemReactiveRepository extends ReactiveMongoRepository<Item, String>{
    Mono<Item> findByDescription(String description);
}
