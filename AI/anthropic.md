claude Enterprise 로그인 방식 
1. 이메일 로그인 (기본)
SSO 강제하지 않은 경우 이메일 + 비밀번호 / Magic Link 로 로그인 
Continue with SSO 또는 Continue with email 중 선택해서 로그인 가능 
2. SSO (Enterprise 핵심 기능)
회사의 기존 계정 시스템으로 Claude 로그인 (별도 비밀번호 사용 x)
지원 프로토콜 : SAML 2.0, OIDC 기반 SSO 
Okta, Azure AD (Microsoft Entra), Ping Identity 같은 주요 Idp 와 연동 가능 
Domain Capture : 회사 이메일 도메인 등록하면 해당 도메인으로 로그인 시도 시 자동 기업 워크스페이스로 라우팅 -> 직원들이 개인 계정으로 우회하는 것 방지 






# Enterprise Login
1. 이메일 Magic Link
   SSO 설정 전 초기 단계
   Require SSO 가 OFF 인 경우 (Require SSO On 하면 사용불가)
   Primary Owner 비상 접근용
2. Google 소셜 로그인
   Continue with Google 을 통해 인증
3. Apple 로그인 (IOS)
   IOS 앱 사용자 한정
   SSO 미설정 상태
   Enterprise 환경에서 사실상 사용하지 않는 방식
   기업 Idp 통제 불가 (Apple x)
4. SSO (IdP 연동) : Enterprise 핵심
   SAML 2.0 과 OIDC 두가지 프로토콜로 SSO 지원
   Continue with SSO 로 사용
   Idp : Azure AD (Entra ID), Okta, Google Workspace(SMAL 2.0 or OIDC), Ping Identity, OneLogin, ADFS
   SSO 설정 후 Require SSO 를 ON 하면 모든 사용자가 즉시 SSO 방식으로만 로그인

*Require SSO 강제
SSO 를 유일한 방식으로 강제하는 정책
Magic Link, Google, Apple 전부 차단
*SCIM 자동 프로비저닝
사용자 계정을 자동으로 생성/삭제하는 Enterprise 전용 기능



# Claude SSO 지원 범위
SAML 2.0과 OIDC 기반 SSO 지원 : 이 두 프로토콜 지원하는 Idp 가능 (Okta, Azure AD, Ping Identity)
WorkOS 미들웨어를 통해 SSO 구현 -> 도메인 인증, JIT 자동 프로비저닝을 지원한다.
WorkOS 가 지원하는 Idp 라면 대부분 연동 가능
Idp : Azure AD (Entra ID), Okta, Google Workspace(SMAL 2.0 or OIDC), Ping Identity, OneLogin, ADFS


Claude 일반로그인 (Enterprise x)
현재 Claude.ai 의 소셜 로그인은 Google 과 Apple 만 지원한다. (일반 로그인)


참고 : https://support.claude.com/en/articles/13189465-logging-in-to-your-claude-account 