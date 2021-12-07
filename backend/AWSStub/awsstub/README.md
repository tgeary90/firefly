
# AWS Stub web service 

## get buckets
```
curl -XGET http://localhost:9000/v1/buckets
[{"id":"1","provider":"aws","contents":["some data"]},{"id":"2","provider":"aws","contents":["some data"]},{"id":"3","provider":"aws","contents":["some data"]}]
```
## get a bucket
```
└─ $ ▶ curl -XGET http://localhost:9000/v1/buckets/1
{"id":"1","provider":"aws","contents":["some data"]}tomg @ UK3169095W1 ~
```
## get bucket count
```
└─ $ ▶ curl -XGET http://localhost:9000/v1/buckets/count
3tomg @ UK3169095W1 ~
```