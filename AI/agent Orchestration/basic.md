# 엔트로픽 
엔트로픽에서의 에이전트 엔지니어링
https://www.anthropic.com/engineering/building-effective-agents

# workflow : 미리 정해진 경로대로 흐르고, Agent는 llm 스스로 다음 행동 결정 
Multi-Agent 핵심 원칙 
- Orchestrator 가 태스크를 분해하고 Subagent 에 위임 
- 각 Subagent 는 독립된 컨텍스트 윈도우를 가짐
- 결과만 Orchestrator 로 반환  -> 컨텍스트 오염 방지 

# Context Window(컨텍스트 윈도우)
AI 모델이 한 번에 읽고 처리할 수 있는 텍스트의 최대 크기 
System Prompt 이전 대화 히스토리 현재 입력 메시지 출력 공간 
Multi-Agent 에서 중요한 이유 : 큰 작업을 하나의 에이전트에 몰아주면 컨텍스트가 넘친다. 


# 에이전트 오케스트레이션 패턴
1. 단일 에이전트 패턴(Single-Agent)
- ReAct : Reasoning + Acting 생각 -> 행동 -> 관찰 반복 
  - 단일 에이전트 기본값 
- Reflexion
  - ReAct + 자기 비판
  - 실패 후 스스로 교정
  - 반복 실수 30 ~ 50 % 감소
2. 협력 멀티에이전트(Collaborative Multi-Agent)
- Plan-and-Execute 
  - 플래너 -> 실행자 2단계
  - 저렴한 모델로 실행
  - 예측 가능한 구조적 워크플로
  - 단점 : 중간 변경에 취약 
- Supervisor-Worker 


3. 경쟁, 적대적 패턴 (Competitive / Adversarial)

5. 오케스트레이션 토폴로지 (Topology)

