package tom.ff.fetch.service.auth

import java.util.Date

import io.jsonwebtoken.{Claims, Jwts, SignatureAlgorithm}
import io.jsonwebtoken.impl.DefaultClock
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import scala.collection.mutable


class JwtTokenUtil(
                    @Value("${jwt.signing.key.secret}") secret: String,
                    @Value ("${jwt.token.expiration.in.seconds}") expiration: Long
                  ) extends Serializable {

  val CLAIM_KEY_USERNAME = "sub"
  val CLAIM_KEY_CREATED = "iat"
  val serialVersionUID = -3301605591108950415L
  val clock = DefaultClock.INSTANCE

  private def calculateExpirationDate(createdDate: Date) = new Date(createdDate.getTime + expiration * 1000)

  def validateToken(token: String, userDetails: UserDetails):  Boolean = {
    val user = userDetails.asInstanceOf[JwtUserDetails]
    val username = getUsernameFromToken(token)
    (username == user.getUsername && ! isTokenExpired(token))
  }

  def refreshToken(token: String): String = {
    val createdDate: Date = clock.now()
    val expirationDate: Date = calculateExpirationDate(createdDate)

    val claims: Claims = getAllClaimsFromToken(token)
    claims.setIssuedAt(createdDate)
    claims.setExpiration(expirationDate)

    Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS512, secret).compact()
  }

  def canTokenBeRefreshed(token: String): Boolean = ( ! isTokenExpired(token) || ignoreTokenExpiration(token))

  import io.jsonwebtoken.Claims
  import io.jsonwebtoken.Jwts
  import org.springframework.security.core.userdetails.UserDetails

  def getUsernameFromToken(token: String): String = getClaimFromToken(token, (c: Claims) => c.getSubject)

  def getIssuedAtDateFromToken(token: String): Date = getClaimFromToken(token, (c: Claims) => c.getIssuedAt)

  def getExpirationDateFromToken(token: String): Date = getClaimFromToken(token, (c: Claims) => c.getExpiration)

  def getClaimFromToken[T](token: String, claimsResolver: (Claims) => T): T = {
    val claims = getAllClaimsFromToken(token)
    claimsResolver(claims)
  }

  private def getAllClaimsFromToken(token: String): Claims = Jwts.parser
    .setSigningKey(secret)
    .parseClaimsJws(token)
    .getBody

  private def isTokenExpired(token: String) = {
    val expiration = getExpirationDateFromToken(token)
    expiration.before(clock.now)
  }

  private def ignoreTokenExpiration(token: String) = {
    // here you specify tokens, for that the expiration is ignored
    false
  }

  def generateToken(userDetails: UserDetails): String = {
    val claims = mutable.Map[String, Any].asInstanceOf[Claims]
    val createdDate: Date = clock.now()
    val expirationDate: Date = calculateExpirationDate(createdDate)

    Jwts.builder()
      .setClaims(claims)
      .setSubject(userDetails.getUsername)
      .setIssuedAt(createdDate)
      .setExpiration(expirationDate)
      .signWith(SignatureAlgorithm.HS512, secret)
      .compact()
  }
}
