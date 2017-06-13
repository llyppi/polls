#Synopsis
    Polls API
    RESTFul WebServices,utilizando plataforma Java8,API(JAX-RS)Jersey,Tomcat 8,Maven

## Installation
    Build project,Netbeas,Eclipse

## Test GET/POST
    https://multware.com/apipolls
 
    Pacote teste 
    https://github.com/llyppi/polls/blob/master/src/main/java/com/app/client/ClientGet.java
    https://github.com/llyppi/polls/blob/master/src/main/java/com/app/client/ClientPost.java

    Listar Entry Point 
    GET http://localhost:8080/apipolls/polls
    Criar nova Question
    POST http://localhost:8080/apipolls/polls/questions   Requer Header Authorization token 
    Vizualizar detalhes da Question
    GET http://localhost:8080/apipolls/polls/questions/1    Requer Header Authorization token 
    Listar Questions por página
    GET http://localhost:8080/apipolls/polls/questions?page=1   Requer Header Authorization token 
    Votar na Questions
    POST http://localhost:8080/apipolls/polls/questions/1/choices/1   Requer Header Authorization token 
    Gerar Token
    POST http://localhost:8084/apipolls/polls/tokens?username=usuario&password=senha

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
