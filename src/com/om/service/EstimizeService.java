package com.om.service;

import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: dosapati
 * Date: 11/9/13
 * Time: 7:48 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
@Path("/estimates")
public class EstimizeService {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/for/{symbol}")
    public String estimates(@PathParam("symbol")String symbol){
        Map<String,String> result = new HashMap<String, String>();
        result.put("success","true");

        Client client = Client.create();

        WebResource webResource = client
                .resource("http://api.estimize.com/companies/" +  symbol+
                        "/estimates/2013");

        ClientResponse response = webResource.header("Accept", "application/json")
                .header("X-Estimize-key", "90aa7db7e143a93db7b7686e").accept("application/json")
                .get(ClientResponse.class);
        /*
        X-Estimize-key:90aa7db7e143a93db7b7686e
Accept: application/json
         */
        Gson gson = new Gson();

        if (response.getStatus() != 200) {
            HashMap<String,String> response1 = new HashMap<String, String>();
            response1.put("error","true");
            return gson.toJson(response1);

        }

        //Gson gson = new Gson();

        String news = response.getEntity(String.class);


        return news;//gson.toJson(result);

    }
}
