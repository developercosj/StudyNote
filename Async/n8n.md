- IMap Email Node 에서 Polling 으로 이벤트 처리를 할때 
이벤트 처리 중 서버 다운되는 경우가 있고, UNSEEN 인 메일은 한번 읽으면 끝이기 때문에 
중복 처리나 유실 발생이 가능해서 재처리 불가능하여 저장이 필요 

- 저장해야 하는것 : UID(IMAP), 계정 ID, 처리 상태, 수신 시각, Message-ID 는 저장 해야한다. 
- 프로세스 설계
  IMAP Polling 
  UID 추출
  이미 처리되면 SKIP 
  Event 저장 (PENDING)
  Workflow 실행
  성공 -> DONE, 
  실패 -> Retry 
 
- 저장 하는 곳
    1. 1순위 : RDB (PostgreSQL)
       -> 가장 안전, 트랜잭션 보장, 조회/정리 쉬움 
    2. Redis 보조 용도 
       - 중복 방지 캐시, 짧은 TTL, RDB 와 병행, 단독 사용은 위험 

## 기존 구조 생각해보기 

    IMAP Polling
    ↓
    Event 저장 (DB)
    ↓
    Redis Publish
    ↓
    Redis Subscribe
    ↓
    Flux<Event>
    ↓
    (SSE ) (->) WebSocket


## 중복 실행 방지 

이벤트 -> 트리거 -> 워크플로우 
- 네트워크 재시작
- 서버 재시작
- Consumer 재할당
- 목표 : 중복 실행되더라도 결과가 한번만 반영되게 할것 / 멱등성 Idempotency :같은 이벤트를 여러 번 처리해도 결과는 동일 


### Redis Streams : 저장되는 이벤트 로그, Redis 에 저장되는 이벤트 로그 자료구조 이벤트가 Stream 에 append, ID 기반으로 순서 관리 
- Redis Stream 이 필요한 순간 : 서버 여러 대, Polling 결과를 여러 워커가 처리, 매일 이벤트 재처리 필요, 트리거 처리 중 서버 죽어도 안전해야함 
- 트리거 중복 실행 방지 (Consumer Group) ? 
- Polling 한 결과를 안전하게 흘려보내는 내부 이벤트 버스가 필요할때 



결론 : Polling 으로 Imap 이메일 구조를 가져오고, Redis Streams 으로 안전하게 데이트 흐름 만들기, Websocket 으로 양방향 통신(Flux)




** 
- Websocket 전송 자체에 timeout 걸기 
- 느린 브라우저는 끊기
- 전체 시스템 보호하기 


- IDLE 

- IMAP IDLE 은 아무것도 안하는 TCP 연결 1개를 유지하는 것으로 Polling 처럼 계속 두드리는 구조는 아니다. 
- 서버 - TCP 연결 - IMAP 서버 (IDLE 명령 - 대기 상태 ) -> 추가 패킷 전송 X CPU 사용 0 에 수렴, 메모리 소켓 1개 , OS 레벨에서 block 상태 
- IDLE 을 안쓰는 이유 : Gmail 은 IDLE 지원, 장시간 연결 관리가 필요해서 구현 난이도가 있음 
- 잘못된 패턴 (Workflow 실행마다 IDLE 연결) 으로 하면 부하가 늘기 때문에 메일 박스당 IDLE 연결은 1개여야 한다. 
- RFC 권장 29분 이내 DONE, 재 IDLE 
- 추가 리스트
  - 메일 박스당 IDLE 1개
  - UID 기반 FETCH
  - 29 분 재연결
  - IDLE 실패 시 Polling 폴백
  - 서버 종료 시 DONE 

- 주의점
  - workflow 마다 IDLE, executionm 마다 IDLE 을 연결하면 연결 폭탄되고, 메일 서버가 차단될 수 있다.
  - uid 는 저장하여 중복처리가 안되도록 해야한다. 
  

- IDLE Listener 유휴 리스너는 메일 왔음 감지 
- Polling : UID 기반 메일 조회 
- Web Socket : 웹소켓 화면 전달 
- Event Publisher 이벤트 게시자 : Flux 발행 
- 무조건 Credential 별로 IDLE 한개씩 연결해야하는데... 


IDLE : 실시간성
Polling : 안정성
UID : 정확성
FLUX : 실시간 UI 



IDLE 대기
↓
EXISTS 수신
↓
IDLE 종료
↓
Polling Worker 실행
↓
UID > lastUid FETCH
↓
WorkflowEvent 생성
↓
Flux publish
↓
WebSocket 전송
↓
다시 IDLER



- 우선 n8n 같은 경우는 Webhook 노드 (IDLE 상태) 가 있고 일반 Polling 노드가 있다. 
- IMAP node 같은 경우는 일반 Polling 노드이다. 









