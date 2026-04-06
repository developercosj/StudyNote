Gemini for Workspace  →  Workspace 안에 Gemini 채팅 추가된 것
(Gmail에서 AI 초안 작성 같은 기능)

Gemini Enterprise     →  Vertex AI Agent Builder로 만드는
우리 브랜드 AI 서비스



# BigQuery

프로젝트 단위 분리 : 회사마다 GCP 프로젝트 따로 (가장 강력한 수준)
데이터셋(스키마같은) 단위 분리 : 같은 프로젝트, 데이터셋만 분리 (중간)
테이블 단위 분리 : 같은 데이터셋, 테이블만 분리 (가장 약함)

Agent 연결시
사용자 로그인
↓
회사 A 소속 확인
↓
데이터셋 A만 접근 허용
↓
Agent가 데이터셋 A에서만 조회




구체적으로는 Google Cloud Console에서 Vertex AI Search 메뉴로 이동한 후 "새 앱 만들기(Create App)"를 클릭하고, 앱 유형으로 Gemini Enterprise를 선택한 다음 데이터 소스(예: BigQuery, Cloud Storage, 웹사이트 등)를 연결하고 설정을 완료해서 "만들기/배포(Deploy)" 버튼을 누르는 일련의 과정입니다.
이 배포 과정이 완료되면 Google Cloud가 자동으로 해당 앱에 접근할 수 있는 웹 앱 URL을 생성해줍니다. 즉, 사용자가 별도로 URL을 설정하거나 서버를 구성하지 않아도 된다는 의미입니다.





사용자 질문
↓
Agent (챗봇)
↓
Engine (두뇌 설정)
↓
Data Store (지식 창고)
↓
답변 생성



데이터 관련 → Data Store + Engine (Vertex AI Search)
챗봇 관련 → Agent (Vertex AI Agent Engine)





BigQuery (원본 데이터)
↓ 동기화/인덱싱
Data Store (AI가 검색할 수 있는 형태로 변환된 데이터)
↓
Agent가 답변에 활용

BigQuery는 데이터 저장/분석용 포맷
Data Store는 AI가 빠르게 검색할 수 있는 포맷


설계시 고려 

BigQuery 데이터가 변경되면 Data Store도 재동기화 필요
동기화 주기를 어떻게 설정할지
데이터 용량에 따라 비용 발생



"앱을 처음 생성(배포)할 때"는 Google Cloud의 Vertex AI Search에서 Gemini Enterprise 앱을 새로 만드는 과정을 의미합니다.
구체적으로는 Google Cloud Console에서 Vertex AI Search 메뉴로 이동한 후 "새 앱 만들기(Create App)"를 클릭하고, 앱 유형으로 Gemini Enterprise를 선택한 다음 데이터 소스(예: BigQuery, Cloud Storage, 웹사이트 등)를 연결하고 설정을 완료해서 "만들기/배포(Deploy)" 버튼을 누르는 일련의 과정입니다.
이 배포 과정이 완료되면 Google Cloud가 자동으로 해당 앱에 접근할 수 있는 웹 앱 URL을 생성



Data Store (지식 창고)
↓
Engine (두뇌 설정) ← Vertex AI Search
↓
Agent ← Vertex AI Agent Engine



Engine → Vertex AI Search에서 Data Store를 묶어놓은 설정
Agent Engine → Vertex AI에서 실제 Agent를 만들고 실행하는 서비스





데이터 관련 → Data Store + Engine (Vertex AI Search)
챗봇 관련 → Agent (Vertex AI Agent Engine)

BigQuery는 데이터 저장/분석용 포맷
Data Store는 AI가 빠르게 검색할 수 있는 포맷

둘의 형태가 다르기 때문에 변환 과정이 필요하다. 



# Web app 

Google Cloud Console
→ Vertex AI Search & Conversation
→ Agent Builder
→ 코드로 만든 앱 확인
→ 웹 앱 URL 활성화

어떻게 할 수 있어? 




Vertex AI Search

검색/추천에 특화된 서비스
Data Store에 있는 데이터를 기반으로 답변
우리가 지금 만들고 있는 것
Agent Builder가 여기에 속함


# Vertex AI

Google Cloud의 AI/ML 플랫폼 전체
Gemini 모델, 학습, 배포 등 광범위한 AI 서비스
Vertex AI Search도 사실 Vertex AI의 한 부분


# Vertex AI 의 데이터 소스 : 호환 
- 웹사이트 
- 정형 데이터 : 데이터베이스, Cloud Storage 의 JSON 파일, Bigquery 테이블 등 정의된 형식으로 구성된 데이터를 검색한다.
- 비정형 데이터 : Cloud Storage 또는 BigQuery 에 저장된 문서 또는 이미지 파일을 검색 
- 혼합검색 : 위 데이터 혼합 -> 여러 데이터 스토어 검색 


# 커스텀 검색용 Vertex AI Search 구성요소 

- 데이터 스토어 : 다양한 데이터 소스 컨텐츠 (공개 웹사이트 데이터, 정형, 비정형 데이터)
- 데이터 처리, 색인 생성 
  - 키워드 추출 
  - 임베딩을 사용한 시멘틱 이해 
  - 메타데이터 처리 
  - 고급 문서 파싱 : OCR 또는 레이아웃 파싱을 사용하여 문서 구조를 이해 
- 맞춤검색의 핵심은 검색 앱 -> 검색 앱은 다양한 소스의 데이터를 가져오는 하나 이상의 데이터 스토어에 연결 
혼합검색의
- 사용자 쿼리 
  - 검색어
  - 탐색 검색어 또는 탐색 -> 사용자의 과거 활동, 현재 카테고리 페이지, 위치와 같은 기타 신호 기반 

    
# Vertex AI 순위 지정

Vertex AI Search는 다음 요소를 기반으로 결과를 순위 지정합니다.
관련성: 검색 중 키워드와 시맨틱 일치의 조합입니다.
웹사이트 검색을 위한 웹 신호: 페이지 품질, 인기도 등의 요소
부스팅 및 다른 항목 밑에 두기: 특정 결과를 홍보하거나 강등하는 맞춤 규칙입니다.
맞춤설정: 사용자 상호작용을 통해 학습합니다. 이는 선택사항이지만 적극 권장됩니다.
정렬: 날짜와 같은 정렬 지침을 적용합니다.

# 지원하는 Connector 종류 

1. Google 자체 서비스
   - Google Drive
   - Google Cloud Storage
   - BigQuery
   - Gmail (제한적)
   - Google Sites
2. 클라우드 스토리지
   - Amazon S3
   - Azure Blob Storage
3. 협업/문서 툴
   - Confluence (Atlassian)
   - SharePoint
   - Notion
   - Jira
4. CRM / 비즈니스
   - Salesforce
   - ServiceNow
   - Zendesk
5. 웹
   - Web Crawl (URL 지정해서 크롤링)





Connector 설정
↓
주기적으로 데이터 Crawl/Ingest (동기화)
↓
Data Processing (청크 분할 등)
↓
Indexing & Embeddings 생성
↓
Data Store에 저장
↓
검색/RAG에 활용











