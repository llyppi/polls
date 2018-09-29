
package com.app;

//import com.sun.jersey.core.util.Priority;
//import java.io.IOException;
//import java.util.Calendar;
//import java.util.Map;
//import javax.ws.rs.NotAuthorizedException;
//import javax.ws.rs.Priorities;
//import javax.ws.rs.container.ContainerRequestContext;
//import javax.ws.rs.container.ContainerRequestFilter;
//import javax.ws.rs.core.HttpHeaders;
//import javax.ws.rs.core.Response;
//import javax.ws.rs.ext.Provider;

/**
 * Classe sempre autentifica metodos que possui @Secured
 * 
 * @author Felipe L. Garcia
 */
//@Secured
//@Provider
//@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter {//implements ContainerRequestFilter {

//    @Override
//    public void filter(ContainerRequestContext requestContext) throws IOException {
//
//        // Get the HTTP Authorization header from the request
//        String authorizationHeader = 
//            requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
//
//        // Check if the HTTP Authorization header is present and formatted correctly 
//        if (authorizationHeader == null 
//                || !authorizationHeader.startsWith("token ")) {
//            throw new NotAuthorizedException("Authorization header must be provided");
//        }
//
//        // Extract the token from the HTTP Authorization header
//        String token = authorizationHeader.substring("token ".length()).trim();
//
//        try {
//
//            validateToken(token);
//
//        } catch (Exception e) {
//            requestContext.abortWith(
//                Response.status(Response.Status.UNAUTHORIZED).build());
//        }
//    }
//
//    /** Validar timeCreate+timeExp > NOW*/
//    private void validateToken(String token) throws Exception {
//        Map<Token,Long> listToken = PollsService.getListToken();
//        
//        for (Map.Entry<Token, Long> entry : listToken.entrySet()) {
//            Token tok = entry.getKey();
//            Long timeCreate = entry.getValue();
//            //BUSCAR TOKEN
//            if(!tok.getAccess_token().equals(token)){
//                continue;
//            }
//
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTimeInMillis(timeCreate);
//            //DIMINUIR TEMPO DE EXPIRAR TOKEN
//            calendar.add(Calendar.MILLISECOND, (int) tok.getExpires_in());
//            //DENTRO DO PRAZO
//            if(calendar.getTimeInMillis() > System.currentTimeMillis()){
//                return ;
//            }
//        }
//        throw new Exception("Token inválido");
//    }
}
