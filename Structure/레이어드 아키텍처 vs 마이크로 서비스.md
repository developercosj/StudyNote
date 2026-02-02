1. 레이어드 아키텍처(모놀로식)
모든 기능이 하나의 프로젝트에 존재

하나의 애플리케이션 
- Presentation Layer
- Business Layer
- Persistence Layer
- Database Layer 

2. 마이크로서비스 (Microservices)

각 서비스가 독립적인 프로젝트 + DB 

```
주문 서비스          상품 서비스          사용자 서비스
├── API             ├── API             ├── API
├── Business        ├── Business        ├── Business
├── Data            ├── Data            ├── Data
└── DB              └── DB              └── DB
```

- 각 서비스를 다른 시간에 배포 가능 
- 각각 서비스의 기술 스택이 자유 
- 서비스 장애가 전체로 번지지 않음

