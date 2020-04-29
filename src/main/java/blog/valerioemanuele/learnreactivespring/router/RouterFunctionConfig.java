package blog.valerioemanuele.learnreactivespring.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import blog.valerioemanuele.learnreactivespring.handler.SampleHandlerFunction;

@Configuration
public class RouterFunctionConfig {
    @Bean
    public RouterFunction<ServerResponse> route(SampleHandlerFunction handlerFunction){
        return RouterFunctions
            .route(GET("/functional/flux").and(accept(MediaType.APPLICATION_JSON)), handlerFunction::flux)
            .andRoute(GET("/functional/mono").and(accept(MediaType.APPLICATION_JSON)), handlerFunction::mono);
    }
}
