# 모던 웹 개발

### 평가 기준

- 프론트엔드 개발
  - 클래스 컴포넌트 기반 React를 함수형 컴포넌트 기반 React로 변경
  - 기능 추가 (ex : 일괄 삭제 기능, 페이징 기능, 검색 기능, 정렬 기능, 외부 API 사용, OAuth 기능 사용)
  - 시연 동영상(1분 내외) - 유튜브 링크 제출
- 본인 깃허브에 소스 제출 - 깃허브 링크 제출
  - 프론트엔드 + 백엔드 둘 다
- 출석

### 백엔드

- 레이어드 아키텍처
  1. 프리젠테이션 레이어(Controller)
  2. 비즈니스 레이어(Service)
  3. 퍼시스턴스 레이어(Persistence)
  4. 데이터베이스 레이어(DB)
  - 프리젠테이션 레이어 : 사용자에게 요청을 받는 레이어, 사용자 요청 처리에 필요한 값을 비즈니스 레이어에 요청
  - 비즈니스 레이어 : 프리젠테이션 레이어에게 요청을 받는 레이어, 해당 요청 처리를 위해 필요한 값을 퍼시스턴스 레이어에 요청
  - 퍼시스턴스 레이어 : 비즈니스 레이어에게 요청을 받는 레이어, 해당 요청 처리를 위해 필요한 값을 데이터베이스 레이어에 요청
  - 데이터베이스 레이어 : 최하단의 레이어, 퍼시스턴스 레이어의 요청한 값을 반환
  - 데이터베이스 레이어 : 최하단의 레이어, 퍼시스턴스 레이어의 요청한
