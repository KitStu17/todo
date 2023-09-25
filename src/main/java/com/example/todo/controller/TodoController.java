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
    //
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
