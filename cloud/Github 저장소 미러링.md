- Github 저장소를 Cloud Source Repository 에 미러링 
Github 저장소로 푸시한 커밋이 Cloud Source Repositories 에서 호스팅된 저장소에 복사 또는 미러링 


Bitbucket Cloud, Github Cloud 저장소만 가능 (-> Cloud Source Repositories)
-> 즉 자체 서버에서 직접 설치해서 운영하는 것은 사용이 어렵다. 

머신 사용자 계정을 사용하는 것이 좋다. 


구글 클라우드 계정
└── 프로젝트 (hyperlounge-newbie-sandbox)
    ├── 저장소 (코드를 저장하는 공간으로 Git 의 Repo 와 완전히 같은 개념이다.) 
    ├── 서버 (Compute Engine)
    ├── DB (Cloud SQL)
    └── 기타 서비스들...



Github 미러링 Github 사용자 인증 정보 제공 필요 
사용자 인증 정보를 통해 Cloud Source Repositories 가 Github 저장소 콘텐츠에 액세스 



