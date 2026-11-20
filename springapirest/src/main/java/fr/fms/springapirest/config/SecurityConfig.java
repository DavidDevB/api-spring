package fr.fms.springapirest.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  @Bean
  SecurityFilterChain apiSecurity(HttpSecurity http, JwtAuthenticationConverter jwtConverter) throws Exception {
    return http
      .csrf(csrf -> csrf.disable())
      .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .authorizeHttpRequests(a ->
        a
          .requestMatchers(HttpMethod.GET, "/api/trainings")
          .permitAll()
          .requestMatchers(HttpMethod.POST, "/api/login")
          .permitAll()
          .requestMatchers(HttpMethod.GET, "/api/trainings/{id}")
          .hasAuthority("ADMIN")
          .requestMatchers(HttpMethod.DELETE)
          .hasAuthority("ADMIN")
          .requestMatchers(HttpMethod.POST, "/api/trainings")
          .hasAuthority("ADMIN")
          .anyRequest()
          .authenticated()
      )
      .oauth2ResourceServer(o -> o.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtConverter)))
      .build();
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  UserDetailsService users(PasswordEncoder encoder) {
    var alice = User.withUsername("user").password(encoder.encode("123")).authorities("USER").build();
    var admin = User.withUsername("admin").password(encoder.encode("456")).authorities("USER", "ADMIN").build();
    return new InMemoryUserDetailsManager(alice, admin);
  }

  @Bean
  AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
    provider.setPasswordEncoder(passwordEncoder);
    return new ProviderManager(provider);
  }

  @Bean
  JwtAuthenticationConverter jwtAuthenticationConverter() {
    var rolesConverter = new JwtGrantedAuthoritiesConverter();

    rolesConverter.setAuthoritiesClaimName("roles");
    rolesConverter.setAuthorityPrefix("");

    var converter = new JwtAuthenticationConverter();
    converter.setJwtGrantedAuthoritiesConverter(rolesConverter);
    return converter;
  }

  @Bean
  SecretKey jwtKey(@Value("${app.jwt.secret}") String b64) {
    byte[] key = Base64.getDecoder().decode(b64);
    if (key.length < 32) throw new IllegalArgumentException(
      "app.jwt.secret must decode to at least 32 bytes for HS256 (got " + key.length + ")"
    );
    return new SecretKeySpec(key, "HmacSHA256");
  }

  @Bean
  JwtEncoder jwtEncoder(SecretKey key) {
    return new NimbusJwtEncoder(new ImmutableSecret<>(key));
  }

  @Bean
  JwtDecoder jwtDecoder(SecretKey key) {
    var d = NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
    return d;
  }
}
