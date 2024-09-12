package org.acme;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.value.ValueCommands;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@ApplicationScoped
public class Factory {

    public static final String PERSON_KEY = "person:";
    private final PeopleRepository repository;
    private final ValueCommands<String, Person> valueCommands;

    public Factory(PeopleRepository repository, RedisDataSource redisDataSource) {
        this.repository = repository;
        this.valueCommands = redisDataSource.value(Person.class);
    }

    private static String personKey(Long id) {
        return PERSON_KEY + id;
    }

    @Produces
    @Named("getFromCache")
    public Function<Long, Optional<Person>> getFromRedis() {
        return (Long id) -> Optional.ofNullable(this.valueCommands.get(personKey(id)));
    }

    @Produces
    @Named("storeInCache")
    public Consumer<Person> storeInRedis() {
        return (Person person) -> this.valueCommands.setex(personKey(person.getId()), 60, person);
    }

    @Produces
    @Named("getFromDb")
    public Function<Long, Optional<Person>> getFromRepo() {
        return (Long id) -> Optional.ofNullable(this.repository.findById(id));
    }

    @Produces
    @Named("storeInDb")
    public Consumer<Person> saveInRepo() {
        return this.repository::persist;
    }
}
