package blog.valerioemanuele.learnreactivespring.controller.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import blog.valerioemanuele.constants.ItemConstants;
import blog.valerioemanuele.learnreactivespring.document.ItemCapped;
import blog.valerioemanuele.learnreactivespring.repository.ItemCappedReactiveRepository;
import reactor.core.publisher.Flux;

@RestController
public class ItemStreamController {
    @Autowired
    ItemCappedReactiveRepository itemCappedReactiveRepository;
    
    @GetMapping(value=ItemConstants.ITEM_STREAM_END_POINT_V1, produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<ItemCapped> getItemsStream() {
        return itemCappedReactiveRepository.findItemsBy();
    }
}
