
package org.acme;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@Path("/people")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PeopleResource {

    private final Function<Long, Optional<Person>> findByIdInCache;
    private final Function<Long, Optional<Person>> findByIdInDatabase;
    private final Consumer<Person> saveInCache;
    private final Consumer<Person> saveInDatabase;

    public PeopleResource(
            @Named("getFromCache")
            Function<Long, Optional<Person>> findByIdInCache,
            @Named("getFromDb")
            Function<Long, Optional<Person>> findByIdInDatabase,
            @Named("storeInCache")
            Consumer<Person> saveInCache,
            @Named("storeInDb")
            Consumer<Person> saveInDatabase) {
        this.findByIdInCache = findByIdInCache;
        this.findByIdInDatabase = findByIdInDatabase;
        this.saveInCache = saveInCache;
        this.saveInDatabase = saveInDatabase;
    }

    @GET
    @Path("/{id}")
    public Person get(Long id) {
        var person = findByIdInCache.apply(id)
                .orElseGet(() -> findByIdInDatabase.apply(id)
                        .orElseThrow(() -> new NotFoundException("Not Found"))
                );
        saveInCache.accept(person);
        return person;
    }

    @POST
    @Transactional
    public Response create(Person person) {
        saveInDatabase.accept(person);
        return Response.created(URI.create("/people/" + person.getId())).build();
    }
}