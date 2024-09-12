
package org.acme;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.value.ValueCommands;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;

@Path("/people")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PeopleResource {

    public static final String PERSON_KEY = "person:";
    private final PeopleRepository repository;
    private final ValueCommands<String, Person> valueCommands;

    public PeopleResource(PeopleRepository repository, RedisDataSource redis) {
        this.repository = repository;
        valueCommands = redis.value(Person.class);
    }

    @GET
    @Path("/{id}")
    public Person get(Long id) {
        var person = valueCommands.get(PERSON_KEY + id);
        if (person != null) {
            return person;
        }
        person = repository.findById(id);
        if (person == null) {
            throw new NotFoundException("Not Found");
        }
        valueCommands.setex(PERSON_KEY + id, 60, person);
        return person;
    }

    @POST
    @Transactional
    public Response create(Person person) {
        repository.persist(person);
        return Response.created(URI.create("/people/" + person.getId())).build();
    }
}