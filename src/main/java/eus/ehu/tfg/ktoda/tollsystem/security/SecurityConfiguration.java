/*
 * Copyright (c) Toll System Prototype, KONSTANTIN TODOROV ANDREEV 2023.
 *
 * Licensed under the Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://creativecommons.org/licenses/by-nc-nd/4.0/legalcode
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package eus.ehu.tfg.ktoda.tollsystem.security;

import eus.ehu.tfg.ktoda.tollsystem.error.CustomAccessDeniedHandler;
import eus.ehu.tfg.ktoda.tollsystem.jwt.JwtRequestFilter;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfiguration {
    private final UserDetailsService userDetailsService;
    private final JwtRequestFilter jwtRequestFilter;
    private final AuthenticationEntryPoint jwtExceptionHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        http
                .csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer
                        .ignoringRequestMatchers("/authenticate", "/api/**"))
                .authorizeHttpRequests((authRegistry) -> authRegistry
                        .requestMatchers("/authenticate", "/authenticate/**").permitAll()
                        .requestMatchers("/api/**").authenticated()
                        .requestMatchers("/js/**", "/style/**", "/font/**", "/img/**", "/error").permitAll()
                        .requestMatchers(
                                "/sections",
                                "/docs",
                                "/cookie-policy",
                                "/terms-privacy").permitAll()
                        .requestMatchers(
                                "/",
                                "/about",
                                "/sign",
                                "/login").anonymous()
                        .requestMatchers("/eagle", "/eagle/**").hasRole("ADMIN")
                        .requestMatchers("/dashboard").hasRole("USER")
                        .requestMatchers("/transactions").hasRole("USER")
                        .requestMatchers("/journeys").hasRole("USER")
                        .requestMatchers("/profile", "/updateProfile", "/deleteProfile").hasRole("USER")
                        .anyRequest().authenticated() // Every other needs authentication
                )
                .formLogin(login -> login
                        .loginPage("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .failureUrl("/login?error=true")
                        .usernameParameter("email")
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessUrl("/")
                )
                .exceptionHandling(excep -> excep
                        .accessDeniedPage("/error")
                        .accessDeniedHandler(new CustomAccessDeniedHandler())
                        .authenticationEntryPoint(jwtExceptionHandler)
                );

        http.sessionManagement(sessm -> sessm
                .sessionFixation()
                .migrateSession()
                .maximumSessions(3)
        );
        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
