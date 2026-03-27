# 전동적 방식 
1. ID + 비밀번호 
ID/PW 입력 → 서버에서 해시 비교 → 세션 쿠키 발급
비밀번호는 bcrypt/argon2로 해싱 후 DB 저장 (평문 저장 금지)
브루트포스, 피싱, 크리덴셜 스터핑에 취약
2. 이메일 + 비밀번호
이메일 주소를 ID로 사용하는 방식. 현재 웹 서비스의 표준에 가까움.
이메일/PW 입력 → 해시 비교 → JWT 또는 세션 발급
이메일이 전 세계 고유식별자 역할
이메일 인증(회원가입 시), 비밀번호 리셋 링크 전송 가능

3. OTP (일회용 비밀번호) -> 매번 새로운 번호 생성  (MFA)
   로그인할 때마다 새로운 6자리 숫자를 생성. 비밀번호와 함께 쓰면 MFA가 됨.
시간 기반 (Google Authenticator). 30초마다 갱신
카운터 기반. 버튼 누를 때마다 새 코드
문자로 전송. 편리하지만 SIM 스와핑 공격에 취약



# 토큰 방식 

1. JWT (JSON Web Token)
   로그인 성공 시 서버가 서명된 JSON 토큰을 발급. 서버는 세션을 저장하지 않아도 됨.
구조 : Header.Payload.Signature 3파트로 구성
검증 : 서버 개인키로 서명 → 공개키로 검증. DB 조회 불필요
저장 : localStorage 또는 HttpOnly 쿠키 (후자가 더 안전)
만료 : access token 15분 + refresh token 7일 패턴이 일반적
   서버 무상태(stateless)
   마이크로서비스에 적합
   토큰 즉시 무효화 어려움
   payload 크기 제한

2. 세션 쿠키
   로그인 시 서버가 세션 ID를 생성·저장하고 쿠키로 클라이언트에 전달. 전통적인 방식.
    동작 : 로그인 → 서버 세션 DB에 저장 → session_id 쿠키 발급
    특징 : 서버에서 즉시 세션 무효화 가능 (로그아웃 완전 처리) 
    설정 : HttpOnly + Secure + SameSite=Strict 필수

즉시 로그아웃 가능
XSS 공격으로 토큰 탈취 어려움
서버 메모리/DB 부담
수평 확장 시 공유 세션 필요


3. OAuth 2.0
사용자가 자신의 계정 정보를 공유하지 않고 특정 권한만 위임하는 표준 프로토콜.
   핵심 개념
   '내 Google 캘린더를 이 앱이 읽을 수 있게 허용'
   토큰 종류
   Access Token (짧은 유효기간) + Refresh Token (긴 유효기간)
    그랜트 타입 : Authorization Code, Client Credentials, Device Flow 등
OAuth는 인증(Authentication)이 아닌 인가(Authorization) 프로토콜
   비밀번호 공유 불필요
   세밀한 권한 설정
   구현 복잡
   잘못 구현하면 보안 취약



# SSO / 연동

SSO 프로토콜 레이어 : Idp <-> SP 간 신원 교환 방식 
어떻게 인증 정보를 전달하는지 
* Idp 와 SP 
    Idp : 신원 제공자 : 너가 누구인지 증명해주는 주체, 사용자의 신원(ID) 을 관리하고 인증해주는 서비스 
    로그인 정보를 가지고 있고, 인증 결과를 다른 서비스에 전달
    Google (구글 계정으로 로그인)
    Microsoft Azure AD
    Okta
    Keycloak
    SP (Service Provider) : 서비스 제공자 
    실제로 사용하려는 서비스 
    사용자가 실제로 이용하는 앱/서비스 
    직접 로그인을 처리하지 않고 IDP 에게 인증을 위임 
    예시 : Slack, Notion, Salesforce, 사내 시스템, ERP
  assertion : IDP 가 SP 에게 건네주는 신원 증명서 
    예시 : <saml:Assertion>

  <!-- 누가 발급했나 -->
  <saml:Issuer>https://idp.company.com</saml:Issuer>

  <!-- 누구에 대한 증명서인가 -->
  <saml:Subject>
  <saml:NameID>user@company.com</saml:NameID>
  </saml:Subject>

  <!-- 언제까지 유효한가 -->
  <saml:Conditions NotBefore="2024-01-01" NotOnOrAfter="2024-01-01T01:00:00"/>

  <!-- 이 사람의 속성(Claim)들 -->
  <saml:AttributeStatement>
  <saml:Attribute Name="email">user@company.com</saml:Attribute>
  <saml:Attribute Name="role">admin</saml:Attribute>
  <saml:Attribute Name="department">개발팀</saml:Attribute>
  </saml:AttributeStatement>

  <!-- 위조 방지 디지털 서명 -->
  <ds:Signature>...</ds:Signature>

