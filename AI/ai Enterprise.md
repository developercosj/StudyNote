Gemini 

- Gemini Business : Gmail, Docs AI 보조, Gemini 1.5 pro 
- Gemini Enterprise : 고급 모델, 회의 요약, 데이터 비학습 보장
- Workspace Enterprise Plus : SSO, DLP, 감사로그 + 모든 Gemini 기능 

** BigQuery 네이티브 연동
같은 GCP 생태계여서 Vertex AI Search 와 BigQuery 직접 연결 파이프라인 구성 가능 

Workspace = 구글 앱(Gmail, Docs 등)에 AI 추가
Enterprise = Workspace 기능 + 더 강력한 보안/관리/커스터마이징


 IdP : "이 사람이 우리 회사 직원이 맞습니다"라고 서명해서 증명서를 발급해주는 서버입니다. 우리 포털은 그 증명서를 믿는(신뢰하는) 관계
 MFA 엔진 — 비밀번호만으로는 부족할 때 OTP, 인증 앱, 하드웨어 키 등 추가 인증을 처리합니다. 엔터프라이즈에선 거의 필수입니다.

정책 엔진 — "특정 IP에서만 접근 허용", "해외에서는 MFA 강제" 같은 조건부 접근 규칙을 적용합니다. Azure AD에서는 Conditional Access라고 부릅니다.


# 각 AI 서비스의 SSO 엔터프라이즈 지원 현황



OpenAI Enterprise

SAML 2.0 기반 SSO 공식 지원
Azure AD, Okta 등과 연동 가능
단, 이건 ChatGPT 웹서비스용 SSO → 우리가 API 프록시 방식을 쓰면 이 SSO는 불필요
우리 포털에 SSO로 로그인하면, OpenAI 쪽은 우리 API Key로만 호출하면 됨

Google Workspace / Vertex AI

Google Workspace 자체가 IdP 역할 가능
Vertex AI는 Google Cloud IAM으로 권한 관리
BigQuery도 같은 IAM으로 통제 → GCP 쓴다면 IAM 하나로 전부 묶을 수 있어서 매우 유리

Anthropic Enterprise

SAML SSO 지원
마찬가지로 API 프록시 방식이면 우리 포털 SSO만 있으면 됨


# 빌링

# AI 서비스 통합

1. 테넌트 분리 방식
2. 통합 SSO 구성 가능 여부
* Gemini — Google 계정이므로 자동
* Claude Enterprise — Google OAuth 로그인 지원
* ChatGPT Enterprise — Google OAuth 로그인 지원
  -> 각 서비스에서 별도 사용자 프로비저닝(계정을 자동으로 만들고, 관리하고, 삭제하는 것) 필요
  -> 직원 입사/퇴사시 Google 계정 하나만 처리하면 끝나는 SSO 와는 달리, 각 서비스에서 개별적으로 접근 권한 부여, 회수
  결국 Google OAuth는 편의성은 높이지만, 중앙 관리 측면에서는 엔터프라이즈 수준의 SAML + SCIM 기반 SSO보다 부족합니다.

3. 라이선스 동적 변경
   고객 선택에 따라 실시간 라이선스 변경 가능 여부

4. LLM 모델에서의 데이터 격리 ![LLM 데이터 테넌트 격리.png](LLM%20%EB%8D%B0%EC%9D%B4%ED%84%B0%20%ED%85%8C%EB%84%8C%ED%8A%B8%20%EA%B2%A9%EB%A6%AC.png)
   BigQuery 의 테넌트 격리가 LLM 서비스에서도 유지되는지 -> 어떤 방식으로 LLM 을 사용하는지에 따라 크게 달라짐

시나리오1. BigQuery 쿼리 결과를 LLM 프롬프트에 직접 전달
(기억은????)
BigQuery 의 격리는 BigQuery 안에서만 유지된다. 데이터를 꺼내 LLM API 에 보내는 순간 격리 경계가 끊긴다.
OpenAI 같은 외부 LLM 을 쓴다면 데이터가 외부 인프라로 전송되므로, 전송 전 민감 데이터를 마스킹하거나, Vertex AI 처럼 GCP 내부 모델을 쓰는 것이 안전

시나리오2. BQML의 ML.GENERATE_TEXT()
BigQuery 내에서 SQL 로 직접 LLM을 호출하는 방식
Vertex AI와 같은 GCP 프로젝트 안에서 처리되며, VPC Service Controls 로 데이터가 조직 외부로 나가는 것을 차단할 수 있어 격리 수준이 가장 높다.

시나리오3. RAG(검색 증강 생성) 패턴 -> RAG 는 기본적으로 그 순간에만 검색하는 구조

가장 일반적이면서 격리 포인트가 많다.
벡터 DB 에 테넌트별 네임스페이스나 메타데이터 필터가 없으면 다른 테넌트의 임베딩이 검색될 수 있음
LLM 컨텍스트에 여러 테넌트 데이터가 섞이는 컨텍스트 오염 문제도 발생할 수 있기 때문에 tenant_id 필터 강제 필요

시나리오 4 — Fine-tuning
가장 위험한 경우입니다. 데이터가 모델 가중치 자체에 녹아들기 때문에, 다른 테넌트가 교묘한 프롬프트로 학습 데이터를 추출할 가능성이 있습니다. 테넌트 데이터를 혼합해서 파인튜닝하는 것은 원칙적으로 피해야 합니다.



* BigQuery : Google Cloud 의 완전 관리형 데이터 웨어하우스 -> 기존 데이터베이스(MySQL, PostgreSQL)와 달리 서버를 직접 관리하지 않아도 되고,
  페타바이트 규모의 데이터를 SQL 로 빠르게 분석 가능
- 서버리스 : 인프라 관리 불필요, 쿼리 실행시에만 비용 발생
- 컬럼형 스토리지 : 분석 쿼리에 최적화 (행 단위가 아닌 열 단위 저장)
- 멀티 테넌트 아키텍처 : 여러 고객이 같은 인프라 공유 -> 격리가 핵심 이슈
- 논리적 격리와 암호화를 결합해 멀티테넌트 환경에서 보안을 구현한다.
- 각 회사는 별도의 GCP 프로젝트를 가진다. -> 프로젝트 간 데이터 접근은 IAM 정책으로 원천 차단
  Dataset → Table → Column 수준까지 세밀한 권한 설정이 가능
- 컴퓨팅(슬롯) 격리: BigQuery 는 쿼리 실행 단위로 슬롯을 사용 기본적으로는 공유 슬롯 풀 사용 하지만 Reservations 기능으로 전용 슬롯을 확보해
  다른 테넌트의 쿼리가 내 쿼리 성능에 영향을 못 주게 할 수 있다.
- 스토리지 격리: 물리 스토리지(Colossus)는 공유되지만, 모든 데이터는 AES-256 으로 암호화되며 키는 테넌트별로 분리된다. CMEK(고객 관리 암호화 키)를 사용하면 Google 도 데이터를 볼 수 없다. 

