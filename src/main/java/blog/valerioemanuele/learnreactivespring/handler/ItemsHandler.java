package blog.valerioemanuele.learnreactivespring.handler;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import blog.valerioemanuele.constants.ItemConstants;
import blog.valerioemanuele.learnreactivespring.document.Item;
import blog.valerioemanuele.learnreactivespring.document.ItemCapped;
import blog.valerioemanuele.learnreactivespring.repository.ItemCappedReactiveRepository;
import blog.valerioemanuele.learnreactivespring.repository.ItemReactiveRepository;
import reactor.core.publisher.Mono;

@Component
public class ItemsHandler {
    
    @Autowired
    private ItemReactiveRepository itemReactiveRepository;
    
    @Autowired
    private ItemCappedReactiveRepository itemCappedReactiveRepository;
    
    private static Mono<ServerResponse> notFound = ServerResponse.notFound().build();
    
    public Mono<ServerResponse> getAllItems(ServerRequest serverRequest){
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemReactiveRepository.findAll(), Item.class);
    }
    
    public Mono<ServerResponse> getOneItem(ServerRequest serverRequest){
        String id = serverRequest.pathVariable("id");
        Mono<Item> itemMono = itemReactiveRepository.findById(id);
        
        return itemMono.flatMap(item -> 
            ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                            .body(BodyInserters.fromValue(item)))
                            .switchIfEmpty(notFound);
                            
    }
    
    public Mono<ServerResponse> createItem(ServerRequest serverRequest){
        Mono<Item> itemMono = serverRequest.bodyToMono(Item.class);
        return itemMono.flatMap(item -> ServerResponse.created(URI.create(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1))
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .body(itemReactiveRepository.save(item), Item.class));
    }
    
    public Mono<ServerResponse> deleteItem(ServerRequest serverRequest){
        String id = serverRequest.pathVariable("id");
        Mono<Void> deletedItem = itemReactiveRepository.deleteById(id);
        
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(deletedItem, Void.class);
    }
    
    public Mono<ServerResponse> updateItem(ServerRequest serverRequest){
        String id = serverRequest.pathVariable("id");
        Mono<Item> updatedItem = serverRequest.bodyToMono(Item.class)
            .flatMap(item -> {
               return itemReactiveRepository.findById(id)
                   .flatMap(currentItem -> {
                       currentItem.setDescription(item.getDescription());
                       currentItem.setPrice(item.getPrice());
                       return itemReactiveRepository.save(currentItem);
                   });
            });
        
        
        
        return updatedItem.flatMap(item -> 
                    ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(item)))
                    .switchIfEmpty(notFound);
    }
    
    public Mono<ServerResponse> itemsEx(ServerRequest serverRequest){
        throw new RuntimeException("RuntimeException Occurred");
    }
    
    public Mono<ServerResponse> itemsStream(ServerRequest serverRequest){
        return ServerResponse.ok().contentType(MediaType.APPLICATION_STREAM_JSON)
                .body(itemCappedReactiveRepository.findItemsBy(), ItemCapped.class);
                
    }
    
}
