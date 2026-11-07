package io.github.viktoralex2000.ledger.security;

import io.github.viktoralex2000.ledger.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            FilterChain filterChain) throws ServletException, IOException {
        String authHeader = httpServletRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }
        authHeader = authHeader.substring(7);
        try {
            String userEmail = jwtService.parse(authHeader).getSubject();

            // Проверяем, что email есть, и мы еще не авторизованы в этом запросе
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Ищем пользователя в базе
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                // Создаем объект аутентификации
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                // Добавляем детали (опционально, но считается хорошим тоном)
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));

                // Кладем в контекст Spring Security
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (JwtException | IllegalArgumentException e) {
            // Если токен битый, мы просто ничего не делаем.
            // Запрос пойдет дальше, но как анонимный.
        }

        // И САМОЕ ВАЖНОЕ: в самом конце метода пропускаем запрос дальше!
        filterChain.doFilter(httpServletRequest, httpServletResponse);


    }
}
