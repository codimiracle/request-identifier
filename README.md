# request-identifier
prevents form or request multi-submits.

## Installation
includes dependency in `pom.xml`
notes: don't publish maven central yet, you can install by `mvn install`
and then using following pom dependency.
```xml
<dependency>
    <groupId>com.codimiracle.web</groupId>
    <artifactId>request-identifier</artifactId>
    <version>0.0.1-snapshot</version>
</dependency>
```
## Feature
* Request infrequent repeat
* Request only once
* Checking result custom logic (implements [`ResultHandler`](src/main/java/com/codimiracle/web/request/identifier/handler))
## Usage
using `@NonRepeatable` annotate in controller layer:

1. only once
    ```java
    @NonRepeatable
    public String onlyOnce(String data) {
        return "Your submission is accepted.";
    }
    ```

2. don't repeat in one seconds interval.
    ```java
    @NonRepeatable(interval = NonRepeatable.DEFAULT_INTERVAL)
    public String defaultInterval(String data) {
        return "Your submission is accepted.";
    }
    ```

3. custom interval
    ```java
    @NonRepeatable(interval = 2000)
    public String customInterval(String data) {
        return "Your submission is accepted.";
    }
    ```
4. using specified request parameter
    parameter value will retrieve from HttpServletRequest#getParameterValues();
    ```java
    @GetMapping("/hello")
    @NonRepeatable(strategy = IdentifierStrategy.REQUEST_PARAMETER, parameterName = "request_id")
    public String customInterval(String data) {
        return "Your submission is accepted.";
    }
    ```
5. using all parameters in request
    ```java
    @GetMapping("/hi")
    @NonRepeatable(strategy = IdentifierStrategy.REQUEST_PARAMETER)
    public String customInterval(String data) {
        return "Your submission is accepted.";
    }
    ```
