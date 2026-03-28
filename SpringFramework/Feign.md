- @EnableFeignClients 
Feign Client 를 사용하기 위해 활성화하는 어노테이션 
Feign Client : HTTP API 호출을 인터페이스 선언만으로 간단하게 할 수 있게 해주는 Spring Cloud 기능



- @FeignClient : 실제 HTTP 호출할 인터페이스에 붙이는 어노테이션 
@EnableFeignClients 가 없으면 @FeignClient 가 Bean 으로 등록이 안돼서 에러 발생함 
- 