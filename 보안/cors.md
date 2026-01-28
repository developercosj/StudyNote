## CORS (Cross-Origin Resource Sharing)

- 서로 다른 출처(Origin) 간의 리소스 공유를 허용하는 보안 메커니즘
- 프로토콜(https://), 도메인(example.com), 포트(3000) 3가지가 모두 같아야 같은 출처가 된다.
- 브라우저는 기본적으로 보안상 다른 출처의 리소스 요청을 차단한다. 
- 왜 CORS 가 필요한가?
- Same-Origin Policy (동일 출처 정책)
  - 브라우저는 보안상 기본적으로 다른 출처의 리소스 요청을 차단
  - 프론트앤드와 백엔드는 일반적으로 개발환경에서 다른 출처를 가지고 있기 때문에 브라우저가 요청을 차단한다.
- CORS 설정 없을 때 상황

    1. 브라우저가 OPTIONS 요청 보냄 (Preflight)

    OPTIONS /api/auth/login HTTP/1.1
    Origin: http://localhost:3000
    
    2. 백엔드 응답 (CORS 설정 없음)
    
    HTTP/1.1 403 Forbidden
    (Access-Control-Allow-Origin 헤더 없음!)
    
    3. 브라우저가 차단
       ❌ 에러: "blocked by CORS policy"

   4. 실제 POST 요청은 전송되지 않음!




## Spring Boot 에서 CORS 설정

1. @CrossOrigin 어노테이션 설정 
@CrossOrigin(origins = ["http://localhost:5173"])


2. 전역 설정 (WebMvcConfigurer)

    
    @Configuration
    class WebConfig : WebMvcConfigurer {

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")  // 모든 경로에 대해
            .allowedOrigins("http://localhost:3000")  // 허용할 출처
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // 허용할 HTTP 메서드
            .allowedHeaders("*")  // 모든 헤더 허용
            .allowCredentials(true)  // 쿠키/인증 정보 허용
                .maxAge(3600)  // preflight 캐시 시간 (초)
        }
    }
