# Reactive Programming 

정의 : 데이터 스트림과 변화 전파에 중심을 둔 프로그래밍 패러다임


1. 비동기 데이터 스트림 처리 
   - 시간에 따라 발생하는 이벤트나 데이터를 스트림으로 모델링

2. 변화 전파 (Propagation of Change)
   - 데이터가 변경되면 자동으로 관련된 모든 부분이 업데이트 
   - 선언적 방식으로 데이터 흐름을 정의 
   
3. 비동기/논블로킹 처리 
   - 블로킹 없이 동시에 여러 작업 처리
   - 리소스 효율적

특징 
    - Observable/Observer 패턴 
    - 함수형 프로그래밍 스타일 (Map, filter, reduce 등)
        * 맵, 필터, 리듀스는 데이터 흐름을 어떻게 바꿀 것인가에 대한 3가지 기본 연산 : 입력 -> filter -> map -> reduce -> 결과 
    - 백프레셔(BackPressure) 처리 
    - 에러 처리와 복구

자바/코틀린에서 사용 
    - RxJava, RxKotlin
    - Spring WebFlux(Project Reactor)
    - Kotlin Flow/Coroutines 




# Flux Mono 

1. Reactor 는 리액티브 프로그래밍을 위한 라이브러리 
2. Spring WebFlux 와 함께 비동기/논블로킹 처리를 지원한다. 
3. 여러개면 Flux, o or 1 이면 Mono, 안오면 Mono.empty(), 실패 : Mono.error() 
4. Mono
    
    - Future<User> : 값 하나 기다림, Mono<User> : 값 + 흐름 + 에러 + 완료 신호 
    - 0 개 또는 1개의 결과를 비동기로 처리 
    - 아직 오지 않은 결과( 0개 또는 1개 ) 를 표현하는 컨테이너 + 실행 파이프라인 
    - 단일 값 반환에 사용
    - Mono<T> 형태
    - Mono<Void> mono = Mono.empty(); : 값 없음 표현하며 검증만하고 끝내기등에서 사용한다. 
    - Webflux 는 Mono 값을 리턴하면 자동으로 비동기 응답이 된다. 
    - 모노를 쓰는 이유 : 비동기 + 논블로킹으로 서버 자원을 아끼기 위해  
    - 동시 요청이 많거나 I/O 가 대기가 긴 API 에서는 의미가 있음 
    - block() : 강제 스레드 멈춤 -> 테스트 코드, 배치, WebFlux 바깥 (CLI, main) 에서 사용 
    - Mono 스타일 : fun getUser(): Mono<User> 
    - suspend 스타일 : suspend fun getUser(): User 
    - WebFlux  
      - 요청 처리중 스레드를 점유하지 않음
    - 지연 평가 
    - 
5. Backpressure 개념과 onBackpressureBuffer() 
    - 정의 : 리액티브 스트림에서 생산자가 소비자보다 빠르게 데이터를 생성할 때 발생하는 압력
    - 소비자가 처리하지 못한 데이터가 쌓이면서 메모리 부족이나 성능 저하가 발생할 수 있다. 
    - outOfMemoryError 발생 가능 시스템 성능 저하 가능 
    - 백프레셔 처리 전략
      - onBackpressureBuffer() 
        - 버퍼에 데이터를 저장하고, 버퍼가 가득 차면 에러를 발생시키거나 대기한다. 
        - 소비자가 처리할 수 있을 때까지 대기
        - 버퍼 크기 제한 가능 (기본값: 무제한)
        - 메모리 사용량 증가 가능 
      - onBackpressureDrop() 
        - 버퍼가 가득 차면 새로운 데이터를 버립니다. 
      - onBackpressureLatest() 
        - 최신 데이터만 유지하고 이전 데이터는 버립니다. 
      - onBackpressureError() 
        - 백프레셔 발생시 즉시 에러 발생 
      - request() 기반 제어 
        - 소비자가 요청한 만큼만 생산자가 전송한다. 
      - tryEmitNext() 결과로 백프레셔 상태를 확인할 수 있습니다. 


# 웹소켓 

    - 웹소켓은 처음 한 번만 HTTP 헤더로 연결을 하고, 그 이후에는 HTTP 가 아니라 전용 연결 (Socket) 위에서 통신하기 때문에 요청마다 헤더를 다시 보낼 필요가 없다. 
    - 초기 HTTP 연결의 목적은 연결 전환(Upgrade) 이다. 
    - 하나의 TCP 연결 위에서 메시지만 주고 받음 
    - 단점
        - 연결 유지 = 메모리 + 소켓
        - 대량 접속 시 서버 부담
        - Redis Pub/Sub , 메시지 브로커, Sticky Session 과 같이 사용한다. 
    - 필수 구현 고려 
        - heartbeat(ping/pong) 
        - 재연결 로직
        - 마지막 이벤트 재전송 
    




