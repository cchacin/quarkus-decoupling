package org.acme;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.value.ValueCommands;

import java.util.Optional;

public class PeopleService {
    public static final String PERSON_KEY = "person:";
    private final PeopleRepository repository;
    private final ValueCommands<String, Person> valueCommands;

    public PeopleService(PeopleRepository repository, RedisDataSource redis) {
        this.repository = repository;
        this.valueCommands = redis.value(Person.class);
    }

    public Optional<Person> get(Long id) {
        var person = Optional.ofNullable(valueCommands.get(PERSON_KEY + id));
        if (person.isPresent()) {
            return person;
        }
        person = Optional.ofNullable(repository.findById(id));
        if (person.isEmpty()) {
            return person;
        }
        valueCommands.setex(PERSON_KEY + id, 60, person.get());
        return person;
    }

    public void persist(Person person) {
        repository.persist(person);
    }
}
