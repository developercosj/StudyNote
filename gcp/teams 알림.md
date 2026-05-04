① Cloud Build → 이벤트 발생
코드가 빌드되고 나면 Cloud Build가 자동으로 빌드 결과(SUCCESS, FAILURE 등)를 내부적으로 발행해요. 별도 설정 없이 자동으로 발생.
② Pub/Sub → 메시지 전달
Cloud Build는 cloud-builds라는 Pub/Sub 토픽에 메시지를 전송. 
메시지 안에는 빌드 ID, 상태, 브랜치명 등이 base64로 인코딩되어 있음. 
Pub/Sub은 이 메시지를 구독자(Cloud Functions)에게 전달하는 우체통 역할이에요.
③ Cloud Functions → 메시지 처리
Pub/Sub 메시지가 오면 자동으로 Cloud Functions가 실행. 
여기서 메시지를 디코딩하고, 성공/실패만 필터링해서 Teams에 보낼 형식으로 가공해요.
④ Teams Webhook → 알림 수신
가공된 메시지를 Teams Incoming Webhook URL로 HTTP POST 요청을 보내면 채팅에 알림이 떠요.




* GCP 프로젝트 하나에 cloud-builds 토픽도 하나이다. 
* 서비스 A 빌드 ──┐
  서비스 B 빌드 ──┼──→ [cloud-builds 토픽 하나] ──→ Cloud Functions
  서비스 C 빌드 ──┘
-> 필터링이 필요하다. 