</saml:Assertion>

* SAML : IDP↔SP 간 인증 정보 교환 표준 (XML 기반 인증/인가 데이터 교환 표준)
* OAuth 2.0 / OIDC : 현대적인 인증 방식
* SSO(Single Sign-On) 
    SSO 에는 두가지 시작 방식이 있다. 
  1. SP-Initiated SSO : 사용자가 SP(서비스)에 먼저 접근하는 방식
     ① 사용자 → SP 접근 시도  (아직 로그인 안 됨)
     ② SP → IDP에 "인증 요청"  (SP가 먼저 IDP에 요청)
     ③ IDP → 사용자에게 로그인 페이지 제공
     ④ 사용자 → 로그인 (ID/PW 입력)
     ⑤ IDP → SP에 토큰/Assertion 발급
     ⑥ SP → 사용자 접근 허용
    예시 : 슬랙 접속 → Google 로그인으로 리다이렉트
     ① 사용자가 slack.com 접속
     ② 슬랙 서버가 "이 사람 세션 없네?" 감지
     ③ 슬랙 서버 → 구글에 자동으로 "이 사람 인증해줘" 요청 (사용자 모르게)
     ④ 브라우저가 구글 로그인 페이지로 리다이렉트
     ⑤ 사용자가 구글에 로그인
     ⑥ 구글 → 슬랙에 "인증 완료" 토큰 전달
     ⑦ 슬랙 접근 허용
  -> SP 가 인증 안 된 사용자를 감지하고 IDP 에 먼저 인증을 요청하는 구조라서, 로그인 페이지보다 인증요청이 먼저 온다. 
  2. IDP-Initiated SSO 
    사용자가 IDP(예: 사내 포털)에 먼저 로그인 하는 방식
     ① 사용자 → IDP에서 먼저 로그인
     ② IDP → 사용자에게 서비스 목록 제공
     ③ 사용자 → 서비스 선택
     ④ IDP → SP에 토큰/Assertion 전달
     ⑤ SP → 접근 허용
    예시 : 사내 포털 로그인 → Jira/Confluence 바로 접근 

     * SSO 구현 방식 3가지 
       1. 우리가 Idp 가 되는 경우 (자체 구축) : 우리 서비스가 직접 인증을 발급하는 방식
          프로토콜OAuth 2.0 + OIDC (OpenID Connect)
          주요 라이브러리Auth0, Keycloak, AWS Cognito
          적합한 경우고객사가 별도 IdP 없는 중소기업일 때
          장점우리가 모든 인증 데이터 통제
       2. 고객사 IdP와 페더레이션 (엔터프라이즈 핵심) 
          이미 자체 Idp(Microsoft AD, Okta 등)를 갖고 있습니다. 우리 서비스가 그걸 신뢰하는 구조
          [고객사 직원]
          ↓
          [고객사 IdP] ── Microsoft Azure AD
          ── Okta
          ── Google Workspace
          ↓ SAML 2.0 또는 OIDC로 우리 서비스에 assertion 전달
          [우리 서비스]
          ↓ 인증 완료, 세션 생성

           프로토콜SAML 2.0 (기업 레거시에 많음) 또는 OIDC
           장점고객사 직원이 기존 회사 계정으로 바로 로그인 가능
           단점고객사마다 연동 설정 필요 (온보딩 공수)
       3. 소셜 로그인 (경량)
          Google, Microsoft 계정으로 로그인하는 방식. SMB 고객이나 초기 MVP에 적합합니다.
            
# 각 AI 서비스의 SSO 엔터프라이즈 지원 현황 
OpenAI Enterprise

SAML 2.0 기반 SSO 공식 지원
Azure AD, Okta 등과 연동 가능
단, 이건 ChatGPT 웹서비스용 SSO → 우리가 API 프록시 방식을 쓰면 이 SSO는 불필요
우리 포털에 SSO로 로그인하면, OpenAI 쪽은 우리 API Key로만 호출하면 됨

Google Workspace / Vertex AI

Google Workspace 자체가 IdP 역할 가능
Vertex AI는 Google Cloud IAM으로 권한 관리
BigQuery도 같은 IAM으로 통제 → GCP 쓴다면 IAM 하나로 전부 묶을 수 있어서 매우 유리

Anthropic Enterprise

SAML SSO 지원
마찬가지로 API 프록시 방식이면 우리 포털 SSO만 있으면 됨


1. SAML 2.0
   XML 기반 SSO 표준. 기업 환경에서 IdP와 서비스 간 인증을 연결할 때 사용.
