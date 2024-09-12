package org.acme;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
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

    private final Function<Long, Optional<Person>> findPersonById;
    private final Consumer<Person> savePerson;

    public PeopleResource(
            Function<Long, Optional<Person>> findPersonById,
            Consumer<Person> savePerson) {
        this.findPersonById = findPersonById;
        this.savePerson = savePerson;
    }

    @GET
    @Path("/{id}")
    public Person get(Long id) {
        return this.findPersonById.apply(id)
                .orElseThrow(() -> new NotFoundException("Not Found"));
    }

    @POST
    @Transactional
    public Response create(Person person) {
        this.savePerson.accept(person);
        return Response.created(URI.create("/people/" + person.getId())).build();
    }
}