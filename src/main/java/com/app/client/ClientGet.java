package com.app.client;

import com.app.Token;
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
    private static String urlPolls = "http://localhost:8084/apipolls/polls";
    
    public static void main(String[] args) {
//        String output = getURL();
//        System.out.println(output);
//                
//        String output = getQuestion(1);
//        System.out.println(output);
//        
        String output = getQuestionPage(0);
        System.out.println(output);
    }
   
    public static String getURL() {

        return restPolls(urlPolls, null,null);
    }
    
    public static String getQuestion(int id) {
        Token token = ClientPost.getToken("usuario", "senha");
        if (token == null) {
            return null;
        }
        String url = urlPolls+"/questions/" + id;
        
        return restPolls(url, null,token.getAccess_token());
    }
    
    public static String getQuestionPage(int page) {
        Token token = ClientPost.getToken("usuario", "senha");
        if (token == null) {
            return null;
        }
        String url = urlPolls+"/questions"+(page>0
                                    ?"?page="+page
                                    :"");
        
        return restPolls(url, null,token.getAccess_token());
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
                    
            if(input!=null && !input.trim().isEmpty()){
                OutputStream os = conn.getOutputStream();
                os.write(input.getBytes());
                os.flush();
            }

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed : HTTP error code : " 
                        + conn.getResponseCode());
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

        } catch (Exception e) {

            System.out.println(e);

        }
        return null;
    }
   
}
