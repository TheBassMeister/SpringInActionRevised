package tacos.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(encoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/design", "/orders/*")
                .access("hasRole('ROLE_USER')")
                .antMatchers("/**").access("permitAll")

                .and()
                .formLogin()
                .loginPage("/login")

                .and()
                .logout()
                .logoutSuccessUrl("/").and().csrf(). disable();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/h2-console/**");
    }

    //In Memory User Store
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication()
//                .withUser("BuzzLightYear").password("{noop}infinity").authorities("ROLE_USER").and()
//                .withUser("Woody").password("{noop}bullseye").authorities("ROLE_USER");
//    }

    //From a Database
//    @Autowired
//    DataSource dataSource;
//
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.jdbcAuthentication().dataSource(dataSource)
//            .usersByUsernameQuery(
//                    "select username, password, enabled from Users where username=?")
//            .authoritiesByUsernameQuery(
//                    "select username, authority from UserAuthorities where username=?")
//            .passwordEncoder(NoOpPasswordEncoder.getInstance());
//    }
    //LDAP configuration
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception{
//            auth
//                    .ldapAuthentication()
//                    .userSearchBase("ou=people")
//                    .userSearchFilter("(uid={0})")
//                    .groupSearchBase("ou=groups")
//                    .groupSearchFilter("member={0}")
//                    .passwordCompare()
//                    .passwordEncoder(new BCryptPasswordEncoder())
//                    .passwordAttribute("passcode")
//                    .contextSource()
//                    .root("dc=tacocloud,dc=com")
//                    .ldif("classpath:users.ldif");
//        }
//    }
}
