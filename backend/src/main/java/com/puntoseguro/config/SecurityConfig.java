package com.puntoseguro.config;
import com.puntoseguro.security.*;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
@Configuration @RequiredArgsConstructor @SecurityScheme(name="bearerAuth",type=SecuritySchemeType.HTTP,scheme="bearer",bearerFormat="JWT")
public class SecurityConfig {
    private final JwtService jwt; private final SecurityProblemHandler errors;
    @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
    @Bean SecurityFilterChain security(HttpSecurity http) throws Exception {
        return http.csrf(c -> c.disable()).sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .requestCache(c -> c.disable()).formLogin(c -> c.disable()).httpBasic(c -> c.disable()).logout(c -> c.disable())
        .exceptionHandling(c -> c.authenticationEntryPoint(errors).accessDeniedHandler(errors))
        .authorizeHttpRequests(a -> a.dispatcherTypeMatchers(DispatcherType.ERROR).permitAll().requestMatchers(HttpMethod.POST,"/api/auth/login","/api/auth/register").permitAll().requestMatchers("/v3/api-docs/**","/swagger-ui/**","/swagger-ui.html").permitAll().requestMatchers(HttpMethod.GET,"/api/students/*/observations").hasAnyRole("PROFESOR","CENTRO").requestMatchers("/api/students/*/observations").hasRole("PROFESOR").requestMatchers("/api/**").authenticated().anyRequest().denyAll())
        .addFilterBefore(new JwtAuthenticationFilter(jwt,errors), UsernamePasswordAuthenticationFilter.class).build();
    }
}
