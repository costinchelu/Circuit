package ro.ase.costin.ecomback.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsSecurityService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/states/list_by_country/**")
                    .hasAnyAuthority(UserType.ADMIN.getName(), UserType.SALES.getName())
                .antMatchers("/users/**", "/settings/**", "/countries/**", "/states/**")
                    .hasAnyAuthority(UserType.ADMIN.getName())
                .antMatchers("/categories/**", "/brands/**")
                    .hasAnyAuthority(UserType.ADMIN.getName(), UserType.EDITOR.getName())
                .antMatchers("/products/new", "/products/delete/**")
                    .hasAnyAuthority(UserType.ADMIN.getName(), UserType.EDITOR.getName())
                .antMatchers("/products/edit/**", "/products/save", "/products/check_unique")
                    .hasAnyAuthority(UserType.ADMIN.getName(), UserType.EDITOR.getName(), UserType.SALES.getName())
                .antMatchers("/products/detail/**")
                .hasAnyAuthority(UserType.ADMIN.getName(), UserType.EDITOR.getName(), UserType.SALES.getName(), UserType.SHIPPER.getName(), UserType.ASSISTANT.getName())
                .antMatchers("/products", "/products/", "/products/page/**")
                    .hasAnyAuthority(UserType.ADMIN.getName(), UserType.EDITOR.getName(), UserType.SALES.getName(), UserType.SHIPPER.getName())
                .antMatchers("/products/**")
                    .hasAnyAuthority(UserType.ADMIN.getName(), UserType.EDITOR.getName())
                .antMatchers("/orders", "/orders/", "/orders/page/**", "/orders/detail/**")
                    .hasAnyAuthority(UserType.ADMIN.getName(), UserType.SALES.getName(), UserType.SHIPPER.getName())
                .antMatchers("/customers/detail/**")
                    .hasAnyAuthority(UserType.ADMIN.getName(), UserType.EDITOR.getName(), UserType.SALES.getName(), UserType.ASSISTANT.getName())
                .antMatchers("/customers/**", "/orders/**", "/get_shipping_cost", "/reports/**")
                    .hasAnyAuthority(UserType.ADMIN.getName(), UserType.SALES.getName())
                .antMatchers("/orders_shipper/update/**")
                    .hasAuthority(UserType.SHIPPER.getName())
                .antMatchers("/reviews/**")
                    .hasAnyAuthority(UserType.ADMIN.getName(), UserType.ASSISTANT.getName())
                .anyRequest()
                .authenticated()
                .and().formLogin()
                    .loginPage("/login")
                    .usernameParameter("email")
                    .permitAll()
                .and().logout()
                    .permitAll()
                .and().rememberMe()
                    .key("abCdefghIjklmNOpqrSTuvwxyz9876543210")
                    .tokenValiditySeconds(60 * 60 * 24 * 2);

        http.headers().frameOptions().sameOrigin();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/images/**", "/js/**", "/webjars/**");
    }
}
