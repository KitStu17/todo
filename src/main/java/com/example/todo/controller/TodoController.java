package com.example.todo.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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
            entity.setUserId("temporary-userid");

            // service.create를 통해 repository에 entity 저장
            // 이떄, 넘어오는 값이 null일 경우를 상정하여 Optional 사용
            Optional<TodoEntity> entities = service.create(entity);
            log.info("Log: service.create ok!!!");

            // entities를 dtios로 스트림 변환
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
}
