package blog.valerioemanuele.learnreactivespring.fluxandmonoplayground;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

class ColdAndHotPublisherTest {

    @Test
    void coldPublisherTest() { //emits the values from the beginning
        Flux<String> stringFlux = Flux.just("A", "B", "C", "D", "E", "F").delayElements(Duration.ofSeconds(1));
        
        stringFlux.subscribe(s-> System.out.println("Subscriber 1: " + s));
        
        sleep(2000);
        
        stringFlux.subscribe(s-> System.out.println("Subscriber 2: " + s));
        
        sleep(4000);
    }

    private void sleep(int delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Test
    void hotPublisherTest() {
        Flux<String> stringFlux = Flux.just("A", "B", "C", "D", "E", "F").delayElements(Duration.ofSeconds(1));
        
        ConnectableFlux<String> connectableFlux = stringFlux.publish();
        connectableFlux.connect();
        
        connectableFlux.subscribe(s-> System.out.println("Subscriber 1: " + s));
        sleep(3000);
        
        connectableFlux.subscribe(s-> System.out.println("Subscriber 2: " + s));
        sleep(4000);
        
    }

}
