package com.om.service;

/**
 * Created with IntelliJ IDEA.
 * User: dosapati
 * Date: 10/2/13
 * Time: 4:36 PM
 * To change this template use File | Settings | File Templates.
 */
import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import java.net.URLEncoder;

public class JerseyClientGet {

    public static void main(String[] args) {
        try {

            Client client = Client.create();

            WebResource webResource = client
                    .resource(URLEncoder.encode("http://dev.markitondemand.com/Api/v2/InteractiveChart/json?parameters={\"Normalized\":false,\"NumberOfDays\":365,\"DataPeriod\":\"Day\",\"Elements\":[{\"Symbol\":\"AAPL\",\"Type\":\"price\",\"Params\":[\"c\"]}]}","UTF-8"));
                    //.resource("http://betawebapi.dowjones.com/fintech/articles/api/v1/source/424/?count=20");

            ClientResponse response = webResource.accept("application/json")
                    .get(ClientResponse.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatus());
            }

            Gson gson = new Gson();

            String output = response.getEntity(String.class);

            System.out.println("Output from Server .... \n");
            System.out.println(output);

        } catch (Exception e) {

            e.printStackTrace();

        }

    }
}