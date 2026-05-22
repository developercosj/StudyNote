인증 
- ADC (애플리케이션 기본 사용자 인증 정보) 
- API Key


Google GenAI SDK 지원 방식 2가지 : Google AI Studio, Vertex AI 

1. Google AI Studio
인증 : API Key 
대상 : 개인, 프로토콜 
데이터 처리 : Google 서버 
컴플라이언스 : 기본 

4. Vertex AI 
인증 : GCP 서비스계정, ADC 
대상 : 기업, 프로덕션 
데이터 처리 : 지정한 GCP 리전 
컴플라이언스 : VPC, 감사 로그 등 가능 

생성형 AI SDK -> Gemini API 코드 개발 



# Google Agent ADK 개발 


ADK 가 찾는 이름 2개가 있다. 
agent.py : ADK 가 이 파일 먼저 열어본다. 
root_agent : ADK 가 파일 안에서 이 이름을 찾는다. 

google-genai : Gemini AI 모델을 호출하는 라이브러리 (Gemini AI 와 통신하는 도구)
Google ADK(Agent Development Kit) : 에이전트 시스템을 만드는 프레임워크 
ADK 가 내부적으로 google-genai 를 사용해서 AI 를 호출한다. 




# Vertex AI Agent Engine 배포시 

1. adk deploy 실행 
2. Google 이 Agent Engine 인스턴스에 서비스 계정을 자동으로 붙여줌 
3. 코드가 bigquery.Client() 를 생성할 때 Google 메타데이터 서버에서 토큰 자동 발급
4. 별도 파일/환경변수 없이 BigQuery 연결 성공


Agent Engine 내부에는 169.254.169.254 메타데이터 서버가 있고, SDK 가 이 서버에서 자동으로 엑세스 토큰을 받아온다. 

# ADC(Application Default Credentials)가 환경을 자동 감지하는 순서 
bigquery.Client() 호출 
1순위 : GOOGLE_APPLICATION_CREDENTIALS 환경변수에 키 파일 경로 (없으면 다음 순서) 
2순위 : gcloud ADC 파일 있는지 확인 (~/.config/gcloud/adc.json) -> 로컬에서 사용
3순위 : GCE/Cloud Run/Agent Engine 메타데이터 서버 응답 -> 배포 환경에서 여기 해당 
인증 실패 


* 배포 시 자동이 되려면 IAM 권한 설정은 수동으로 진행 필요 
* 배포 환경에서는 서버에 붙은 서비스 계정으로 로그인된다. ADC 가 알아서 판단해서 BigQuery 에 접속 


# Bigquery 연결 방식
로컬 개발 : ADC(gcloud auth application-default login)
배포 (GCP 내부) : 서비스 계정 자동 주입 (현재 방식)
배포 (GCP 외부 서버) : 서비스 계정 키 파일 or Workload Identity
CI/CD(Giyhub Actions 등) : Workload Identity Federation 
사용자별 권한 분리 필요 : OAuth2 토큰 직접 사용
키 파일 절대 금지 정책 : Workload Identity or ADC 방법 사용 


# 배포시 빅쿼리 서비스 계정 자동 주입 설정 방법 
1. GCP에서 서비스 계정 생성
   ↓
2. 서비스 계정에 BigQuery 권한 부여
   ↓
3. Vertex AI Agent Engine 배포 시 서비스 계정 지정
   ↓
4. 코드는 그대로 → 자동으로 해당 계정으로 BQ 접근

# README 기준 현재 배포 명령어
uv run adk deploy agent_engine \
--project=hyperlounge-dev \
--region=us-central1 \
--display_name="aify_agent_test" \
--staging_bucket=gs://hyperlounge-dev-adk-staging \
--service_account="aify-agent-sa@hyperlounge-dev.iam.gserviceaccount.com" \
agent1



# Workload Identity  
지원되는 외부 환경 : GitHub Actions, AWS EC2, Azure VM, 온프레미스 
키 등록하지 않고 미리 GCP 에 등록하는 방식 




