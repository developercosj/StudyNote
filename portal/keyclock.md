#  Realm
독립된 인증 공간 
Realm 간 사용자/ 설정이 완전히 분리됨 

공통으로 가질 수 있음

Realm 안에 들어있는것 

- Users : 이 Realm 에 가입된 사용자 계정들 
Google SSO 로 로그인하면 자동으로 여기 등록 
- Clients - Realm 을 사용하는 앱 목록 (ex: llm-portal)
- Identity Providers : 외부 로그인 수단 Google OAuth 를 여기에 연결한다. 
- Roles : 권한 등급 예를들어 admin, user 같은 역할 정의 
- Sessions : 현재 로그인 중인 사용자 목록으로 강제 로그아웃도 여기서 할 수 있다. 
- Token  설정 : Access Token 만료시간, Refresh Token 유효 기간 등 


Aify 는 Realm 구성 방식 


1. 단일 Realm 

aiify-studio (Realm 하나)
├── customer1.com 사용자들
├── customer2.com 사용자들
└── customer3.com 사용자들
→ 도메인으로 고객사 구분 가능 (BE 코드에서 처리) 또는 그룹으로 가능
*** 로그인 시 JWT에 groups 클레임 자동 포함 → BE에서 읽으면 됨 


2. 고객사별 Realm 분리
   완전한 데이터 분리가 되지만, 고객사 추가할 때마다 Realm 하나씩 새로 설정
   realm-customer-a  →  customer1.com 전용
   realm-customer-b  →  customer2.com 전용
   realm-customer-c  →  customer3.com 전용


Realm 을 만들면 인증 URL 이 자동으로 생긴다. 

- aiify-studio Realm의 인증 URL
https://auth.aiify.com/realms/aiify-studio/protocol/openid-connect/auth
- 토큰 발급 URL
https://auth.aiify.com/realms/aiify-studio/protocol/openid-connect/token
- 로그아웃 URL
https://auth.aiify.com/realms/aiify-studio/protocol/openid-connect/logout
- OIDC 설정 자동 조회 (Spring Boot가 이걸 읽어서 나머지 URL 자동 설정)
https://auth.aiify.com/realms/aiify-studio/.well-known/openid-configuration

Realm 하나에서 Groups 또는 이메일 도메인으로 고객사를 구분할 수 있다. 
고객사가 추가돼도 Realm 설정은 건드릴 필요 없고, BE DB 에 고객사 정보만 추가하면 된다. 
만약 고객사의 강한 보안 요구사항이 있을시에 Realm 분리 고려 가능 

** 로그인 시 JWT 에 groups 클레임 자동 포함 -> BE 에서 읽으면 된다. 
-> Groups 으로만 구분하면 로그인할 때 JWT 토큰 안에 groups 정보가 담겨서 온다. 

ex) // JWT에서 Group 읽기
val groups = oidcUser.claims["groups"] as? List<*>
// → ["/마케팅팀"] 이런 식으로 들어있음 

# 유저 등록 (직접 vs API)

퇴사자 차단 : Admin API로 비활성화
- Google 계정을 삭제해도 Keyclock 세션이 살아있으면 계속 접근할 수 있어서, Keyclock 에서도 명시적으로 비활성화 필요 
- 신규 고객사 대량 등록Admin API로 CSV import
- Google 인증 성공하는 순간 Keycloak 이 자동으로 생성해준다. -> 첫 로그인 시 한번으로 유저 생성 + Group 배정이 동시에 끝난다. 
-> Google 인증이 되면 Keycloak User 가 자동생성이 되고 Group 은 없으며
  BE 처리
  도메인 확인
  Group 배정
  (자동 가능) 
group 배정을 자동으로 할 수는 있다. -> BE 가 이메일 도메인 보고 바로 배정 가능 


# 키 로테이션 (Key Rotation)

Keycloak은 보안을 위해 주기적으로 키를 교체할 수 있습니다.

새 개인키로 서명 → 새 공개키를 JWKS에 추가
kid 값으로 어떤 키로 서명됐는지 구분
서비스 서버는 kid를 보고 맞는 공개키로 검증

