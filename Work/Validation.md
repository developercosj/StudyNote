
## Global Exception Handler 만들기

- @ControllerAdvice (@Controller) / @RestControllerAdvice (@RestController 일때) 과 @ExceptionHandler 어노테이션을 기반으로 Controller 내에서 발생하는 에러에 대해서 해당 핸들러에서 캐치하여 오류를 발생시키지 않고 응답 메시지로 클라이언트에게 전달해 주는 기능 (AOP)
    - @ControllerAdvice : @Controller 로 선언한 지점에서 발생한 에러를 도중에 @ControllerAdvice로 선언한 클래스 내에서 이를 캐치하여 Controller 내에서 발생한 에러를 처리할 수 있도록 하는 어노테이션
- HTTP Status 코드로 정상코드가 아닌 오류코드로 반환하였을 시 실제 에러가 발생하기에 이를 위해 중간에 GlobalException 을 통해 Exception 발생 시에도 HTTP Status 코드로 정상코드를 보내고 커스텀한 코드를 보냄으로써 실제 Client 내에서 이를 처리할 수 있게 돕기 위함
- @ExceptionHandler(클래스::class) : 발생한 클래스에 대해서 오류 처리를 해준다.

- 형테
    - GlobalExceptionHandler 생성
        @RestControllerAdvice
        - class GlobalExceptionHandler {
          @ExceptionHandler
          fun exceptionHandler(e:Exception): ExceptionResponse {
          ....
          return ExceptionResponse.responseException(e)
          }

          @ExceptionHandler(CommonException::class)
          fun exceptionHandlerCommon(): ExceptionResponse {
          ....
          return ExceptionResponse.responseExceptionCommon(e)
          }

          @ExceptionHandler(NullPointerException::class, ....)
          fun exceptionHandler..(): ExceptionResponse {
          ....
          return  ExceptionResponse.responseExceptionNotValid(e)
          }
          }
    - ExceptionResponse 생성 (Response 공통 클래스를 만들어 놓음)
      class ExceptionResponse(): ResponseEntity<CommonResult>(httpStatus: HttpStatusCode....) {
      companion object {
      fun responseException(e: Exception) = ExceptionResponse(CommonResult(ResultCode.UNKNOWN_ERROR))
      fun responseExceptionCommon(e: CommonException): ExceptionResponse = ExceptionResponse(CommonResult(e.code, e.message, e.data), e.httpStatus)
      fun responseExceptionNotValid(e: Exception) = ExceptionResponse(CommonResult(ResultCode.REQUEST_NOT_VALID))
      }
      }
  - Global Exception 을 적용하려면 Unchecked Exception 에 해당해야 한다. (Checked Exception 은 try catch 문으로 에러 처리 로직을 넣어야 하기 때문에 GlobalException 을 사용하는 의미가 없다.)
  - Checked Error, Unchecked Error : 


 ## 코틀린 @valid 사용 

### 디팬던시 
    dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-validation' }



### 클래스 멤버 필드 nullable 지정 필수
 1. 코틀린에서 validation 적용하기 위해서 (특히 NotNull) nullable type 으로 선언하는 것이 필요 
-> Vallidation 유효성 검사보다 type matching 에 대한 검사가 우선이다.

 2. primitive type 과 관련있는 타입에 대해서는 확정형 입력시 @RequestBody 형태를 받을 때 해당 필드에 대해 입력을 하지 않아도 default value 로 설정된다. 
    primitive type, wrapper class type 둘 다 존재하고 nullable 하지 않은 확정형 클래스 필드로 선언 시 primitive type 이 되어 해당 필드 데이터를 입력하지 않아도 default value 로 컴파일러가 지정해준다. 
    NotNull Validation 을 설정 해도 제대로 동작하지 않는다.
결론 : Validation 적용 시 멤버 필드를 nullable type 으로 선언할 것

 3. @field:NotEmpty, @get:NotEmpty 를 붙여줘야 제대로 동작한다. 
    - 사용 위치 지정자(@field:, @get:, @set:, @param:)를 사용하지 않으면 기본적으로 어노테이션은 @get: 을 사용하게 된다. 
    - Spring에서 @Valid는 Field, Getter 둘다 검사하기때문에 어디에 붙여도 원하는대로 정상적으로 동작이 됩니다.
