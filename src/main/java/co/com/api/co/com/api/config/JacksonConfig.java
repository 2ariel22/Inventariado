package co.com.api.co.com.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // Registrar módulo para Java 8 time types (LocalDate, LocalDateTime, etc.)
        mapper.registerModule(new JavaTimeModule());
        
        // Permitir comentarios en JSON
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        
        // Permitir comillas simples
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        
        // Permitir comillas no escapadas
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        
        // Ignorar propiedades desconocidas
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        // Deshabilitar la escritura de fechas como timestamps
        mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        return mapper;
    }
}
