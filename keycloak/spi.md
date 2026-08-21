keyclock 에서 인증 JWT 토큰 발급 전에 백엔드 서버를 호출해서, 권한으로 사용할 팀 리스트 추가

ProtocolMapper는 JWT 토큰을 만들때 실행
ProtocolMapper SPI 를 사용 -> JWT 가 만들어지는 시점에 끼어들어서 ai_permissions 필드 추가


Keycloak 이 토큰을 만들다가 setClaim() 이 구현된 클래스가 있으면 자동으로 호출해주는 방식

Jar 파일은 Keyclock 서버 자체에서 들어간다.
Jar 파일을 Keyclock 이 실행되는 서버의 특정 폴더에 넣으면, Keyclock JVM 프로세스가 시작할때 그 Jar 를 자기 메모리 안으로 불러들인다.

# 폴더 구조
/opt/keycloak/
├── bin/
├── conf/
└── providers/          ← 여기에 JAR 파일을 복사
    └── my-mapper.jar   ← 이게 Keycloak 재시작 시 JVM에 로드됨



# Admin Console에 목록이 나타나는 이유
   Keycloak이 시작할 때 providers/ 폴더를 스캔하면서 JAR 안의 META-INF/services/ 파일을 읽습니다. 거기에 "나는 ProtocolMapper를 구현했어요" 라고 선언되어 있으면, Keycloak의 SPI 레지스트리에 등록되고, Admin Console UI가 그 레지스트리를 읽어서 드롭다운 목록에 보여주는 구조입니다.

# Docker 로 쓴다면 
Jar 파일 하나를 볼륨으로 마운트하면 됨 


어드민콘솔에서 이미 등록된 JAR 를 Mapper 등록 


순서
1. Jar 파일을 Keyclak 서버 파일 시스템에 복사
2. Keycloak 재시작 (JAR 로드)
3. Admin Console 에서 AI Permission Mapper 타입이 드롭다운에 나타남
4. Admin Console 에서 Mapper 등록 


keycloak 은 vm 에 docker 로 올려져 있다. 

# 마운트 볼륨	없음 ("Mounts": []) 의 의미 

볼륨(Volume) 이란 : Docker 컨테이너는 격리된 박스 
컨테이너 안에서 파일을 만들어도 컨테이너가 삭제되면 다 사라진다. 
볼륨이란 이 박스와 실제 서버 폴더를 연결하는 통로 
[VM 서버 폴더] <-> Docker 컨테이너 내부 
볼륨 연결이 하나도 없음 = 컨테이너가 완전히 독립된 박스로만 돌아가고 있음 


# 문제 : 볼륨 설정이 없다. 
vm 서버에 jar 파일을 복사하고 
keycloak 재시작하면 끝이며 이미지 재빌드가 필요 없음 
볼륨설정이 없는것이 문제 

1. 볼륨 추가 (docker-compose.yml 수정)
- 간단하고 파일만 교체하면 되지만, Keycloak 재시작해야함 
2. 이미지 재빌드 
- 영구적으로 이미지에 포함
- 빌드 파이프라인 필요하고 복잡하다.

┌─────────────────────────────────────────┐
│           GCP VM 서버                    │
│                                         │
│  ┌──────────────────────────────────┐   │
│  │  Docker                          │   │
│  │                                  │   │
│  │  ┌─────────────┐  ┌──────────┐  │   │
│  │  │  keycloak   │  │sso-bridge│  │   │
│  │  │  컨테이너    │  │ 컨테이너  │  │   │
│  │  └─────────────┘  └──────────┘  │   │
│  └──────────────────────────────────┘   │
│                                         │
│  /home/seungtak_choi_hyperlounge_ai/    │
│    keycloak/docker-compose.yml          │
│    sso-bridge/docker-compose.yml        │
│                                         │
└─────────────────────────────────────────┘
    