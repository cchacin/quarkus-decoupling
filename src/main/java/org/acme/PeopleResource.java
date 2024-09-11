package org.acme;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.List;

@Path("/people")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PeopleResource {

    private final PeopleRepository repository;

    public PeopleResource(PeopleRepository repository) {
        this.repository = repository;
    }

    @GET
    public List<Person> list() {
        return repository.listAll();
    }

    @GET
    @Path("/{id}")
    public Person get(Long id) {
        return repository.findById(id);
    }

    @POST
    @Transactional
    public Response create(Person person) {
        repository.persist(person);
        return Response.created(URI.create("/persons/" + person.getId())).build();
    }
}