package com.app;

import java.lang.reflect.Method;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import org.json.JSONObject;

/**
 *
 * @author Felipe L. Garcia
 */
@Path("/polls")
public class PollsService {

    private static Map<Token, Long> listToken;
    private static List<Question> listQuestions;

    public PollsService() {
    }

    public static Map<Token, Long> getListToken() {
        return listToken;
    }

    /**
     * ROOT
     * Listar Entry Point 
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getURL() {
        System.out.println("GET /root");

        List<EntryPoint> listEntry = new ArrayList<>();
        
        Method[] listMethod = this.getClass().getDeclaredMethods();
        for (Method method : listMethod) {
            Path[] paths = method.getAnnotationsByType(Path.class);
            if(paths==null || paths.length==0){
                continue;
            }
            EntryPoint entry = new EntryPoint();
            entry.setQuestion_url(paths[0].value());
            listEntry.add(entry);
        }
        String json = getJson(listEntry.toArray(new EntryPoint[listEntry.size()]));
        
        return Response.status(Status.OK).entity(json).build();//200
    }

    /**
     * Vizualizar detalhes da Question
     * @param token Requer Header Authorization token ....
     */
//    @Secured
    @GET
    @Path("/questions/{question_id}")
    @Produces(MediaType.APPLICATION_JSON)    
    public Response getQuestion(@PathParam("question_id") int id
                     ,@HeaderParam("Authorization") String token) {
        
        System.out.println("GET /questions/" + id);
        
        if(!validateToken(token)){
            return Response.status(Status.UNAUTHORIZED).build();//401
        }

        Question question = buscarQuestion(id);
        if (question != null) {
            String json =  getJson(question);
            
            ResponseBuilder builder = Response.status(Status.OK);//200
            builder.header("Authorization","token "+token);
            
            Response resp = builder.entity(json).build();
            
            return resp;
        }
        return Response.noContent().build();//RETORNAR VAZIO 204
    }

    /**
     * Coleção de Questions por página
     * @param token Requer Header Authorization token ....
     */
    @GET
    @Path("/questions")
    @Produces(MediaType.APPLICATION_JSON)    
    public Response getQuestionPage(@DefaultValue("0")
            @QueryParam("page") int page
            ,@HeaderParam("Authorization") String token) {
        
        System.out.println("GET /questions" + page);        
        
        if (!validateToken(token)) {
            return Response.status(Status.UNAUTHORIZED).build();//401
        }
        
        if (listQuestions == null || listQuestions.isEmpty()) {
            System.out.println("listQuestions null");
            return Response.noContent().build();//RETORNAR VAZIO 204
        }
        System.out.println("listQuestions "+listQuestions.size());
        
        if (page < 0) {
            System.out.println("page invalida");
            return Response.noContent().build();//RETORNAR VAZIO 204
        }
        
        if (page == 0) {//LISTAR TODOS
            Question[] list = listQuestions
                    .toArray(new Question[listQuestions.size()]);
            
            String json = getJson(list);
            
            ResponseBuilder builder = Response.status(Status.OK);//200
            builder.header("Link","</questions?page=" + page + ">;rel=\"next\"");
            
            Response resp = builder.entity(json).build();
//            resp.getHeaders().add("Link",
//                     "</questions?page=" + page + ">;rel=\"next\"");
//            resp.getMetadata().add("Link",
//                    "</questions?page=" + page + ">;rel=\"next\"");

            return resp;
        }

        Question[] list = new Question[page];
        int pg = 1;
        //MOTAR LISTA
        for (Question quest : listQuestions) {
            list[pg-1]=quest;
            if(pg==page){
                break;
            }
            pg++;
        }
        ResponseBuilder builder = Response.status(Status.OK);//200
        builder.header("Link", "</questions?page=" + page + ">;rel=\"next\"");

        String json = getJson(list);
        
        Response resp = builder.entity(json).build();
        return resp;
    }

