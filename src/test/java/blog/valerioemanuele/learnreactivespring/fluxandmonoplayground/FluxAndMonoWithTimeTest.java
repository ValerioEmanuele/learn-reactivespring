package blog.valerioemanuele.learnreactivespring.fluxandmonoplayground;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

class FluxAndMonoWithTimeTest {

    @Test
    void infiniteSequence() {
        Flux<Long> infiniteFlux = Flux.interval(Duration.ofMillis(200))
            .log(); // starts from 0 -> ....

        infiniteFlux.subscribe(element -> System.out.println("Value is : " + element));

        sleep(3000);
    }

    private void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Test
    void infiniteSequenceTest() {
        Flux<Long> finiteFlux = Flux.interval(Duration.ofMillis(200))
                .take(3)
                .log(); //starts from 0 -> ....
        
        StepVerifier.create(finiteFlux)
            .expectSubscription()
            .expectNext(0l, 1l, 2l)
            .verifyComplete();
     }
    
    @Test
    void infiniteSequenceMap() {
        Flux<Integer> finiteFlux = Flux.interval(Duration.ofMillis(200))
                .delayElements(Duration.ofSeconds(1))
                .map( l -> Integer.valueOf(l.intValue()))
                .take(3)
                .log(); //starts from 0 -> ....
        
        StepVerifier.create(finiteFlux)
            .expectSubscription()
            .expectNext(0, 1, 2)
            .verifyComplete();
     }

}
