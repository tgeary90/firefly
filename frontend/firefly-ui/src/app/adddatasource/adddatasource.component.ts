import { Component, OnInit } from '@angular/core';
import { BucketsService } from '../service/data/buckets.service';

export class Bucket {
  constructor(
  public name: String,
  public numObjects: number,
  public lastETLDate: Date,
  public provider: string)
  {}
}

@Component({
  selector: 'app-adddatasource',
  templateUrl: './adddatasource.component.html',
  styleUrls: ['./adddatasource.component.css']
})
export class AdddatasourceComponent implements OnInit {

  buckets = []

  constructor(private bucketService: BucketsService) { 
    
  }

  ngOnInit() {
  }

  refreshBuckets() {
    // TODO removed hardcoded 'gcp' and add provider checkbox or similar
    this.bucketService.executeRefreshBuckets("gcp").subscribe(
      response => this.handleSuccessfulResponse(response)
    );
    console.log("last line of refresh buckets")
  }

  handleSuccessfulResponse(response) {
    console.log(response);
    this.buckets = response
  }
}
