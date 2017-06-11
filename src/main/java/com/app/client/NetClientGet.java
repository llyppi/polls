package com.app.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author Felipe L. Garcia
 */
public class NetClientGet {
    
    public static void main(String[] args) {
        String output = getQuestion(1);
        System.out.println(output);
    }
   
    public static String getURL() {
        String url = "http://localhost:8084/polls";

        return restPolls(url, null,null);
    }
    public static String getQuestion(int id) {
        String token = NetClientPost.createToken("usuario", "senha");
        
        String url = "http://localhost:8084/polls/questions/" + id;
        String param = "{\"question_id\":" + id + "\"}";

        return restPolls(url, null,token);
    }
    
    public static String getQuestionPage(int page) {
        String token = NetClientPost.createToken("usuario", "senha");
        
        String url = "http://localhost:8084/polls/questions"+page;
        String param = "{\"page\":" + page + "\"}";

        return restPolls(url, null,token);
    }

    public static String postChoices(int idQuest,int idChoice) {
        String token = NetClientPost.createToken("usuario", "senha");

        String url = "http://localhost:8084/polls/questions/"+idQuest
                +"/choices/"+idChoice;
        String param = "{\"idQuest\":" + idQuest + "\""
                + ",\"idChoice\":" + idChoice + "\"}";

        return restPolls(url, null, token);
    }
    
    public static String postNewQuestion(String strQuestion,String[] strchoices){
        String token = NetClientPost.createToken("usuario", "senha");

        String url = "http://localhost:8084/polls/questions";
        
        String param = "{\"question\":" + strQuestion + "\""
                + ",\"choices\":" + strchoices + "\"}";

        return restPolls(url, param, token);
    }

    public static String postToken(String username,String password) {
        String url = "http://localhost:8084/polls/tokens/"+username
                +":"+password;
        
        String param = "{\"username\":" + username + "\""
                + ",\"password\":" + password + "\"}";

        return restPolls(url, null, null);
    }
    
    private static String restPolls(String urlGet,String input,String token) {
        // http://localhost:8080/RESTfulExample/json/product/get
        try {
            URL url = new URL(urlGet);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            
            //Authorization
            if(token!=null && !token.trim().isEmpty()){
                conn.setRequestProperty("Authorization", "token "+token);
            }
            //Authorization
//            String encoding = Base64Encoder.encode("test1:test1");
//            HttpPost httppost = new HttpPost("http://host:post/test/login");
//            httppost.setHeader("Authorization", "Basic " + encoding);
//            
//            String input = "{\"qty\":100,\"name\":\"iPad 4\"}";                         
            if(input!=null && !input.trim().isEmpty()){
                OutputStream os = conn.getOutputStream();
                os.write(input.getBytes());
                os.flush();
            }

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
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
   
}
