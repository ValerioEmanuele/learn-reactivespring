package blog.valerioemanuele.learnreactivespring.fluxandmonoplayground;

import java.util.List;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class FluxAndMonoFactoryTest {
    
    List<String> names = List.of("Adam", "Anna", "Jack", "Jenny");
    
    @Test
    void fluxUsingIterable() {
        Flux<String> namesFlux = Flux.fromIterable(names);
        
        StepVerifier.create(namesFlux.log())
            .expectNext("Adam", "Anna", "Jack", "Jenny")
            .verifyComplete();
    }
    
    @Test
    void fluxUsingArray() {
        String[] names = new String[] {"Adam", "Anna", "Jack", "Jenny"};
        
        Flux<String> namesFlux = Flux.fromArray(names);
        
        StepVerifier.create(namesFlux)
            .expectNext("Adam", "Anna", "Jack", "Jenny")
            .verifyComplete();
    }
    
    
    @Test
    void fluxUsingStrem() {
        Flux<String> namesFlux = Flux.fromStream(names.stream());
        
        StepVerifier.create(namesFlux)
            .expectNext("Adam", "Anna", "Jack", "Jenny")
            .verifyComplete();
    }
    
    
    @Test
    void monoUsingJustOrEmpty() {
        Mono<String> mono = Mono.justOrEmpty(null);
        
        StepVerifier.create(mono.log())
                .verifyComplete();
        
    }
    
    @Test
    void monoUsingSupplier() {
        Supplier<String> stringSupplier = () -> "Adam";
        
        Mono<String> stringMono = Mono.fromSupplier(stringSupplier);
        
        StepVerifier.create(stringMono.log())
            .expectNext("Adam")
            .verifyComplete();
    }
    
    
    @Test
    void fluxUsingRange() {
        Flux<Integer> integerFlux = Flux.range(1, 5);
        
        StepVerifier.create(integerFlux.log())
            .expectNext(1,2,3,4,5)
            .verifyComplete();
        
    }

}
