package at.yeoman.photobackup.server.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    private Logger log = LoggerFactory.getLogger(SecurityConfiguration.class);

    private final PasswordEncoder encoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final StoredPassword storedPassword;

    @Autowired
    SecurityConfiguration(AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        storedPassword = StoredPassword.load();
    }

    public boolean isAuthorizationEnabled() {
        return storedPassword != null;
    }

    public boolean passwordMatches(String password) {
        log.info("Checking password: " + (storedPassword != null);
        return storedPassword != null && storedPassword.matches(password);
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
    }
}
