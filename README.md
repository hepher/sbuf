# SBUF
SBUF (Spring Boot Utility Framework) is a library to wrap and simplify some spring boot functionality

## Aspect
## Config
## Resolver
## Security
## Cache
## RestClientService
Spring boot 3.x deprecated spring template, and replace it with RestService.
The class RestClientService in this project wraps Spring Boot classes, adding some utilities.
See some examples.

### Rest call 
```java
RestClientService.instance()
    .url("https://myurl.com")
    .method(HttpMethod.GET)
    .resultClass(MyKlass.class)
    .exchange();
```

### Rest call with query parameter 
Single key-value query parameter
```java

List<String> myList = new ArrayList<>();
myList.add("value1");
myList.add("value2");

RestClientService.instance()
    .url("https://myurl.com")
    .method(HttpMethod.GET)
    .queryParameter("string", "hello")
    .queryParameter("number", 1234)
    .queryParameter("list", myList)
    .resultClass(MyKlass.class)
    .exchange();
```
Composed key-value map
```java
Map<String, Object> map = new HashMap<>();
map.put("string", "hello");
map.put("number", 1234);

RestClientService.instance()
    .url("https://myurl.com")
    .method(HttpMethod.GET)
    .queryParameters(map)
    .resultClass(MyKlass.class)
    .exchange();
```
NB: method with map input not clear already present single key-value; both method go in append strategy

### Rest call with path parameter
Single key-value path parameter
```java
RestClientService.instance()
    .url("https://myurl.com")
    .method(HttpMethod.GET)
    .queryParameter("string", "hello")
    .queryParameter("string2", "world")
    .resultClass(MyKlass.class)
    .exchange();
```
Composed key-value map
```java
Map<String, Object> map = new HashMap<>();
map.put("string", "hello");
map.put("string2", "world");

RestClientService.instance()
    .url("https://myurl.com/{string}/{string2}")
    .method(HttpMethod.GET)
    .pathParameters(map)
    .resultClass(MyKlass.class)
    .exchange();
```
### Rest call with body request
```java
RestClientService.instance()
        .url("https://myurl.com/user/")
        .method(HttpMethod.POST)
        .requestBody(MyRequestBody.class)
        .resultClass(MyKlass.class)
        .exchange();
```
### Rest call with auth header
Bearer Auth
```java
RestClientService.instance()
    .url("https://myurl.com/user/")
    .method(HttpMethod.POST)
    .bearerAuth("myToken")
    .resultClass(MyKlass.class)
    .exchange();
```
Basic Auth
```java
RestClientService.instance()
    .url("https://myurl.com/user/")
    .method(HttpMethod.POST)
    .basicAuth("client-id", "client-secret")
    .resultClass(MyKlass.class)
    .exchange();
```
### Rest call with errors handling

```java
RestClientService.instance()
    .url("https://myurl.com/user/")
    .method(HttpMethod.POST)
    .resultClass(MyKlass.class)
    .onHttpStatusError(HttpStatus.UNAUTHORIZED, ((httStatusHandlerParam) -> {
        if (httStatusHandlerParam.retryDone()) {
            return new MyEmptyResponse();
        } else {
            // refresh auth token
            return true;
        }
    }))
    .onHttpStatusError(HttpStatus.INTERNAL_SERVER_ERROR, (httStatusHandlerParam) -> new MyEmptyResponse())
    .onHttpStatusError(HttpStatus.BAD_GATEWAY, (httStatusHandlerParam) -> new MyEmptyResponse())
    .onHttpStatusError(HttpStatus.SERVICE_UNAVAILABLE, (httStatusHandlerParam) -> new MyEmptyResponse())
    .onHttpStatusError(HttpStatus.BAD_REQUEST, (httStatusHandlerParam) -> new MyEmptyResponse())
    .exchange();
```
The function onHttpStatusError() accept two parameter: 
 - HttpStatusError
 - Function to execute on HttpStatus

The function is going to be called on specific error one time and must return an Object; 
the function's behaviour is different for object value:
 - Returned Object is a boolean value:
   - True: retry the call
   - False: not retry the call
 - Returned Object isn't a boolean value: the Object il returned

Use case for returned value:
 - True -> rest call fails for expired token; the function can call another service to refresh token, and the RestClientService retry the rest call
 - False -> rest call fails for FORBIDDEN or SERVICE_UNAVAILABLE, so the function can log on databases for statics
 - Object -> rest call fails for INTERNAL_SERVER_ERROR, so the function return an empty object

There are another two function to handle the errors: on5xxServerError, on4xxServerError

```java
RestClientService.instance()
    .url("https://myurl.com/user/")
    .method(HttpMethod.POST)
    .resultClass(MyKlass.class)
    .on5xxServerError((httStatusHandlerParam) -> new MyEmptyResponse())
    .on4xxServerError((httStatusHandlerParam) -> new MyEmptyResponse())
    .exchange();
```

## Configuration properties
```yml
sbuf:
  aspect:
    base-package: 
    controller:
      enabled:
      traced-headers:
      success-operation-trace.enable:
    service:
      enabled:
    repository:
      enabled:
    logging-component:
      enabled:
  config:
    mongo:
      enabled:
    tracing:
      on-database:
  security:
    basic:
      client-id:
      client-secret:
  service:
    smart-cache:
      enabled:
```
