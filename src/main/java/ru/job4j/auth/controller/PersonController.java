package ru.job4j.auth.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.job4j.auth.domain.Person;
import ru.job4j.auth.service.PersonService;

import java.util.List;

@RestController
@RequestMapping("/person")
public class PersonController {
    private final PersonService persons;
    private final BCryptPasswordEncoder encoder;
    private static final Logger LOG = LoggerFactory.getLogger(PersonController.class.getName());

    public PersonController(final PersonService persons, BCryptPasswordEncoder encoder) {
        this.persons = persons;
        this.encoder = encoder;
    }

    @GetMapping("/")
    public List<Person> findAll() {
        return this.persons.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> findById(@PathVariable int id) {
        var person = this.persons.findById(id);
        return new ResponseEntity<>(
                person.orElse(new Person()),
                person.isPresent() ? HttpStatus.OK : HttpStatus.NOT_FOUND
        );
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@RequestBody Person person) {
        ResponseEntity<Void> response;
        person.setPassword(encoder.encode(person.getPassword()));
        try {
            if (!persons.update(person)) {
                response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else {
                response = new ResponseEntity<>(HttpStatus.OK);
            }
        } catch (Exception e) {
            LOG.error("Ошибка обновления person: " + e.getMessage(), e);
            response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        Person person = new Person();
        person.setId(id);
        ResponseEntity<Void> response;
        try {
            if (!persons.delete(person)) {
                response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else {
                response = new ResponseEntity<>(HttpStatus.OK);
            }
        } catch (Exception e) {
            LOG.error("Ошибка удаления person с id: " + id + e.getMessage(), e);
            response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    @PostMapping("/sign-up")
    public void signUp(@RequestBody Person person) {
        person.setPassword(encoder.encode(person.getPassword()));
        persons.save(person);
    }
}