package io.github.viktoralex2000.ledger.controller;

import io.github.viktoralex2000.ledger.dto.*;
import io.github.viktoralex2000.ledger.model.UserEntity;
import io.github.viktoralex2000.ledger.security.JwtAuthenticationFilter;
import io.github.viktoralex2000.ledger.security.UserPrincipal;
import io.github.viktoralex2000.ledger.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

//Тест контроллера
@WebMvcTest(
        //Какой контроллер тестируем
        controllers = AuthController.class,
        //Исключаем из security фильтр проверки токена.
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class))
@Import(AuthControllerTest.TestConfig.class)
public class AuthControllerTest {
    @TestConfiguration
    static class TestConfig implements WebMvcConfigurer {
        @Override //Метод вызывается автоматически при запуске тестового контекста
        //В нём спринг даёт список резольверов, а тест добавляет в них еще один.
        public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
            //Конкретно этот резольвер умеет лазить в секьюрити
            resolvers.add(new AuthenticationPrincipalArgumentResolver());
            //Зачем это нужно? отключив jwt мы сделали так что ЮзерПринципал не положится в секьюрити
            //Из за аннотации @WebMvcTest резольвер секьюрити который должен был положить юзер,
            //Не запустится, и поэтому мы должны сами создать этот же резольвер который сделает эту работу
            //Сам же юзер мы создадим и положим туда в начале метода me, в ручную положим, а потом вызовем контроллер
            //В котором как раз в бой вступит наш резольвер.

        }
    }

    @Autowired //Симулятор бразуера для отправки http запросов в контроллер
    private MockMvc mockMvc;

    @Autowired //Переводчик на JSON
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @Test
    @DisplayName("Успешная регистрация пользователя")
    void register_Success() throws Exception {
        //Создаём запрос от клиента
        RegisterRequest registerRequest = new RegisterRequest(
                "Human",
                "Humanovich",
                "Human@human.com",
                "password11"
        );
        //Создаём то что должен ответить сервер после успешной регистрации
        AuthResponse authResponse = new AuthResponse(
                "access-token",
                "Bearer",
                3600L,
                "refresh-token",
                null
        );

        //Создаём фейковую логику сервиса
        Mockito.when(authService.register(
                ArgumentMatchers.any(RegisterRequest.class))).thenReturn(authResponse);

        //Симуляция клиента - создание http запроса POST на указанный адрес.
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                        //Прикрепить заголовок о том что мы отправляем JSON - для спринга.
                        .contentType(MediaType.APPLICATION_JSON)
                        //В тело запроса положим переведённый в джейсон объект registerRequest
                        .content(objectMapper.writeValueAsString(registerRequest)))
                //Ожидание статуса 201
                .andExpect(MockMvcResultMatchers.status().isCreated())
                //Проверка что токены есть
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));

        //Проверка что сервис вызывали один раз - нужно чтобы убедиться что контроллер корректен
        Mockito.verify(authService, Mockito.times(1))
                //И вызывали именно метод register, и передавали именно этот класс
                .register(any(RegisterRequest.class));
    }

    @Test
    @DisplayName("Успешная авторизация пользователя")
    void login_Success() throws Exception {
        LoginRequest loginRequest = new LoginRequest(
                "Human@human.com",
                "password11"
        );

        AuthResponse authResponse = new AuthResponse(
                "access-token",
                "Bearer",
                3600L,
                "refresh-token",
                null
        );

        Mockito.when(authService.login(
                ArgumentMatchers.any(LoginRequest.class))).thenReturn(authResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                //Ожидание статуса 200
                .andExpect(MockMvcResultMatchers.status().isOk())
                //Проверка что токены есть
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));

        Mockito.verify(authService, Mockito.times(1))
                .login(any(LoginRequest.class));
    }

    @Test
    @DisplayName("Успешное обновление пользователя")
    void refresh_Success() throws Exception {
        RefreshRequest refreshRequest = new RefreshRequest(
                "refresh-token"
        );

        AuthResponse authResponse = new AuthResponse(
                "access-token",
                "Bearer",
                3600L,
                "refresh-token",
                null
        );

        Mockito.when(authService.refresh(
                ArgumentMatchers.any(String.class))).thenReturn(authResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                //Ожидание статуса 200
                .andExpect(MockMvcResultMatchers.status().isOk())
                //Проверка что токены есть
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));

        Mockito.verify(authService, Mockito.times(1))
                .refresh(any(String.class));

    }

    @Test
    @DisplayName("Успешная деаутентификация пользователя")
    void logout_Success() throws Exception {

        RefreshRequest refreshRequest = new RefreshRequest(
                "refresh-token"
        );

        //Говорим Мокито что вызов метода сервиса ничего не вернёт
        Mockito.doNothing().when(authService).logout(ArgumentMatchers.any(String.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                //Ожидание статуса 200
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(authService, Mockito.times(1))
                .logout(any(String.class));

    }

    @Test
    @DisplayName("Успешное получение текущего пользователя")
    void me_Success() throws Exception {

        UserEntity user = new UserEntity();
        ReflectionTestUtils.setField(user, "id", 1L);
        user.setEmail("Human@human.com");
        UserDto userDto = new UserDto(
                1L,
                "Human@human.com",
                "Human",
                "Humanovich",
                "USER",
                "ACTIVE",
                Instant.now()
        );

        //Если сервису передадут любой объект UserEntity то пусть он просто вернет этот userDto
        Mockito.when(authService.me(ArgumentMatchers.any(UserEntity.class))).thenReturn(userDto);

        //Создание объекта аутентификации
        UsernamePasswordAuthenticationToken upaToken = new UsernamePasswordAuthenticationToken(
                new UserPrincipal(user), //Оборачиваем user в Principal
                null, //Пароль не нужен
                List.of() //Список ролей тут не важен
        );

        //Создаём новый - пустой - карман Security (Чтобы не задеть данные других тестов)
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        //Кладём в него объект аутентификации
        context.setAuthentication(upaToken);
        //Заменяем этим карманом тот что там лежал
        SecurityContextHolder.setContext(context);

        try {
            mockMvc.perform(MockMvcRequestBuilders.get("/api/auth/me"))
                    .andDo(MockMvcResultHandlers.print()) //Вывести лог в консоль
                    .andExpect(MockMvcResultMatchers.status().isOk()) //Статус 200
                    //Проверка тела - айди и эмейл
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.email").value("Human@human.com"));

            //Проверка что метод был вызван, и всего 1 раз
            Mockito.verify(authService, Mockito.times(1))
                    .me(ArgumentMatchers.any(UserEntity.class));
        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}