## Spring Security 정의 : 

Java/Spring 애플리케이션에서 인증(Authentication) 과 인가(Authorization) 를 제공하는 보안 프레임워크 웹 보안 전체를 다루는 강력한 프레임워크

- 인증 (로그인 처리)
- 인가 (권한/역할 기반 접근 제어)
- 세션/쿠키 관리
- CSRF, XSS, CORS 보호
- 암호화, 패스워드 인코딩
- OAuth2 로그인(구글, 네이버, 카카오 로그인)
- JWT 인증 처리
- 보안 필터 체인 구성 



## Spring Security 가 작동하는 핵심 개념

1. Authentication (인증)
- 로그인 처리 
- 대표 개념
  - UserDetails
  - UserDetailService
  - AuthenticationManager
  - PasswordEncoder 

2. Authorization (인가)
- 이 사람이 어떤 권한을 갖고 있는지 
- 즉 권한/역할 기반 접근 제어 

ROLE_USER, ROLE_ADMIN
@PreAuthorize()
URL 기반 접근 제어 

3. Filter Chain (보안 필터 체인) -> Security 구조의 80 % 이상 

Spring Security 는 일반 MVC Controller 이전에 수많은 필터를 동작시켜서 보안 처리함

예시) 
- UsernamePasswordAuthenticationFilter
- JwtAuthenticationFilter(커스텀)
- SecurityContextPersistenceFilter
- LogoutFilter


## Spring Security 동작 흐름 (정리)

폼 로그인(form login)  기준 흐름 : 필터 -> 매니저 -> 서비스 -> 유저 조회 -> 비밀번호 검증 -> SecurityContext 저장 

1. 사용자 요청 
2. Spring Security Filter Chain
3. UsernamePasswordAuthenticationFilter
4. AuthenticationManager(ProviderManager)
5. UserDetailService.loadUserByUsername()
6. UserDetails 반환 (유저 정보)
7. PasswordEncoder.matches() 비밀번호 검증
8. 성공 : SecurityContext 에 인증 정보 저장, 실패 : AuthenticationException 발생 



## 기본 설정 구조 
*Spring Security 6 부터는 WebSecurityConfigurerAdapter 가 사라지고 SecurityFilterChain Bean 등록 방식을 사용해 
- Spring Security 에서 인증은 UserDetails(User) 객체를 기반으로 이뤄짐 
- UserDetailsService : DB 에서 사용자 조회 
- PasswordEncoder(암호화) : Spring Security 는 BCryptPasswordEncoder 권장
- 인가 (Authorization) - 권한 기반 접근 제어 
- JWT 인증 (Spring Security + JWT 구조) 
  - 요즘의 표준 인증 방식 (세션 X)
  - 흐름 : 로그인 -> JWT 발급 -> 클라이언트 저장 -> 모든 요청 헤더에 JWT 전송 -> Security 필터가 검증 -> Authentication 세팅
  - 서버 확장성 좋음
  - 세션 저장 필요 없음
  - 모바일/SPA 친화적
- OAuth2 (구글/카카오 로그인)
  - Spring Security 는 OAuth2 로그인도 매우 쉽게 지원함 
  - 로그인 성공 시 사용자 정보 저장
  - 소셜 + 자체 회원 DB 연결 


## Spring Security 가 제공하는 보호 기능 
- CSRF 보호 : POST/PUT/DELETE 요청을 공격으로부터 보호
- CORS 관리 : 다른 도메인에서 오는 요청 제어 
- Session fixation protection : 세션 탈취 방지 
- Password hashing : 안전한 해싱(BCrypt)
- Brute force 공격 방어 : 인증 실패 핸들링으로 구현 가능 


## 특징
- 필터 체인으로 확장성 매우 높음
- 커스텀 인증(JWT, OAuth2, API Key) 손쉽게 구현
- Controller 보다 먼저 동작하는 수십 개의 보안 필터들로 구성 


## 핵심 구조
1. 필터 체인(Security Filter Chain)
2. 인증 (Authentication) 흐름
3. 인가(Authorization) 규칙 



## 인증 방식(Authentication Methods)

