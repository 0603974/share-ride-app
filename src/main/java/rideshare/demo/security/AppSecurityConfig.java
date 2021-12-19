package rideshare.demo.security;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.lang.reflect.Array;
import java.util.Arrays;


@Configuration
@EnableConfigurationProperties
@EnableWebSecurity
public class AppSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsService userDetailsService;

	@Bean
	public AuthenticationProvider authProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetailsService);
		provider.setPasswordEncoder(new BCryptPasswordEncoder());
		return provider;
	}

//	@Bean
//	public HttpFirewall configureFirewall() {
//		StrictHttpFirewall strictHttpFirewall = new StrictHttpFirewall();
//		strictHttpFirewall.setAllowedHttpMethods(Arrays.asList("GET","POST"));
//		strictHttpFirewall.setAllowBackSlash(true);
//		strictHttpFirewall.setUnsafeAllowAnyHttpMethod(true);
//		return strictHttpFirewall;
//	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http
				.csrf().disable()
				.authorizeRequests()
				.antMatchers("/assets/**", "/login", "/register", "/driver-register", "/user-register", "/registration", "/driver-registration", "/account-activation",
						"/activate-account", "/register", "/error", "/ride","/shared-ride","/","/search","/reset-password","/smtp.gmail.com","/community-page","/save-comment","/review-page").permitAll()
				.antMatchers().hasRole("ADMIN")
				.antMatchers().hasRole("USER")
				.antMatchers("/driver-profile","/driver-ride","driver","/update-driver-profile","/edit-ride","/start-ride","/ride-initiated","/journey-details/{id}","/accept-journey/{id}"
				,"/reject-journey/{id}","/cancel-sharing","/end-journey/{id","/share-ride","/update-car").hasRole("DRIVER")
				.antMatchers("/dashboard","/data-table","/delete-user/{id}","/review-user/{id}","/admin-profile","/update-admin-profile","/add-admin","/save-admin").hasRole("ADMIN")

				.anyRequest().authenticated()
				.and()
				.formLogin()
				.defaultSuccessUrl("/ride")
				.loginPage("/login").permitAll()
				.and()
				.logout().invalidateHttpSession(true)
				.clearAuthentication(true)
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
				.logoutSuccessUrl("/logout-success").permitAll();


	}


}

