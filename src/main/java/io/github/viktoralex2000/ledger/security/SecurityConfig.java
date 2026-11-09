package io.github.viktoralex2000.ledger.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        //Так как мы не используем куки, отключаем просто отключаем защиту от них.
        httpSecurity.csrf(csrf -> csrf.disable());
        //Выбираем не хранить в памяти сессию клиента, вместо этого полагаться на срок жизни authToken
        httpSecurity.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        //Обязуем Spring Security применить corsConfigurationSource
        httpSecurity.cors(cors -> cors.configurationSource(corsConfigurationSource()));
        //Требуем токен у всех запросов кроме /api/auth/**
        httpSecurity.authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated());
        //Прежде чем проверять логин и пароль пропустить через jwtAuthenticationFilter
        httpSecurity.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        //Пустой бланк "разрешения"
        CorsConfiguration configuration = new CorsConfiguration();
        //Откуда можно отправлять запросы? (пока даём право приходить отовсюду)
        configuration.setAllowedOrigins(List.of("*"));
        //Какие методы можно использовать
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));

        //"Доставщик" данных с бланка.
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        //Взять это бланк с правилами и повесить его на все эндпоинты нашего сервера.
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

}