https://velog.io/@lsb156/SpringBoot-Kotlin%EC%97%90%EC%84%9C-Valid%EA%B0%80-%EB%8F%99%EC%9E%91%ED%95%98%EC%A7%80-%EC%95%8A%EB%8A%94-%EC%9B%90%EC%9D%B8JSR-303-JSR-380
    - @field : 필드에 직접 영향을 미치는 경우 -> 직렬화, 의존성 주입, 데이터 바인딩
    - @get : 프로퍼티 읽기(게터) 에 관련된 경우  -> JSON 매핑, 프로퍼티 읽기 관련 기능
      필드 검증	@field	일반적인 DTO 검증에 사용.
      게터 검증	@get	게터가 필요한 경우에 사용.
      컨트롤러 파라미터	@Valid	DTO를 검증할 때 사용.
    - 기본적으로 Spring Boot 검증 실패 시 MethodArgumentNotValidException 를 발생시킨다. -> @ControllerAdvice 로 커스텀 처리 가능
    - 필드가 Long, Int 같은 primitive type과 관련있는 타입에 대해서는 확정형 입력시 @RequestBody 형태로 받아 올 때 해당 필드에 대해 입력을 하지 않아도 default value로 설정됩니다.


    
### Binding Result  -> Global Exception Handler 만들어서 사용 

- BindingResult 와 @Valid 적용된 @ModelAttribute, @RequestBody 에 argument 를 작성하는 것이 필요
- @Valid를 사용했을 때 BindingResult 가 없다면 검증 실패 시 예외(Exception) 가 발생한다.
- BindingResult 는 Spring MVC 에서 요청 데이터 바인딩 및 검증 결과를 처리하기 위해 사용된다. 
- 주로 @Valid 또는 @Validate 와 함께 사용하며 입력 값 검증 후 발생하는 에러를 컨트롤러 레벨에서 처리 

### BindingResult 와 GlobalExceptionHandler 와의 차이 
- BindingResult 
    - 컨트롤러 내부에서 발생한 데이터 바인딩 에러 처리
    - 유효성 검사 결과를 직접 확인 및 처리 가능
    - 단순한 입력 데이터 검증과 에러 처리시 사용
    - 컨트롤러에서 오류 담는 공간 및 뷰에 전달할 모델 역할을 함
    - 파라미터 바인딩 오류 생기면 오류에 대한 정보가 BindingResult 에 담겨서 컨트롤러는 정상적으로 바인딩 됨
    - BindingResult는 @Valid가 적용된 @ModelAttribute, @RequestBody 바로 뒤에 argument 설정을 해야 합니다.
- GlobalExceptionHandler 
    - 컨트롤러 외부(또는 컨트롤러 포함) 에서 발생한 예외 처리
    - 다양한 예외를 한 곳에서 중앙 집중적으로 처리
    - 복잡한 예외 처리 및 공통 로직 처리에 적합 
    - GlobalExceptionHandler 에서 BindingResult 를 가지고 와서 사용해도 된다. 

- 결론
  - 둘을 병행해서 사용하며 간단한 유효성 검증은 BindingResult 로 처리, 공통적으로 처리해야 하는 예외는 GlobalExceptionHandler 로 처리(컨트롤러는 더 깔끔해지나 모든 검증 결과가 전역 처리 로직에 의존하게 된다.)

### @Valid 에러 
- @ModelAttribute
  - @Valid 적용시 유효성 검증 에러가 발생하면 기본적으로 400 응답코드를 반환한다. BindException 발생 (BindingResult 를 받아와서 예외 핸들링 가능)
- @RequestBody
  - @Valid 적용시 유효성 검증 에러가 발생하면 400 응답 코드를 반환한다. MethodArgumentNotValidException 발생 (BindException 확장한 에러 클래스이기 때문에 BindException 으로 잡을 수 있음)
  -> json 형태로 보내지 않으면 HttpMessageNotReadableException 이 발생한다.
- @RequestParam
  - @RequestParam 은 클래스 단계에서  @Validated 어노테이션이 필요하다. 
  - validation 에러 코드는 500 응답 코드를 반환하고 ConstraintViolationException 을 던진다. ControllerAdvice 를 사용해서 500 -> 400 으로 변경이 가능
  - BindingResult 따로 사용 불가함 
- @PathVariable 
  - @RequestParam 과 동일하다. 