# 서비스의 흐름 (SSE, WebSocket, Trigger 구조 정리)

    [외부 세계]
    └─ 메일 서버 / 외부 API / DB 변화
    ↓
    [서버 내부 이벤트 감지] : 서버가 주기적으로 외부 시스템에 물어봄 (이벤트를 만드는쪽)
    ├─ Polling 
    └─ IDLE : 이벤트 기반/ 서버가 연결을 유지하고 있다가 변화가 오면 즉시 통보 받음 
        - 거의 실시간, 트래픽 적음, 연결 관리는 필요, 서비와 네트워크에 의존, IDLE 끊어지면 Polling fallback 필수, heartbeat, reconnect 필요 
    ↓
    [Event 생성]
    ↓
    [Event Bus] : 이벤트를 만드는쪽과 이벤트를 보내는 쪽 사이 
    ├─ Flux<Event> (단일 서버)
    ├─ Redis Pub/Sub (멀티 서버)
    └─ Kafka (대규모/영속)
    ↓
    [전송 계층] : 이벤트를 보내는 쪽 
    ├─ SSE
    └─ WebSocket


## 서버 내부 : Polling(가장 단순) / IDLE

### Polling : 서버가 주기적으로 외부 시스템에 물어봄
    

    while (true) {
        sleep(30s)
        checkNewMail() 
    }

- 장점 
  - 구현 쉬움, 거의 모든 시스템에서 가능
- 단점
  - 지연 발생
  - 불필요한 요청 많음
  - 예시 : IMAP without IDLE, 외부 API 상태 확인, DB batch 체크 
  
    
### IDLE (이벤트 기반)
- 개념 : 서버가 연결을 유지하고 있다가 변화가 오면 즉시 통지 받음 
- 예시 : IMAP IDLE, DB Listen/notify 등 
  

    connect
    wait...
    (wait...)
    EVENT ! 

- 장점
  - 거의 실시간, 트래픽 적음
- 단점 
  - 연결, 관리 필요 , 서버/네트워크 의존 
- 주의
  - IDLE 끊어지면 Polling fallback 필수
  - heartbeat/reconnect 필요 



## Event Bus (Flux, Redis Pub/Sub, Kafka )

| 구분           | **Flux (단일 서버)**     | **Redis Pub/Sub** | **Apache Kafka** |
| ------------ | -------------------- | ----------------- | ---------------- |
| 서버 범위        | 단일 JVM               | 멀티 서버             | 멀티 서버            |
| 네트워크         | ❌ 없음 (in-process)    | ⭕ TCP             | ⭕ TCP            |
| 이벤트 영속성      | ❌ 없음                 | ❌ 없음              | ⭕ 있음             |
| 재시작 후 복구     | ❌                    | ❌                 | ⭕                |
| 소비자 그룹       | ❌                    | ❌                 | ⭕                |
| Backpressure | ⭕ (Reactive Streams) | ❌                 | ⭕                |
| 순서 보장        | 코드에 따라               | 채널 단위             | 파티션 단위           |
| 지연(latency)  | **최저 (ns~μs)**       | 낮음 (ms)           | 상대적으로 높음         |
| 운영 복잡도       | ⭐                    | ⭐⭐                | ⭐⭐⭐⭐⭐            |
| 주 용도         | 단일 서버 Event Flow     | 서버 간 실시간 fan-out  | 대규모 이벤트 파이프라인    |

    [Single Server]
    Business Event
    ↓
    Flux
    ↓
    SSE / WebSocket
    
    [Multi Server - Real-time]
    Business Event
    ↓
    Redis Pub/Sub
    ↓
    Flux
    ↓
    Client
    
    [Event-driven System]
    Business Event
    ↓
    Kafka
    ↓
    Flux
    ↓
    Client / DB / API
    




### Flux<Event> (단일 서버 Event Bus 쓰일때 )

- 개념 : Reactive Streams 스펙을 구현한 Publisher 타입으로 계약
- 예시 : JVM 내부 Event Stream, 네트워크를 타는 스트림(SSE/Websocket) 등
- Flux<T> = 
  - Reactive Streams Publisher<T> 의 구현체이다. 0 ~ N 개의 비동기 데이터 스트림, backpressure 지원 pull 기반 (subscriber 가 demant 요청)
  - 시간에 따라 여러 개의 값을 비동기로 흘려보내는 Reactive Stream (Publisher)
- JVM 내부에서 쓰는 Reactive Stream 일때 
    val eventSink = Sink.many().multicast().onBackpressureBuffer<Event>()
    val eventFlux: Flux<Event> = eventSink.asFlux() 
- 메모리 기반
- 매우 빠름
- 서버 1대 
- scale 필요 없음
- WebFlux 기반
- 서버 죽으면 이벤트 사라짐
- 서버 여러대면 공유 불가
- 사용 : 단일서버, WebSocket/ SSE 브로드 캐스트 
- 서비스 내부 이벤트 전달 
- n8n trigger 내부 이벤트 흐름 
- Controller → Service → Flux → SSE/WebSocket 형태에서 사용 
- 플럭스는 Event Bus 가 아니라 Event Flow 추상화이다.
- 실무에서 쓰임
  - SSE / Websocket 스트림
  - 서버 내부 이벤트 전달
  - Redis/Kafka 이벤트를 클라이언트로 흘려보내는 마지막 단계 
  - 
