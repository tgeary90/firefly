package tom.ff.fetch.service.auth

import java.util

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import scala.collection.JavaConverters._

class JwtUserDetails(id:  Long,
                     username: String,
                     password: String,
                     authorities: Seq[GrantedAuthority]) extends UserDetails {

  override def getAuthorities: util.Collection[_ <: GrantedAuthority] = authorities.asJava
  override def getPassword: String = password
  override def getUsername: String = username
  override def isAccountNonExpired: Boolean = true
  override def isAccountNonLocked: Boolean = true
  override def isCredentialsNonExpired: Boolean = true
  override def isEnabled: Boolean = true
}