- 지식 기반 인증 : ID/Password 
- 소지기반 인증 : OTP(Authenticator, Microsoft Authenticator등), SMS 인증, 보안키(U2F) 
- 행동 기반 인증 : 타이핑패턴, 마우스 움직임, 평소 로그인 위치, IP 기반 위험 평가로 요즘 Google/Microsoft 로그인에 내장된 리스크 기반 인증(Risk-based Authentication)
- 인증 전달 방법 (Session, Token) : 사용자가 인증되었다는 사실을 어떻게 서버에 계속 증명할것인지 
  - Session-Based Authentication : 세선 기반 인증
    로그인 성공 -> 서버가 세션 ID 생성 -> 클라이언트 브라우저가 쿠키로 저장 -> 이후 요청은 쿠키 전송 
    전통적인 웹 방식
    Spring Security 기본 방식 
  - 쿠키 기반 인증
    세션이 아닌 인증 정보를 쿠키 자체에 저장하는 방식
    민감 정보 저장 시 반드시 서명 + 암호화 필요 
  - 토큰 기반 인증(Token Authentication)
    세션을 저장하지 않고 클라이언트에게 토큰을 주어 인증하는 방식 Statelsee JWT 가 대표 
- JWT (JSON Web Token)
  서버는 로그인 성공 -> 클라이언트에게 암호화된 인증 토큰을 발급 -> 클라이언트는 이후 요청에 토큰을 헤더로 포함 
  구조 : header.payload.signature 
  stateless 
  토큰 탈취 시 만료될 때까지 못 막음
  토큰 크기가 커서 네트워크 비용 증가 
- Basic Authentication : HTTP 표준 인증 방식 
  요청 헤더에 아래처럼 base64 로 인코딩한 ID/PW 를 보냄
- OAuth2 : 내 계정의 비밀번호를 주지 않고, 다른 서비스의 인증을 활용하는 방식 
- Passkey : 비밀번호 없는 인증 



## Spring Security 구현 가능한 인증 방식 

- Form Login : ID/Password 인증  
  - UsernamePasswordAuthenticationFilter 를 통해 구현
  - 커스텀 로그인 페이지도 가능 
- 세션 기반 인증(Session Authentication)
  - 로그인 후 세션 생성 -> 쿠키(JSESSIONID) 로 인증
  - Spring Security 기본 동작 
- JWT 기반 인증 (Token Authentication)
  - Filter(OncePerPrquestFilter) + AuthenticationProvider 를 조합하면 쉽게 구현 가능 
  - OAuth2 Resource Server 설정으로 JWT 검증도 가능 
- OAuth2 로그인 (소셜 로그인)
  - Spring Security 가 가장 강력하게 지원하는 기능 
  - Google Login, Naver Login, Kakao Login, GitHub Login 




## 구현 방식 선택 기준
JWT, OAuth2, 세션 기반 인증등 선택할 때 기준
1. 애플리케이션 구조(웹/SPA/모바일/서버 간 통신)
2. 보안 수준과 운영 편의성 요구사항


## 인증 방식 비교 

1. 세션(Session + Spring Security)
- 웹 서버에서 렌더링하는 전통적 웹 서비스 (Tyymeleaf 등)
- 보안 강함, 구현 쉬움
- 확장성 제한 (서버 메모리 필요)

2. JWt (API 기반)
- SPA(React/Vue) + 벡엔드 API / 모바일 앱, 마이크로서비스 
- 확장성 좋음 stateless 
- 토큰 탈취 시 위험(만료까지 무료화 어려움)




## Spring Security 전체 흐름 

사용자 요청 -> Spring Security FilterChain -> 인증 처리 (Authentication) -> 인가 처리 (Authorization) -> Controller 실행


## Security Filter Chain 종류  (인증과 관련된)

1. LogoutFilter                     
2. UsernamePasswordAuthenticationFilter (로그인 처리)
3. BasicAuthenticationFilter        
4. BearerTokenAuthenticationFilter (JWT 등)
5. OAuth2LoginAuthenticationFilter  
6. AnonymousAuthenticationFilter    
7. ExceptionTranslationFilter       
8. FilterSecurityInterceptor (인가 처리)

-> 이 필터들을 지나면서 요청이 인증/인가 처리된다.

## Spring Security 로 로그인 로직 개발

1. 의존성 추가
2. 패스워드 인코더 설정
3. 사용자 정보 제공자 (UserDetailsService 구현 or JPA 연동)
4. 인증 제공자 (AuthenticationProvider)
5. 보안 필터 체인(SecurityFilterChain) - 로그인 URL, 인증/인가 규칙, 세션/CSRF 등
6. (선택) 폼 로그인 또는 API(JSON) 로그인 방식 선택
7. (선택) 세션 기반 vs JWT 토큰 기반 선택
8. 테스트/검증 










## Bearer 토큰 (JWT) 기반 로그인


