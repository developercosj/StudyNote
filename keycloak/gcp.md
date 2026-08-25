순차 롤링 재시작 : 서비스는 안 죽는다. 세션은 조건부 

health check 용 9000 번 이 없다. 
keycloak 26.5.4 는 /health/ready 를 관리포트 9000 에서 서빙하는데 (실측: 9000 -> 200, 8080 -> 404) 
현재 방화벽은 tcp:8080 만 열려 있어서 두 VM 이 LB 에서 UNHEALTHY 

처리해야 할 명령어 
gcloud compute firewall-rules create allow-lb-healthcheck-9000 --project=aifybusiness-ops --network=aify-ops-vpc --direction=INGRESS --action=allow --rules=tcp:9000 --source-ranges=130.211.0.0/22,35.191.0.0/16 --target-tags=keycloak-ops --description="LB health check to Keycloak management port"


* VM 입장에서 SSH 접속의 출처는 항상 IAP 대역이다. (35.235.240.0/20
* 
* 
* 
)



무중단을 진짜로 보장하는것은 트래픽을 받고 있다이기 떄문에 
서버가 살아있다가 아닌 LB 설정(HC 포트/경로, 방화벽, named port)이 잘 되어 있고, 트래픽을 잘 받고 있는 상태여야 한다. 


목표 : HEALTHY 처리 


방화벽 규칙 생성은 compute.securityAdmin 또는 compute.networkAdmin이 필요합니다. 지금 개인 계정에 있는 3개(loadBalancerAdmin, osAdminLogin, iap.tunnelResourceAccessor)에는 포함되지 않습니다. 그룹으로 상속받았을 수도 있으니 위 명령을 그냥 돌려보면 알 수 있고, 권한 오류가 나면 인증팀에 요청해야 합니다.


# LB 설정 3가지 

       구글 LB
         │
         ├── "너 살아있어?"     → HC 포트/경로 가 결정  ← ①
         │                        방화벽이 허용해야 도달 ← ②
         │
         └── "이 사용자 받아"   → named port 가 결정    ← ③
                                  방화벽이 허용해야 도달
         ↓
    keycloak-ops-vm-1
    8080 듣고 있음 (서비스)
    9000 듣고 있음 (관리/health)


keycloak-ops-hc 의 현재설정은 
port : 8080 
경로 : 	/health/ready	이 주소로 HTTP GET

원인 : Keycloak 25 부터 health metrics 같은 운영 정보를 서비스 포트에 노출하지 않으려고 관리포트(9000) 로 분리함 
현재는 8080 /health/ready 주소로 되어 있어서 -> 9000 /health/ready 로 변경 필요 

추가 처리 
현재 allow-lb-healthcheck 규칙은 tcp:8080만 허용합니다. 그래서 ①에서 HC를 9000으로 바꾸면 → 방화벽에 막혀서 여전히 실패합니다.
-> 따라서 9000 은 관리포트라서 HC 대역으로만 제한한다. 


① HC 포트/경로	"살아있나?" 묻는 문	8080 /health/ready	KC26은 8080에 없음(404)	9000 /health/ready
② 방화벽	그 문에 도달 가능한지	tcp:8080만 허용	9000이 막힘	tcp:9000 추가
③ named port	실 트래픽이 갈 문	미설정 → 80	Keycloak은 8080	http:8080




┌─────────────────────────────────────────────────────────────┐
│  [경로 A] 실제 로그인 트래픽                                    │
└─────────────────────────────────────────────────────────────┘

①  사용자 브라우저
│  "로그인 화면 줘"  (보냄)
↓
②  login.aify.ai  →  DNS  →  LB IP
│
↓
③  구글 LB
│  "어느 VM에?"  →  HEALTHY인 VM 중에서 고름
│  "몇 번 포트로?" →  portName: http 를 조회
│                      ↓
│              keycloak-ops-ig-a 의 namedPorts
│              "http = 8080"        ← ③번 작업으로 채울 것
│                      ↓
│  보냄 → 10.10.0.2 : 8080
↓
④  keycloak-ops-vm-1 : 8080     ← Keycloak이 받음
│
↓
⑤  Keycloak이 로그인 화면 응답  (되돌려 보냄)


┌─────────────────────────────────────────────────────────────┐
│  [경로 B] 헬스체크 (사용자와 무관, 10초마다 자동)                 │
└─────────────────────────────────────────────────────────────┘

①  구글 LB 인프라
│  keycloak-ops-hc 규칙을 읽음
│  "포트 9000, 경로 /health/ready"     ← ①번 작업으로 바꾼 값
│
│  방화벽 통과?  allow-lb-healthcheck  tcp:8080,9000   ← ②번 작업
│               소스 130.211.0.0/22, 35.191.0.0/16    ✅
│
│  보냄 → 10.10.0.2 : 9000  "GET /health/ready"
↓
②  keycloak-ops-vm-1 : 9000     ← Keycloak이 받음
│
↓
③  200 응답  (되돌려 보냄)
│
↓
④  LB: "2회 연속 200 → HEALTHY"
│
↓
경로 A에서 이 VM을 트래픽 대상에 포함



로드밸런서	전체 묶음	아무것도 (프런트엔드 없음)
keycloak-ops-backend	백엔드 서비스 — "어디로 보낼지" 설정	portName: http 읽기만
keycloak-ops-ig-a/b	인스턴스 그룹 — VM 묶음	http:8080 추가 ← ③
keycloak-ops-vm-1/2	실제 서버	안 건드림
keycloak-ops-hc	헬스체크 규칙	포트 9000으로 변경 ← ① 완료


