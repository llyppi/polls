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
public class NetClientPost {
    
    public static void main(String[] args) {
        String output = createToken("usuario","senha");
        System.out.println(output);
    }

    /**
     * Gerar Token
     */
    public static String createToken(String user, String pw) {
        String url = "http://localhost:8084/polls/tokens/post";
        String input = "{\"username\":" + user
                + ",\"password\":\"" + pw + "\"}";

        return restPolls(url, input,null);
    }
    
    private static String restPolls(String urlPOST,String input,String token) {
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

        } catch (MalformedURLException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();

        }
        return null;
    }

}
