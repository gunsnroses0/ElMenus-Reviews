package Commands;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;

import Model.Review;

public class PostReview extends Command{
    public void execute() {
        HashMap<String, Object> props = parameters;
        Channel channel = (Channel) props.get("channel");
        JSONParser parser = new JSONParser();

        try {
            JSONObject messageBody = (JSONObject) parser.parse((String) props.get("body"));
            
            JSONObject form = (JSONObject) messageBody.get("form");
            System.out.println();
            HashMap<String, Object> requestBodyHash  = jsonToMap((JSONObject) messageBody.get("form"));
            AMQP.BasicProperties properties = (AMQP.BasicProperties) props.get("properties");
            AMQP.BasicProperties replyProps = (AMQP.BasicProperties) props.get("replyProps");
            Envelope envelope = (Envelope) props.get("envelope");
            String type = ((String) props.get("params"));
            System.out.println(props);
			String url = ((JSONObject) parser.parse((String) props.get("body"))).get("uri").toString();
			url = url.substring(1);
			String[] parametersArray = url.split("/");
			String id = parametersArray[1];
	        if(id.contains("?")){
	            id = id.substring(0,id.indexOf("?"));
	        }
            HashMap<String, Object> createdMessage = Review.create(requestBodyHash,id);
            JSONObject response = jsonFromMap(createdMessage);
            channel.basicPublish(
                    "",
                    properties.getReplyTo(),
                    replyProps,
                    response.toString().getBytes("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (org.json.simple.parser.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}