그래서 JWT Header에 kid가 있는 것


Keycloak:
1. payload 해시값 계산  →  "abc123..."
2. 개인키로 그 해시값을 변환  →  Signature 생성

서비스 서버:
1. 받은 payload 해시값 계산  →  "abc123..."
2. 공개키로 Signature를 역변환  →  "abc123..."
3. 둘이 일치?  →  ✅ Keycloak이 만든 토큰이 맞다!



# keyclock 기반 SSO 통합 전략 

- 원하는 형태

사용자
↓ 1회 로그인
Keycloak (중앙 IDP)
↓
우리 서비스 ──→ Gemini/Vertex AI
──→ 서비스 B        → 재로그인 없음 ✅
──→ 서비스 C


- 방법1. keyclock 을 중앙 IDP 로 세우고 각 서비스를 Client 로 등록 
  - 각 서비스를 keyclock 의 Client 로 등록
  - 사용자가 한 번 로그인하면 Keyclock 이 세션을 유지
  - 다른 서비스 접근 시 Keyclock 세션 확인 후 자동 토큰 발급
  - 프로토콜 : OIDC(OpenID Connect) 또는 SAML 


서비스 A 로그인 → Keycloak 세션 생성
서비스 B 접근 → Keycloak 세션 있음 → 자동 로그인 ✅
서비스 C 접근 → Keycloak 세션 있음 → 자동 로그인 ✅


** 아래는 구글 방법 
- 방법 2. Google Workspace 연동 서비스는 Google Token 전달
Gemini/Vertex AI 같은 Google 서비스 한정
Keycloak에서 Google SSO로 로그인한 경우,
Google Access Token을 Keycloak이 보관하고 있어요.
사용자 → Keycloak (Google SSO로 로그인)
↓ Google Access Token 발급됨
우리 서비스에서 해당 토큰을 꺼내서
Gemini/Vertex AI 호출 시 함께 전달

Keycloak의 Identity Brokering 기능 활용
Identity Providers → Google → Store Tokens: ON 설정
우리 서비스 백엔드에서 토큰 꺼내서 API 호출에 사용



- 원하는 방식 프로세스 정리 

사용자가 Keycloak ID/PW 로그인
↓
Keycloak → Google에 SAML로 "이 사람 인증됐어" 전달
↓
Google → "Keycloak 믿을게" (사전 등록 되어있으니까)
↓
Google 세션 자동 발급
↓
Gemini, Vertex AI 등 재로그인 없이 접근 ✅


- 사전작업 필요
  - Google Workspace 관리 콘솔에서 Keycloak을 SAML IDP로 등록 
  - Google Workspace 관리자Keycloak에서 Google Workspace를 Client로 등록Keycloak 관리자


# SAML 과 OIDC 프로토콜 

1. SAML (Security Assertion Markup Language)

- 특징 
  - XML 기반 인증/인가 프로토콜 
  - 주로 엔터프라이즈 환경 (사내시스템, B2B)
  - 레거시 시스템에서 많이 사용 

- 구성요소 

    Idp(Identity Provider) : 인증 담당, (Keyclock, Okta)
    SP (Service Provider) : 서비스 담당, (우리앱) 
    Assertion : 인증결과 XML 문서 (사용자 정보 담긴 xml)

- 흐름 (sp-iNITIATED)

  1. 사용자 → SP 접근 (우리 서비스)
  2. SP → SAML Request 생성 (XML) → IdP로 리다이렉트
  3. 사용자 → IdP에서 로그인
  4. IdP → SAML Assertion (XML) 생성 → SP로 POST
  5. SP → Assertion 검증 후 세션 발급


