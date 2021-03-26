var chai = require('chai');
var chaiHttp = require('chai-http');
var expect = chai.expect;

chai.use(chaiHttp);
chai.request.Request = chai.request.Test;

var app_agent = 'http://app-agent:9001';

describe('Fetch Buckets', function() {

	before('before', function(done) {
		console.log("Before test")
	});

	after('after', function(done) {
		console.log("After test")
	});

	it('Bucket Count', function (done) {
		this.timeout(10000);
		var sleepy = 5000;

		chai.request(app_agent)
		.get("/buckets")
		.end(function (err, res) {
			setTimeout(function () {
				expect(res).to.have.status(200);
				done();
			}, sleepy);
		});
	});
});