- 단일 서버에서는 Event Bus 처럼 쓸 수 있지만 멀티 서버에서는 Redis 나 kafka 가 필요하다.

      Kafka / Redis : 이벤트의 "저장과 전달"
      Flux          : 이벤트의 "흐름과 소비 제어"
      SSE/WebSocket : 이벤트의 "전송"
      Flux        : 이벤트를 "흐르게" 만드는 추상화 (in-process) 저장 X 전달 O 
      Redis Pub/Sub : 이벤트를 "즉시 전달"하는 브로커 (휘발성)
      Kafka       : 이벤트를 "기록하고 재생"하는 로그 시스템

### Redis Pub/Sub 

- 서버 여러대, 지금 발생한 이벤트만 중요, 알림,상태 변경 브로드 캐스트 
- server A → Redis → Server B/C/D → Flux → Client
- 메시지를 저장하지 않는 "즉시 전달용" Event Bus 
- publish -> subscribe(여러 서버)
- subscriber 없으면 메시지 소멸
- Redis 가 Server 간 지속 연결 확인 (ChannelTopic 에 지정된) : 서버와 Redis 사이에 하나의 TCP 연결을 계속 열고 있는 상태로 유지하고,
Redis 가 그 연결로 메시지를 밀어준다. -> x 프로토콜(RESP) 로 (SUBSCRIBE email-events) -> Redis 가 이 연결을 Subsciber 목록에 등록, 이후 이 연결은 차단(blocked) 상태
여기서 blocked 상태란 메시지가 올 때까지 Redis 서버 쪽에서 대기 상태로 두기 때문에 CPU 사용하지 않으며 Polling 등도 하지 않고 비시 루프 X, 커널수준 epoll/kqueue 로 대기 
- 메시지가 Publish 가 되었을때(PUBLISH email-events payload) Redis 내부
  - email-events 채널의 subsciber connection 목록 조회 
  - 각 TCP 연결로 payload 를 즉시 write
  - Server 쪽 socket 에 데이터 도착
  - 서버 Redis client 라이브러리가 읽음
  - 콜백 실행 
  - push 
- 매우 빠름, 구조 단순 
- 유실 가능성
- 재처리 X 
- fan-out O 
- backpressure X 
- Flux 를 붙이는 이유는 Redis 는 push-only, 느린 클라이언트 제어 불가, Flux 로 속도 조절 & 보호 
- 금융, 주문, 정산 이벤트에서는 사용하지 않는다. 유실되면 안되는 데이터에는 사용하지 않는다. 

### Kafka

- 이벤트가 비즈니스 데이터
- 이벤트 로그 시스템 
- 이벤트를 로그로 저장하고 여러 Consumer 가 각자 읽는 시스템 
- 재처리, 감사, 이력 필요
- 대규모 트래픽 
- 순서 보장 단위
- 병렬 처리 가능
- Offset : 재처리 가능, 어디까지 읽었는지 기록 
- 메시지 저장, 유실 방지, 재처리 가능, backpressure 는 애플리케이션 레벨 
- Kafka 이벤트를 그대로 클라이언트에 보내면 안되며 반드시 필터링, 축약, 샘플링 필요 
- 주문, 결제, 로그 시 사용 
- 재처리 필요한 시스템에서 사용 
- 이벤트 소싱 
- kafka 에서 backpressure 를 직접 구현하는 방법들
  - poll size 줄이기 (한번에 덜 가져오기)
  - consumer pause/resume 처리중이면 중단
  - 내부 큐 제한 (메모리 보호)
  - Flux 로 감싸기 (downstream 속도 맞춤)
- 이벤트 자체가 Source of Truth 
- 이벤트 발생 -> Kafka (디스크에 append) -> Consumer 가 나중에 읽음 
- Producer → Kafka → Consumer Group → DB / API 

      Producer
      ↓
      :contentReference[oaicite:1]{index=1}
      ↓
      Topic (Partition)
      ↓
      Consumer Group




WebSocket 의 IDLE 관리 
- IDLE  타임 아웃 설정 
- ex : IMAP IDLE, DB Listen, notify 등이 있다. 
- Idle 끊어지면 Polling fallback 필수 
- heartbeat/recoonect 필요 
- IDLE 이란 서버가 연결을 유지하고 있다가 변화가 오면 즉시 통지 받음 
- Heartbeat (Ping/Pong) : Websocket 에서 IDLE 상태를 관리하는 핵심 매커니즘 



* Event Bus : 이벤트를 여러 컴포넌트에게 전달하기 위한 전달 계층 
* fan-out : 이벤트가 저장소에 쌓였다가 나중에 처리 
* 트리거 기반 자동화 (중요한 업무) 에는 Kafka 사용 할것 

[Gmail/IMAP Server]
↓ Polling (주기적 확인)
[IMAP Polling Service]
↓ 새 이메일 감지
[Database] ← Event 저장
↓
[Redis Pub/Sub] ← Publish
↓
[Redis Subscriber]
↓ Subscribe
[Flux<Event>] ← Reactive Stream
↓
[WebSocket] ← Server Push
↓
[Client Browser/App]
