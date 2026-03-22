
separate your application from your infrastructure


1. Docker 기본
    - 개발 환경을 통일할 수 있다. -> 로컬마다 다른 환경
    - 컨테이너에 필요한 OS, 라이브러리, 설정 등을 포함하기 때문에 환경 차이가 없음 
    - 컨테이너 이미지 하나로 개발 -> 테스트 -> 배포
    - CI/CD 파이프라인에서 도커는 표준 
    - 기존 가상머신보다 훨씬 가볍고 빠르게 실행
    - OS 전체를 가상화하지 않고 호스트 OS 커널 공유 
    - 버전 관리와 롤백 용이 
    - 멀티 플랫폼 지원 : 한번 만든 도커 이미지를 리눅스, 맥, 윈도우 등 다양한 환경에서 재사용 가능 


2. The Docker Daemon
    - listens for Docker API requests
    - manage Docker objects (images, containers, networks, volumes)
    - can communicate with other daemons

3. Docker Desktop include

    - Docker daemon (dockerd)
    - Docker client (docker)
    - Docker Compose
    - Docker Content Trust
    - Kubernetes
    - Credential Helper


4. Docker registries
    - A docker registry stores Docker images
    - Docker Hub is a public registry


5. namespace
    - provide the isolated workspace
    - Docker creates a set of namespaces when run container
    - namespaces provide a layer of isolation
    - Each aspect of a container runs in a separate namespace and its access is limited to the namespace


6. Docker Objects
    - Images
        - An image is a read-only template with instructions for creating a Docker container
        - An image is based on another image ( + additional customization)
        * important
            - To build your own image, you create a Dockerfile with a simple syntax for defining the steps needed to create the image and run it
              Each instruction in a Dockerfile creates a layer in the image
              When you change the Dockerfile and rebuild the image, only those layers which have changed are rebuild

    - Containers
        -  runs an ubuntu container example : docker run -i -t ubuntu /bin/bash
        -  process reference : https://docs.docker.com/guides/docker-overview/#docker-registries
            1. If you don't have the ubuntu image locally, Docker pulls it from your configured registry, as though you had run docker pull ubuntu manually.

            2. Docker creates a new container, as though you had run a docker container create command manually.

            3. Docker allocates a read-write filesystem to the container, as its final layer. This allows a running container to create or modify files and directories in its local filesystem.

            4. Docker creates a network interface to connect the container to the default network, since you didn't specify any networking options. This includes assigning an IP address to the container. By default, containers can connect to external networks using the host machine's network connection.

            5. Docker starts the container and executes /bin/bash. Because the container is running interactively and attached to your terminal (due to the -i and -t flags), you can provide input using your keyboard while Docker logs the output to your terminal.

            6. When you run exit to terminate the /bin/bash command, the container stops but isn't removed. You can start it again or remove it.

7. 도커 이미지
    - 컨테이너 만드는데 사용하는 읽기 전용 템플릿
    - 컨테이너를 운영할때 사용됨
8. 도커 컨테이너
    - 도커 이미지 실행
9. Docker compose
    - 단일 서버에서 여러개의 컨테이너를 하나의 서비스로 운용할 수 있도록 컨테이너 묶음 관리
    - 여러 개의 컨테이너의 옵션과 환경을 정의한 파일을 읽어 컨테이너를 순차적으로 생성하는 방식으로 동작
    - 설정 파일 : rund 명령어 옵션 그대로 사용
    - 도커 컴포즈 사용시 컨테이너 설정 저장 yaml 파일 필요
        docker-compose.yml 파일

10. File sharing


    By default the /Users, /Volumes, /private, /tmp and /var/folders directory are shared.
    If your project is outside this directory then it must be added to the list,
    otherwise you may get Mounts denied or cannot start service errors at runtime.
    # 출처 : https://docs.docker.com/desktop/settings/mac/?uuid=90c10b31-676a-4e10-a1bc-f7f42e973e36%0A#file-sharing


