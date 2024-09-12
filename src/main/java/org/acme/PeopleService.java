package org.acme;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class PeopleService {
    private final Function<Long, Optional<Person>> findByIdInCache;
    private final Function<Long, Optional<Person>> findByIdInDatabase;
    private final Consumer<Person> saveInCache;
    private final Consumer<Person> saveInDatabase;

    public PeopleService(
            Function<Long, Optional<Person>> findByIdInCache,
            Function<Long, Optional<Person>> findByIdInDatabase,
            Consumer<Person> saveInCache,
            Consumer<Person> saveInDatabase) {
        this.findByIdInCache = findByIdInCache;
        this.findByIdInDatabase = findByIdInDatabase;
        this.saveInCache = saveInCache;
        this.saveInDatabase = saveInDatabase;
    }

    public Optional<Person> get(Long id) {
        var person = findByIdInCache.apply(id)
                .or(() -> findByIdInDatabase.apply(id));
        person.ifPresent(saveInCache);
        return person;
    }

    public void persist(Person person) {
        saveInDatabase.accept(person);
    }
}
