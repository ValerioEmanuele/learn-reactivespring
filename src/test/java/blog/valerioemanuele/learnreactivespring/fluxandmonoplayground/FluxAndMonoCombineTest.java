package blog.valerioemanuele.learnreactivespring.fluxandmonoplayground;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;
class FluxAndMonoCombineTest {

    @Test
    void combineUsingMerge() {
        Flux<String> flux1 = Flux.just("A", "B", "C");
        Flux<String> flux2 = Flux.just("D", "E", "F");
        
        Flux<String> mergedFlux = Flux.merge(flux1, flux2);
        
        StepVerifier.create(mergedFlux)
        .expectNextCount(6)
        .verifyComplete();
        
        
        StepVerifier.create(mergedFlux.log())
        .expectSubscription()
        .expectNext("A", "B", "C", "D", "E", "F")
        .verifyComplete();
    }
    
    

    @Test
    void combineUsingMerge_withDelay() {
        Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));
        
        Flux<String> mergedFlux = Flux.merge(flux1, flux2);
        
        StepVerifier.create(mergedFlux)
        .expectSubscription()
        .expectNextCount(6)
        .verifyComplete();
    }
    
    @Test
    void combineUsingConcat_withDelay() {
        VirtualTimeScheduler.getOrSet();
        
        Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));
        
        Flux<String> mergedFlux = Flux.concat(flux1, flux2);
        
        StepVerifier.withVirtualTime(() -> mergedFlux.log())
            .expectSubscription()
            .thenAwait(Duration.ofSeconds(6))
            .expectNext("A", "B", "C", "D", "E", "F")
            .verifyComplete();
        
      /*  StepVerifier.create(mergedFlux)
        .expectNextCount(6)
        .verifyComplete();
        
        
        StepVerifier.create(mergedFlux.log())
        .expectSubscription()
        .expectNext("A", "B", "C", "D", "E", "F")
        .verifyComplete();*/
    }
    
    @Test
    void combineUsingZip() {
        Flux<String> flux1 = Flux.just("A", "B", "C");
        Flux<String> flux2 = Flux.just("D", "E", "F");
        
        Flux<String> mergedFlux = Flux.zip(flux1, flux2, (t1,t2) -> {
            return t1.concat(t2);
        }); //A,D : B,E : C:F

        
        StepVerifier.create(mergedFlux.log())
        .expectSubscription()
        .expectNext("AD", "BE", "CF")
        .verifyComplete();
    }

}