    /**
     * Votar na Choice
     * @param token Requer Header Authorization token ....
     */
//    @Secured
    @POST
    @Path("/questions/{question_id}/choices/{choice_id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postChoices(@PathParam("question_id") int idQuest,
                             @PathParam("choice_id") int idChoice
                        ,@HeaderParam("Authorization") String token) {
        
        System.out.println("POST /questions/" + idQuest
                + "/choices/" + idChoice);
        
        if (!validateToken(token)) {
            return Response.status(Status.UNAUTHORIZED).build();//401
        }

        Question quest = buscarQuestion(idQuest);
        if (quest == null) {
            return Response.serverError().build();//RETORNAR ERRO 500
        }

        Choice[] listChoice = quest.getChoices();
        Choice choice = buscarChoice(idChoice, listChoice);
        if (choice == null) {
            return Response.serverError().build();//RETORNAR ERRO 500
        }
        choice.setVotes(choice.getVotes() + 1);//SOMAR VOTOS

        ResponseBuilder builder = Response.status(Status.CREATED);//201
        builder.header("Location", quest.getUrl());

        Response resp = builder.build();
        return resp;
    }

    /**
     * Criar nova Question
     * @param token Requer Header Authorization token ....
     */
//    @Secured
    @POST
    @Path("/questions")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postQuestion(QuestionNew questNew
                            ,@HeaderParam("Authorization") String token) {
        
        System.out.println("POST /questions/" + getJson(questNew));

        if (!validateToken(token)) {
            return Response.status(Status.UNAUTHORIZED).build();//401
        }
        
        if (listQuestions == null) {
            listQuestions = new ArrayList<>();
        }
        //SE NAO CONTEM RESPOSTAS
        if (questNew.getChoices() == null || questNew.getChoices().length==0) {
            return Response.serverError().build();//RETORNAR ERRO 500
        }
        Question question = new Question();
        question.setQuestion_id(listQuestions.size() + 1);
        question.setQuestion(questNew.getQuestion());
        question.setPublished_at(getDateISO(new Date()));//DATA DE CRIACAO

        String urlQuest = "/question/" + (listQuestions.size() + 1);
        question.setUrl(urlQuest);//URL REQUEST

        //MONTAR AS RESPOSTAS
        String[] array = questNew.getChoices();

        //SE NAO CONTEM RESPOSTAS
        if (array == null || array.length == 0) {
            return Response.serverError().build();//RETORNAR ERRO 500
        }

        Choice[] listChoice = new Choice[array.length];

        int idx = 1;
        for (String ch : array) {
            Choice choice = new Choice();
            choice.setChoice_id(idx);
            choice.setChoice(ch);
            choice.setUrl(urlQuest + "/choices/" + idx);

            listChoice[idx - 1] = choice;
            idx++;
        }
        question.setChoices(listChoice);

        listQuestions.add(question);

        String json =  getJson(question);
        
        ResponseBuilder builder = Response.status(Status.CREATED);//201
        builder.header("Location", urlQuest);
        
        Response resp = builder.entity(json).build();
        return resp;
    }

    /**
     * Criar Token com usuario e senha
     */
    @POST
    @Path("/tokens/{username}/{password}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response postToken(@PathParam("username") String username,
            @PathParam("password") String password) {

        System.out.println("POST /tokens/" + username
                + "/" + password);

        if (listToken == null) {
            listToken = new HashMap<>();
        }
        //VALIDACÃO ILUSTRATIVA
        if (!validateUser(username, password)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();//401
        }

        Token token = createToken(username);
        listToken.put(token, System.currentTimeMillis());

        // Return the token on the response
        return Response.status(Status.CREATED).entity(token).build();//Status 201

    }

    /**
     * Buscar Question por ID
     */
    private Question buscarQuestion(int id) {
        if (listQuestions == null) {
            return null;
        }
        for (Question quest : listQuestions) {
            if (quest.getQuestion_id() == id) {
                return quest;
            }
        }
        return null;
    }

    /**
     * Buscar Choice por ID
     */
    private Choice buscarChoice(int id, Choice[] list) {
        if (list == null) {
            return null;
        }
        for (Choice choice : list) {
            if (choice.getChoice_id() == id) {
                return choice;
            }
        }
        return null;
    }

