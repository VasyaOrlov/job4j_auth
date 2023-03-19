package ru.job4j.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.auth.domain.Person;
import ru.job4j.auth.service.PersonService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/person")
public class PersonController {
    private final PersonService persons;
    private final BCryptPasswordEncoder encoder;
    private static final Logger LOG = LoggerFactory.getLogger(PersonController.class.getName());
    private final ObjectMapper objectMapper;

    public PersonController(final PersonService persons, BCryptPasswordEncoder encoder, ObjectMapper objectMapper) {
        this.persons = persons;
        this.encoder = encoder;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/")
    public List<Person> findAll() {
        return this.persons.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> findById(@PathVariable int id) {
        var person = this.persons.findById(id);
        return new ResponseEntity<>(
                person.orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Person с данным id не найден."
                )),
                HttpStatus.OK
        );
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@RequestBody Person person) {
        if (person.getLogin().isEmpty() || person.getPassword().isEmpty()) {
            throw new NullPointerException("Поля бъекта person не заполнены");
        }
        person.setPassword(encoder.encode(person.getPassword()));
        if (!persons.update(person)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Person с данным id не найден.");
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        Person person = new Person();
        person.setId(id);
        if (!persons.delete(person)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Person с данным id не найден.");
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/sign-up")
    public void signUp(@RequestBody Person person) {
        if (person.getLogin().isEmpty() || person.getPassword().isEmpty()) {
            throw new NullPointerException("Поля бъекта person не заполнены");
        }
        if (person.getPassword().chars().filter(Character::isUpperCase).findFirst().isEmpty()) {
            throw new IllegalArgumentException("Ошибка пароля. Должна быть хотя бы 1 заглавная буква");
        }
        person.setPassword(encoder.encode(person.getPassword()));
        persons.save(person);
    }

    @ExceptionHandler(value = { IllegalArgumentException.class })
    public void exceptionHandler(Exception e, HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(new HashMap<>() { {
            put("message", e.getMessage());
            put("type", e.getClass());
        }}));
        LOG.error(e.getMessage(), e);
    }
}