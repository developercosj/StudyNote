## 4장
클래스, 객체, 인터페이스 


자바와 달리 코틀린은 인터페이스에 프로퍼티 선언이 들어갈 수 있다.
코틀린 선언은 기본적으로 final 이며 public 이다. 
중첩 클래스는 기본적으로는 내부 클래스가 아니다. 코틀린 중첩 클래스에는 외부 클래스에 대한 참조가 없다. 

중첩 클래스(Nested Class) Vs 내부 클래스(Innter Class) 

- 중첩 클래스는 기본적으로 static 클래스처럼 동작
- 외부 클래스의 인스턴스와 연결되지 않고 외부 클래스의 멤버에 직접 접근 불가능 
- 중첩 클래스는 외부 클래스의 인스턴스에 대한 참조를 가지지 않는다. 
- 따라서 외부 클래스의 속성이나 메서드에 접근하려면 명시적으로 외부 클래스의 인스턴스를 전달해야 한다. 


내부클래스 (Inner Class)

- 내부 클래스는 외부 클래스의 인스턴스와 연결
- inner 키워드를 사용하여 정의하며 외부 클래스의 멤버에 직접 접근 가능 
- 내부 클래스는 외부 클래스의 인스턴스를 암시적으로 참조하여 외부 클래스의 속성이나 메서드 사용 가능 


결론 : 중첩 클래스는 외부 클래스의 인스턴스와 독립적으로 작동, 외부 클래스의 속성이나 메서드를 암시적으로 참조할 수 없다. 
만약 외부 클래스의 멤버를 참조해야 하는 경우라면 Inner Class 를 사용해야 한다. 


data class 
컴파일러가 일부 표준 메서드를 생성해준다. 



## 4.1 클래스 계층 정의
- 가시성, 접근 변경자


### 4.1.1 코틀린 인터페이스
- 추상 메서드, 구현 메서드 모두 정의 가능 
- 필드는 들어갈 수 없음
- : -> 코틀린에서는 클래스 확장과 인터페이스 구현을 모두 : 기호로 처리 
- 자바와 마찬가지로 클래스는 인터페이스를 원하는 만큼 개수 제한 없이 마음대로 구현할 수 있지만, 클래스는 오직 하나만 확장할 수 있다.
- override : 상위 클래스나 상위 인터페이스에 있는 프로퍼티나 메서드를 오버라이드한다는 표시 -> 필수 사용 
- 인터페이스 메서드에 디폴트 구현을 할 수 있으며 자바와 다르게 default 등의 키워드가 필요 없다.
* 추상클래스 안에 있는 메서드는 기본 구현이 있으면 따로 override 할필요 없으나 구현이 없는 추상 메서드는 override 키워드로 구현해주어야 한다. 
* Kotlin에서는 기본 구현이 있는 함수는 상속받는 클래스에서 재정의하지 않아도 자동으로 사용할 수 있다.

- 여러개의 interface 를 구현하는 경우 디폴트 구현이 들어있는 동일한 메서드가 있을때 -> 오류가 발생하기 때문에 직접 구현을 강제한다. 
```
  

  class Button : Clickable, Focusable {
    override fun click() = println("I was clicked")
    // 상위 타입의 이름을 꺾쇠 괄호(<>) 사이에 넣어서 어떤 상위 타입의 멤버 메서드를 호출할지 지정할 수 있다. 
    override fun showOff() {
      super<Clickable>.showOff() 
    }
  }
  
  interface Clickable {
    fun click()
    fun showOff() = println("I'm clickable!")
  }
  
  interface Focusable {
    fun showOff() = println("I'm focusable!")
  
  }
  
  

```

- 코틀린은 자바와 호환되게 설계하기 뒤해서 인터페이스의 디폴트 메서드를 지원하지 않는다. -> 코틀린은 디폴트 메서드가 있는 인터페이스를 일반 인터페이스와 디폴트 메서드 구현이 정적 메서드로 들어있는 클래스를 조합해서 구현 -> 자바클래스에서 코틀린 인터페이스 (디폴트 인터페이스가 있는) 구현하는 것은 코틀린 1.5 부터는 코틀린 컴파일러가 생성해주며 
전버전 부터는 코틀린에서 메서드 본문을 제공하는 메서드를 포함하는 모든 메서드에 대한 본문 작성 필요 


### 4.1.2 open, final, abstract 변경자: 기본적으로 final 

final : 변경 불가능성을 보장 

취약한 기반 클래스 fragile base class : 하위 클래스가 기반 클래스에 대해 가졌던 가정이 기반 클래스를 변경함으로써 깨져버린 경우 발생
자바의 클래스와 메서드는 기본적으로 상속에 열려있지만 코틀린의 클래스와 메서드는 기본적으로 final 

