## 기본 

set : Interceptor 에서 인증 성공 시 
get : controller 단에서 유저, 테넌트 조회 
remove : Filter 의 finally 에서 요청 종료 시 (필수)

요청 스레드 단위로 인증 정보를 넣었다 빼었다 하기 위한 저장소 
interceptor 가 채우고, Filter 가 비우고
스레드 격리	ThreadLocal이라 요청(스레드)마다 값이 분리됨

## 사용 이유 
  - 스레드 풀로 요청 처리 
  - 요청 A 가 끝나도 그 스레드는 다시 요청 B 에 재사용된다. 
  - AuthContext.info 를 스레드마다 두면 
    - 요청 A 처리 중에는 A 의 AuthInfo 만 보이고,
    - 요청 B 처리 중에는 B 의 AuthInfo 만 보이게 된다. 
    - 반드시 요청 종료시 remove() 해야 이전 요청의 정보가 다음 요청에 섞이지 않는다. 



## 요청 진입
  → Filter (CommonFilter)
  → Interceptor.preHandle
  → 인증 성공 시 AuthContext.info.set(AuthInfo(...))
  → Controller
  → AuthContext.info.get() 으로 tenantId, userId 등 사용
  → Service / Feign 등
  → 필요 시 AuthContext.info.get() 사용
  → Filter.finally
  → AuthContext.info.remove()
  → 응답







## Coroutine / Reactive 전환 대비

  Kotlin + Spring에서는
  Coroutine
  WebFlux
  를 쓰면 ThreadLocal이 깨집니다.


Spring + 비동기 처리 + 코루틴 환경 
  Spring Framework 
  Spring Webflux
  Project Reactor 
  



1. ThreadLocal 은 같은 Thread 에서만 값이 유지된다. 
  @Async
  CompletableFuture
  Scheduler 
  WebFlux
  Coroutine 

   2. Async 작업은 다른 ThreadPool 에서 실행
   Thread A -> AuthContext 있음
   Thread B -> AuthContext 없음 

3. Async AuthContext 유실 해결방법

3-1. SecurityContext 사용 
  Spring Security 를 사용하면 
  SecurityContextHolder 가 Context 전파를 처리한다. 
  Spring 이 Async 전파도 지원 
  
  ex) SecurityContextHolder.getContext().authentication 


3-2. Context 전달 : Async 실행할 때 직접 전달 (매우 번거롭다.)

    
    val auth = AuthContext.get()
    
    executor.submit {
    AuthContext.set(auth!!)
    try {
    service.run()
    } finally {
    AuthContext.clear()
    }
    }

3-3. Coroutine Context 사용 (Kotlin)
  Kotlin Coroutines 에서는 ThreadLocal 대신 CoroutineContext 사용 

4. ThreadPool 재사용하지 않도록 중요 
  Spring에서 보통 Filter, HandlerInterceptor 에서 clear 필요 



5. 사용 방식 정리 
  
  Spring MVC : ThreadLocal AuthContext
  Spring Security : SecurityContextHolder 
  WebFlux /Reactive : Reactor Context 



6. 비동기에서도 통용가능한 방법은? 또 깔끔한 방법은? 

  6-1. Controller 에서 AuthContext 직접 사용하지 않기
  6-2. ThreadLocal 의존 최소화
  6-3. @Async 같은 상황에서도 확장 가능
  6-4. 코드 가독성 좋게 유지 

  -> Spring 생태계에서 보통 Argument Resolver 패턴을 사용한다. 



7. AuthContext(ThreadLocal)를 아예 만들지 않고 jwt 인증을 처리하는 구조를 많이 사용 

7-1. 핵심은 인증 정보는 Request Scope 또는 Spring Security Context 로만 전달하는 것 
  - ThreadLocal 없음
  - Async 문제 없음
  - Spring 표준 구조
  - 테스트 쉬움
  - 보안 기능 활용 가능 

7-2. 전체 구조 (AuthContext 자체가 없음)

  Client
  ↓
  JWT Filter
  ↓
  Spring SecurityContext
  ↓
  Controller (@AuthenticationPrincipal)
  ↓
  Service


7-3. 핵심 코드
SecurityContextHolder.getContext().authentication = authentication


7-4. Controller 에서 사용

  @GetMapping("/me")
  fun me(
  @AuthenticationPrincipal auth: AuthInfo
  ): UserResponse {
      return userService.getUser(auth.userId)
  }


7-5. Service 에서는 AuthContext 를 몰라도 된다. 

