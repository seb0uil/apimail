package org.apimail.controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apimail.dao.MongoDAO;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.mongodb.client.FindIterable;

@Controller
@Path("/mail")
public class MailController {

    @Autowired
    MongoDAO dao;

    @GET
    @Path("/")
    @Produces("application/json")
    public FindIterable<Document> find() throws Exception {
        return dao.find();
    }

    @GET
    @Path("/last")
    @Produces("application/json")
    public FindIterable<Document> last() throws Exception {
        return dao.last();
    }

    @GET
    @Path("/{header}/{value}")
    @Produces("application/json")
    public FindIterable<Document> find(@PathParam("header") String header, @PathParam("value") String value) throws Exception {
        return dao.find(header, value);
    }

    @GET
    @Path("/{header}/{value}/last")
    @Produces("application/json")
    public FindIterable<Document> findLast(@PathParam("header") String header, @PathParam("value") String value) throws Exception {
        return dao.findLast(header, value);
    }

    /* Getter && Setter */
    public void setDao(MongoDAO dao) {
        this.dao = dao;
    }
}
