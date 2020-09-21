package com.ferroeduardo.springchat.Config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private UserDetailsService userDetailsService;

    public WebSecurityConfig(@Qualifier("myUserDetailsService") UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/css/**","/js/**", "/assets/**",
                            "/webjars/**/**", "/login*", "/signup*",
                            "/invalidSession", "sessionExpired").permitAll()
                .antMatchers( "/favicon.ico").permitAll()
                .antMatchers("/adm/css/**", "/adm/js/**").hasRole("ADMIN")
                .antMatchers("/chat*").hasRole("USER")
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/actuator/**").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/chat", true)
                .failureHandler(loginFailureHandler())
                .permitAll()
                .and()
            .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .permitAll()
                .and()
            .sessionManagement()
                .sessionFixation().migrateSession()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .sessionAuthenticationErrorUrl("/login?authenticated")
//                .invalidSessionUrl("/login?invalid")
                .maximumSessions(1)
                .maxSessionsPreventsLogin(true)
                .expiredUrl("/login?expired");
    }

    // Eensure that expired sessions are cleaned up
    @Bean
    public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean<>(new HttpSessionEventPublisher());
    }

    @Bean
    public AuthenticationFailureHandler loginFailureHandler() {
        AuthenticationFailureHandler failureHandler = new SimpleUrlAuthenticationFailureHandler(){
            @Override
            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                if (exception.getClass().isAssignableFrom(BadCredentialsException.class)) {
                    response.sendRedirect("/login?credentials");
                } else if (exception.getClass().isAssignableFrom(DisabledException.class)) {
                    response.sendRedirect("/login?disabled");
                } else if (exception.getClass().isAssignableFrom(SessionAuthenticationException.class)) {
                    response.sendRedirect("/login?authenticated");
                } else {
                    response.sendRedirect("/login?error");
                }
            }
        };
        return failureHandler;
    }

}
