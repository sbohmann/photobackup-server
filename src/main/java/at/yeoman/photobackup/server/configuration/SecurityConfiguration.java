package at.yeoman.photobackup.server.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    private Logger log = LoggerFactory.getLogger(SecurityConfiguration.class);
    
    private final PasswordEncoder encoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    
    @Autowired
    SecurityConfiguration(AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        configureUser();
    }
    
    private void configureUser() {
        try {
            configureUserThrowing();
        } catch (Exception error) {
            log.error("Unable to configure user", error);
        }
    }
    
    private void configureUserThrowing() throws Exception {
        UserDetails user = createUserDetails();
        authenticationManagerBuilder
                .inMemoryAuthentication()
                .withUser(user);
    }
    
    private UserDetails createUserDetails() {
        return User
                .withUsername("user")
                // encoded password: "pass"
                .password("{bcrypt}$2a$10$MqNI3sJAgVUdgSsAqvYzueEloNwYPRXrmcKHLMgvFqnxSfSB1qtm6")
                .roles("USER")
                .build();
    }
    
    @Bean
    PasswordEncoder getEncoder() {
        return encoder;
    }
    
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf().disable()
                .authorizeRequests().anyRequest().permitAll();

//        httpSecurity
//                .authorizeRequests()
//                .anyRequest()
//                .authenticated()
//                .and()
//                .formLogin()
//                .and()
//                .httpBasic();
    }
}
