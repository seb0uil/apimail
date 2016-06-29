package org.apimail.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.springframework.stereotype.Controller;

@Controller
@Path("/html")
public class HtmlController {
    String sCurrentLine;

    @GET
    @Path("/")
    @Produces("text/html; charset=utf-8")
    public String home() throws Exception {

        String content = "";
        InputStream is = getClass().getResourceAsStream("/index.html");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

            while ((sCurrentLine = br.readLine()) != null) {

                content += sCurrentLine + "\n";
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;

    }

}
