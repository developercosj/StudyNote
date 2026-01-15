JAR / WAR 


1. JAR : 바로 실행하는 앱  

- Java Archive
  - 일반 Java 애플리케이션/라이브러리를 배포하기 위한 ZIP 기반 아카이브 형식
  - META-INF/MANIFEST.MF 의 메타데이터(예: Main-Class, Class-Path) 로 실행 진입점을 정의하거나 라이브러리로서 클래스패스를 구성하는 데 사용 
- JAR 자체는 servlet/JSP 같은 웹 스펙을 요구하지 않음 
- 실행 방식 : java -jar 로 직접 실행
- 용도 : 일반 앱, Spring Boot
- 구조 : 자유로운 구조
- 배포 방식 : 단독 실행 가능 
- 실행/배포 모델과 클래스로더 
  - 실행 : java -jar my-app.jar (Main-CLass 진입점)
  - 배포 : 단일 파일로 어디서든 실행 (자바 런타임 필요)
  - Spring Boot 실행형 JAR : 내장 톰캣/제티를 애플리케이션 안에 포함시켜 WAS 없이도 HTTP 서버 구동
  - 클래스로더 : 일반적으로 애플리케이션 단일 클래스로더 트리 (부트스트랩 -> 시스템 -> 애플리케이션) 하에 모든 의존성이 로딩
- 실행형 애플리케이션으로 Application ClassLoader 가 JAR 안의 클래스 + 의존 JAR 들을 직접 로딩



2. WAR : 서버(tomcat) 에 올려서 실행하는 웹앱 

- 실행 : 단독 실행 불가, WAS 에 배포해야 함 : Tomcat webapps 폴더에 my-webapp.war 복사 -> auto -deploy
-클래스로더: 컨테이너(서버) 클래스로더와 웹앱(컨텍스트) 클래스로더가 분리.
 컨테이너 제공 라이브러리(예: Servlet API)는 서버 클래스패스에 존재.
 WEB-INF/lib/의 라이브러리는 해당 웹앱 컨텍스트에만 유효.
 이 분리는 여러 웹앱 간 라이브러리 충돌 방지, 격리에 유리하지만, 버전 상이/중복 시 NoSuchMethodError, ClassCastException 같은 문제를 유발
- 서블릿 컨테이너(WAS: Tomcat, Jetty, Undertow, WebSphere, WebLogic 등) 에 배포할 웹 애플리케이션 패키징 표준 형식
- Jakarta EE( 구 JAVA EE) 웹 애플리케이션 구조를 따르며, WEB-INF / 디렉터리 구조와 배치 서술자 
- 실행 방식 : Tomcat 같은 WAS 에 배포
- 용도 : 웹 프로젝트 (WAS 기반)
- 구조 : WEB-INF 기반 웹 구조
- 배포 방식 : 서버 필요
- WAS(Tomcat) 가 자체 클래스로더 시스템을 가짐
- 각 웹앱(WAR) 마다 웹앱 전용 클래스로더가 존재 
  - WEB-INF/classes -> 웹앱 전용 클래스
  - WEB-INF/lib/*.jar -> 웹앱 전용 라이브러리 
  - tomcat 전체가 공용으로 쓰는 클래스는 lib 디렉터리에서 로딩 

  
*클래스 로더 (Class Loader) 
- JVM 이 .class 파일(bytecode) 을 읽어서 메모리에 올리는 로드 담당자 
- Java 프로그램이 실행될 때 필요한 클래스를 찾아서 메모리에 적재하는 시스템이 클래스 로더이다. 
- Java 는 런타임에 클래스 로딩(dynamic loading) 가능 
- 필요할 때마다 클래스를 로드해서 사용하는 구조 

클래스로더의 계층 구조 (JVM)
1. Bootstrap ClassLoader : JVM 최상위 (c 언어)
2. Extension(Platform) ClassLoader : JDK 확장 기능(Jakarta) 로딩
3. Application ClassLoader : 우리가 만든 .class 와 라이브러리(JAR) 로딩, classpath 에 있는 클래스들을 읽는 주체 


*클래스로더는 부모위임(parent delegation) 방식을 사용함 
즉, 클래스 로딩 요청이 들어오면 먼저 부모가 있는지 확인 후 -> 없을때만 자신이 로딩 



*클래스패스(Classpath)

- JVM 이 클래스를 찾을 때 "어디에서 찾을지" 알려주는 검색 경로 설정
- 어디서 .class 파일을 클래스로더가 찾을지 알려주는 파일 경로 목록 



*Tomcat : Java 웹 애플리케이션을 실행해주는 웹 서버 (WAS) / Java 웹 플로그램을 띄워주는 엔진 
정식 명칭 : Apache Tomcat 이고 Java 로 만든 웹 서비스(HTTP 요청/응답)를 실행해주는 WAS(Web Application Server) 중 하나 
Tomcat 이 하는일 
  - 브라우저에서 요청 받기 
  - Java 웹 애플리케이션 코드 실행
  - 실제 결과 브라우저로 응답 
  - 스레드 관리, 커넥션 관리, 세션 관리 등 웹 서버 기능 제공 

*War 란 
WAR = Tomcat 같은 WAS 에 배포하는 웹 애플리케이션 묶음 파일 
Web Application Archive 파일로 웹 애플리케이션 전체를 하나로 포장한 압축 파일 


Tomcat 은 웹서버이고 WAR 는 그 서버에서 실행되는 웹앱 
