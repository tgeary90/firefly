package tom.ff.fetch.service.auth

import org.springframework.security.core.userdetails.{UserDetails, UserDetailsService, UsernameNotFoundException}
import org.springframework.stereotype.Service

@Service
class JwtInMemoryUserDetailsService extends UserDetailsService {

  // TODO add a hardcoded jwt token for user - tomgeary
  val inMemoryUserList: Seq[JwtUserDetails] = Seq[JwtUserDetails](new JwtUserDetails(1L, "tomgeary", jwt, "ROLE_USER_2"))

  override def loadUserByUsername(username: String): UserDetails = {
    if (! inMemoryUserList.exists(user => user.username == username)) {
      throw new UsernameNotFoundException(s"user{$username} not found")
    }
    inMemoryUserList.filter(user => user.username == username)(0)
  }
}
