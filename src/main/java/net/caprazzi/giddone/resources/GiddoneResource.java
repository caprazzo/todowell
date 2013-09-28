package net.caprazzi.giddone.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public class GiddoneResource {
    @GET @Path("/ping")
    public String ping() {
        return "PONG";
    }
}
