package blog.valerioemanuele.learnreactivespring.controller;

import java.time.Duration;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class FluxAndMonoController {
    
    @GetMapping("/flux")
    public Flux<Integer> getFlux(){
        return Flux.just(1, 2, 3, 4)
            //.delayElements(Duration.ofMillis(500))
            .log();
    }
    
    @GetMapping(value="/fluxstream", produces= MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Long> getFluxStream(){
        return Flux.interval(Duration.ofMillis(500)).log();
    }
    
    @GetMapping("/mono")
    public Mono<Integer> getMono(){
        return Mono.just(1).log();
    }
}
