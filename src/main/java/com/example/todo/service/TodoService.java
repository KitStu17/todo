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