    /**
     * Converter Date ISO
     */
    public static String getDateISO(Date date) {
        ZonedDateTime zone = ZonedDateTime.of(
                LocalDateTime.ofInstant(date.toInstant(),
                         ZoneId.systemDefault()),
                 ZoneId.systemDefault());
        return zone.format(DateTimeFormatter.ISO_INSTANT);
    }
    /**
     * Converter Object no formato Json
     */
    public static String getJson(Object[] list) {
        String json = "";
        for (Object question : list) {
            String js = getJson(question);
            json += "," + js;
        }
        json = json.replaceFirst(",", "");
        json = "[" + json + "]";
        
        return json;
    }
    /**
     * Converter Object no formato Json
     */
    public static String getJson(Object obj) {
        JSONObject json = new JSONObject(obj);
        
        String str = json.toString();
//        str = str.replaceAll("\\\"", "\"");
        
        return str;
    }
    /**
     * Converter Object no formato Json
     */
    public static String getJson2(Object obj) {
        Method[] metodosLista = obj.getClass().getDeclaredMethods();

        String lista = "";
        for (Method method : metodosLista) {
            if (method.getReturnType().equals(Void.TYPE)) {
                continue;
            }
            if (method.getName().equalsIgnoreCase("tostring")) {
                continue;
            }
            String value = "";
            try {
                Object invoke = method.invoke(obj);
                
                if(String[].class.isInstance(invoke)){
                    String join = "[\""+String.join("\",\"",(String[]) invoke)+ "\"]";
                    invoke = join;
                }
                if(Date.class.isInstance(invoke)){
                    Date date = (Date) invoke;
                    ZonedDateTime zone = ZonedDateTime.of(
                            LocalDateTime.ofInstant(date.toInstant()
                                                    ,ZoneId.systemDefault())
                                            , ZoneId.systemDefault());
                    invoke = zone.format(DateTimeFormatter.ISO_INSTANT);
                }
                
                value = String.valueOf(invoke);
                
            } catch (Exception ex) {
            }
            String atributo = method.getName();
            atributo = atributo.replaceAll("^set|^get|^is", "");

            lista += ",\"" + atributo.toLowerCase() + "\": \"" + value + "\"";
        }
        return lista.replaceFirst(",", "");
    }    

    /**
     * Criar Token com 1 dia de validade
     */
    private Token createToken(String username) {
        Token token = new Token();
        token.setExpires_in(86400);//1 DIA

        //TOKEN ILUSTRATIVO,HEX DA STRING
        String tk = String.format("%040x", new BigInteger(1, username.getBytes()));
        token.setAccess_token(tk);

        return token;
    }
    