- CRUD 제작
  1. H2 DataBase 접속

     - application.properties에 h2 database configuration 설정
       ```
       spring.datasource.url=jdbc:h2:mem:testdb
       spring.datasource.driverClassName=org.h2.Driver
       spring.datasource.username=admin
       spring.datasource.password=1234
       spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
       spring.h2.console.enabled=true
       spring.h2.console.path=/h2-console
       spring.h2.console.settings.trace=false
       spring.h2.console.settings.web-allow-others=false
       ```
     - 웹 브라우저에서 “http://localhost:8080/h2-console”로 접근
       ![image 68.png](%E1%84%86%E1%85%A9%E1%84%83%E1%85%A5%E1%86%AB%20%E1%84%8B%E1%85%B0%E1%86%B8%20%E1%84%80%E1%85%A2%E1%84%87%E1%85%A1%E1%86%AF%20d4c18f2b315e40b691e08dd9b042c169/image_68.png)
     - 위의 화면에서 username, password 입력 후 접속 후 테이블 조회
       ![image 69.png](%E1%84%86%E1%85%A9%E1%84%83%E1%85%A5%E1%86%AB%20%E1%84%8B%E1%85%B0%E1%86%B8%20%E1%84%80%E1%85%A2%E1%84%87%E1%85%A1%E1%86%AF%20d4c18f2b315e40b691e08dd9b042c169/image_69.png)

  1. CRUD 기능 추가
     - TodoEntity.java
       ```java
       package com.example.todo.model;

       import javax.persistence.Entity;
       import javax.persistence.GeneratedValue;
       import javax.persistence.Id;
       import javax.persistence.Table;

       import org.hibernate.annotations.GenericGenerator;

       import lombok.AllArgsConstructor;
       import lombok.Builder;
       import lombok.Data;
       import lombok.NoArgsConstructor;

       @Builder
       @NoArgsConstructor
       @AllArgsConstructor
       @Data
       @Entity
       @Table(name = "Todo")
       public class TodoEntity {
           @Id
           @GeneratedValue(generator = "system-uuid")
           @GenericGenerator(name = "system-uuid", strategy = "uuid")
           private String id;
           private String userId;
           private String title;
           private boolean done;
       }
       ```
     - TodoRepository.java
       ```java
       package com.example.todo.persistence;

       import org.springframework.data.jpa.repository.JpaRepository;
       import org.springframework.data.jpa.repository.Query;
       import org.springframework.stereotype.Repository;

       import com.example.todo.model.TodoEntity;
       import java.util.List;

       @Repository
       public interface TodoRepository extends JpaRepository<TodoEntity, String> {

           @Query("select t from TodoEntity t where t.userId = ?1")
           List<TodoEntity> findByUserId(String userId);

       }
       ```
     - TodoDTO.java
       ```java
       package com.example.todo.dto;

       import com.example.todo.model.TodoEntity;

       import lombok.AllArgsConstructor;
       import lombok.Builder;
       import lombok.Data;
       import lombok.NoArgsConstructor;

       @Builder
       @NoArgsConstructor
       @AllArgsConstructor
       @Data
       public class TodoDTO {
           private String id;
           private String title;
           private boolean done;

           public TodoDTO(final TodoEntity entity) {
               this.id = entity.getId();
               this.title = entity.getTitle();
               this.done = entity.isDone();
           }

           public static TodoEntity toEntity(final TodoDTO dto) {
               return TodoEntity.builder()
                       .id(dto.getId())
                       .title(dto.getTitle())
                       .done(dto.isDone()).build();
           }
       }
       ```
     - ResponseDTO.java
       ```java
       package com.example.todo.dto;

       import java.util.List;

       import lombok.AllArgsConstructor;
       import lombok.Builder;
       import lombok.Data;
       import lombok.NoArgsConstructor;

       @Builder
       @NoArgsConstructor
       @AllArgsConstructor
       @Data
       public class ResponseDTO<T> {
           private String error;
           private List<T> data;
       }
       ```
     - TodoService.java
       ```java
       package com.example.todo.service;

       import java.util.List;
       import java.util.Optional;

       import org.springframework.beans.factory.annotation.Autowired;
       import org.springframework.stereotype.Service;

       import com.example.todo.model.TodoEntity;
       import com.example.todo.persistence.TodoRepository;

       import lombok.extern.slf4j.Slf4j;

       @Slf4j
       @Service
       public class TodoService {

           @Autowired
           private TodoRepository repository;

           public Optional<TodoEntity> create(final TodoEntity entity) {
               // Validations
               validate(entity);
               repository.save(entity);
               return repository.findById(entity.getId());
           }

           public List<TodoEntity> retrieve(final String userId) {
               return repository.findByUserId(userId);
           }

           public Optional<TodoEntity> update(final TodoEntity entity) {
               // Validations
               validate(entity);
               if (repository.existsById(entity.getId())) {
                   repository.save(entity);
               } else {
                   throw new RuntimeException("Unknown id");
               }
               return repository.findById(entity.getId());
           }

           public Optional<TodoEntity> updateTodo(final TodoEntity entity) {
               // Validations
               validate(entity);

               // 테이블에서 해당하는 id의 데이터셋 가져오기
               final Optional<TodoEntity> original = repository.findById(entity.getId());

               // original에 담긴 내용을 todo에 할당, tile, done 값 변경
               if (original.isPresent()) {
                   final TodoEntity todo = original.get();
                   todo.setTitle(entity.getTitle());
                   todo.setDone(entity.isDone());
                   repository.save(todo);
               }

               return repository.findById(entity.getId());
           }

           public String delete(final String id) {
               if (repository.existsById(id)) {
                   repository.deleteById(id);
               } else {
                   throw new RuntimeException("id does not exist");
               }
               return "Delete";
           }

           public void validate(final TodoEntity entity) {
               if (entity == null) {
                   log.warn("Entity cannot be null");
                   throw new RuntimeException("Entity cannot be null");
               }
               if (entity.getUserId() == null) {
                   log.warn("Unknown User");
                   throw new RuntimeException("Unknown User");
               }
           }
       }
       ```
     - TodoController.java
       ```java
       package com.example.todo.controller;

       import java.util.ArrayList;
       import java.util.List;
       import java.util.Optional;
       import java.util.stream.Collectors;

       import org.springframework.beans.factory.annotation.Autowired;
       import org.springframework.http.ResponseEntity;
       import org.springframework.web.bind.annotation.DeleteMapping;
       import org.springframework.web.bind.annotation.GetMapping;
       import org.springframework.web.bind.annotation.PostMapping;
       import org.springframework.web.bind.annotation.PutMapping;
       import org.springframework.web.bind.annotation.RequestBody;
       import org.springframework.web.bind.annotation.RequestMapping;
       import org.springframework.web.bind.annotation.RestController;

       import com.example.todo.dto.TodoDTO;
       import com.example.todo.model.TodoEntity;
       import com.example.todo.service.TodoService;
       import com.example.todo.dto.ResponseDTO;

       import lombok.extern.slf4j.Slf4j;

       @Slf4j
       @RestController
       @RequestMapping("todo")
       public class TodoController {

           @Autowired
           private TodoService service;

           @PostMapping
           public ResponseEntity<?> createTodo(@RequestBody TodoDTO dto) {
               try {
                   // POST localhost:8080/todo
                   log.info("Log: create Todo entrance");

                   // dto를 이용해 테이블에 저장하기 위한 entity 생성
                   TodoEntity entity = TodoDTO.toEntity(dto);
                   log.info("Log: dto => entity ok!!!");

                   // entity userId 임시 지정
                   entity.setUserId("temporary-user");

                   // service.create를 통해 repository에 entity 저장
                   // 이떄, 넘어오는 값이 null일 경우를 상정하여 Optional 사용
                   Optional<TodoEntity> entities = service.create(entity);
                   log.info("Log: service.create ok!!!");

                   // entities를 dtos로 스트림 변환
                   List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());
                   log.info("Log: entities => dtos ok!!!");

                   // ResponseDTO 생성
                   ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();
                   log.info("Log: response dto ok!!!");

                   return ResponseEntity.ok().body(response);

               } catch (Exception e) {
                   String error = e.getMessage();
                   ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().error(error).build();
                   return ResponseEntity.badRequest().body(response);
               }
           }

           @GetMapping
           public ResponseEntity<?> retrieveTodoList() {
               // GET localhost:8080/todo

               // repository에서 userId가 "temporary-user"인 entity 가져오기
               String temporaryUserId = "temporary-user";
               List<TodoEntity> entities = service.retrieve(temporaryUserId);

               // entities를 dtos로 스트림 변환
               List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());

               // ResponseDTO 생성
               ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();

               return ResponseEntity.ok().body(response);
           }

           // SpringBoot 에서 Get 메소드로 body의 값을 받아올 수 없기 때문에 해당 메소드 사용이 불가능 하다.
           // Get을 이용한 update 방법
           @GetMapping("/update")
           public ResponseEntity<?> update(@RequestBody TodoDTO dto) {
               try {
                   // GET localhost:8080/todo/update

                   TodoEntity entity = TodoDTO.toEntity(dto);

                   // entity userId 임시 지정
                   entity.setUserId("temporary-user");

                   // service.update를 통해 repository에 entity 갱신
                   // 이떄, 넘어오는 값이 null일 경우를 상정하여 Optional 사용
                   Optional<TodoEntity> entities = service.update(entity);

                   // entities를 dtos로 스트림 변환
                   List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());

                   // ResponseDTO 생성
                   ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();

                   return ResponseEntity.ok().body(response);
               } catch (Exception e) {
                   String error = e.getMessage();
                   ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().error(error).build();

                   return ResponseEntity.badRequest().body(response);
               }
           }

           // Put을 이용한 update 방법
           @PutMapping()
           public ResponseEntity<?> updateTodo(@RequestBody TodoDTO dto) {
               try {
                   // PUT localhost:8080/todo

                   TodoEntity entity = TodoDTO.toEntity(dto);

                   // entity userId 임시 지정
                   entity.setUserId("temporary-user");

                   // service.updateTodo를 통해 repository에 entity 갱신
                   // 이떄, 넘어오는 값이 null일 경우를 상정하여 Optional 사용
                   Optional<TodoEntity> entities = service.updateTodo(entity);

                   // entities를 dtos로 스트림 변환
                   List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());

                   // ResponseDTO 생성
                   ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();

                   return ResponseEntity.ok().body(response);
               } catch (Exception e) {
                   String error = e.getMessage();
                   ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().error(error).build();

                   return ResponseEntity.badRequest().body(response);
               }
           }

           @DeleteMapping
           public ResponseEntity<?> delete(@RequestBody TodoDTO dto) {
               try {
                   // DELETE localhost:8080/todo

                   List<String> message = new ArrayList<>();

                   // service.delete를 통해 repository에 entity 삭제
                   String msg = service.delete(dto.getId());
                   message.add(msg);

                   // ResponseDTO 생성
                   ResponseDTO<String> response = ResponseDTO.<String>builder().data(message).build();

                   return ResponseEntity.ok().body(response);
               } catch (Exception e) {
                   String error = e.getMessage();
                   ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().error(error).build();

                   return ResponseEntity.badRequest().body(response);
               }
           }
       }
       ```
