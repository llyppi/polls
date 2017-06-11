package com.app;

import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 *
 * @author Felipe L. Garcia
 */
@Path("/test")
public class PollsService {

    private static Map<Token, Long> listToken;
    private static List<Question> listQuestions;

    public PollsService() {
    }

    public static Map<Token, Long> getListToken() {
        return listToken;
    }

//    /**
//     * Listar urls
//     */
//    @GET
//    @Path("/")
//    @Produces(MediaType.APPLICATION_JSON)
//    public String[] getURL() {
//        return new String[]{};
//
//    }

    /**
     * Vizualizar detalhes da Question
     */
    @Secured
    @GET
    @Path("/questions/{question_id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getQuestion(@PathParam("question_id") int id) {
        Question quest = buscarQuestion(id);
        if (quest != null) {
            return Response.status(Status.OK).entity(quest).build();
        }
        return Response.noContent().build();//RETORNAR VAZIO
    }
    
    /**
     * Coleção de Questions por página
     */
    @GET
    @Path("/questions")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getQuestionPage(@QueryParam("page")int page) {
        
        if (listQuestions == null || listQuestions.isEmpty()) {
            return Response.noContent().build();//RETORNAR VAZIO
        }
        
        if (page == 0) {
            Question[] list = listQuestions
                    .toArray(new Question[listQuestions.size()]);
            
            Response resp = Response.status(Status.OK).entity(list).build();
            resp.getHeaders().add("Link",
                     "</questions?page=" + page + ">;rel=\"next\"");
        }
        
        int pg=1;
        for (Question quest : listQuestions) {
            if (page == pg) {
                Response resp = Response.status(Status.OK).entity(quest).build();
                resp.getHeaders().add("Link"
                        , "</questions?page="+page+">;rel=\"next\"");
                
                return Response.status(Status.OK).entity(quest).build();
            }
            pg++;
        }
        return Response.noContent().build();//RETORNAR VAZIO
    }

    /**
     * Votar na Choice
     */
    @Secured
    @POST
    @Path("/questions/{question_id}/choices/{choice_id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postChoices(@PathParam("question_id") int idQuest
            ,@PathParam("choice_id") int idChoice) {

        Question quest = buscarQuestion(idQuest);
        if (quest == null) {
            return Response.serverError().build();//RETORNAR ERRO
        }

        Choice[] listChoice = quest.getChoice();
        Choice choice = buscarChoice(idChoice, listChoice);
        if (choice == null) {
            return Response.serverError().build();//RETORNAR ERRO
        }
        choice.setVotes(choice.getVotes() + 1);//SOMAR VOTOS

//        Response resp = getQuestion(idQuest);
//        return Response.created(resp.getMetadata().getFirst("Location")).build();
        Response resp = Response.status(Status.CREATED).build();
        resp.getHeaders().putSingle("Location", quest.getUrl());
        
        return resp;
    }

    /**
     * Criar nova Question
     */
    @Secured
    @POST
    @Path("/questions/{question}/{choices}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postNewQuestion(@PathParam("question") String strQuestion
            ,@PathParam("choices") String strChoices){
        
        if (listQuestions == null) {
            listQuestions = new ArrayList<>();
        }
        //SE NAO CONTEM RESPOSTAS
        if (strChoices == null || strChoices.isEmpty()) {
            return Response.serverError().build();//RETORNAR ERRO
        }
        
        Question question = new Question();
        question.setQuestion_id(listQuestions.size()+1);
        question.setQuestion(strQuestion);
        question.setPublished_at(new Date());//DATA DE CRIACAO
        
        String urlQuest = "/question/"+listQuestions.size()+1;
        question.setUrl(urlQuest);//URL REQUEST
        
        //MONTAR AS RESPOSTAS
        strChoices = strChoices.replaceAll("\\[|\\]|\\{|\\}", "");
        String[] array = strChoices.split(",");
        
        //SE NAO CONTEM RESPOSTAS
        if (array == null || array.length==0) {
            return Response.serverError().build();//RETORNAR ERRO
        }
        
        Choice[] listChoice = new Choice[array.length];
        
        int idx=1;
        for (String ch : array) {
            Choice choice = new Choice();
            choice.setChoice_id(idx);
            choice.setChoice(ch);
            choice.setUrl(urlQuest+"/choices/"+idx);
                
            listChoice[idx-1]=choice;
            idx++;
        }
        question.setChoice(listChoice);
        
        listQuestions.add(question);
        
        Response resp = Response.status(Status.CREATED).entity(question).build();
        resp.getHeaders().putSingle("Location",urlQuest);
        
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

        if (listToken == null) {
            listToken = new HashMap<>();
        }
        //VALIDACÃO ILUSTRATIVA
        if (!validarUsuario(username, password)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();//RETORNAR ERRO
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
        for (Choice choice : list) {
            if (choice.getChoice_id() == id) {
                return choice;
            }
        }
        return null;
    }
    /**
     * Criar Token com 1 dia de validade
     */
    private Token createToken(String username) {
        Token token = new Token();
        token.setExpires_in(86400);//1 DIA

        //HEX DA STRING,TOKEN ILUSTRATIVO
        String tk = String.format("%040x", new BigInteger(1, username.getBytes()));
        token.setAccess_token(tk);

        return token;
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
// 
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
    /**
     * Print Object no formato Json
     */
    public static String getAtributosValue(Object obj) {
        Method[] metodosLista = obj.getClass().getDeclaredMethods();

        String lista = "";
        for (Method method : metodosLista) {
            if (method.getReturnType().equals(Void.TYPE)) {
                continue;
            }
            String value = "";
            try {
                value = String.valueOf(method.invoke(obj));
            } catch (Exception ex) {
            }
            String atributo = method.getName();
            atributo = atributo.replaceAll("^set|^get|^is", "");

            lista += ",\"" + atributo + "\": \"" + value + "\"";
        }
        return lista.replaceFirst(",", "");
    }

    private boolean validarUsuario(String username, String password) {
        return true;//INLUSTRATIVO
    }

}
