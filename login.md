


Keycloak = 이 SSO 서버를 우리 회사가 직접 운영하는 것
Firebase Auth = SSO 서버를 Google이 대신 운영해주는 것
Google SSO = "Google 계정으로 로그인" 버튼 — Keycloak이나 Firebase에 연결하는 로그인 수단


[직접 연동]
Google SSO 버튼 → Google 서버 → JWT 토큰 → 우리 백엔드
(Google 공개키로 검증)

[Keycloak 경유]
Google SSO 버튼 → Keycloak → Google 서버 → Keycloak → 우리 백엔드
(중간에 껴있음)


# Keyclock 은 왜 쓰냐? 

상황: 로그인 수단이 여러 개일 때

Google SSO만 쓰면:
백엔드가 Google 토큰 검증 로직 따로 작성
카카오 추가하면 → 카카오 검증 로직 추가
사내 ID/PW 추가하면 → 자체 인증 로직 추가
GitHub 추가하면 → GitHub 검증 로직 추가
↓
백엔드가 복잡해짐

Keycloak 쓰면:
Google, 카카오, GitHub, ID/PW 전부 Keycloak이 처리
백엔드는 Keycloak 토큰 하나만 검증하면 끝


✅ 맞음 — ID/PW 로그인은 Keycloak 방식 중 하나

Keycloak이 제공하는 로그인 수단들:
├── ID/PW 직접 입력    ← Keycloak 자체 DB 계정
├── Google SSO        ← Google에 인증 위임
├── 카카오 로그인      ← 카카오에 인증 위임
├── GitHub 로그인      ← GitHub에 인증 위임
└── LDAP/AD 연동      ← 사내 디렉토리 연동

전부 Keycloak 하나로 관리, 백엔드엔 토큰 하나로 통일


[Google SSO 버튼]     → 구글 서버로 이동 → 구글이 인증 → 토큰 반환
[Keycloak ID/PW 입력] → Keycloak 서버가 인증 → 토큰 반환

둘 다 최종적으로 "JWT 토큰"을 우리 백엔드에 전달하는 건 동일
차이는 "누가 인증하느냐"


백엔드 입장에서는 토큰만 보기 떄문에 둘을 구분할 필요가 없음
Google 인증 완료 후 Keycloak이 추가로 하는 것:

Google JWT (구글 발급)
↓
Keycloak 처리
├── 자체 DB에 유저 등록/동기화
├── 사내 역할 부여 (admin, manager, user...)
├── 다른 로그인 수단 계정과 연결
│   ex) 같은 이메일의 ID/PW 계정과 Google 계정을 하나로
└── 세션 중앙 관리 (강제 로그아웃 등)
↓
Keycloak JWT (Keycloak 발급) → 백엔드

방식 1 (Google 단독):
백엔드가 Google 공개키로 검증
토큰 안에 역할 정보 없음 → 별도 DB 조회 필요
Google 계정 = 우리 서비스 계정 (1:1)

방식 2 (Google + Keycloak):
백엔드가 Keycloak 공개키로 검증 (Google 몰라도 됨)
토큰 안에 역할 정보 있음 → realm_access.roles
Google 계정이 Keycloak 계정으로 흡수됨



Google SSO + Keycloak = 인증(Authentication)은 Google, 인가·관리(Authorization·Management)는 Keycloak
구글은 "이 사람이 구글 계정 주인 맞다" 만 확인해주고
Keycloak이 "그럼 우리 서비스에서 이 사람은 admin이야" 를 결정




앱을 처음 생성할때 Google Cloud Vertex AI Search 에서 Gemini Enterprise 앱을 새로 만드는 과정 


2. 핵심 구성요소
   용어역할예시
- IdP (Identity Provider) : 신원을 증명해주는 주체 
Okta, Azure AD, Google Workspace
- SP (Service Provider) : 서비스를 제공하는 앱
Slack, GitHub, Claude, Notion
- Token/Assertion : 신원 증명서 
(디지털)SAML Assertion, JWT Token


# 주요 IdP 종류
☁️ Microsoft Azure AD (Entra ID)

귀사처럼 Microsoft 365 사용 기업에서 가장 많이 사용
Windows 환경과 깊이 통합
@회사도메인.com 계정으로 로그인

🔐 Okta

전용 IdP 서비스 중 가장 유명
어떤 앱이든 연동 쉬움
중소~대기업에서 광범위하게 사용

🟡 Google Workspace

Google 계정(@회사.com)이 IdP 역할
Google 생태계 중심 기업에서 사용

기타

Ping Identity, OneLogin, Auth0 등

SSO 동작 프로토콜 (90% 이상 : SAML 2.0, OIDC, OAuth 2.0)
- SAML 2.0 
  - XML 기반, 무겁지만 안정적 
- OAuth 2.0 + OIDC 
  - 인가 프로토콜 
  - 이 앱이 내 데이터에 접근해도 돼 허가하는 용도 
- OIDC (OpenID Connect)
  - OAuth 2.0 위에 인증 레이어를 얹은 것 
  - OAuth 2.0 만으로 누구인지 알 수 없어서 만들어짐 
  - 현대 웹/모바일의 사실상 표준 
- LDAP / Active Directory 
  - 가장 오래된 방식
  - 사내 네트워크(온프레미스)환경에서 사용 
- Kerberos 
  - 주로 Windows 도메인 환경 내부에서 사용 
- WS-Federation
  - Microsoft 생태계에서 주로 사용
  - SAML 과 유사하지만 MS 전용
- RADIUS
  - 주로 네트워크 장비 인증에 사용
  - VPN, WI-FI 접속 인증
  - 회사 VPN 로그인 
