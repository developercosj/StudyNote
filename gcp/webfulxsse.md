문제 현상 : 기존 채팅 서비스에서 가능하던 사용자 채팅 종료시 API 끊어짐 현상 GCP 에서는 불가 



# SSE 

- 클라이언트 <-> 서버 (HTTP 연결 유지)
- 스트림 데이터 받음 
- 실시간 업데이트 
- 단방향 통신 서버 -> 클라이언트 
- HTTP 기반 : WebSocket 처럼 별도 프로토콜 불필요 
- 자동 재연결 : 연결 끊기면 자동으로 재시도


# Webflux 

- Spring 의 리액티브 웹 프레임워크 
- 논블로킹 I/O 와 함수형 프로그래밍 지원 



# 논블로킹/블로킹 

- 블로킹 : 작업이 완료될 때까지 다음 코드 실행 기다림
SSE 채팅은 연결이 오래 유지 Spring MVC 는 연결 1개당 스레드 1개를 점유한다. 
WebFlux 는 CPU 코어수 * 2개 스레드로 수천 개 연결 처리 

Tomcat 스레드 풀 + boundedElastic 튜닝으로 충분히 커버
리액티브 코드는 디버깅/유지보수가 훨씬 어려움


# ThreadLocal 전파 문제 해결 

- 채팅 시스템에서 ThreadLocal 전파 문제 파악
요청 스레드: TenantContext 에 set 후 ThreadLocal 저장 후 
-> Schedulers.boundedElastic() 스레드로 전환된 후 
-> doOnComplete/doOnError 실행 스레드시 TenantContext.get() 을 하면 다른 스레드가 된다. 

해결방법 1. 현재 방식 (클로저 캡처)
파라미터로 테넌트 코드 받아서 각 다른 스레드이지만 set 을 해줌 
클로저 캡처 : 람다 안에서 바깥변수를 참조하는 것 자체를 클로저 캡처라고 한다. 
JVM 이 람다 객체 안에 tenantSchema(파라미터) 값을 복사해서 보관하고 있다. 
클로저캡처 : 람다가 바깥 변수를 기억하는 메커니즘                                                                                                                                      ㅜㅜㅜㅜ                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               


# Reactor Context
ThreadLocal이 스레드마다 별도 저장소라면,
Reactor Context는 리액티브 파이프라인마다 별도 저장소

리액티브 파이프라인 : 스레드가 바뀌어도 파이프라인을 따라 값이 함께 이동한다. 

.contextWrite {
Context.of("tenantCode", tenantSchema)  // 파이프라인에 값 저장
}
-> Reactor Context 에 값을 넣는 코드 

Project Reactor 의 Context 는 리액티브 파이프라인 전체에 걸쳐 데이터를 전달하는 불변 key-value 저장소 
ThreadLocal 의 리액티브 버전 


# ThreadLocal vs Reactor Context

- ThreadLocal : 특정 스레드에 데이터 바인딩
바인딩 대상이 스레드이고 전파 방향은 없고 불변성은 가변이며 읽기 방법은 get()
  [ ThreadLocal 방식 ]
      Thread-1: { tenantCode: "aify" }   ← 스레드 안에 저장
      Thread-2: { tenantCode: "kakao" }
      // Thread-1이 A 처리 끝내고 C 처리하면?
      Thread-1: { tenantCode: "naver" }  ← 덮어씀! A가 아직 안 끝났으면 문제!


- Reactor Context : 구독(Subscription)에 데이터 바인딩
바인딩 대상이 구독(Subscription)이고 전파 방향이 위(upstream) -> 아래로 전파 , 불변 (새 Context 생성)
읽기 방법은 Mono.deferContextual {ctx -> ...} 
  [ Reactor Context 방식 ]
    Thread-1 → SubscriptionA(0x001) → "aify"   처리 중
    Thread-1 → SubscriptionC(0x003) → "naver"  처리 중  ← 객체가 다르니까 안전!

