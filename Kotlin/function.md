# inline 함수
- 함수를 호출하는 대신, 함수의 내용을 호출한 위치에 복사해서 붙여주는 기능
- 함수 호출에 따른 비용을 줄이기 위한 최적화 방법 
- 함수의 본문을 컴파일 시점에 복사해서 넣어버림 
- 주의점
  - 너무 큰 함수를 Inline 하면 오히려 고드가 커져서 역효과
  - 재귀 함수는 Inline aht TMa
  - inline 함수 안에서 return 을 쓰면 Non-local return 

    


# reified (with intline function)
  - inline 함수는 컴파일 시점에 타입을 알 수 있는 경우에만 사용 가능
  - inline 함수와 함께 reified 키워드를 쓰면 타입 정보를 런타임에도 알 수 있게 됨
  - reified 키워드는 inline 함수의 타입 파라미터에만 사용할 수 있음
  - 제네릭 타입 정보를 런타임에 유지할 수 있도록 해준다.
  - Kotlin 의 reified 타입 파라미터는 JVM 의 제네릭 타입 소거(Type Erasure)를 우회하는 방법 제공
    RestTemplate, WebClient(Spring WebFlux) 등에서 더 나은 API 사용가능하도록 함 
  - JVM 제네릭은 타입 정보를 삭제한다. (Type Erasure)
    - 런타임에 제네릭 타입 정보를 알 수 없음
  - inline 함수는 그냥 복사해서 붙여넣기 때문에 타입을 알 수 있게 됨 
  - 예시 사용
    - inline fun <reified T> Gson.fromJson(json: String): T{
        return this.fromJson(json, T::class.java)
    }
    - val user: User = gson.fromJson(json) -> 여기서 타입을 넘겨줄 필요 없음 
