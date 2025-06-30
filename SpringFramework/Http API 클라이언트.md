# HTTP API 클라이언트

## RestTemplate

- 가장 기본적인 HTTP 클라이언트 
- 동기식 
- 블로킹 호출이 발생할 수 있다. 
  - 블로킹 호출 
    - 요청을 보내고 응답을 받을 때까지 기다리는 방식
    - 서버가 응답을 보내기 전까지 다른 작업을 수행할 수 없음
- 사용 방법

```

val restTemplate = RestTemplate()
val response: User = restTemplate.getForObject("/api/user", User::class.java)

```


## WebClient 
 
- Spring WebFlux에서 제공하는 비동기식 HTTP 클라이언트
- 비동기/리액티브 처리가 필요한 경우
- 대규모 트래픽 처리 
- kotlin + 코루틴 사용 시 


## OkHttp 

- Java/Kotlin에서 사용할 수 있는 HTTP 클라이언트 라이브러리
- 간결하고 빠름
- Spring 을 안쓰는 Kotlin 프로젝트 
- Android 개발에서 많이 사용

## FeignClient (Spring Cloud OpenFeign)

- Retrofit 처럼, 인터페이스로 API 호출 정의
- Spring Boot + 마이크로서비스에서 많이 사용 
- RestTemplate, WebClient 기반으로 이행 
- 마이크로서비스 아키텍처 (Spring Cloud)
- 서비스간 통신 