open : 클래스의 상속을 허용하기 위해 클래스 앞에 붙이는 변경자이며 메서드나 프로퍼티의 오버라이드를 허용하기 위해서도 open 변경자를 붙여야함 
클래스가 open 이라고 하더라도 그 내부 함수가 open 이 아니면(final) 이 메서드를 오버라이드 할 수 없다. 



```
open class RichButton : Clickable {
  final override fun click() {} // 이렇게 함으로써 오버라이드한 함수를 하위 클래스에서는 변경할 수 없도록 설정 가능
}

```


- 열린 클래스와 스마트 캐스트
  - 스마트 캐스트는 타입 검사 뒤에 변경될 수 없는 변수에만 적용 가능
  - 클래스 프로퍼티의 경우 이는 val 이면서 커스텀 접근자가 없는 경우에만 스마트 캐스트를 쓸 수 있다.
  - 프로퍼티가 final 이어야 한다. 
    - 프로퍼티가 final 이 아니면 프로퍼티를 다른 클래스가 상속하여 커스텀 접근자를 정의하여 스마트 캐스트가 불가할 수 있다. 
    - open 하지 않으면 계속 final 이기 때문에 스마트 캐스트를 사용가능하다. 
  

- abstract 클래스 (추상 클래스)
  - 직접 인스턴스를 생성할 수 없으며, 추상 메서드나 구현된 메서드를 포함할 수 있는 클래스 
  - abstract 메서드 : 본문을 가질 수 없음 
  - abstract 프로퍼티를 선언 가능 
  - abstract 메서드나 필드를 반드시 포함할 필요는 없다. 
  - 추상 멤버는 항상 열려있다. 
  - 추상 멤버 앞에 open 변경자를 명시할 필요가 없다. 
  - class 안에 아무것도 없이 abstract 만 붙여도 abstract 클래스가 된다. 
  - 추상 클래스에 속하더라도 비추상 함수는 final 이기 때문에 오버라이드를 허용하기 위해서는 open 을 붙인다.


- 인터페이스
  - 인터페이스 멤버는 항상 열려 있고 final 로 변경할 수 없다. 
  - 인터페이스 멤버의 경우 final, open, abstract 사용하지 않는다. 
  - 인터페이스 멤버에게 본문이 없으면 자동으로 추상 멤버가 되지만 abstract 을 붙일 필요는 없다.


- 상속 제어 변경자 정리
  ![Screenshot 2024-12-26 at 12.11.04 AM.png](..%2F..%2F..%2F..%2F..%2F..%2Fvar%2Ffolders%2Fpm%2Fmc91xbw16t34jtpdz2hrby4r0000gn%2FT%2FTemporaryItems%2FNSIRD_screencaptureui_zjdRZe%2FScreenshot%202024-12-26%20at%2012.11.04%E2%80%AFAM.png)



### 4.1.3 가시성 변경자(visibility modifier)
- 코틀린의 기본 가시성 : 아무 변경자도 없는 경우 선언은 모두 public (자바는 package-private)
- package-private 은 코틀린에 없음
- internal : 모듈 내부에서만 볼 수 있음 
  - 모듈이란 한번에 컴파일되는 코틀린 파일들 (같은 프로젝트 내의 모든 소스 코드 파일들을 포함하는 하나의 빌드 단뒤)
  - 자바에서는 package 이름만 같으면 프로젝트가 다르더라도 접근이 가능함
  
- 최상위 선언이란 : 함수, 클래스, 프로퍼티 등을 클래스 내부가 아닌 파일의 최상위 수준에서 정의하는 것 
- 최상위 선언에 private 접근 제한자를 추가하면 같은 파일 안에서만 접근 가능하게 된다. -> 파일 단위의 캡슐화 구현 가능
- 코틀린의 가시성 변경자
  ![Screenshot 2024-12-26 at 12.30.25 AM.png](..%2F..%2F..%2F..%2F..%2F..%2Fvar%2Ffolders%2Fpm%2Fmc91xbw16t34jtpdz2hrby4r0000gn%2FT%2FTemporaryItems%2FNSIRD_screencaptureui_MzsH47%2FScreenshot%202024-12-26%20at%2012.30.25%E2%80%AFAM.png)
 ** protected 는 같은 클래스와 자식 클래스에서 접근 가능 Private 은 같은 클래스에서만 접근 가능


