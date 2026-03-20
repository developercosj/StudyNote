TenantContext : 멀티테넌시(Multi-tenantcy) 아키텍처에서 현재 요청이 어느 테넌트(고객사/조직)에 속하는지를 추적하는 컨텍스트 홀더 
- 보통 내부적으로 ThreadLocal 기반으로 구현 
- 각 스레드는 자신만의 독립된 저장공간을 가지므로, 테넌트 정보가 섞이지 않는다. 



# 컨텍스트가 유실되는 주요 원인들

- clear() 미호출 -> 스레드 재사용 오염 
  - 요청 종료 후 clear 안 하면 스레드풀에서 재사용될 때 이전 테넌트 정보가 남아있음 
- 비동기 처리(@Async, CompletableFuture)
  - 새 스레드는 부모의 ThreadLocal 을 상속받지 않음 

        TenantContext.setCurrentTenant("COMPANY_A");
        CompletableFuture.runAsync(() -> {
            TenantContext.getCurrentTenant(); // null ! 유실됨         
    
        }


        )

- 스케줄러(@Scheduled) 
  - 스레줄러 스레드는 요청 컨텍스트가 없음


        @Scheduled(fixedDelay = 1000) 
        public void scheduledTask() {
            

        }


- 유실 없이 안전하게 유지하는 방법 
  - 1. Filter/Interceptor 에서 생명주기 완전 관리
  1. 1. @Async 스레드풀에 TaskDecorator 적용