- JWT (Json Web Token) : 클라이언트와 서버 간 인증 정보를 안전하게 전송하기 위한 토큰 
- Bearer 토큰은 HTTP Authorization 헤더에 Bearer <token> 형식으로 전달 
- SPA/모바일/서버 간 API 보호, MSA, 게이트웨이 뒤 API 
- spring security OAuth2 Resource server 기능으로 JWT 서명 검증만 담당
  토큰 발급은 외부(idP) 혹은 별도 Authorization Server 가 수행한다. 
- 해시 사용
- jwt 토큰의 구조 
  - header.payload.signature 
    - Header (헤더) : 토큰 타입과 서명 알고리즘 정보를 담고 있다. Base64Uri 로 인코딩 된다. 
      - {
        "alg" : "HS256",
        "typ" : "JWT"
      }
    - Payload (페이로드)
      - {
        "sub": "username",
        "roles": ["ROLE_USER"],
        "iat": 1516239022, // 발생시간
        "exp": 1516242622 // 만료시간 
        }
      - 실제 전달할 데이터(클레임)를 담고 있다. 
      - Base64Url 로 인코딩 된다. 
      
- 토큰 생성 프로세스 
  - Claims 객체가 JSON 으로 변환
  - Header 와 Payload 가 각각 Base64Url 인코딩된다. 
  - header.payload 를 비밀키로 HMAC-SHA256 해싱하여 서명을 생성한다.
  - header.payload.signature 형태로 결합하여 최종 토큰 반환 


- 인증 필터 동작 프로세스 
  - 토큰을 .으로 분리하여 header, payload, signature 로 나눈다.
  - header 와 payload 를 서버의 비밀키로 다시 서명한다. 
  - 생성된 서명과 토큰의 서명을 비교한다. 
  - 서명이 일치하고 만료 시간이 유효하면 검증 성공한다. 

- 사용자 정보 추출 
  - ThreadLocal 에 인증 정보 저장 -> 현재 요청 스레드 동안 유지 -> @AuthenticationPrincipal 등으로 접근 가능


[클라이언트] → POST /api/auth/login
↓
[AuthenticationManager]
↓
사용자 인증 성공
↓
[JwtTokenProvider]
토큰 생성 (header.payload.signature)
↓
[클라이언트] ← JWT 토큰 반환

─────────────────────────────────────

[클라이언트] → GET /api/user (Authorization: Bearer <token>)
↓
[JwtAuthenticationFilter]
↓
토큰 추출 및 검증
↓
서명 검증 (변조 확인)
만료 시간 확인
↓
토큰에서 username 추출
↓
UserDetailsService로 사용자 조회
↓
SecurityContext에 Authentication 저장
↓
컨트롤러 실행
↓
[클라이언트] ← 응답 반환



- 핵심 보안 메커니즘
  - 서명 : 서버만 알고 있는 비밀키로 서명하기 때문에 클라이언트가 토큰을 임의로 수정 불가, 
  - 만약 payload 의 username 을 user1 -> admin 으로 변경하면, 서명이 달라져서 검증 실패 
- Stateless 특성 : 
  - 서버는 세션을 저장하지 않고, 매 요청마다 토큰 검증
  - 토큰 자체에 모든 인증 정보가 포함되어 있어 확장성이 좋다. 
  - 토큰에 만료 시간이 포함되어 있어, 검증 시 자동 확인 
  - 만료된 토큰은 자동 거부 

** JwtAuthenticationFilter 

- 매 요청 마다 
  1. 요청이 들어옴
  2. JwtAuthenticationFilter 가 실행됨
  3. 토큰이 있고 유효하면 -> SecurityContext 에 인증 정보 저장
  4. 토큰이 없거나 유효하지 않으면 SecurityContext 에 아무것도 저장하지 않음
  5. 다음 필터 진행
  6. FilterSecurityInterceptor 가 SecurityContext 확인
  7. 인증 정보 있으면 -> Controller 실행
  8. 인증 정보 없으면 401 Unauthorized 반환 





## 

React (프론트엔드)
- 로그인 폼에서 사용자 입력(아이디/비밀번호) -> API 호출
- 성공시 JWT 토큰을 로컬 스토리지나 쿠키에 저장 
- 이후 요청 시 Authorization: Bearer <token> 헤더 추가

Spring Boot (백엔드)
- Spring Security 설정
- 로그인 요청 처리 -> JWT 발급 
- JWT 검증 필터 추가 -> 인증된 사용자만 접근 가능 


