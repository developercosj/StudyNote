# Reactive Programming 

정의 : 데이터 스트림과 변화 전파에 중심을 둔 프로그래밍 패러다임


1. 비동기 데이터 스트림 처리 
   - 시간에 따라 발생하는 이벤트나 데이터를 스트림으로 모델링

2. 변화 전파 (Propagation of Change)
   - 데이터가 변경되면 자동으로 관련된 모든 부분이 업데이트 
   - 선언적 방식으로 데이터 흐름을 정의 
   
3. 비동기/논블로킹 처리 
   - 블로킹 없이 동시에 여러 작업 처리
   - 리소스 효율적

특징 
    - Observable/Observer 패턴 
    - 함수형 프로그래밍 스타일 (맵, 필터, 리듀스 등)
    - 백프레셔(BackPressure) 처리 
    - 에러 처리와 복구

자바/코틀린에서 사용 
    - RxJava, RxKotlin
    - Spring WebFlux(Project Reactor)
    - Kotlin Flow/Coroutines 