사용처 : Azure AD, Okta 등 기업 IdP와 연동
흐름 : 사용자 → SP → IdP → 로그인 → Assertion 발급 → SP 접근
Assertion : XML 서명된 인증 증명서. 사용자 정보 + 권한 포함

엔터프라이즈 표준
강력한 서명 검증
XML 복잡도 높음
모바일 구현 어려움

5. OIDC (OpenID Connect) OAuth 2.0 위의 인증 레이어를 추가한 현대적인 프로토콜

    
   OAuth 2.0에 인증 기능을 추가한 현대적 표준. JWT ID Token으로 사용자 신원 증명.
    SAML 과 차이 : JSON 기반, 모바일 친화적, 구현 훨씬 간단
    ID Token : JWT 형태, sub(사용자ID), email, name 등 클레임 포함
    사용처 : Google, Kakao 로그인 모두 OIDC 기반
    2안 적용 :
   신규 고객사, 스타트업, Google Workspace 고객사에 적합

    * OAuth2.0 vs OIDC 차이 
                    OAuth 2.0  /  OIDC
목적 :  권한 부여 (Authorization)  /  인증 (Authentication)
응답 :  Access Token             / Access Token + ID Token
사용자 정보  :               없음  /      ID Token (JWT) 포함

OIDC 인증 흐름 
사용자 → 앱에서 로그인 요청
앱 → IdP로 리다이렉트 (client_id, scope 포함)
사용자 → IdP에서 로그인
IdP → Authorization Code 발급 후 앱으로 리다이렉트
앱 → Code로 IdP에 토큰 요청
IdP → ID Token + Access Token 발급
앱 → ID Token 검증 후 로그인 처리

사용자
│
▼
[IdP] ──── 인증 프로토콜 ────▶ [SP / 앱]
SAML (XML)
또는
OIDC (JWT)






구현 간단
모바일·SPA 친화적
SAML만 지원하는 레거시 IdP 대응 불가

6. 소셜 로그인 : 카카오, 네이버, 구글, 애플 
내부적으로는 OAuth2.0 + IODC 를 사용
   글로벌
   Google, Apple, GitHub, Microsoft 계정
   Apple 특이점
   이메일 숨기기 옵션 제공. 앱스토어 앱은 Apple 로그인 필수




# 비밀번호 없는 
1. 매직 링크 : 이메일로 로그인 링크 전송
2. 이메일 OTP : 코드를 이메일로 전송 
3. 패스키(PassKey) : 차세대 비밀번호 대체 



# 생체/기기 
1. 지문/Face ID
2. 하드웨어 보안 키 : YubikEY 등 물리키 



# 위험 기반 
1. 위험 기반 인증 (RBA) : 행동 패턴 분석 후 추가 인증
2. IP/ 기기 화이트 리스트 : 허용된 환경에서만 접근 




# Session Cookie vs JWT 비교 

1. Session Cookie
서버가 상태를 저장하는 전통적 방식
   저장 위치
   서버 (DB 또는 Redis)
   클라이언트
   session_id 쿠키만 보유
   검증 방식
   매 요청마다 DB 조회
   로그아웃
   서버에서 즉시 삭제 → 완전 차단
2. JWT
   JWT는 반드시 HttpOnly 쿠키에 저장 -> localStorage 에 저장하면 XSS 공격으로 그대로 탈취된다. 쿠키도 JWT 도 아닌 HTTPOnly 쿠키에 JWT 를 담는 방식이 보안과 편의를 둘 다 잡는 패턴 


3. 참고 : 즉각적인 계정 차단이 비즈니스 필수 조건인 경우, 예를들어 금융 서비스에서 이상 거래 탐지 즉시 세션을 끊어야 하거나,. 
보안 사고 발생 시 모든 세션을 1초 내로 무효화해야하는 상황이라면 Redis 세션 + 세션 쿠키 조합이 더 적합하다. 
4. 
4.  상황별 선택 

금융, 의료 등 즉시 강제 로그아웃이 필수인 서비스
세션 쿠키
마이크로서비스, 여러 API 서버가 동일 인증을 공유해야 할 때
JWT
모바일 앱 + 웹 동시 지원 (쿠키 없는 환경)
JWT
단순 웹 서비스, 서버 1~2대, 빠른 개발이 우선
세션 쿠키
SSO / 외부 IdP (Azure AD, Okta) 연동
JWT
트래픽이 많고 서버를 수평 확장해야 하는 서비스
JWT
엔터프라이즈 포털 (2안 서비스처럼 복잡한 권한 구조)
JWT




2안으로 정해졌을때 빌링, 권한 데이터 접근을 통합 통제하는 핵심 인프라 