```
  internal open class TalkativeButton : Focusable {
    private fun yell() = println("Hey!")
    protected fun whisper()=println("Let's talk!")
  }
  fun TalkativeButton.giveSpeech(){ // public 멤버 
    yell() 
    whisper ()
  }
  
  1.  giveSpeech : public 함수 
  TalkativeButton : internal 함수 
  -> public 함수에서 가시성이 낮은 TalkativeButton 를 참조하지 못함 giveSpeech() 함수의 가시성을 internal 로 변경하거나 TalkativeButton 클래스의 가시성을 Public 으로 변경이 필요 
```
- protected 멤버는 오직 어떤 클래스나 그 클래스를 상속한 클래스 안에서만 보이기 때문에 클래스를 확장한 함수는 그 클래스의 private 이나 protected 멤버에 접근할 수 없다. 
- 자바와 코틀린 모두 private 클래스는 내부 클래스에만 적용 가능하며 최상위 클래스에 Private 을 사용할 수 없다.
- 코틀린에서는 최상위 함수나 프로퍼티에도 private 으로 선언할 수 있다는 점이 자바와의 차이점이다. 
- internal 변경자는 바이트 코드상에서는 Public 이 된다 (자바 컴파일시)
  - 이름을 임의로 변경하는데 한 모듈에 속한 클래스를 모듈 밖에서 상속한 경우 그 하위 클래스 내부의 메서드 이름이 상위 클래스의 internal 메서드와 같아져서 내부 메서드를 오버라이드 하는 경우 방지
  - 실수로 internal 클래스를 모듈 외부에서 사용하는 일 방지
  
### 4.1.4 내부 클래스와 중첩된 클래스: 기본적으로 중첩 클래스
  - 중첩 클래스 (nested class) 는 명시적으로 요청하지 않는 한 바깥쪽 클래스 인스턴스에 대한 접근 권한이 없다
  - 직렬화 : 객체를 바이트 스트림으로 변환 
  - 코틀린 중첩 클래스에 아무런 변경자가 붙지 않으면 자바 static 중첩 클래스와 같다. 
  - 코틀린 중첩 클래스에 inner 를 붙이면 내부 클래스로 변경되며 바깥쪽 클래스에 대한 참조를 포함할 수 있게 된다. 
  - ![Screenshot 2024-12-26 at 1.14.58 AM.png](..%2F..%2F..%2F..%2F..%2F..%2Fvar%2Ffolders%2Fpm%2Fmc91xbw16t34jtpdz2hrby4r0000gn%2FT%2FTemporaryItems%2FNSIRD_screencaptureui_R5QV1y%2FScreenshot%202024-12-26%20at%201.14.58%E2%80%AFAM.png)
  - 내부 클래스 Inner Class 안에서 바깥쪽 클래스 Outer 참조에 접근하기 위해서 : this@@Outer 
  - 중첩클래스는 외부 클래스의 인스턴스를 만들지 않고 독립적으로 사용할 수 있다.

### 4.1.5 봉인된 클래스: 클래스 계층 정의 시 계층 확장 제한 
  - sealed 클래스 : 상위 클래스에 sealed 변경자를 붙이면 그 상위 클래스를 상속한 하위 클래스 정의를 제한할 수 있음 
    - sealed 클래스의 하위 클래스를 정의할 떄는 반드시 상위 클래스 안에 중첩시켜야 한다. 
    - ![Screenshot 2024-12-26 at 1.30.44 AM.png](..%2F..%2F..%2F..%2F..%2F..%2Fvar%2Ffolders%2Fpm%2Fmc91xbw16t34jtpdz2hrby4r0000gn%2FT%2FTemporaryItems%2FNSIRD_screencaptureui_UYTCAc%2FScreenshot%202024-12-26%20at%201.30.44%E2%80%AFAM.png)
      - 이떄는 else 를 붙이지 않아도 된다. 
    - sealed 로 표시된 클래스는 자동으로 open 클래스이다. -> 별도의 open 붙일 필요 없음 

## 4.2 뻔하지 않은 생성자와 프로퍼티를 갖는 클래스 선언

