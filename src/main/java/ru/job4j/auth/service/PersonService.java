package ru.job4j.auth.service;

import org.springframework.stereotype.Service;
import ru.job4j.auth.domain.Person;
import ru.job4j.auth.repository.PersonRepository;

import java.util.List;
import java.util.Optional;

@Service
public class PersonService {
    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public List<Person> findAll() {
        return personRepository.findAll();
    }

    public Optional<Person> findById(int id) {
        return personRepository.findById(id);
    }

    public Person save(Person person) {
        return personRepository.save(person);
    }

    public boolean update(Person person) {
        if (personRepository.findById(person.getId()).isEmpty()) {
            return false;
        }
        personRepository.save(person);
        return true;
    }

    public boolean delete(Person person) {
        if (personRepository.findById(person.getId()).isEmpty()) {
            return false;
        }
        personRepository.delete(person);
        return true;
    }
}
