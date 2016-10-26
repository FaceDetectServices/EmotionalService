package com.yoshio3.services;

/**
 * Created by yoterada on 2016/10/26.
 */


import com.yoshio3.services.entities.EmotionRequestJSONBody;
import com.yoshio3.services.entities.EmotionResponseJSONBody;
import com.yoshio3.services.entities.utils.MyObjectMapperProvider;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author Yoshio Terada
 */
@Component
@Path("")
public class EmotionService implements Serializable {
    private static final Logger LOGGER = Logger.getLogger(EmotionService.class.getName());
    private static final String BASE_URI = "https://api.projectoxford.ai/emotion/v1.0/recognize";

    @Value("${vcap.services.emotional-service.credentials.subscriptionId:デフォルト値}")
    String subscriptionId;


    @GET
    @Path("/emotionservice")
    @Produces("application/json")
    public String getEmotionInfo(@QueryParam("url") String fileURL){
        try{
            String file = URLDecoder.decode(fileURL,"UTF-8");
            Future<Response> responseForEmotion = getEmotionalInfo(fileURL);
            Response emotionRes = responseForEmotion.get();
            return jobForEmotion(emotionRes);
        } catch (InterruptedException |ExecutionException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String jobForEmotion(Response emotionRes) {
        EmotionResponseJSONBody[] persons = null;
        if (checkRequestSuccess(emotionRes)) {
            persons = emotionRes.readEntity(EmotionResponseJSONBody[].class);
        } else {
            return emotionRes.readEntity(String.class);
        }
        //現在は一人のみ解析処理
        EmotionResponseJSONBody emotionalPerson = persons[0];
        Map<String, Object> scores = emotionalPerson.getScores();

        //感情の情報を取得
        Double anger = convert((Double) scores.get("anger"));
        Double contempt = convert((Double) scores.get("contempt"));
        Double disgust = convert((Double) scores.get("disgust"));
        Double fear = convert((Double) scores.get("fear"));
        Double happiness = convert((Double) scores.get("happiness"));
        Double neutral = convert((Double) scores.get("neutral"));
        Double sadness = convert((Double) scores.get("sadness"));
        Double surprise = convert((Double) scores.get("surprise"));
        return "{"+
                "\"anger\":" + anger +
                ",\"contempt\":" + contempt +
                ",\"disgust\":" + disgust +
                ",\"fear\":" + fear +
                ",\"happiness\":" + happiness +
                ",\"neutral\":" + neutral +
                ",\"sadness\":" + sadness +
                ",\"surprise\":" + surprise +
                "}";
    }
    /* パーセント表示のためにデータをコンバート */
    private Double convert(Double before) {
        if (before == null) {
            return before;
        }
        String after = String.format("%.2f", before);
        return Double.valueOf(after) * 100;
    }
    /*
    REST 呼び出し成功か否かの判定
    */
    protected boolean checkRequestSuccess(Response response) {
        Response.StatusType statusInfo = response.getStatusInfo();
        Response.Status.Family family = statusInfo.getFamily();
        return family != null && family == Response.Status.Family.SUCCESSFUL;
    }


    public Future<Response> getEmotionalInfo(String pictURI) throws InterruptedException, ExecutionException {
        Client client = ClientBuilder.newBuilder()
                .register(MyObjectMapperProvider.class)
                .register(JacksonFeature.class)
                .build();
        WebTarget target = client.target(BASE_URI);

        EmotionRequestJSONBody entity = new EmotionRequestJSONBody();
        entity.setUrl(pictURI);

        Future<Response> response = target
                .request(MediaType.APPLICATION_JSON)
                .header("Ocp-Apim-Subscription-Key",subscriptionId)
                .async()
                .post(Entity.entity(entity, MediaType.APPLICATION_JSON_TYPE));
        return response;
    }
}