- 결론 
  - @RequestParam 을 사용하면 Controller 에서 파라미터를 검증하는 코드가 추가될 수가 있음 
    - BindingResult 를 사용하지 않음
    - validation 에 대한 에러는 500 (ConstraintViolationException) 발생, type 변환에 대한 오류는 400 (MethodArgumentTypeMismatchException) 에러를 반환 
    - validation 절차를 거치지 않고 기본 타입 변환이 이루어짐 
  - @ModelAttribute (추천)
    - 검증 과정 진행
    - BindingResult 담을 수 있음
    - type 변환 오류 validation 에 대한 오류 모두 400 (BindException) 으로 반환 
    - BindingResult 를 Controller 단에 argument 로 가져오면 400 에러를 반환하지 않고 처리가 가능 
  
  



### 검증 어노테이션 종류
    - 문자열 검증 
        - @NotBlank : null 이 아닌 값 : "", " ", null 이 아니어야 한다. 
        - @NotEmpty : null 이거나 empty(빈 문자열)이 아니어야 한다. "", null 이 아니어야 한다. 
        - @NotNull : null 이 아닌 값은 어떤 타입이든 상관없다.
        - @Null : 타입 상관 없으며 null 값 허용
    - 최대 최소 검증
        - @DecimalMax : 지정된 최댓값보다 작거나 같아야함.
        - @DecimalMin : 지정된 최솟값보다 크거나 같아야함.
        - @Max : 지정된 최댓값보다 작거나 같아야함.
        - @Min : 지정된 최솟값보다 크거나 같아야함.
    - 범위 검증
        - @Positive : 양수인 값
        - @PositiveOrZero : 0이거나 양수인 값
        - @Negative : 음수인 값
        - @NegativeOrZero : 0이거나 음수인 값
    - 시간 값 검증
        - @Future : Now 보다 미래의 날짜, 시간이어야 한다.
        - @FutureOrPresent : Now 거나 미래의 날짜, 시간이어야 한다.
        - @Past : Now 보다 과거의 날짜, 시간이어야 한다.
        - @PastOrPresent : Now 거나 과거의 날짜, 시간이어야 한다.
    - Boolean 검증
        - @AssertTrue : 항상 True 여야 한다.
        - @AssertFalse : 항상 False 여야 한다.
    - 크기 검증 
        - @Size(max=25, min=7)
            - max 값의 크기가 max보다 작거나 같아야 한다.
            - min 값의 크기가 min보다 크거나 같아야 한다.

    - 기타
        - @Pattern : 정규식 적용 (예시 :  @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$") ) 



## 검증로직
- Validation : 유효성 검증 HTTP Request 에서 잘못된 파라미터 검증
  - 데이터 검증 : 필수 데이터, 이메일 등 타입
  - 비즈니스 검증 : 서비스 정책에 따라 검증
- Validation 방법
  - Java bean Validation : Dto 클래스에 Annotation 붙이기 -> 현재 필수로 적용되어야 하는 사항 
  - Spring Validator 인터페이스 구현 -> 추가 구현 가능한 사항 




## 추가 사항 
- 검증부에 도메인 로직이 추가되면 안된다. -> 단위 테스트에서는 검증부를 하드코딩하는 것이 좋다.
- https://jojoldu.tistory.com/615
- 소프트 코드 검증문 (도메인 로직을 포함한 검증문) 을 사용할 때는 다음과 같은 것을 유의할것 
  - 무의미한 검증
  - 구현 코드와의 강한 결합
  - 거짓성공 : 검증하고자 하는 것이 로직인데 도메인 로직을 그대로 복사할때 해당 코드에 버그가 있어도 검증문이 동일한 코드를 사용하고 있어서 테스트가 실패할 가능성이 크다. 
  - 결과 예측의 어려움
  - Test First 개발의 어려움
  - https://velog.io/@coo9292/Spring-Framework-Validation%EA%B2%80%EC%A6%9D-%EC%B4%9D%EC%A0%95%EB%A6%AC 
    


## 실제 코드에 적용
1. @Valid 어노테이션을 사용해서 각 오류가 발생시 -> GlobalExceptionHandler 에서 처리할 것 (  BindException,MethodArgumentNotValidException)




 ## 참고
- https://docs.spring.io/spring-framework/docs/3.2.x/spring-framework-reference/html/validation.html
- https://why-dev.tistory.com/267 
- https://mopil.tistory.com/79 
- https://beaniejoy.tistory.com/72




