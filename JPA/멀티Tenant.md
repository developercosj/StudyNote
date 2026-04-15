# 클래스 역할
TenantContext	현재 요청의 테넌트 코드를 ThreadLocal에 보관
TenantIdentifierResolver	Hibernate가 쿼리 전에 호출 → TenantContext에서 테넌트 코드 꺼내서 전달
SchemaMultiTenantConnectionProvider	테넌트 코드 받아서 SET search_path로 DB 스키마 전환
JpaMultiTenancyConfig	위 두 클래스를 Hibernate에 등록 + SCHEMA 전략 활성화
