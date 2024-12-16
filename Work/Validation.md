
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

 ## 코틀린 @valid 사용 


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

- BindingResult 와 @Valid 적용된 @ModelAttribute, @RequestBody 뒤에 argument 를 작성하는 것이 필요


@ModelAttribute
@RequestBody
@RequestParam, @PathVariable

- @Valid를 사용했을 때 BindingResult 가 없다면 검증 실패 시 예외(Exception) 가 발생한다.
 



### 검증 어노테이션 









