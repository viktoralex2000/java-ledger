package io.github.viktoralex2000.ledger.security;

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
    //Расшифровщик токенов
    private final JwtService jwtService;
    //Умеет искать юзера в базе по email
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            FilterChain filterChain) throws ServletException, IOException {
        String authHeader = httpServletRequest.getHeader("Authorization");
        //Если клиент авторизован - у него есть токен, то метод идет дальше
        //Если нет, то данная проверка обрывается, а запрос передаётся дальше по цепочке
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }
        authHeader = authHeader.substring(7);
        try {
            String userEmail = jwtService.parse(authHeader).getSubject();
            //Проверяем наличие эмейл и что пользователь действительно
            // авторизован из контекста секьюрити этого потока
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                //Создаем объект аутентификации - юзер, пароль null так как подлинность доказана, и роли/права
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                // Добавляем детали - ip адрес клиента и id http сессии (у нас null из за STATELESS)
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                // Кладём собранный объект аутентификации "пропуск" в контекст безопасности данного запроса.
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (JwtException | IllegalArgumentException e) {
            // Если токен битый, мы просто ничего не делаем.
            // Запрос пойдет дальше, но как анонимный.
        }

        // Обязательно пропускаем запрос дальше
        filterChain.doFilter(httpServletRequest, httpServletResponse);

    }
}
