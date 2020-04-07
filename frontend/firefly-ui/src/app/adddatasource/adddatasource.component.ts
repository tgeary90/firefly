import { Component, OnInit } from '@angular/core';

export class DataSource {
  constructor(
    public id: number,
    public cloudProvider: string,
    public bucketUrl: string,
    public lastEtl: Date)
  {}
}

@Component({
  selector: 'app-adddatasource',
  templateUrl: './adddatasource.component.html',
  styleUrls: ['./adddatasource.component.css']
})
export class AdddatasourceComponent implements OnInit {

  datasources = [
    new DataSource(1, 'GCP', 'abc', new Date()),
    new DataSource(2, 'AWS', 'def', new Date())
  ]

  constructor() { }

  ngOnInit() {
  }

}
