import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Bucket } from 'src/app/adddatasource/adddatasource.component';
import { API_URL } from 'src/app/app.constants';

@Injectable({
  providedIn: 'root'
})
export class BucketsService {

  constructor(
    private http: HttpClient
  ) { }

  executeRefreshBuckets(provider) {
    let basicAuthHeaderString = this.createBasicAuthenticationHttpHeader();

    // create the HTTP auth header - pass in our basic auth string (creds base 64 encoded)
    let headers = new HttpHeaders({
      Authorization: basicAuthHeaderString
    })
    console.log("async call to /buckets - execute refresh buckets")
    return this.http.get<Bucket[]>(`${API_URL}/buckets?provider=${provider}`, {headers})
  }

  createBasicAuthenticationHttpHeader() {
    let username = 'tomgeary'
    let password = 'dummy'
    let basicAuthHeaderString = 'Basic ' + window.btoa(username +':'+ password);
    return basicAuthHeaderString;
  }
}