11. 도커 실행
    : docker run -d -p 8080:80 docker/welcome-to-docker
    -  -d : 컨테이너를 백그라운드에서 실행
    - 8080 은 호스트 머신에서 접근할 포트
    - 80은 컨테이너 내부에서 사용되는 포트로 컨테이너 내부에서 애플리케이션이 수신 대기하는 포트

12. docker compose watch
    - 도커 컴포즈에서 파일 변경 자동 감지, 변경사항이 발생할 때 마다 컨테이너를 다시 빌드하거나 재시작하는 기능 제공 도구


13. docker postgresql
    - https://hub.docker.com/_/postgres

14. docker-compose.yml
    - docker-compose.yml 파일은 일반적으로 프로젝트 루트에 위치해 있고 docker-compose up 명령어 실행시 자동으로 해당 파일을 찾아 컨테이너를 세팅함
      containers lifecycle

15. docker architecture
    - The Docker client talks to the Docker Deamon
      -> heavy lifting of building, running, and distributing your Docker containers
        - operation
            - docker client, deamon can be run on the save system or can connect a Docker client to a remote Docker daemon (using a REST API, over UNIX socket or network interface)
    - Docker Compose
        - another Docker client (applications consisting of a set of containers)


16. Docker Compose 

    - .env 파일을 찾는 순서
    1. docker-compose.yml 이 있는 폴더 내부의 .env 파일
    2. 환경변수 (Window/Mac Linux OS 환경변수)
    3. docker-compose.yml 내부의 직접 입력된 값 
    -> compose.yml 과 같은 폴더에 .env 파일만 있으면 자동으로 적용됨 


17. Dockerfile

Dockerfile 만 쓸때 
- 앱 하나만 컨테이너로 만들때 
- CI/CD 에서 이미지 빌드할때 



Dockerfile 
- 이미지 설계도 
- Docker 자체 전용 문법이라 별도 확장자 불필요 
docker-composel.yml 
- 컨테이너 실행 설정 




전체 흐름 
로컬 -> Docker 이미지 빌드 -> GCP 서버에 올려서 실행 

1. Dockerfile 작성 
2. 로컬에서 Docker 이미지 빌드 & 테스트 
# 이미지 빌드
docker build -t my-spring-app .

# 로컬에서 테스트 실행
docker run -p 8080:8080 my-spring-app
3. GCP 서버로 이미지 전달 
3-1. Docker Hub 이용 
# 로컬에서
docker tag my-spring-app 도커허브아이디/my-spring-app:latest
docker push 도커허브아이디/my-spring-app:latest

# GCP 서버에서
docker pull 도커허브아이디/my-spring-app:latest
docker run -d -p 8080:8080 도커허브아이디/my-spring-app:latest

3-2. GCP Artifact Registry 이용 (GCP 정석)

# 로컬에서
docker tag my-spring-app asia-northeast3-docker.pkg.dev/[프로젝트ID]/[저장소명]/my-spring-app:latest
docker push asia-northeast3-docker.pkg.dev/...

# GCP 서버에서
docker pull asia-northeast3-docker.pkg.dev/...
docker run -d -p 8080:8080 ...

4. GCP 서버에 Docker 설치 
# GCP VM SSH 접속 후
sudo apt-get update
sudo apt-get install -y docker.io
sudo systemctl start docker
sudo systemctl enable docker

# sudo 없이 docker 쓰려면
sudo usermod -aG docker $USER
# 로그아웃 후 재접속


유용한 Docker 명령어 

docker ps                         # 실행 중인 컨테이너 확인
docker logs [컨테이너ID]           # 로그 확인
docker stop [컨테이너ID]           # 중지
docker rm [컨테이너ID]             # 삭제
docker run -d ...                 # 백그라운드 실행 (-d 옵션)



# Docker 의 전체 구조 
2단계(멀티 스테이지)로 나뉜다. 
1단계 (Builder) : 소스코드 -> JAR 파일로 빌드 
2단계 (runtime) : JAR 파일만 가져와서 실행 

