package tom.ff.fetch.service.auth

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.{EnableWebSecurity, WebSecurityConfigurerAdapter}
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class JwtWebSecurityConfig(
                            jwtUnAuthorizedResponseAuthenticationEntryPoint: JwtUnAuthorizedResponseAuthenticationEntryPoint,
                            jwtInMemoryUserDetailsService: UserDetailsService,
                            jwtAuthenticationTokenFilter: JwtTokenAuthorizationOncePerRequestFilter,
                            @Value ("${jwt.get.token.uri}") authenticationPath: String
                          ) extends WebSecurityConfigurerAdapter {


  @Bean def passwordEncoderBean = new BCryptPasswordEncoder

  @Bean
  @Override
  override def authenticationManagerBean(): AuthenticationManager = super.authenticationManagerBean

  @Override
  override def configure(auth: AuthenticationManagerBuilder): Unit = {
    super.configure(auth)
    auth
      .userDetailsService(jwtInMemoryUserDetailsService)
      .passwordEncoder(passwordEncoderBean)
  }

  @Override
  override def configure(http: HttpSecurity): Unit = {
    http
      .csrf().disable()
      .exceptionHandling().authenticationEntryPoint(jwtUnAuthorizedResponseAuthenticationEntryPoint).and()
      .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
      .authorizeRequests()
      .anyRequest()
      .authenticated()

    http
      .addFilterBefore(jwtAuthenticationTokenFilter, classOf[UsernamePasswordAuthenticationFilter])
  }
}
