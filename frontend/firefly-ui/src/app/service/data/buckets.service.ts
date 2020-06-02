import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Bucket } from 'src/app/adddatasource/adddatasource.component';

@Injectable({
  providedIn: 'root'
})
export class BucketsService {

  constructor(
    private http: HttpClient
  ) { }

  executeRefreshBuckets(provider) {
    console.log("execute refresh buckets")
    return this.http.get<Bucket[]>(`http://localhost:9001/buckets?provider=${provider}`)
  }
}
