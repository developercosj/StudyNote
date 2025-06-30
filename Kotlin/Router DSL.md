Spring Framework Kotlin DSL 기반의 라우팅 방식 
정의 : 클라이언트(브라우저, 앱 등)에서 보낸 요청(Request) 을 서버에서 어떤 코드(핸들러)가 처리할지를 결정하는 길 안내 시스템 
- 전통적인 라우팅 방식(Spring MVC)
  @RestController
  class HelloController {
  @GetMapping("/hello")
    fun hello(): String = "Hello!"
  }

- Kotlin Router DSL 
  - 기존의 애노테이션 기반보다 더 명시적이고 함수형 스타일로 설계 가능 
  - router {
    GET("/hello") { ok().bodyValue("Hello!") }
  }

- DSL 의 3가지 종류 설명 (Spring Framework comes with a Kotlin router DSL available in 3 flavors)

  - WebMvc.fn DSL
  - WebFlux.fn Reactive DSL
  - WebFlux.fn Coroutines DSL 
  - WebFlux.fn DSL
  - Spring Boot.fn DSL


- 함수형 스타일(functional style) 라우팅 방식