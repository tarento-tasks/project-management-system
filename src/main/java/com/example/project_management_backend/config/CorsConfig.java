import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")  // Allow all API endpoints
                        .allowedOrigins("http://localhost:5173")  // Allow frontend requests
                        .allowedMethods("GET", "POST", "PUT", "DELETE"); // Allowed HTTP methods
            }
        };
    }
}
