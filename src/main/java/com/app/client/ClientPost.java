package com.app.client;

import com.app.Token;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.JSONObject;

/**
 *
 * @author Felipe L. Garcia
 */
public class ClientPost {
    private static String urlPolls = "http://localhost:8084/apipolls/polls";
    
    public static void main(String[] args) {
//        String output = postToken("usuario", "senha");
//        System.out.println(output);

        String output = postNewQuestion("Qual seu nome", "Felipe","João");
        System.out.println(output);
        output = postNewQuestion("Qual sua idade", "18","19");
        System.out.println(output);

//        String output = postChoices(1, 2);
//        System.out.println(output);

//        String output = test();
//        System.out.println(output);
        
    }

    public static String postChoices(int idQuest, int idChoice) {
        Token token = ClientPost.getToken("usuario", "senha");
        if (token == null) {
            return null;
        }

        String url = urlPolls+"/questions/" + idQuest
                + "/choices/" + idChoice;
        
        String param = "{\"idQuest\":" + idQuest 
                + ",\"idChoice\":" + idChoice + "}";
        //System.out.println(param);

        return restPolls(url, null, "312321321");
    }

    public static String postNewQuestion(String strQuestion, String... strchoices) {
        Token token = ClientPost.getToken("usuario", "senha");
        if (token == null) {
            return null;
        }

        String url = urlPolls+"/questions";
        
        String array = "[\""+String.join("\",\"",strchoices)+ "\"]"; 
        
        String param = "{\"question\":\"" + strQuestion + "\""
                + ",\"choices\":" +array+ "}";
        //System.out.println(param);

        return restPolls(url, param, token.getAccess_token());
    }

    public static Token getToken(String username, String password) {
        String response = ClientPost.postToken(username, password);
        if(response==null){
            return null;
        }
        
        JSONObject json = new JSONObject(response);

        Token token = new Token();
        token.setAccess_token(json.getString("access_token"));
        token.setExpires_in(json.getLong("expires_in"));
        
        return token;
    }
    
    public static String postToken(String username, String password) {
        String url = urlPolls+"/tokens"
                + "/"+username
                + "/"+password;

        String param = "{\"username\":\"" + username + "\""
                + ",\"password\":\"" + password + "\"}";
        //System.out.println(param);
        
        return restPolls(url, null, null);
    }

    public static String test() {
        String url = urlPolls + "/test";

//        String param = "{\"qty\":100,\"name\":\"iPad 4\"}";
        String strQuestion = "Qual seu nome";
        String[] strchoices = {"Felipe", "João"};
        String array = "[\"" + String.join("\",\"", strchoices) + "\"]";
        String param = "{\"question\":\"" + strQuestion + "\""
                + ",\"choices\":" + array + "}";

        //System.out.println(param);

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
            
            //Authorization
            if (token != null && !token.trim().isEmpty()) {
//                System.out.println("Authorization "+token);
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