### 4.2.1 클래스 초기화: 주 생성자와 초기화 블록

  - 주생성자 (primary constructor) : 클래스 이름 뒤에 오는 괄호로 둘러싸인 코드
    - 생성자 파라미터 지정
    - 그 생성자 파라미터에 의해 초기화되는 프로퍼티 정의 
  - constructor 키워드 : 주 생성자나 부 생성자 정의 시작시 사용
    - 주 생성자 앞에 별다른 가시성 변경자나 에너테이션이 없으면 생략 가능 
  - init : 초기화 블록
    - 초기화 블록에는 클래스의 객체 생성시 실행될 초기화 블록이 들어간다. 
  - 프로퍼티를 초기화하는 식이나 초기화 블록 안에서만 주 생성자의 파라미터를 참조할 수 있다. 
    - ![Screenshot 2024-12-26 at 1.48.09 AM.png](..%2F..%2F..%2F..%2F..%2F..%2Fvar%2Ffolders%2Fpm%2Fmc91xbw16t34jtpdz2hrby4r0000gn%2FT%2FTemporaryItems%2FNSIRD_screencaptureui_StMqDH%2FScreenshot%202024-12-26%20at%201.48.09%E2%80%AFAM.png)
    - ![Screenshot 2024-12-26 at 1.48.25 AM.png](..%2F..%2F..%2F..%2F..%2F..%2Fvar%2Ffolders%2Fpm%2Fmc91xbw16t34jtpdz2hrby4r0000gn%2FT%2FTemporaryItems%2FNSIRD_screencaptureui_GFa9tz%2FScreenshot%202024-12-26%20at%201.48.25%E2%80%AFAM.png)
    - ![Screenshot 2024-12-26 at 1.49.11 AM.png](..%2F..%2F..%2F..%2F..%2F..%2Fvar%2Ffolders%2Fpm%2Fmc91xbw16t34jtpdz2hrby4r0000gn%2FT%2FTemporaryItems%2FNSIRD_screencaptureui_N5KVSE%2FScreenshot%202024-12-26%20at%201.49.11%E2%80%AFAM.png)
  - 클래스에 기반 클래스가 있다면 주 생성자에서 기반 클래스의 생성자를 호출해야 한다. 
    - 기반 클래스를 초기화하기 위해 () 괄호를 붙여주며 생성자의 인자가 있다면 괄호 안에 인자를 넣어줘야 한다.
  - 인터페이스는 생성자가 없기 때문에 인터페이스 구현시 상위 클래스 목록에 있는 인터페이스 이름뒤에는 괄호가 없다. 
    -> 기반클래스와 인터페이스를 구별 가능 
  - 클래스 외부에서 인스턴스화 하지 못하게 막으려면 모든 생성자를 private 으로 만든다. 
    ```
        class Secretive private constructor() {} // 외부에서는 인스턴스화 할 수 없다. 
    ```

### 4.2.2 부 생성자: 상위 클래스를 다른 방식으로 초기화
- 코틀린은 디폴트 파라미터 값을 이용하는 등으로 부 생성자를 여러개 만들 필요가 없다. 
- 부 생성자는 constructor 키워드로 시작한다. 
- this() 를 통해 클래스 자신의 다른 생성자를 호출 할 수 있다. 

     
        class MyButton: View (
        constructor (ctx: Context): this (ctx, MY_STYLE) {
        }
       constructor (ctx: Context, attr: AttributeSet): super (ctx, attr) {
        }
      
![Screenshot 2024-12-26 at 2.07.53 AM.png](..%2F..%2F..%2F..%2F..%2F..%2Fvar%2Ffolders%2Fpm%2Fmc91xbw16t34jtpdz2hrby4r0000gn%2FT%2FTemporaryItems%2FNSIRD_screencaptureui_Rxyy32%2FScreenshot%202024-12-26%20at%202.07.53%E2%80%AFAM.png)
        
- 클래스에 주 생성자가 없다면 모든 부 생성자는 반드시 상위 클래스를 초기화하거나 다른 생성자에게 생성을 위임(다른 생성자가 상위클래스를 생성자를 호출)해야함 

### 4.2.3 인터페이스에 선언된 프로퍼티 구현 
    
- 인터페이스에 있는 프로퍼티 선언 (추상 프로퍼티 선언) 에는 필드나 게터등의 정보가 들어있지 않음
- 인터페이스는 아무 상태도 포함할 수 없기 때문에 상태를 저장하기 위해 하위 클래스에서 프로퍼티등을 만들어야 한다. 


### 4.2.4 게터와 세터에서 뒷받침하는 필드에 접근

- 코틀린 프로퍼티를 2가지로 나눈다면
    - 값을 저장하는 프로퍼티: 단순히 값을 저장하고 읽는 프로퍼티.
    - 커스텀 접근자를 사용하는 프로퍼티: 값을 계산하거나 특정 로직을 실행하는 프로퍼티.

### 4.2.5 접근자의 가시성 변경 
    
- 접근자의 가시성은 기본적으로는 프로퍼티의 가시성과 같다.
- get, set 앞에 가시성 변경자를 추가해서 접근자의 가시성 변경 가능 
![Screenshot 2024-12-26 at 2.14.15 AM.png](..%2F..%2F..%2F..%2F..%2F..%2Fvar%2Ffolders%2Fpm%2Fmc91xbw16t34jtpdz2hrby4r0000gn%2FT%2FTemporaryItems%2FNSIRD_screencaptureui_Bdu7s3%2FScreenshot%202024-12-26%20at%202.14.15%E2%80%AFAM.png)
- 클래스 내부에서만 길이를 변경 -> 세터의 가시성을 Private dmfh wlwjd 
- lateinit : 널이 될 수 없는 프로퍼티에 지정하여 프로퍼티를 생성자가 호출된 다음에 초기화 