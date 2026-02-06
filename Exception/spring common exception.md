# Spring Kotlin 에러 공통 처리 구조

- 기본 구조 
  - Controller -> Service (도메인 로직) -> Custom Exception 발생 -> @RestControllerAdvice (Global Error Handler) -> 공통 Error Response 반환
- @RestControllerAdvice + @ExceptionHandler : 애플리케이션 전체에서 발생하는 예외를 한 군데서 잡아서, 같은 규칙으로 응답 생성
- @RestControllerAdvice : 모든 Controller 에서 터진 예외를 가로채는 전역 인터셉터 역할 
- 예외 발생시 스프링이 ExceptionResolver 탐색하여 @RestControllerAdvice 안에 @ExceptionHandler(가장 구체적인 타입 찾음) 발견 
- 해당 메서드 실행하여 공통 에러 응답을 생성한다.
- GlobalExceptionHandler 클래스 역할 
- 예외 ->  HTTP 응답 변환기
- 매칭 우선순위
  - NullPointerException -> RuntimeException -> Exception 

- 필요성
  - 없으면 try catch 를 모두 다 붙여줘야 한다. 
  - 프론트에 일관된 형식의 에러 응답을 줄 수 있다. 
  - code로 메시지가 아니라 판단할 수 있음 
  - Global Handler 에서 log 전역 추가 가능 
  - Service 에서는 ResponseEntity 반환을 하면 안되는데, Service 는 HTTP 를 몰라야 하는 계층이다. 
    - controller : HTTP, Service : 비즈니스 Repository : DB 
    - Service 테스트할 때도 HTTP 객체 필요
    - 나중에 REST -> gRPC 바꾸면 전부 수정
    - 도메인이 인프라에 종속됨 
  - HTTP 변환은 GlobalExceptionHandler 역할 
  - RuntimeException 남발하면 에러 관리가 불가해 

# Annotation 지정 

- 단일 지정

    @ExceptionHandler(CommonException::class)
    fun handleCommon(ex: CommonException): ResponseEntity<ApiError> {

- 복수개 지정 (파라미터에는 리스트로 지정 및 파라미터는 상위에러클래스로 지정)

  @ExceptionHandler(value = [AuthException::class, AccessDeniedException::class])
  fun handleAuth(e: RuntimeException): ResponseEntity<ApiError> {