    /**
     * Validar Usuário e Senha
     */
    private boolean validateUser(String username, String password) {
        return true;//INLUSTRATIVO
    }
    /**
     * Validar timeCreate+timeExp > NOW
     */
    private boolean validateToken(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        
        Map<Token,Long> listToken = PollsService.getListToken();
        
        if (listToken == null || listToken.isEmpty()) {
            return false;
        }
        
        for (Map.Entry<Token, Long> entry : listToken.entrySet()) {
            Token tok = entry.getKey();
            Long timeCreate = entry.getValue();
            
            token = token.replace("token ", "");
            //BUSCAR TOKEN
            if(!tok.getAccess_token().equals(token)){
                continue;
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timeCreate);
            //DIMINUIR TEMPO DE EXPIRAR TOKEN
            calendar.add(Calendar.MILLISECOND, (int) tok.getExpires_in());
            //DENTRO DO PRAZO
            if(calendar.getTimeInMillis() > System.currentTimeMillis()){
                return true;
            }
        }
        return false;
    }
//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response showAll( @Context Application application,
//                             @Context HttpServletRequest request){
//        String basePath = request.getRequestURL().toString();
// 
//        ObjectNode root = JsonNodeFactory.instance.objectNode();
//        ArrayNode resources = JsonNodeFactory.instance.arrayNode();
// 
//        root.put( "resources", resources );
// 
//        for ( Class<?> aClass : application.getClasses() )
//        {
//            if ( isAnnotatedResourceClass( aClass ) )
//            {
//                AbstractResource resource = IntrospectionModeller.createResource( aClass );
//                ObjectNode resourceNode = JsonNodeFactory.instance.objectNode();
//                String uriPrefix = resource.getPath().getValue();
// 
//                for ( AbstractSubResourceMethod srm : resource.getSubResourceMethods() )
//                {
//                    String uri = uriPrefix + "/" + srm.getPath().getValue();
//                    addTo( resourceNode, uri, srm, joinUri(basePath, uri) );
//                }
// 
//                for ( AbstractResourceMethod srm : resource.getResourceMethods() )
//                {
//                    addTo( resourceNode, uriPrefix, srm, joinUri( basePath, uriPrefix ) );
//                }
// 
//                resources.add( resourceNode );
//            }
// 
//        }
// 
// 
//        return Response.ok().entity( root ).build();
//    }
//// 
//    private void addTo( ObjectNode resourceNode, String uriPrefix, AbstractResourceMethod srm, String path )
//    {
//        if ( resourceNode.get( uriPrefix ) == null )
//        {
//            ObjectNode inner = JsonNodeFactory.instance.objectNode();
//            inner.put("path", path);
//            inner.put("verbs", JsonNodeFactory.instance.arrayNode());
//            resourceNode.put( uriPrefix, inner );
//        }
// 
//        ((ArrayNode) resourceNode.get( uriPrefix ).get("verbs")).add( srm.getHttpMethod() );
//    }
// 
// 
//    private boolean isAnnotatedResourceClass( Class rc )
//    {
//        if ( rc.isAnnotationPresent( Path.class ) )
//        {
//            return true;
//        }
// 
//        for ( Class i : rc.getInterfaces() )
//        {
//            if ( i.isAnnotationPresent( Path.class ) )
//            {
//                return true;
//            }
//        }
// 
//        return false;
//    }
 
    
//    @POST
//    @Path("/questions/{question}/{choices}")
//    @Consumes(MediaType.APPLICATION_JSON)
//    public Response postNewQuestion(@PathParam("question") String strQuestion,
//            @PathParam("choices") String strChoices) {
//
//        System.out.println("POST /questions/" + strQuestion
//                + "/" + strChoices);
//
//        if (listQuestions == null) {
//            listQuestions = new ArrayList<>();
//        }
//        //SE NAO CONTEM RESPOSTAS
//        if (strChoices == null || strChoices.isEmpty()) {
//            return Response.serverError().build();//RETORNAR ERRO
//        }
//
//        Question question = new Question();
//        question.setQuestion_id(listQuestions.size() + 1);
//        question.setQuestion(strQuestion);
//        question.setPublished_at(new Date());//DATA DE CRIACAO
//
//        String urlQuest = "/question/" + listQuestions.size() + 1;
//        question.setUrl(urlQuest);//URL REQUEST
//
//        //MONTAR AS RESPOSTAS
//        strChoices = strChoices.replaceAll("\\[|\\]|\\{|\\}", "");
//        String[] array = strChoices.split(",");
//
//        //SE NAO CONTEM RESPOSTAS
//        if (array == null || array.length == 0) {
//            return Response.serverError().build();//RETORNAR ERRO
//        }
//
//        Choice[] listChoice = new Choice[array.length];
//
//        int idx = 1;
//        for (String ch : array) {
//            Choice choice = new Choice();
//            choice.setChoice_id(idx);
//            choice.setChoice(ch);
//            choice.setUrl(urlQuest + "/choices/" + idx);
//
//            listChoice[idx - 1] = choice;
//            idx++;
//        }
//        question.setChoices(listChoice);
//
//        listQuestions.add(question);
//
//        Response resp = Response.status(Status.CREATED).entity(question).build();
////        resp.getHeaders().putSingle("Location",urlQuest);
//        resp.getMetadata().putSingle("Location", urlQuest);
//
//        return resp;
//    }


    @POST
    @Path("/test")
    @Consumes("application/json")
    public Response test(Question quest) {
        System.out.println("POST /post/" + quest);

        String result = "Product created : " + quest;
        return Response.status(201).entity(result).build();

    }    
}
