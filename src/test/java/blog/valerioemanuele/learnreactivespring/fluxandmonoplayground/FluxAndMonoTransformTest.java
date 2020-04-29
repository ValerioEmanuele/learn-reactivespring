package blog.valerioemanuele.learnreactivespring.fluxandmonoplayground;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

class FluxAndMonoTransformTest {
    
    List<String> names = List.of("Adam", "Anna", "Jack", "Jenny");
    
    @Test
    void transformUsingMap() {
        Flux<String> namesFlux = Flux.fromIterable(names)
                .map(s -> s.toUpperCase())
                .log();
        
        StepVerifier.create(namesFlux)
            .expectNext("ADAM", "ANNA", "JACK", "JENNY")
            .verifyComplete();
    }
    
    @Test
    void transformUsingMap_Length() {
        Flux<Integer> namesFlux = Flux.fromIterable(names)
                .map(s -> s.length())
                .log();
        
        StepVerifier.create(namesFlux)
            .expectNext(4, 4, 4, 5)
            .verifyComplete();
    }
    
    @Test
    void transformUsingMap_Length_repeat() {
        Flux<Integer> namesFlux = Flux.fromIterable(names)
                .map(s -> s.length())
                .repeat(1)
                .log();
        
        StepVerifier.create(namesFlux)
            .expectNext(4, 4, 4, 5, 4, 4, 4, 5)
            .verifyComplete();
    }
    
    @Test
    void transformUsingMap_Filter() {
        Flux<String> namesFlux = Flux.fromIterable(names)
                .filter(s -> s.length() > 4)
                .map(s -> s.toUpperCase())
                .log();
        
        StepVerifier.create(namesFlux)
            .expectNext("JENNY")
            .verifyComplete();
    }
    
    @Test
    void transformUsingFlatMap() {
        Flux<String> stringFlux = Flux.fromIterable(List.of("A","B","C","D","E","F"))
                .flatMap( s -> {
                    return Flux.fromIterable(convertToList(s));
                }).log();
        
        
        StepVerifier.create(stringFlux)
            .expectNextCount(12)
            .verifyComplete();
        
        
    }

    private List<String> convertToList(String s) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            
            e.printStackTrace();
        }
        return List.of(s, "newValue");
    }
    
    
    @Test
    void transformUsingFlatMap_usingParallel() {
        Flux<String> stringFlux = Flux.fromIterable(List.of("A","B","C","D","E","F"))
                .window(2) //Flux<Flux<String>> -> (A,B), (C,D), (E,F)
                .flatMap( (s) -> 
                    s.map(this::convertToList).subscribeOn(Schedulers.parallel()))
                    .flatMap(s -> Flux.fromIterable(s))
                    
                .log();
        
        
        StepVerifier.create(stringFlux)
            .expectNextCount(12)
            .verifyComplete();
        
        
    }
    
    @Test
    void transformUsingFlatMap_usingParallel_maintainOrder() {
        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A","B","C","D","E","F"))
                .window(2) //Flux<Flux<String>> -> (A,B), (C,D), (E,F)
                .flatMapSequential( (s) -> 
                    s.map(this::convertToList).subscribeOn(Schedulers.parallel()))
                    .flatMap(s -> Flux.fromIterable(s))
                .log();
        
        
        StepVerifier.create(stringFlux)
            .expectNextCount(12)
            .verifyComplete();
        
        
    }

}
