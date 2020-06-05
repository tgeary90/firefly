package tom.ff.fetch.service.auth

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component


@Component
class JwtUnAuthorizedResponseAuthenticationEntryPoint extends AuthenticationEntryPoint {
  override def commence(request: HttpServletRequest, response: HttpServletResponse, authException: AuthenticationException): Unit = {
    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You need to provided a JWT token to access this resource")
  }
}
