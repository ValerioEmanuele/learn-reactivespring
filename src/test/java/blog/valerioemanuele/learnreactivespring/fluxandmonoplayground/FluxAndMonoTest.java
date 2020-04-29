package blog.valerioemanuele.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class FluxAndMonoTest {

    @Test
    void fluxTest() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                //.concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("After the error"))
                .log();
        
        stringFlux.subscribe(System.out::println,
                (e) -> System.err.println("Exception is " + e),
                () -> System.out.println("COMPLETED!"));
    }
    
    @Test
    public void fluxTestElements_WithoutError() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
            .log();
        
        StepVerifier.create(stringFlux)
            .expectNext("Spring")
            .expectNext("Spring Boot")
            .expectNext("Reactive Spring")
            .verifyComplete();
    }
    
    @Test
    public void fluxTestElements_WithError() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
            .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
            .log();
        
        StepVerifier.create(stringFlux)
            .expectNext("Spring", "Spring Boot", "Reactive Spring")
            .expectErrorMessage("Exception Occurred")
            //.expectError(RuntimeException.class)
            .verify(); 
    }
    
    @Test
    public void fluxTestElementsCount_WithError() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
            .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
            .log();
        
        StepVerifier.create(stringFlux)
            .expectNextCount(3)
            .expectError(RuntimeException.class)
            .verify(); 
    }
    
    @Test
    public void monoTest() {
        Mono<String> stringMono = Mono.just("Spring");
        
        StepVerifier.create(stringMono.log())
                .expectNext("Spring")
                .verifyComplete();
    }
    
    
    @Test
    public void monoTest_withError() {
        StepVerifier.create(Mono.error(new RuntimeException("Exception Occurred")).log())
                .expectError(RuntimeException.class)
                .verify();
    }

}