- 실제 SAML Assertion XML 
    
    <samlp:Response>
    <saml:Assertion>
    <saml:Subject>
    <saml:NameID>user@example.com</saml:NameID>
    </saml:Subject>
    <saml:AttributeStatement>
    <saml:Attribute Name="email">
    <saml:AttributeValue>user@example.com</saml:AttributeValue>
    </saml:Attribute>
    <saml:Attribute Name="role">
    <saml:AttributeValue>admin</saml:AttributeValue>
    </saml:Attribute>
    </saml:AttributeStatement>
    <saml:Conditions
    NotBefore="2024-01-01T00:00:00Z"
    NotOnOrAfter="2024-01-01T01:00:00Z"/>
    </saml:Assertion>
    </samlp:Response>


2. OIDC (Open ID Connect)

- 특징
  OAuth 2.0 위에 인증 레이어를 추가한 프로토콜
  JSON/JWT 기반 → 가볍고 현대적
  모바일, SPA, API 환경에 적합
  Google, Kakao, Naver 소셜 로그인이 다 이거예요


- 구성요소 

용어설명
OP (OpenID Provider) : 인증 서버 (Keycloak, Google)
RP (Relying Party) : 우리 서비스
ID Token : 사용자 정보 담긴 JWT
Access Token : API 접근용 토큰
Refresh Token : Access Token 재발급용


- 흐름 (Authorization Code Flow)

1. 사용자 → RP 접근
2. RP → IdP로 리다이렉트
   GET /auth?
   response_type=code
   &client_id=my-app
   &redirect_uri=https://myapp.com/callback
   &scope=openid email profile
   &state=random_string

3. 사용자 → IdP에서 로그인

4. IdP → RP로 리다이렉트 (Authorization Code 전달)
   GET /callback?code=abc123&state=random_string

5. RP → IdP에 Code로 토큰 요청 (백채널)
   POST /token
   {
   grant_type: authorization_code,
   code: abc123,
   client_id: my-app,
   client_secret: secret
   }

6. IdP → 토큰 응답
   {
   id_token: eyJ...,      ← 사용자 정보
   access_token: eyJ...,  ← API 접근용
   refresh_token: eyJ..., ← 재발급용
   expires_in: 3600
   }

7. RP → id_token 검증 후 세션 발급


- ID Token(JWT) 구조 

Header.Payload.Signature

# Payload 디코딩하면
{
"iss": "https://keycloak.mycompany.com",  // 발급자
"sub": "user-uuid-1234",                  // 사용자 고유ID
"aud": "my-app",                          // 대상 클라이언트
"exp": 1704067200,                        // 만료시간
"iat": 1704063600,                        // 발급시간
"email": "user@example.com",
"name": "홍길동",
"roles": ["admin", "user"]
}


- Keyclock 에서 실제 설정 
  - OIDC Client 등록
    Keycloak 관리콘솔
    → Clients → Create
    → Client ID: my-service
    → Client Protocol: openid-connect
    → Root URL: https://myservice.com
    → Valid Redirect URIs: https://myservice.com/callback
  - SAML Client 등록
    Keycloak 관리콘솔
    → Clients → Create
    → Client ID: https://myservice.com/saml
    → Client Protocol: saml
    → Valid Redirect URIs: https://myservice.com/saml/callback
  


- OIDC, OAuth, SAML 비교

  OAuth 2.0      = 인가만 함 (누군지 몰라)
  SAML           = 인증만 함
  OIDC           = OAuth 2.0 + 인증 추가
  = OAuth 2.0의 단점을 보완한 것



현재 Google Workspace 는 외부 IDP 연동 시 SAML 만 지원 OIDC 는 지원하지 않음 
-> Google 자체는 OIDC Provider 로 동작하지만, 외부 IDP 를 신뢰하는 것은 saml 만 가능 


- 전체 개발 체크리스트
  □ Keycloak 서버 구축 (Docker or 클라우드)
  □ Realm 생성
  □ 사용자 등록 or LDAP 연동
  □ 우리 서비스 OIDC Client 등록
  □ 프론트엔드 keycloak-js 연동
  □ 백엔드 토큰 검증 미들웨어 적용
  □ 외부 서비스별 OIDC/SAML 설정
  □ Google Workspace SAML 연동
  □ 로그아웃 처리
  □ 토큰 갱신 처리