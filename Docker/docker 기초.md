
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
