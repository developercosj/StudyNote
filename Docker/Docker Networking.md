- A Container only sees a network interface with an IP address, a gateway, a routing table, DNS services, and other networking details 

## User defined networks 
docker network create -d bridge my-net
docker run --network=my-net -itd --name=container3 busybox


## Connecting to multiple networks 

- A container can be connected to multiple networks 
- ex : a frontend container may be connected to a bridge network with external access(외부 접근이 가능한 브릿지 네트워크),
and a --internal network to communicate with containers running backend services that do not need external network access.  (외부 네트워크 접근이 필요없는 백엔드 서비스 컨테이너들과 통신하기 위해 --internal 네트워크에 연결될 수 있다.)
- 
### 브릿지 네트워크 
- 정의 : Docker 에서 기본적으로 제공하는 네트워크로, 외부와 통신이 가능


## Connecting to multiple networks 

- when sending packets, if the destination is an address directly connected network,  
packets are sent to that network. 
Otherwise, packets are sent to a default gateway for routing to their destination.


*ipvlan 네트워크 : Docker 나 Linux 컨테이너나 가상 인터페이스를 설정할 때 사용하는 네트워크 드라이버 중 하나 
ipvlan 은 호스트의 네트워크 인터페이스를 공유하면서도 독립적인 IP 사용 가능 
ipvlan network to provide internet access, and a bridge network for access to local services
ipvlan 네트워크를 사용할 때, 해당 네트워크의 게이트웨이 설정은 반드시 시스템의 기본 게이트웨이와 같아야 한다. 

- Gateway (게이트 웨이) : 네트워크에서 외부로 나가는 출입구 역할 장치 (라우터등)
- Default Gateway : 시스템이 외부 네트워크로 데이터를 보낼 때 기본적으로 사용하는 게이트 웨이 


# Publiched Ports 

By default, when you create or run a container using docker create or docker run, containers on bridge networks don't expose any ports to the outside world
Use the --publish or -p flag to make a port available to services outside the bridge network
This creates a firewall rule in the host, mapping a container port to a port on the Docker host to the outside world