빌드도구(Gradle, JDK 등)는 빌드할 때만 필요하고 실제 서버 실행 시엔 불필요하다. 
2단계로 나누면 최종 이미지 용량이 훨씬 작아진다. 


# 예시로 파일 알아보기 


## 1단계: Build Stage

1-1.FROM gradle:8.12-jdk21 AS builder
Gradle + JDK 12 이 설치된 환경을 베이스로 사용. builder 라고 이름 붙임
1-2. WORKDIR /app
컨테이너 안에서 작업할 디렉토리를 /app 으로 지정 (cd /app 과 같은 효과)
Docker 컨테이너는 독립된 가상 리눅스 환경입니다. 
그 가상환경 안에서 앞으로 작업할 디렉토리를 /app 입니다라고 지정하는 것 

1-3. COPY build.gradle.kts settings.gradle.kts gradle.properties ./
   COPY gradle ./gradle
로컬의 Gradle 설정 파일들을 컨테이너로 복사 

1-4. RUN gradle dependencies --no-daemon --quiet
build.gradle.kts에 정의된 라이브러리들을 미리 다운로드.

1-5. COPY src ./src
   RUN gradle bootJar --no-daemon -x test
실제 소스코드를 복사한 뒤 빌드. -x test는 테스트 실행을 건너뜀.
결과물로 /app/build/libs/aify-1.0.0.jar 같은 JAR 파일이 생성됨.


## 2단계: Run Stage

2-1.FROM eclipse-temurin:21-jre-jammy
JRE(실행 전용, JDK 아님)만 있는 가벼운 이미지를 베이스로 사용.
1단계의 Gradle, JDK 컴파일러 등은 여기엔 없음 → 이미지가 가벼워짐.

2-2.RUN groupadd -r aify && useradd -r -g aify aify
보안을 위해 aify라는 전용 사용자 계정 생성.
기본적으로 Docker는 root로 실행되는데, root로 서버를 돌리면 보안상 위험함.

2-3. COPY --from=builder /app/build/libs/*.jar app.jar
1단계(builder) 에서 만든 JAR 파일만 가져옴.
소스코드, Gradle 등은 가져오지 않음.

2-4. RUN chown aify:aify app.jar
USER aify
jar 파일 소유자를 aify 유저로 변경하고 이후 명령을 aify 유저로 실행 

2-5. ENV TZ=Asia/Seoul 
서버 타임존을 한국 시간으로 설정 (application.yml 설정과 맞춤) 

2-6. EXPOSE 8080
이 컨테이너가 8080 포트를 사용한다고 선언. (실제로 포트를 여는 건 docker run 시 -p 옵션)

2-7. ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
컨테이너가 시작될 때 실행할 명령어.
java -jar app.jar와 동일. -Djava.security.egd=...는 난수 생성 속도를 높여 서버 시작 속도를 개선하는 옵션.



*** 참고 
EXPOSE 8080 
EXPOSE 는 실제로 포트를 연느 게 아니라 이 컨테이너는 8080 포트를 쓸 예정이에요 라고 문서화하는 선언이다. 
실제로 포트를 열러면 docker run 할때 -p 옵션이 필요하다 

내 PC (호스트)              컨테이너 내부
:9090          ←→         :8080

docker run -p 9090:8080 aify-backend
#              ↑     ↑
#           내 PC   컨테이너

# 보통은 같은 포트로 맞춰서 씁니다
docker run -p 8080:8080 aify-backend

EXPOSE 8080	"나는 8080 쓸 거야" 선언만 함 (실제로 열지 않음)
-p 8080:8080	실제로 포트를 연결함
EXPOSE 없이 -p만 써도 동작은 하지만, 다른 개발자가 Dockerfile을 봤을 때 어떤 포트를 쓰는지 바로 알 수 있도록 관습적으로 작성합니다.





