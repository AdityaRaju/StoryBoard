package com.om.service;

import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.MessageFactory;
import com.twilio.sdk.resource.instance.Message;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dosapati
 * Date: 11/9/13
 * Time: 7:43 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
@Path("/sms")
public class TwilloService {

    public static final String ACCOUNT_SID = "AC479936df224416104498f30c96ffac8c";
    public static final String AUTH_TOKEN = "f638b22989408f5913a4dfea717cd4ac";

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/send/{from}/{to}/{body}")
    public String sendSMS(@PathParam("from")String from,@PathParam("to")String to,@PathParam("body")String body) throws TwilioRestException {
        TwilioRestClient client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);

        // Build a filter for the MessageList
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("Body", body));
        params.add(new BasicNameValuePair("To", "+1"+to));
        params.add(new BasicNameValuePair("From", "+13475156410"));
        MessageFactory messageFactory = client.getAccount().getMessageFactory();
        Message message = messageFactory.create(params);
        System.out.println(message.getSid());
        return "{'success':true}";
    }
}
