## JpaRepository 상속 구조 

Repository : 최상위 (마커 인터페이스)

CrudRepository<T, ID>     : 기본 CRUD 
    save, findById, delete 등   

PagingAndSortingRepository<T, ID> : 페이징, 정렬 추가 
    findAll(Pageable), findAll(Sort)

JpaRepository<T, ID>  : JPA 특화 기능 추가 
    flush, saveAndFlush, deleteInBatch 등

GyChatRoomDao : 우리가 만든것 


** 마커 인터페이스 (Marker Interface) : 아무 메서드도 없지만 특정 의미를 표시하기 위해 사용하는 인터페이스 
기능 구현이 아니라 표시 역할

- JpaRepository<GyChatRoom, Long> 제네릭 의미 
  GyChatRoom 은 엔티티 타입이고 Long 은 PK 타입 

- JpaRepository 는 : 기본 CRUD + 페이징 + 정렬 자동 제공 (CrudRepository 포함)

- JpaRepository 가 기본으로 제공하는 메서드

// 저장 / 수정

save(entity)           // INSERT 또는 UPDATE
saveAll(entities)      // 여러 개 한번에
saveAndFlush(entity)   // 저장 후 즉시 DB 반영

// 조회

findById(id)           // PK로 단건 조회 → Optional 반환
findAll()              // 전체 조회
findAll(pageable)      // 페이징 조회
findAll(sort)          // 정렬 조회
findAllById(ids)       // 여러 PK로 조회
existsById(id)         // 존재 여부 확인
count()                // 전체 개수

// 삭제

deleteById(id)         // PK로 삭제
delete(entity)         // 엔티티로 삭제
deleteAll()            // 전체 삭제
deleteAllInBatch()     // 배치로 전체 삭제 (쿼리 1번)


QuerydslPredicateExecutor 가 추가하는 이유  

JpaRepository 만 있을 때는 조건이 고정된 쿼리만 가능하나,
QuerydslPredicateExecutor 가 추가되면 동적 쿼리가 가능하다. 















