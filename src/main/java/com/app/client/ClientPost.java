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
public class ClientPost {
    private static String urlPolls = "http://localhost:8084/polls";
    
    public static void main(String[] args) {
        String output = postToken("felipe", "1234");
        System.out.println(output);
     
//        String output = postNewQuestion("Qual seu nome", "Felipe","João");
//        System.out.println(output);
        
    }

    public static String postChoices(int idQuest, int idChoice) {
        String token = ClientPost.postToken("usuario", "senha");

        String url = urlPolls+"/questions/" + idQuest
                + "/choices/" + idChoice;
        
        
        String param = "{\"idQuest\":" + idQuest 
                + ",\"idChoice\":" + idChoice + "}";
        System.out.println(param);

        return restPolls(url, null, token);
    }

    public static String postNewQuestion(String strQuestion, String... strchoices) {
        String token = ClientPost.postToken("usuario", "senha");

        String url = urlPolls+"/questions";
        
        
        String array = "[\""+String.join("\",\"",strchoices)+ "\"]"; 
        
        String param = "{\"question\":\"" + strQuestion + "\""
                + ",\"choices\":" +array+ "}";
        System.out.println(param);

        return restPolls(url, param, token);
    }

    public static String postToken(String username, String password) {
        String url = urlPolls+"/tokens";
        

        String param = "{\"username\":\"" + username + "\""
                + ",\"password\":\"" + password + "\"}";
        System.out.println(param);
        
        return restPolls(url, param, null);
    }

    private static String restPolls(String urlPOST, String input, String token) {
        System.out.println(urlPOST);
        // http://localhost:8080/RESTfulExample/json/product/post
        try {
            URL url = new URL(urlPOST);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
//            conn.setRequestProperty("Accept", "application/json");
            
//            //Authorization
//            if (token != null && !token.trim().isEmpty()) {
//                conn.setRequestProperty("Authorization", "token " + token);
//            }

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