# 현재 채팅 서비스에서 변경 
리액티브에서는 doOnNext, doOnComplete 등이 다른 스레드에서 실행될 수 있어서 ThreadLocal 이 깨진다. 
Context 는

.subscribeOn(Schedulers.boundedElastic()) -> 스레드가 바뀌면서 TenantContext 가 초기화됨
subscribe() 를 호출하면 Reactor 는 아래에서 위로 Subscriber 체인을 조립한다. 

# Reactor Context 스레드 변경시 파이프라인을 따라 살아있는 이유 
Context 가 스레드가 아니라 Subscriber 객체에 붙어있기 때문에 
Context 가 스레드를 타는 원리 

1. subscibe() 시점에 Subscriber 체인이 만들어진다. 

.contextWrite { Context.of("tenantCode", "aify") }
.subscribeOn(Schedulers.boundedElastic())
.subscribe()  ← 이 순간! -> Reactor 가 아래에서 위로 Subscriber 체인 조립
(doOnNext -> subscribeOn -> contextWrite -> 실제 Subscriber)

2. Context는 Subscriber 객체(힙 메모리) 안에 저장됨 

*ThreadLocal 은 스레드 안에 값을 저장하지만, Context 는 객체(힙) 안에 값을 저장한다.
Context 는 JVM 힙의 객체이고, 스레드는 그 객체의 참조를 전달받아 실행하는 것뿐이라 스레드가 바뀌어도 Context 는 유지된다.


Subscription 마다 Context 객체가 따로 만들어진다. 


# OnEach 
- Kotlin Flow 의 연산자 
- // doOnEach → Signal을 통해 Context 접근 가능!
  .doOnEach { signal ->
  val tenantCode = signal.contextView.getOrDefault("tenantCode", "")
  TenantContext.setCurrentTenant(tenantCode)  // Context에서 읽어서 세팅
  }
  - Signal은 onNext / onComplete / onError 모든 이벤트를 감싸는 객체이고, Context를 들고 있습니다.
    데이터 흐름에서 발생하는 모든 이벤트를 Signal이라 부름

      signal.isOnNext       → 데이터가 하나 왔을 때
      signal.isOnComplete   → 스트림이 정상 종료됐을 때
      signal.isOnError      → 에러가 발생했을 때
      signal.isOnSubscribe  → 구독이 시작됐을 때
    
      signal.contextView    → 현재 Context 접근 가능!  ← 핵심

- doOnEach 는 Signal 을 통해 Context 를 읽을 수 있는 유일한 side-effect 연산자
- contextWrite는 아래에 배치해야 위의 모든 operator에 전파됩니다.



# Reactor 흐름 

(위)
.doOnEach { }       ↑ Context 전파 (구독 시점)
.doOnNext { }       ↑
.doOnComplete { }   ↑
.doOnError { }      ↑
.onErrorReturn { }  ↑
.contextWrite { }   ← 여기서 Context 생성 후 위로 전파
.subscribeOn(...)   
.subscribe()        ← 여기서 구독 시작, 위로 올라가며 Context 전달
(아래)


(위)
.doOnEach { }       ↓ 데이터 흐름 (실행 시점)
.doOnNext { }       ↓
.doOnComplete { }   ↓
.doOnError { }      ↓
AI 서버 응답        → 데이터 생산
(아래)


1단계: subscribe() 호출
아래 → 위로 구독 체인 조립
contextWrite 가 Context 를 만들어 위로 전달
모든 operator 가 Context 를 들고 준비 완료

2단계: AI 서버가 데이터 전송 시작
위 → 아래로 데이터 흐름
doOnEach → doOnNext → doOnComplete 순서로 실행

설계 방식 

Publisher(AI서버) ──데이터──▶ operator ──▶ operator ──▶ Subscriber(내 코드)
│
Context ◀──────────────────
(구독 시점에 역방향으로 전파)




