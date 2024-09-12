package org.acme;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.value.ValueCommands;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@ApplicationScoped
public class Factory {

    public static final String PERSON_KEY = "person:";
    private final PeopleRepository repository;
    private final ValueCommands<String, Person> valueCommands;
    private final PeopleService peopleService;

    public Factory(
            PeopleRepository repository,
            RedisDataSource redisDataSource) {
        this.repository = repository;
        this.valueCommands = redisDataSource.value(Person.class);
        this.peopleService = new PeopleService(
                (Long id) -> Optional.ofNullable(this.valueCommands.get(personKey(id))),
                (Long id) -> Optional.ofNullable(this.repository.findById(id)),
                (Person person) -> this.valueCommands.setex(personKey(person.getId()), 60, person),
                this.repository::persist
        );
    }

    private static String personKey(Long id) {
        return PERSON_KEY + id;
    }

    @Produces
    public Function<Long, Optional<Person>> getPersonById() {
        return this.peopleService::get;
    }

    @Produces
    public Consumer<Person> savePerson() {
        return this.peopleService::persist;
    }
}
