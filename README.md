## Continuous Integration|Coverage|Analysis
 [![Code Climate](https://codeclimate.com/github/llyppi/polls.png)](https://codeclimate.com/github/llyppi/polls)
[![codecov](https://codecov.io/gh/llyppi/polls/branch/master/graph/badge.svg)](https://codecov.io/gh/llyppi/polls)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/7c7334e6f740470fbe6920c4c974ff0b)](https://www.codacy.com/app/llyppi/polls?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=llyppi/polls&amp;utm_campaign=Badge_Grade) 
[![Build Status](https://travis-ci.org/llyppi/polls.svg?branch=master)](https://travis-ci.org/llyppi/polls)

## Synopsis  
    Polls API
    RESTFul WebServices Java8 API(JAX-RS)Jersey Tomcat 8 Maven

## Installation
    Build project,Netbeas,Eclipse

</tab>[ClientGet](../master/src/main/java/com/app/client/ClientGet.java)
    <br>[ClientPost](../master/src/main/java/com/app/client/ClientPost.java)

* Listar Entry Point 
* GET http://localhost:8080/apipolls/polls
* Criar nova Question
* POST http://localhost:8080/apipolls/polls/questions   Requer Header Authorization token 
* Vizualizar detalhes da Question
* GET http://localhost:8080/apipolls/polls/questions/1    Requer Header Authorization token 
* Listar Questions por página
* GET http://localhost:8080/apipolls/polls/questions?page=1   Requer Header Authorization token 
* Votar na Questions
* POST http://localhost:8080/apipolls/polls/questions/1/choices/1   Requer Header Authorization token 
* Gerar Token
* POST http://localhost:8084/apipolls/polls/tokens?username=usuario&password=senha            

## Java            

``````javascript
try {
    String urlPolls = "http://localhost:8080/apipolls/polls";

    String urlQuestions = urlPolls+"/questions/" + 1; 

    int page = 1;
    String urlQuestionsPage = urlPolls+"/questions"+(page>0
                            ?"?page="+page
                            :""); 

    String username="user";
    String password="pass";
    String urlToken = urlPollsurlPolls+"/tokens"
                      + "?username="+username+"&password="+password;

    String urlChoices = urlPolls+"/questions/" + 1+ "/choices/" + 1;

    String urlNewQuestion = urlPolls+"/questions";

    String question = "Qual sua idade";
    String[] choices = {"18","19"};

    String array = "[\""+String.join("\",\"",choices)+ "\"]"; 

    String input = "{\"question\":\"" + question + "\""
        + ",\"choices\":" +array+ "}";

    Token token = ClientPost.getToken("usuario", "senha");

    URL url = new URL(?);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setDoOutput(true);
    conn.setRequestMethod("POST");
    conn.setRequestProperty("Content-Type", "application/json");

    //Authorization
    if (token != null) {
        conn.setRequestProperty("Authorization"
                , "token " + token.getAccess_token());
    }

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

} catch (Exception ex) {

}
