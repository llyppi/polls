package com.app.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author Felipe L. Garcia
 */
public class NetClientPost {

    public static void main(String[] args) {
//        String output = postToken("felipe", "1234");
//        System.out.println(output);
     
        String output = postNewQuestion("Qual seu nome", "Felipe","João");
        System.out.println(output);
    }

    public static String postChoices(int idQuest, int idChoice) {
        String token = NetClientPost.postToken("usuario", "senha");

        String url = "http://localhost:8084/polls/questions/" + idQuest
                + "/choices/" + idChoice;
        String param = "{\"idQuest\":" + idQuest + "\""
                + ",\"idChoice\":" + idChoice + "\"}";

        return restPolls(url, null, token);
    }

    public static String postNewQuestion(String strQuestion, String... strchoices) {
//        String token = NetClientPost.postToken("usuario", "senha");

        String url = "http://localhost:8084/polls/questions";

        String param = "{\"question\":" + strQuestion + "\""
                + ",\"choices\":" + strchoices + "\"}";

        return restPolls(url, param, null);
    }

    public static String postToken(String username, String password) {
        String url = "http://localhost:8084/polls/tokens/" + username
                + "/" + password;

        String param = "{\"username\":" + username + "\""
                + ",\"password\":" + password + "\"}";

        return restPolls(url, null, null);
    }

    private static String restPolls(String urlPOST, String input, String token) {
        // http://localhost:8080/RESTfulExample/json/product/post
        try {
            URL url = new URL(urlPOST);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            //Authorization
            if (token != null && !token.trim().isEmpty()) {
                conn.setRequestProperty("Authorization", "token " + token);
            }

//            String input = "{\"qty\":100,\"name\":\"iPad 4\"}";             
            if (input != null && !input.trim().isEmpty()) {
                OutputStream os = conn.getOutputStream();
                os.write(input.getBytes());
                os.flush();
            }

            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode()
                +" "+getErro(conn.getResponseCode()));
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output = "";
            String read;
            while ((read = br.readLine()) != null) {
                output += read;
            }
            conn.disconnect();

            return output;

        } catch (MalformedURLException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();

        }
        return null;
    }

    public static String getErro(int code) {
        Field[] fields = HttpURLConnection.class.getDeclaredFields();
        
        for (Field field : fields) {
            try {
                if(field.toString().equals(code)){
                    return field.getName();
                }
            } catch (Exception ex) {
               
            }
        }
        return "";
    }
}
