package tom.ff.fetch.io

import javax.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation._
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.security.authentication.{AuthenticationManager, BadCredentialsException, DisabledException, UsernamePasswordAuthenticationToken}
import org.springframework.security.core.userdetails.{UserDetails, UserDetailsService}
import org.springframework.util.StringUtils
import tom.ff.fetch.service.auth.{JwtTokenUtil, JwtUserDetails}

class AuthenticationException(val message: String, val cause: Throwable) extends RuntimeException(message, cause) {}

@RestController
@CrossOrigin
class JwtAuthenticationRestAPI(
                                @Value("${jwt.http.request.header}") tokenHeader: String,
                                authenticationManager: AuthenticationManager,
                                jwtTokenUtil: JwtTokenUtil,
                                jwtInMemoryUserDetailsService: UserDetailsService
                              ) {

  @RequestMapping(value = Array("${jwt.get.token.uri}"), method = Array(RequestMethod.POST))
  def createAuthenticationToken(@RequestBody authenticationRequest: JwtTokenRequest): ResponseEntity[Any] = {
    authenticate(authenticationRequest.getUsername, authenticationRequest.getPassword)

    val userDetails: UserDetails = jwtInMemoryUserDetailsService.loadUserByUsername(authenticationRequest.getUsername)

    val token: String = jwtTokenUtil.generateToken(userDetails)

    ResponseEntity.ok(new JwtTokenResponse(token))
  }

  @GetMapping(value = Array("${jwt.refresh.token.uri}"))
  def refreshAndGetAuthenticationToken(req: HttpServletRequest): ResponseEntity[Any] = {
    val authToken: String = req.getHeader(tokenHeader)
    val token: String = authToken.substring(7) // remove "Bearer: " prefix
    val username = jwtTokenUtil.getUsernameFromToken(token)
    val user: JwtUserDetails = jwtInMemoryUserDetailsService.loadUserByUsername(username).asInstanceOf[JwtUserDetails]

    if (jwtTokenUtil.canTokenBeRefreshed(token)) {
      val refreshedToken: String = jwtTokenUtil.refreshToken(token)
      ResponseEntity.ok(new JwtTokenResponse(refreshedToken))
    }
    else {
      ResponseEntity.badRequest().body(null)
    }
  }

  @ExceptionHandler(Array(classOf[AuthenticationException]))
  def handleAuthenticationException(e: AuthenticationException): ResponseEntity[String] = {
    ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Firefly auth error: " + e.getLocalizedMessage)
  }

  private def authenticate(username: String, password: String): Unit = {
    if ( ! StringUtils.isEmpty(username) && ! StringUtils.isEmpty(password)) {
      try {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password))
      }
      catch {
        case de: DisabledException => throw new AuthenticationException("user disabled", de)
        case bc: BadCredentialsException => throw new AuthenticationException("invalid credentials", bc)
      }
    }
  }
}

@SerialVersionUID(-5616176897013108345L)
class JwtTokenRequest() extends Serializable {
  private var username: String = _
  private var password: String = _

  def this(username: String, password: String) {
    this()
    this.setUsername(username)
    this.setPassword(password)
  }

  def getUsername: String = this.username

  def setUsername(username: String): Unit = {
    this.username = username
  }

  def getPassword: String = this.password

  def setPassword(password: String): Unit = {
    this.password = password
  }
}

@SerialVersionUID(8317676219297719109L)
class JwtTokenResponse(val token: String) extends Serializable {
  def getToken: String = this.token
}