import { Injectable } from '@angular/core';
import { HttpHeaders, HttpClient } from '@angular/common/http';
import { Bucket } from 'src/app/adddatasource/adddatasource.component';
import { API_URL, AUTHENTICATED_USER, TOKEN } from 'src/app/app.constants';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class BasicAuthenticationService {

  constructor(private http: HttpClient) { }

  // authenticate(username, password) {
  //   // console.log('before ' + this.isUserLoggedIn());
  //   if (username === "tomgeary" && password === "dummy") {
  //     sessionStorage.setItem('authenticatedUser', username)
  //     // console.log('after ' + this.isUserLoggedIn());
  //     return true;
  //   }
  //   return false;
  // }

  // executeBasicAuthenticationService(username, password) {

  //   let basicAuthHeaderString = 'Basic ' + window.btoa(username +':'+ password);

  //   // create the HTTP auth header - pass in our basic auth string (creds base 64 encoded)
  //   let headers = new HttpHeaders({
  //     Authorization: basicAuthHeaderString
  //   })
  //   console.log("invoking authentication service")
  //   return this.http.get<Token>(`${API_URL}/authenticate`).pipe(
  //     map(
  //       data => {
  //         sessionStorage.setItem('authenticatedUser', username);
  //         sessionStorage.setItem('token', basicAuthHeaderString); // TODO -> token
  //         return data;
  //       }
  //     )
  //   )
  // }


  executeJWTAuthenticationService(username, password) {

    // console.log("invoking authentication service")
    return this.http.post<any>(`${API_URL}/authenticate`, {
      username,
      password
    }).pipe(
      map(
        data => {
          sessionStorage.setItem(AUTHENTICATED_USER, username);
          sessionStorage.setItem(TOKEN, `Bearer ${data.token}`); 
          return data;
        }
      )
    );
  }

  getAuthenticatedUser() {
    return sessionStorage.getItem('authenticatedUser')
  }

  getAuthenticationToken() {
    if (this.getAuthenticatedUser()) {
      return sessionStorage.getItem('token')
    }
  }

  isUserLoggedIn() {
    let user = sessionStorage.getItem('authenticatedUser')
    return !(user === null)
  }

  logout() {
    sessionStorage.removeItem('authenticatedUser')
    sessionStorage.removeItem('token')
  }
}
