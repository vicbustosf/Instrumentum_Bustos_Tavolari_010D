package cl.instrumentum.service_gira.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
    //builder arma objeto WebClient, que es el 
    // cliente HTTP reactivo de Spring, para hacer llamadas a otros servicios.
}