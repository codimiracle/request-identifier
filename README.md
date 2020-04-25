# request-identifier
prevents form or request multi-submits.

## Installation
includes dependency in `pom.xml`
```xml
<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis-spring</artifactId>
    <version>0.0.1-snapshot</version>
</dependency>
```
## Feature
* Request infrequent repeat
* Request only once
* Checking result custom logic (implements [`ResultHandler`]())
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
    @NonRepeatable
    public String customInterval(String data) {
        return "Your submission is accepted.";
    }
    ```
