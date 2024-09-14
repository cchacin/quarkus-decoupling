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

@Path("/people")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PeopleResource {

    private final PeopleService service;

    public PeopleResource(PeopleService service) {
        this.service = service;
    }

    @GET
    @Path("/{id}")
    public Person get(Long id) {
        return this.service.get(id)
                .orElseThrow(() -> new NotFoundException("Not Found"));
    }

    @POST
    @Transactional
    public Response create(Person person) {
        this.service.persist(person);
        return Response.created(URI.create("/people/" + person.getId())).build();
    }
}