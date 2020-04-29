package blog.valerioemanuele.learnreactivespring.fluxandmonoplayground;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.util.retry.Retry;

class FluxAndMonoErrorTest {

    @Test
    void fluxErrorHandling() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("D"))
                .onErrorResume( e -> {
                    System.out.println("Exception is " + e);
                    return Flux.just("default", "default 1");
                });
        
        
        StepVerifier.create(stringFlux.log())
            .expectSubscription()
            .expectNext("A", "B", "C")
            //.expectError(RuntimeException.class)
            .expectNext("default", "default 1")
            .verifyComplete();  
    }

    
    @Test
    void fluxErrorHandling_OnErrorReturn() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("D"))
                .onErrorReturn("default");
        
        
        StepVerifier.create(stringFlux.log())
            .expectSubscription()
            .expectNext("A", "B", "C")
            .expectNext("default")
            .verifyComplete();
    }
    
    @Test
    void fluxErrorHandling_OnErrorMap() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("D"))
                .onErrorMap( e -> new CustomException(e));
        
        
        StepVerifier.create(stringFlux.log())
            .expectSubscription()
            .expectNext("A", "B", "C")
            .expectError(CustomException.class)
            .verify();
    }
    
    @Test
    void fluxErrorHandling_OnErrorMap_withRetry() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("D"))
                .onErrorMap( e -> new CustomException(e))
                .retry(2);
        
        
        StepVerifier.create(stringFlux.log())
            .expectSubscription()
            .expectNext("A", "B", "C")
            .expectNext("A", "B", "C")
            .expectNext("A", "B", "C")
            .expectError(CustomException.class)
            .verify();
    }
    
    @Test
    void fluxErrorHandling_OnErrorMap_withRetryBackoff() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("D"))
                .onErrorMap( e -> new CustomException(e))
                .retryWhen(Retry.backoff(2, Duration.ofSeconds(2)));
        
        
        StepVerifier.create(stringFlux.log())
            .expectSubscription()
            .expectNext("A", "B", "C")
            .expectNext("A", "B", "C")
            .expectNext("A", "B", "C")
            .expectError(RuntimeException.class)
            .verify();
    }
}
