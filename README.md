[![Build Status](https://travis-ci.com/dexmo007/jackson-proxy-module.svg?branch=master)](https://travis-ci.com/dexmo007/jackson-proxy-module)
[![Coverage Status](https://coveralls.io/repos/github/dexmo007/jackson-proxy-module/badge.svg?branch=master)](https://coveralls.io/github/dexmo007/jackson-proxy-module?branch=master)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=com.dexmohq.jackson%3Aproxy-module&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.dexmohq.jackson%3Aproxy-module)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=com.dexmohq.jackson%3Aproxy-module&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=com.dexmohq.jackson%3Aproxy-module)

# Jackson Proxy Module
A Jackson Module that enables deserializing to an interface using a proxy.

### Usage
Given you have some DTO interface, maybe already used for Spring Data projections:
```java
public interface DtoInterface {
    String getFoo();
}
```
Then just register this module with your object mapper and you will be able to deserialize JSON to an interface
type.
```java
new ObjectMapper()
    .registerModule(new ProxyModule())
    .readValue("{\"foo\":\"bar\"}", DtoInterface.class);
```

### Development
#### Coverage
`mvn test jacoco:report` can be used to analyze test coverage locally. 
Results can be found in `target/site/jacoco`.
