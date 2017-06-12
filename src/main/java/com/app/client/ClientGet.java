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
public class ClientGet {
    private static String urlPolls = "http://localhost:8084/polls";
    
    public static void main(String[] args) {
        String output = getURL();
        System.out.println(output);
                
        output = getQuestion(1);
        System.out.println(output);
        
        output = getQuestionPage(1);
        System.out.println(output);
    }
   
    public static String getURL() {

        return restPolls(urlPolls, null,null);
    }
    public static String getQuestion(int id) {
        String token = ClientPost.postToken("usuario", "senha");
        
        String url = urlPolls+"/questions/" + id;
        
        String param = "{\"question_id\":" + id + "}";
        System.out.println(param);

        return restPolls(url, null,token);
    }
    
    public static String getQuestionPage(int page) {
        String token = ClientPost.postToken("usuario", "senha");
        
        String url = urlPolls+"/questions"+page;
        
        
        String param = "{\"page\":" + page + "}";
        System.out.println(param);

        return restPolls(url, null,token);
    }    
    
    private static String restPolls(String urlGet,String input,String token) {
        System.out.println(urlGet);
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
