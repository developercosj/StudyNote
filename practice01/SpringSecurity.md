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











