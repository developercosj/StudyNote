

    #Client-server application 

    - dockerd : A server with a long-running daemon process 
        - 항상 실행중인 핵심 백그라운드 프로세스
        -  항상 켜져 있어야 컨테이너 관리 가능 
        - 도커 클라이언트로 명령어로 요청을 받아서 컨테이너를 생성하고, 관리하고, 네트워크 설정, 이미지 저장소와 통신하는 등 작업 수행 


    - Docker APIs 
        - Docker daemon은 REST API를 통해 클라이언트와 통신 
        - 클라이언트는 HTTP 요청을 통해 Docker daemon에 명령을 보냄 
        - Docker daemon은 요청을 처리하고 결과를 클라이언트에게 반환


    - Storage 
        - 컨테치너 안에 만들어진 파일들은 writable containter layer 에 저장된다. 
            **writable container layer : unique layer for each container 
        - Storage mount Options 
            - Docker 는 storage mount options 을 지원하여 컨테이너의 writable layer 의 바깥에 데이터를 저장할 수 있다. 

            - 종류
                1. Volume mounts 
                    Volume data is stored on the filesystem 
                    must mount the volume to container 
                    직접적으로 볼륨 데이터에 접근하는것은 지원하지 않아서 오류가 발생 할 수 있다.    
                    중요 데이터는 컨테이너 외부에 저장 필요 
                    volumes are ideal for performance-critical data processing and long-term storage needs
                        -> storage location is managed on daemon host

                2. Bind mounts
                3. Bind Mounts
                    - 호스트 시스템의 디렉토리나 파일을 컨테이너에 연결하는 방법 
                    - 컨테이너가 종료되더라도 호스트의 데이터는 유지됨 
                    - 데이터베이스, 로그 파일 등 지속적인 데이터 저장에 유용
                    - Bind mounts create a direct link between the host system path and a container, allowing access to tiles or directories stored anywhere omn the host
                4. Tmpsfs Mounts (임시 파일 시스템)
                    - 메모리에 저장되는 임시 파일 시스템  (A tmpfs mount stores files directly in the host machine's memory)
                    - 민감한 데이터를 일시적으로 저장할 때 사용 
                    - 컨테이너가 실행되는 동안만 존재하는 임시 저장소 
                    - 컨테이너가 종료되면 데이터가 사라짐 (A tmpfs mounts do not persist data either on the Docker host or within the container's filesystem) 
                    - 민감한 데이터나 캐시 데이터를 저장하는 데 사용됨

                5. Named pipes 
                    Named pipes can be used for communitaion between the Docker host and a container


    - 도커 컨테이너는 여러 Image 로 구성 
    1.  Image Layers 
        - 여러 개의 read-only layer 로 구성 레이어 
        - 각 레이어는 이전 레이어를 기반으로 쌓임 Ubuntu -> Python -> App 코드 
        - 변경 불가능 immutable 
        - 도커 이미지를 구성 

    2.  Container layer    
        - 컨테이너가 실행될 때 생성되는 writable layer 
        - 컨테이너가 실행되는 동안 파일 시스템의 변경 사항이 저장됨 
        - 컨테이너가 종료되면 이 레이어는 삭제됨
        - Data written to this layer is not persisted after the container stops 
        - 

    3. Union File System (UFS)
        - 여러 레이어를 하나의 파일 시스템으로 결합하는 기술 
        - 각 레이어는 독립적으로 존재하지만, 최종적으로는 하나의 통합된 파일 시스템으로 보여짐 -> 통합된 디렉토리처럼 보인다. 
        - OverlayFS, aufs, btrfs 등 다양한 UFS 구현이 존재
        - 도커는 UFS를 사용하여 이미지와 컨테이너의 파일 시스템을 관리

