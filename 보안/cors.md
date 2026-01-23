## CORS (Cross-Origin Resource Sharing)

- 서로 다른 출처(Origin) 간의 리소스 공유를 허용하는 보안 메커니즘
- 프로토콜(https://), 도메인(example.com), 포트(3000) 3가지가 모두 같아야 같은 출처가 된다.
- 브라우저는 기본적으로 보안상 다른 출처의 리소스 요청을 차단한다. 

## Spring Boot 에서 CORS 설정

1. @CrossOrigin 어노테이션 설정 
@CrossOrigin(origins = ["http://localhost:3000"])


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
