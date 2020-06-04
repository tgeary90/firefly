package tom.ff.fetch.service.auth

import javax.servlet.FilterChain
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtTokenAuthorizationOncePerRequestFilter(
                                                 jwtInMemoryUserDetailsService: JwtInMemoryUserDetailsService,
                                                 jwtTokenUtil: JwtTokenUtil,
                                                 @Value("${jwt.http.request.header}")tokenHeader: String
                                               ) extends OncePerRequestFilter {

  private val logger: Logger = LoggerFactory.getLogger(this.getClass)

  override def doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain): Unit = {
    
  }
}