7-6. Async 에서도 문제가 없다. 
    SecurityContext 는 기본적으로 ThreadLocal 이지만, Spring 이 Async 전파 기능 제공한다. 
    보통은 Controller 에서 값 전달 Controller -> Service(auth) -> Async(auth)
  
  -> 보통은 실무에서 Context 공유에 의존하지 않는다. 
  
  
  Controller
  ↓
  Service(userId)
  ↓
  Async(userId)
  
  즉 필요한 값만 전달한다. 
  
  Controller 에서 AuthInfo 를 바로쓰면  Controller 가 인증모델(AuthInfo) 에 의존하게 된다. 
  
  Controller <-> Security Layer 결합되게 된다. 

7-7. Controller 에서 자동 주입되는 이유 


7-8. JWT 가 결정 안됐을 때 추천 구조 
  -> 인증 방식과 Controller 를 완전히 분리한다. 
  
  
  Request
  ↓
  AuthInterceptor / AuthFilter
  ↓
  SecurityContext 또는 Request Attribute
  ↓
  ArgumentResolver
  ↓
  @CurrentUser
  ↓
  Controller
  
  
  
  @GetMapping("/me")
  fun me(
  @CurrentUser user: CurrentUser
  ): UserResponse {
  return userService.getUser(user.id)
  }


7-9. Service 에서 AuthContext 를 몰라도 된다. 

  - 테스트 용이성
  - 재사용성 ()  
  - Service 는 비즈니스 로직만 담당하며, 인증 정보 파싱은 Controller 의 책임
  - 권장 요청 흐름 (JWT 미결정 시)
    - Request -> AuthFilter -> ArgumentResolver -> Controller 
      -> SecurityContext  또는 RequestAttribute 에 저장 
  - SecurityContext는 기본적으로 ThreadLocal 에 저장된다. 
    - @Async 로 새 스레드가 생성되는 순간 Context 가 사라진다. 
    - Spring은 SecurityContextHolder.MODE_INHERITABLETHREADLOCAL로 자식 스레드에 전파하는 기능을 제공하지만, 실무에서는 이 방법에 의존 X 쓰레드 풀을 쓰면 InheritableThreadLocal 도 보장 안된다. 
    또한 Coroutine/WebFlux 환경에서는 완전히 다른 모델이기 때문 

  - 예시
    // Controller
    @GetMapping("/orders")
    fun getOrders(@CurrentUser user: CurrentUser): List<Order> {
    return orderService.getOrders(user.id)  // userId만 전달
    }

// Service
fun getOrders(userId: Long): List<Order> {
asyncProcessor.process(userId)  // 그대로 전달
...
}

// Async
@Async
fun process(userId: Long) {
// SecurityContext 없이도 동작
} 
  


7-10. 
  Controller 파라미터에서 자동으로 작동하는 이유는 HandlerMethodArgumentResolver 덕분이다. 

7-11. Context 유실 
    - Interceptor 는 Thread-1 의 HTTP 요청 시작 시점에 한 번만 실행된다. Thread-2 는 Spring 이 내부적으로
        스레드 풀에서 꺼내서 실행하는 것이라 Interceptor 를 거치지 않습니다. 
    -
// @Async 호출 전, Thread-1에서 값을 꺼내서 넘기기
val authInfo = AuthContext.info.get()  // Thread-1에서 꺼냄

asyncTask.doSomething(authInfo)  // 파라미터로 전달

// @Async 내부 Thread-2
@Async
fun doSomething(authInfo: AuthInfo) {
AuthContext.info.set(authInfo)  // Thread-2에서 다시 set
TenantContext.setCurrentTenant(authInfo.tenantCode)
}


7-12. 현재 구조에 따른 개발 
    현재 로그인 방식이 구현되어 있지 않기 때문에 아래와 같은 방향으로 개발함
    Authentication Mechanism (JWT / Session / API Key / Mock)
    ↓
    AuthInfo 생성
    ↓
    AuthContext.set()
    ↓
    Controller / Service 어디서든 사용


7-13. Spring Security

request
↓
Security Filter Chain
↓
AuthFilter (OncePerRequestFilter)
↓
SecurityContextHolder
↓
AuthContext wrapper
↓
Controller / Service

7-14. 전체 구조 

Request
↓
AuthFilter (JWT / Session / Header 등 파싱)
↓
AuthResolver (AuthInfo 생성)
↓
AuthContext 저장
↓
Controller
↓
Service





# 실제 개발 

- Spring Security 인증 정보 저장하는 곳 : SecurityContextHolder.getContext().authentication
  - 구조 : 보통 principal 에 사용자 객체를 넣습니다. 
  SecurityContextHolder
    ↓
    SecurityContext
    ↓
    Authentication
    ↓
    principal

- 사용하는 방법 : val userId = AuthContext.get().userId


