GCP 콘솔, gcloud (터미널)

gcloud 가 훨씬 빠르고 자동화하기 좋다. 


# GCP 배포 방법 선택지

옵션 1: Cloud Run (추천 - 가장 간단)
컨테이너 기반, 서버리스, 자동 스케일링

옵션 2: GKE (Google Kubernetes Engine)
Kubernetes 기반, 대규모 운영에 적합

옵션 3: Compute Engine
VM 직접 운영, 가장 유연하지만 관리 부담 있음


# Cloud Run 

- 서비스와 작업의 차이 

**서비스 (Service)**는 HTTP 요청을 받아서 응답하는 상시 대기형.

웹 API, 웹사이트처럼 누군가 호출할 때 실행
요청이 오면 자동으로 스케일 업/다운
예: REST API 서버, 웹 앱, Keycloak 같은 서버

**작업 (Job)**은 실행하고 끝나는 일회성 배치형.

HTTP 요청 없이 그냥 코드 실행 후 종료
스케줄 등록하거나 수동으로 트리거
예: DB 마이그레이션, 데이터 처리, 리포트 생성, 크론 작업

- 배포유형 
  - 소스코드배포(Source)
  - 컨테이너 배포(Container) : 직접 빌드한 Docker 이미지를 Artifact Registry 나ㅣ Docker Hub 에서 가져와서 배포
    - CI/CD 파이프라인과 연동하기 좋음
  - Artifact Registry 연동 
    - Google 의 컨테이너 저장소인 Artifact Registry 에서 이미지를 가져와서 배포한다. 
    - IAM 권한으로 접근 제어 가능 
    - 컨테이너 이미지 배포와 비슷하지만 Google 생태계 내에서 관리 
  


Docker 전체 흐름 

Dockerfile (레시피)
↓ docker build
이미지 (완성된 요리)
↓ docker run
컨테이너 (실제 실행중인 앱) ✅


# Dockerfile 



# Cloud Run

Cloud Run : 컨테이너를 서버리스로 실행하는 Google 서비스 
- Docker 이미지로 배포 
- 서버리스 
- 자동 스케일링
- Https 자동 제공 : 배포하면 자동으로 URL 생성 
- SSL 인증서 관리 불필요 

Artifact Registry 는 Docker 이미지를 저장하는 창고
Artifact Registry
└── my-repo (repository) : 창고 안의 폴더 같은 개념 
└── my-app:latest (이미지)
└── my-app:v1.0.0 (이미지)


# Dockerfile 생성 

로컬 → Docker 이미지 빌드 → Artifact Registry 푸시 (창고에 저장) → Cloud Run 배포

asia-northeast3-docker.pkg.dev/hyperlounge-dev/aifybusiness-dev/aify-backend:latest
- 한번만 지정하면 됨 
gcloud auth configure-docker asia-northeast3-docker.pkg.dev
- 도커 빌드 (로컬)
docker build -t asia-northeast3-docker.pkg.dev/hyperlounge-dev/aifybusiness-dev/aify-backend:latest
- 도커 이미지 
gcloud artifacts docker images list asia-northeast3-docker.pkg.dev/hyperlounge-dev/aifybusiness-dev/aify-backend

# GCP 환경의 DB 연결 (Google Cloud SQL 사용)

1. Cloud Run 에 Cloud SQL 연결 추가 
Cloud Run 서비스 배포 시 Cloud SQL 인스턴스를 연결해줘야 한다. 
- 명령어 : gcloud run services update [서비스명] \
  --add-cloudsql-instances=[PROJECT_ID]:[REGION]:[INSTANCE_NAME]
- 콘솔 : Cloud Run → 서비스 → 편집 → 연결 탭 → Cloud SQL 연결 추가
- Cloud Run 의 Cloud SQL 커넥터는 Unix 소켓으로 연결된다. 
- TCP(127.0.0.1:5432) 방식은 Cloud Run 에서 동작하지 않는다. 
- Cloud SQL JDBC Socket Factory 사용 필요 
- TCP + Proxy 서버를 띄워서 적용하는 로컬과 달리 개발서버는 Cloud Run 으로 Proxy 서버 못 띄운다. (컨테이너라서)
- Socket 방식 로컬 Proxy 자동, Cloud Run Proxy 자동 실행 -> 두 환경이 동일한 방식으로 연결됨
  - 로컬과 클라우드 동일 환경
  - 최고 보안 (자동 mTLS)
  - IP 화이트리스트 불필요




