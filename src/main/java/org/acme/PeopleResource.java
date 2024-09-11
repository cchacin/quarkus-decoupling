package org.acme;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.List;

@Path("/people")
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

    @PUT
    @Path("/{id}")
    @Transactional
    public Person update(Long id, Person person) {
        Person entity = repository.findById(id);
        if (entity == null) {
            throw new NotFoundException();
        }

        // map all fields from the person parameter to the existing entity
        entity.setName(person.getName());

        return entity;
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public void delete(Long id) {
        Person entity = repository.findById(id);
        if (entity == null) {
            throw new NotFoundException();
        }
        repository.delete(entity);
    }
}