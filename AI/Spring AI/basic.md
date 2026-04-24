Flux<T> 는 내부적으로 이벤트 루프(event loop)나 비동기 스케줄러를 활용해 T 항목을 처리하므로, I/O 작업 (웹 요청, 데이터베이스조회, 파일 입출력)이 끝날 때까지 스레드를 블로킹하지 않습니다. 
따라서 고성능, 대규모 동시성 처리가 가능하며, 자원을 효율적으로 사용할 수 있습니다.


- Message 인터페이스

Message 역할에 따라서 사용되며 기본적으로 Message 인터페이스를 구현한다. 
그리고 멀티모달을 지원하기 위해 UserMessage 와 AssistantMessage 는 MediaContent 인터페이스를 추가적으로 구현 (텍스트, 음성, 이미지 등을 저장할 수 있도록 List<Media> 를 추가적으로 가지고 있다.)

- SystemMessage : LLM 의 행동과 응답 스타일을 지시하는 메시지 주로 LLM 이 입력을 해석하는 방법과 답변하는 방식 지시 
- UserMessage : 사용자의 질문, 명령을 답고 있는 메시지 LLM 의 응답을 형성하는 기초가 되므로 매우 중요한 메시지 
- AssistantMessage : LLM 의 응답 메시지 단순한 답변 전달을 넘어, 대화 기억 유지에도 사용되어 일관되고 맥락에 맞는 대화에 도움을 준다. 
- ToolResponseMessage : 도구 호출 결과를 LLM 로 다시 반환할 때 사용하는 내부 메시지 

# Message 는 공통적으로 텍스트 내용(text), Map 타입의 메타데이터 (metadata), 메시지 타입(message type) : SYSTEM, USER, ASSISTANT, TOOL 



- Generation 클래스
Generation 은 LLM 출력 내용을 AssistantMessage 형태로 저장한다. 그리고 관련된 메타데이터를 ChatGenerationMetadata 로 저장한다. 


- LLM 제공 업체별 구현 클래스
  https://docs.spring.io/spring-ai/reference/api/chatmodel.html#_available_implementations

- 구현체 비교
  https://docs.spring.io/spring-ai/reference/api/chat/comparison.html



# 토큰 
토큰은 LLM 이 문장을 처리할 때 문장을 잘게 나누는 단위 
클라우드에서 서비스하는 LLM 은 입력 문장과 출력 문장을 구성하는 전체 토큰 수로 모델 사용료 집계 
- 컨텍스트 윈도우 (Context Window) : 입력 토큰 한계 

# ChatClient 

복잡한 데이터 흐름을 관리하는 고급 수준의 ChatClient 를 별도로 제공
Advisor 들을 체인으로 엮어 전,후처리 진행 
이전 대화내용 프롬프트에 추가, RAG 시스템에서 벡터 저장소의 검색 결과를 프롬프트에 추가, 도구 호출 등
- 도구 : 도구는 애플리케이션 쪽에서 실행, 호출 결과는 LLM이 이용 (둘의 복잡한 대화흐름을 ChatClient 가 관리해준다.)

RAG 시스템에서 벡터 저장소의 검색 결과를 프롬프트에 추가하는 작업 가능 
사용자의 질문이 LLM 에서 처리하기 힘들 경우, LLM 은 도구 호출(Tool Calling) 요청을 할 수 있다. 


# 프롬프트 템플릿 

프롬프트는 정적 텍스트일 수도 있지만, 데이터가 바인딩될 자리 표시자를 가지고 있는 동적 텍스트 일 수 있다. 
동적 텍스트 : 프롬프트 템플릿 -> 데이터로 바인딩되어 완성된 프롬프트로 생성 
Spring AI 에서 프롬프트 템플릿을 위해 PromptTemplate 을 제공하고 있다. 

- 복수 메시지 추가 : LLM 에 요청할 때 하나의 SystemMessage 와 여러개의 UserMessage, AssistentMessage(응답) 도 같이 포함 가능
대화 기억을 유지하기 위해, 이전 대화 내용 (UserMessage + AssistantMessage) 전체를 프롬프트에 포함시킬 수도 있다. 


# 디폴트 메시지와 옵션 

LLM 요청시 공통 사용 메시지, 옵션 존재 -> ChatClient 생성 시 기본 설정 가능 
ChatClient.Builder 
- defaultSystem() : 기본 SystemMessage 를 추가 
- defaultUser() : 기본 UserMessage 추가 
- defaultOptions() : 기본 대화 옵션 설정 


# 프롬프트 엔지니어링 

대규모 언어모델 효과적 활용 위해 입력 프롬프트 설계, 최적화 하는 과정 

- 프롬프트 엔지니어링 기본가이드 
  - 명확, 답변 범위, 방향 명확히 정의
  - 배경정보, 문맥 제공
  - 간결, 직관적 문장 사용 
  - 적절한 예시
  - 한가지 질문 집중 
  - LLM 처리 범위와 한계 이해 
  - 명확한 역할 부여 

# 제로-샷 프롬프트 (zero shot)
  예시 x -> 모델의 이해, 실행 능력 필요
# 퓨-샷 프롬프트 (few shot)
  LLM 에게 몇 개의 예시를 제공하여 사용자가 원하는 방식으로 출력하도록 유도하는 기법 (한개의 예시 제공 : 원 샷)
  원하는 출력이 구조화되어 있을 경우, 예시제시를 통해 결과물향상 가능 
# 역할 부여 프롬프트 (role assignment)
  특정 역할, 인물, 특정 정체성, 관점 등 부여
# 스탭-백 프롬프트 (step - back)
  복잡한 질문을 여러 단계로 분해해, 단계별로 배경 지식을 확보
# 생각의 사슬 프롬프트 (Chain-of-Thought Prompt)
  AI 가 최종 답변에 도달하기 전에 단계별 추론 과정을 명시적으로 보여주도록 하는 프롬프팅 기법
# 자기 일관성 
  여러번 풀었을때 같은 답이 나왔는지 확인하는 것 



# 구조화된 출력 

구조화된 출력 변환기의 공통 인터페이스 : StructuredOutputConverter<T> 
- 저수준 : 변환기를 직접 생성해서 형식 지침을 제공, 변환 
- 고수준 : Chat Client 의 메소드 체이닝 맨 마지막에 entity() 메소드 호출 

