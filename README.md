# Explicit Filter

[![Run on Google Cloud](https://deploy.cloud.run/button.png)](https://deploy.cloud.run)

## Local Dev

Run the server:
```
./gradlew bootRun
```

Test it:
```
curl localhost:8080/filter \
  -H "ce-id: 0001"  \
  -H "ce-specversion: 1.0" \
  -H "ce-type: filter" \
  -H "ce-source: spring.io/spring-event" \
  -H "Content-Type: application/json" \
  -d '{"body": "asdf foo zxcv"}'
```

Create container & run with docker:
```
./gradlew bootBuildImage --imageName=explicit-filter

docker run -it -p8080:8080 explicit-filter
```
