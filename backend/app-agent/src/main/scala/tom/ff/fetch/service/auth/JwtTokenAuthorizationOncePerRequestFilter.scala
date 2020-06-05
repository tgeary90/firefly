package tom.ff.fetch.service.auth

import io.jsonwebtoken.ExpiredJwtException
import javax.servlet.FilterChain
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.util.StringUtils
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource

@Component
class JwtTokenAuthorizationOncePerRequestFilter(
                                                 jwtInMemoryUserDetailsService: JwtInMemoryUserDetailsService,
                                                 jwtTokenUtil: JwtTokenUtil,
                                                 @Value("${jwt.http.request.header}") tokenHeader: String
                                               ) extends OncePerRequestFilter {

  override def doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain): Unit = {
    logger.debug(s"Authentication request for '${request.getRequestURI}'")

    val requestTokenHeader = request.getHeader(tokenHeader)
    val jwtToken = requestTokenHeader.substring(7) // remove "Bearer: " prefix

    val username: String = if ( ! StringUtils.isEmpty(requestTokenHeader) && requestTokenHeader.startsWith("Bearer ")) {
      try {
        jwtTokenUtil.getUsernameFromToken(jwtToken)
      } catch {
        case iae: IllegalArgumentException => logger.error(iae.getLocalizedMessage); ""
        case eje: ExpiredJwtException => logger.error(eje.getLocalizedMessage); ""
      }
    }
    else {
      logger.warn("The JWT token either doesnt exist or is corrupt."); ""
    }

    logger.debug(s"processing JWT for ${username}")

    createSessionForUser(request, jwtToken, username)
  }

  private def createSessionForUser(request: HttpServletRequest, jwtToken: String, username: String) = {
    if (!StringUtils.isEmpty(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
      val userDetails = jwtInMemoryUserDetailsService.loadUserByUsername(username)

      if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
        val usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities)
        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request))
        SecurityContextHolder.getContext.setAuthentication(usernamePasswordAuthenticationToken)
      }
    }
  }
}
