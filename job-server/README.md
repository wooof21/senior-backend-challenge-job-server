# Overview

This service accepts job submissions and processes them asynchronously by calling an external (mock) service. 
Job state is persisted in MySQL and can be queried by `jobId`.

### Key features:

* Async job processing (non-blocking API response)
* Reactive persistence (R2DBC MySQL)
* Failure handling with WebClient timeouts + Resilience4j (Retry / Circuit Breaker)
* Clean architecture (Hexagonal / Ports & Adapters)


### Running the Service
```declarative
    docker-compose up
```
### Access MySQL Container In Exec Mode(run sql queries): 
```declarative
    docker exec -it jobs-db mysql -uroot -proot
```

### POSTMAN Collection (src/main/resources/Assessment.postman_collection.json)
1. `Create Job`: Submit a Job with mandatory `jodId` and `userId` and an optional `projectId` - 
   * Sample `Users` and `Projects` are inserted into MySQL when app starts
   * userId: `user123`, `user456`, `user789`
   * projectId: `p-1234`, `p-5678`, `p-9999`
   * Submit a Job with existing `jobId`, API respond with 400 and error message
   * Submit a Job with null `jobId` or `userId`, API respond with 500 error, failed the validation
   * Submit a Job with non-exist `userId`, API respond with 404 and user not found error message
2. `Search Job`: Retrieve a Job by `jobId`
   * Respond 404, if `jobId` not found
3. Mock Service was modified to enable `First n requsts fail` or `All requests fail`
   1. `Retry`: retry is configured to retry for 3 times
      * `Step 1: Fail First 2 times`: make first 2 requests fail, then 3rd request success
      * `Step 2: Submit a job`: retry for 3 times, first 2 requests failed, 2 exceptions were logged, and 3rd request success
      * `3. Reset Mock Service`: reset the mock service to behave normally
   2. `Circuit Breaker`: Circuit Breaker is configured to monitor a window of 10 requests with failing threshold of 50%
      * `Step 1: Enable always-fail mode`: make all requests fail
      * `Step 2: Submit a job repeatedly`: repeatedly send the requests
        * First 5 calls were failed and exception logged
        * Then `CircuitBreaker is OPEN`
        * Later call was failed immediately 


## Design Notes
### Architecture (Hexagonal / Ports & Adapters)

The service follows ports & adapters:
* Domain / Application (core logic): `JobService`, domain models, exceptions
* Ports (interfaces/contracts):
  * `JobContract`, `UserContract`, `ProjectContract` (DB contract)
  * `ExternalContract` (external service contract)
* Adapters:
  * DB client with R2DBC repository
  * External Service client: Mock Service
  * Web client: RestController

### Async Job Processing
* POST /jobs persists a job with `PENDING` status, returns immediately.
* Job then is sent and processed in background with Java 21 Virtual Thread. 
  * Reactive is already running the job in non-blocking mode, but it uses the default scheduler thread, 
  which is tied to the CPU. Use the Virtual Thread here, its cheap to create and can create thousands of threads at same time.
* Background processing first updates job to `PROCESSING`, calls external service, then updates to `COMPLETED` or `FAILED`.

### Failure Strategy

External service calls are protected by:

* WebClient timeouts (connect/response/read/write) configured via YAML
  * The `timeout` is using the WebClient native timeout property
  * The Resilience4j `TimeLimiter` does not work well with reactive streams
  * `TimeLimiter` will be timing on entire reactive chain, it measures time until the `Mono` completes,
    if the `Mono` does not signal `onNext` or `onComplete` within the `timeout`, it will trigger `TimeoutException`,
    which will result a Job becomes FAILED in DB even the background processing complete successfully.
  * Using `Reactive Native Timeout`, it measures the time only on HTTP call
* `Resilience4j Retry`: retries transient failures (configurable)
* `Resilience4j Circuit Breaker`: fails fast when dependency is unhealthy
* Error mapping:
  * `Circuit open` → "External Service Unavailable (Circuit Breaker Open)"
  * `Connection timeout` → "External Service Connection Timeout"
  * `Read/response timeout` → "External Service Response Timeout"
  * `HTTP 4xx/5xx` → "External Service Error: HTTP <status>"
  * `Anything else` → "Unexpected Processing Error"

### OpenAPI 3 Specification
* OpenAPI Api Doc: http://localhost:8080/v3/api-docs
* Swagger UI: http://localhost:8080/swagger-ui.html



