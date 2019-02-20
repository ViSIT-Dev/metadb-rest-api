package rest.configuration;

import com.opencsv.CSVReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Value("${visit.rest.auth.user}")
    private String user;

    @Value("${visit.rest.auth.password}")
    private String password;

    @Value("${visit.rest.auth.role}")
    private String role;

    @Value("${visit.rest.sparql.endpoint.query}")
    private String query;

    @Value("${visit.rest.sparql.endpoint.update}")
    private String update;

    @Value("${visit.rest.templates.basepath}")
    private String basepath;

    private final static String REALM = "Visit";

    @Autowired
    public void configureGlobalSecurity(AuthenticationManagerBuilder auth) throws Exception {
        if(this.query.equals("none") && this.update.equals("none")) {
            // Non-productive system
            auth.inMemoryAuthentication().withUser(this.user).password("{noop}" + this.password).roles(this.role);
        } else {
            // Productive System, read out tokens from csv
            String csv = this.basepath + "users.csv";

            CSVReader reader = null;

            try {
                reader = new CSVReader(new FileReader(csv));

                String line[];

                while ((line = reader.readNext()) != null) {
                    auth.inMemoryAuthentication().withUser(line[0]).password("{noop}" + line[1]).roles(line[2]);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/digrep/**").hasRole(this.role)
                .and().httpBasic().realmName(this.REALM).authenticationEntryPoint(this.getBasicAuthEntryPoint())
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Bean
    public CustomBasicAuthenticationEntryPoint getBasicAuthEntryPoint() {
        return new CustomBasicAuthenticationEntryPoint();
    }
}
