individual  버전, Business 및 Enterprise 버전(확인된 도메인이 있어야 함)

# 구독 옵션 
Business: 사용자가 1~300명인 비즈니스를 위한 생산성 제품군
Education: 자격요건을 갖춘 교육 기관을 위한 할인된 제품군
Enterprise: 사용자 수에 제한 없이 제공되는 프리미엄 보안 및 고급 제어 기능이 추가됨
Essentials: Gmail을 사용하지 않는 생산성 및 공동작업 도구


* Business Plus 버전
  Google Vault로 데이터 보관 및 디지털 증거 검색


Cloud shell 


데이터


# CSE 
CSE 를 사용하여 조직의 데이터 보호 가능
- CSE 로 조직의 데이터를 사용자가 암호화한 상태로 올려놓을 수 있다. 
- 내용검색등 구글이 내용을 못 읽는다. 
- 대신 파일 이름이나 파일명/메타데이터는 검색이 가능하다. 
- 같은 조직 + 권한(+키)이 있으면 열때 자동으로 복호화됨 



# 도메인 

- 도메인 소유자는 하위 도메인을 만들어 최상위 도메인 내의 웹 페이지나 서비스에 기억하기 쉬운 주소 제공 가능 

- 네임 서버 : 대부분의 도메인 업체나 클라우드 서비스는 기본 네임서버 제공한다. 도메인 등록시 자동으로 기본 네임서버가 할당 별도로 설치할 필요는 없다.

- 자체 네임서버를 운영하고 싶을 때
예: 회사 내부 서버에서 DNS를 직접 관리하고 싶거나, 클라우드 대신 자체 인프라에서 DNS를 운영하고 싶을 때
해야 할 일: 서버 환경 준비 → 네임서버 소프트웨어 설치(BIND, NSD 등) → 도메인 등록기관에 권한 네임서버 정보 등록
- 도메인을 외부 DNS로 옮기거나 다른 네임서버를 사용할 때
예: AWS Route 53, Cloudflare 같은 서비스로 변경
해야 할 일: 해당 서비스에서 제공하는 네임서버 주소를 도메인 등록기관에 설정


# LDAP  (LightWeight Directory Access Protocol)
- 조직 내 사용자/자원 정보를 중앙에서 관리하고 조회하는 프로토콜
"김철수가 누구야?" → LDAP 서버에 물어봄
→ 이름, 이메일, 부서, 권한 등 반환

- 폴더구조처럼 계층적으로 데이터 관리한다. 
- IAP 는 서비스 접근 통제를 하고 게이트웨이/프록시에 가깝지만 LDAP 은 사용자 정보 저장 및 조회에 가깝다. 
- 같이 쓰이는 경우
  사용자 접근
  ↓
  [IAP] → 이 사람 접근 가능한지 확인
  ↓
  [LDAP] → 이 사람 정보/권한 조회
  ↓
  서비스 접근 허용



# Cloud Search Platform 

데이터베이스 및 파일 시스템과 같은 서드 파티 저장소 검색 가능
https://knowledge.workspace.google.com/admin/getting-started/editions/cloud-search-platform?hl=ko

- 보안 중심 : 사용자별 접근 권한에 따라 검색 결과 제한 
- AI 기반 검색 : Google 의 검색 알고리즘과 자연어 처리 기술 적용 
- 개발자용 API 제공 : 데이터 커넥터 개발 가능, 맞춤형 검색 경험 제공 

보안 및 권한

OAuth 2.0 기반 인증
Google Workspace 사용자 권한과 연계

API & Connectors

Indexing API: 외부 데이터 색인
Query API: 맞춤형 검색 UI/UX 구성
Pre-built connectors: Salesforce, SharePoint, Jira 등


1. 서드 파티 저장소의 색인 생성 (Indexing of Third-Party Repositories)
Cloud Search 는 Gmail, Drive 같은 Google Workspace 데이터뿐만 아니라 Salesforce, Sharepoint, Jira, 내부 DB 등 
외부 시스템도 색인할 수 있습니다. 
Indexing API 또는 기본 제공 커넥터를 통해 외부 데이터를 색인화하며, 권한 정보도 함께 가져와 접근제어 유지 
*색인 : 검색할 때마다 Jira, SharePoint, Salesforce 서버에 직접 가서 전체 데이터를 뒤져야 하기에 엄청 느리고, 서버에 부하 -> 색인으로 검색 
-> 크롤링(외부 시스템에 접근해서 데이터를 읽어옴) -> 분석 (텍스트에서 키워드, 의미 등을 추출) -> 저장 (빠르게 검색할 수 있는 형태로 Cloud Search 내부에 저장) 
-> 권한 정보도 함께 저장 : 이 문서는 A 팀만 볼 수 있음 같은 접근 제어 정보도 포함 
2. 필터 및 패싯 (Filters & Facets)
      검색 결과를 카테고리별로 나누거나 필터링할 수 있습니다.
      예: 문서 유형, 작성자, 날짜, 프로젝트별, 부서별 등으로 검색 결과를 좁힘
      대규모 기업 환경에서 원하는 정보를 빠르게 찾아주는 기능입니다.
3. 기본 제공 및 맞춤 검색 연산자 (Built-in & Custom Search Operators)
   Google 검색에서 사용하는 것과 유사한 연산자를 사용해 세밀한 검색 가능
   기본: from:, to:, owner: 등
   맞춤 연산자: 조직 내 특정 필드(예: 고객ID, 프로젝트코드)에 맞춰 검색
4. 맞춤 검색 인터페이스 (Custom Search Interfaces)
      단순한 기본 UI 외에, 자체 웹 애플리케이션이나 내부 포털에 Cloud Search 검색 기능 통합 가능
      개발자가 필요에 따라 검색 결과 표시 방식, 정렬, 필터 등을 커스터마이징 가능

5. 삽입 가능한 검색 위젯 (Embeddable Search Widgets)
   Cloud Search UI를 직접 애플리케이션에 위젯 형태로 삽입 가능
   예: 인트라넷 포털, 팀 협업 앱, CRM 시스템 내 검색 기능 제공
6. 기본 제공 커넥터 (Pre-built Connectors)
   외부 시스템과 연동하기 위해 Google이 제공하는 커넥터
   Salesforce, SharePoint, Jira, Confluence 등
   커넥터를 사용하면 권한 매핑과 데이터 동기화가 자동으로 처리됨
7. 사용자 및 그룹 ID 매핑 (User & Group ID Mapping)
   검색 결과는 각 사용자 권한에 따라 제한됨
   Google Workspace ID와 외부 시스템의 사용자/그룹 ID를 매핑하여 정확한 접근 제어 가능
8. 세부적인 액세스 수준 관리 (Granular Access Control)
      검색 결과별로 읽기, 쓰기, 보기 제한 등 권한 적용
      기업 정책 준수 가능, 민감 정보 노출 방지 



- DNS(TXT 인증) : 이 도메인이 내 것임을 Google에 증명하는 과정



