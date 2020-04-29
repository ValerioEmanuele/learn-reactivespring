package blog.valerioemanuele.learnreactivespring.fluxandmonoplayground;

import java.util.List;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

class FluxAndMonoTestCase {

    List<String> names = List.of("Adam", "Anna", "Jack", "Jenny");
    
    @Test
    void filterTest() {
        Flux<String> namesFlux = Flux.fromIterable(names)
                .filter( s -> s.startsWith("A"))
                .log();
        
        StepVerifier.create(namesFlux.log())
        .expectNext("Adam", "Anna")
        .verifyComplete();
    }
    
    @Test
    void filterTestLength() {
        Flux<String> namesFlux = Flux.fromIterable(names)
                .filter( s -> s.length() > 4)
                .log();
        
        StepVerifier.create(namesFlux.log())
        .expectNext("Jenny")
        .verifyComplete();
    }

}
