package guru.sfg.brewery.config;

import guru.sfg.brewery.security.RestHeaderAuthFilter;
import guru.sfg.brewery.security.SfgPasswordEncoderFactories;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    PasswordEncoder passwordEncoder() {
        return SfgPasswordEncoderFactories.createDelegatingPasswordEncoder();
        /*return PasswordEncoderFactories.createDelegatingPasswordEncoder();*/
    }

    public RestHeaderAuthFilter restHeaderAuthFilter(AuthenticationManager authenticationManager) {
        RestHeaderAuthFilter filter = new RestHeaderAuthFilter(new AntPathRequestMatcher("/api/**"));
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(restHeaderAuthFilter(authenticationManager()),
                UsernamePasswordAuthenticationFilter.class)
                .csrf().disable();

        http.authorizeRequests(authorize -> {
            authorize
                    .antMatchers("/", "/login").permitAll()
                    .antMatchers("/webjars/**", "/resources/**").permitAll()
                    .antMatchers("/beers/find", "/beers*").permitAll()
                    .antMatchers(HttpMethod.GET, "/api/v1/beer/**").permitAll()
                    .mvcMatchers(HttpMethod.GET, "/api/v1/beerUpc/{upc}").permitAll();
        })
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin().and()
                .httpBasic();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("spring")
                .password("{bcrypt}$2a$10$LeO4qWYOKPqjgAlAuK/wN.nJXizwry2BP370.rJzxW.8g6aYLxrlO")
                .roles("ADMIN")
                .and()
                .withUser("user")
                .password("{sha256}e6b1357224ae9b40b8c350379f4751e5e852b50bc8c7280999c92f425260b2c4320fd68bd655d396")
                .roles("USER")
                .and()
                .withUser("scott")
                .password("{bcrypt15}$2a$15$za3kmduuelaYMymaBouAO.0b2FBr7USX3TdfeF0JhNUV1i042kwoW")
                .roles("CUSTOMER");
    }

    /*@Override
    @Bean
    protected UserDetailsService userDetailsService() {
        UserDetails admin = User.withDefaultPasswordEncoder()
                .username("spring")
                .password("guru")
                .roles("ADMIN")
                .build();

        UserDetails user = User.withDefaultPasswordEncoder()
                .username("user")
                .password("password")
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, user);
    }*/
}
