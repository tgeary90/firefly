package tom.ff.fetch.service.auth

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.{UserDetails, UserDetailsService, UsernameNotFoundException}
import org.springframework.stereotype.Service

@Service
class JwtInMemoryUserDetailsService extends UserDetailsService {

  val inMemoryUserList: Seq[JwtUserDetails] = Seq[JwtUserDetails](
    new JwtUserDetails(
      1L,
      "tomgeary",
      "$2a$10$3zHzb.Npv1hfZbLEU5qsdOju/tk2je6W6PnNnY.c1ujWPcZh4PL6e",
      Seq[GrantedAuthority](
        new GrantedAuthority() {
          override def getAuthority: String = "ROLE_USER_2"
        }
      )
    )
  )

  override def loadUserByUsername(username: String): UserDetails = {
    if (! inMemoryUserList.exists(user => user.getUsername == username)) {
      throw new UsernameNotFoundException(s"user{$username} not found")
    }
    inMemoryUserList.filter(user => user.getUsername == username)(0)
  }
}
