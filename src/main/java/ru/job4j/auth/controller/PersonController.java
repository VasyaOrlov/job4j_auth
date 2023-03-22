package ru.job4j.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.auth.domain.Person;
import ru.job4j.auth.dto.PersonDTO;
import ru.job4j.auth.service.PersonService;
import ru.job4j.auth.util.ValidGroup;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/person")
@Validated
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
    public ResponseEntity<List<Person>> findAll() {
        return new ResponseEntity<>(persons.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> findById(@Min(0) @PathVariable int id) {
        var person = this.persons.findById(id);
        return new ResponseEntity<>(
                person.orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Person с данным id не найден."
                )),
                HttpStatus.OK
        );
    }

    @PutMapping("/")
    @Validated(ValidGroup.OnUpdate.class)
    public ResponseEntity<Void> update(@Valid @RequestBody Person person) {
        person.setPassword(encoder.encode(person.getPassword()));
        if (!persons.update(person)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Person с данным id не найден.");
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Min(0) @PathVariable int id) {
        Person person = new Person();
        person.setId(id);
        if (!persons.delete(person)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Person с данным id не найден.");
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/sign-up")
    @Validated(ValidGroup.OnCreate.class)
    public ResponseEntity<?> signUp(@Valid @RequestBody Person person) {
        person.setPassword(encoder.encode(person.getPassword()));
        persons.save(person);
        return ResponseEntity.ok("success");
    }

    @PatchMapping("/")
    public ResponseEntity<Void> updatePassword(@Valid @RequestBody PersonDTO personDTO) {
        Person person = persons.findByLogin(personDTO.getLogin());
        if (person == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Person с данным login не найден.");
        }
        person.setPassword(personDTO.getPassword());
        persons.update(person);
        return new ResponseEntity<>(HttpStatus.OK);
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