/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.app.ws;

import br.com.utilitarios.UteisEmail;
import br.com.utilitarios.UteisMetodos;
import br.com.utilitarios.UteisProjeto;
import java.util.Calendar;
import java.util.Date;
import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.encoding.ser.BeanDeserializerFactory;
import org.apache.axis.encoding.ser.BeanSerializerFactory;

/**
 * EXECUTA REQUISIÇÕES AO WEBSERVICE DEFINIDO NO CONFIG
 * POSSUI 2 FORMAS DE COMUNICAÇÃO
 * 1 - VIA CLASSE GENERICA_WS.JWS PRESENTE NO WEBSERVICE 
 * 2 - VIA CLASSE DIRETA ONDE URL VAI CONTER CLASSE.JWS PRESENTE NO WEBSERVICE 
 * 
 * @author Felipe L. Garcia
 */
public class WS {
    private String msgErro;
        
    private static long lastEmailErro;

    private String urlRoot="";
    protected String url;
    
    public WS() {
        url = UteisProjeto.getConfig("urlWebservice")
            + "/" + this.getClass().getSimpleName()+"?wsdl";

    }

    public void setUrlRoot(String urlRoot) {
        this.urlRoot = urlRoot;
        this.url = urlRoot+ "/" +url;
    }

    public String getMsgErro() {
        return msgErro;
    }

    public void setMsgErro(String msgErro) {
        this.msgErro = msgErro;
    }

    /**
     * REQUISITA CLASSE PRINCIPAL WS.CLASS NO WEBSERVICE
     * <P>
     * CRIARA INSTANCIA DA CLASSE CONTIDA NO PARAMENTRO E EXECUTA O METODO DEZEJADO
     * <P>
     * TODAS AS CLASSES QUE ESTAO CONTIDAS NO ParametroRemoto 
     * DEVEM POSSUIR implements Serializable
     * <P>
     * RETORNA SEMPRE BYTE[] QUE SERA CONVERTIDO NO OBEJETO JA ESPERADO
     * OU EXCEPTION DE ERROS OCORRIDOS NO WEBSERVICE
     */
    public Object getRemoteWS(ParametroWS paramRemoto) {
        //URL DA CLASSE GENERICA NO WEBSERVICE 
        //http://localhost:8080/webservice/WS.jws
        url = urlRoot+ "/" +UteisProjeto.getConfig("urlWebservice")
                +"/GenericWS.jws";

        try {
            if (paramRemoto == null) {
                throw new Exception("paramRemote=null");
            }
            if (paramRemoto.getClasseCreate() == null || paramRemoto.getClasseCreate().isEmpty()) {
                throw new Exception("classe=null");
            }
            if (paramRemoto.getMethod() == null || paramRemoto.getMethod().isEmpty()) {
                throw new Exception("method=null");                
            }

            Call call = new Call(url);
            call.setTargetEndpointAddress(url);

            //DEFINE PARAMETRO DO METODO REMOTE(getRemote)
            call.addParameter("paramRemotoBty",
                    getQNamePrimitive(byte[].class)//TIPO CORRESPONDENTE JAVA/XML
                    , byte[].class, ParameterMode.IN);

            //PARAM DO METODO getRemote NO WEBSERVICE
            //public byte[] getRemote(byte[] paramRemotoBty){
            byte[] paramRemoteBty = UteisMetodos.toByte(paramRemoto);
            if (paramRemoteBty == null) {
                throw new Exception("paramRemoteBty not Serializable");
            }
            Object[] paramInvokeWS = new Object[]{paramRemoteBty};

            //DEFINE METODO Q SERA EXEC
            call.setOperationName(new QName("urn:" + "getRemote", "getRemote"));
            //DEFINE Return METODO Q SERA EXEC
            call.setReturnType(XMLType.XSD_BYTE, byte[].class);

            //EXEC getRemote
            Object retorno = call.invoke(paramInvokeWS);

            if (retorno == null) {
                System.out.println("retorno null WEBSERVICE");
                return null;
            }

            if (!(retorno instanceof byte[])) {
                System.out.println("retorno UNKNOW WEBSERVICE");
                return null;
            }
            //DESERIALIZABLE
            retorno = UteisMetodos.toObject((byte[]) retorno);

            if (retorno == null) {
                throw new Exception("retorno not Serializable");
            }

            //SE HOUVER ERRO NO WEBSERVICE,retorno = Exception
            if (retorno instanceof Exception) {
                throw (Exception) retorno;
            }
            
            System.out.println("retorno ok WEBSERVICE");
            return retorno;

            
        } catch (Exception e) {
            if (e.getCause() instanceof java.net.ConnectException) {
                sendEmailOffline(url);
            }
            e.printStackTrace();
        }finally{
            System.out.println("WEBSERVICE "+url);
        }
        return null;
    }

    /**
     * FAZ REQUISICAO DIRETA VIA URL CLASSE.JWS
     * <P>
     * ARQUIVO CLASSE.JWS DEVE EXISTIR NO WEBSERVICE
     * <P>
     * PARAMENTROS NAO PRIMITIVOS SAO CONVERTIDOS/BYTE
     * <P>
     * CLASSE.JWS JA DEVE IMPLEMENTAR METODO QUE ESPERA PARAM/BYTE
     * <P>
     * TODOS OBJS DEVEM TER implements Serializable
     * 
     */
    public Object getRemoteJwsByte(ParametroJWS param) {
        if (param.getMethod() == null) {
            System.out.println("method=null");
            return null;
        }
        //DEFINE RETORNO DO METODO Q SERA EXECUTADO
        Class returnType = param.getReturnType();

        //CLASSE POSSUE METODO DECLARADO
        String declareClass = param.getDeclaringClass();
        declareClass = declareClass.replaceAll("\\.", "/");//SEPARACAO DE PACOTES

        String methodName = param.getMethod();

        System.out.println("WEBSERVICE "+param.getUrl());
        
        try {
            Call call = new Call(param.getUrl());
            call.setTargetEndpointAddress(param.getUrl());

            //PARAM DO METODO Q SERA REQUISITADO
            Class[] paramTypes = param.getParamTypes();
            
            if(paramTypes!=null){
                for (int i = 0; i < paramTypes.length; i++) {
                    //TIPO CORRESPONDENTE JAVA/XML
                    QName qParam = getQNamePrimitive(paramTypes[i]);
                    
                    if (qParam == null) {
                        qParam = XMLType.XSD_ANYURI;
//                        qParam = new QName(getNameSpace(paramTypes[i])
//                                ,paramTypes[i].getName(),"tns");
//                        
//                        //Serializer
                        call.registerTypeMapping(paramTypes[i], qParam
                            , new BeanSerializerFactory(paramTypes[i], qParam)
                            , new BeanDeserializerFactory(paramTypes[i], qParam));                        
                    }

                    call.addParameter(param.getParamName()[i]
                            , qParam, paramTypes[i], ParameterMode.IN);
                   
                }
            }            

            //DEFINE METODO Q SERA EXEC
            call.setOperationName(new QName(param.getNameSpace()
                                        ,methodName));
//            call.setOperationName(new QName("urn:" + methodName, methodName));
//            call.setOperationName(methodName);

            //TIPO CORRESPONDENTE JAVA/XML
            QName qReturn = getQNamePrimitive(returnType);
            Object retorno;

            if (qReturn != null) {//isPrimitive TIPO
                call.setReturnType(qReturn, returnType);
            } else {//NOT isPrimitive SERA SEMPRE BYTE[]
                call.setReturnType(XMLType.XSD_BYTE, byte[].class);
            }
            
            retorno = call.invoke(param.getParamValue());
            
            if (retorno == null) {
                return null;
            }
           
            if (retorno instanceof byte[]) {
                //DESERIALIZABLE
                retorno = UteisMetodos.toObject((byte[]) retorno);
                
                if (retorno == null) {
                    throw new Exception("retorno not Serializable");
                }
                System.out.println("retorno ok WEBSERVICE");
                return retorno;
            }
            
            //SE HOUVER ERRO NO WEBSERVICE,retorno = Exception
            if (retorno instanceof Exception) {
                throw (Exception) retorno;
            }

            System.out.println("retorno ok WEBSERVICE");
            return retorno;

        } catch (Exception e) {
            if (e.getCause() instanceof java.net.ConnectException) {
                sendEmailOffline(param.getUrl());
            }
            e.printStackTrace();
        }finally{
//            System.out.println("WEBSERVICE "+param.getUrl());
        }
        return null;
    }

    /**
     * FAZ REQUISICAO VIA URL http://webservice/CLASSE?wsdl
     * <P>
     */

    public String getRemoteSOAP(ParametroJWS param){
        String declareClass = param.getDeclaringClass();
        //SEPARACAO DE PACOTES
        declareClass = declareClass.replaceAll("\\.", "/");
        
        System.out.println("WEBSERVICE "+param.getUrl());
        
        try {
            SOAPMessage message = MessageFactory.newInstance().createMessage();
            SOAPHeader header = message.getSOAPHeader();
            header.detachNode();
            /*
            SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
            envelope.setAttribute("namespace","namespaceUrl");
            */
            SOAPBody body = message.getSOAPBody();    
            
            QName bodyName = new QName(param.getNameSpace()
                    , param.getMethod(), "ns");                        
            
            SOAPBodyElement bodyElement = body.addBodyElement(bodyName);

            if(param.getParamValue()!=null){
                for (int i = 0; i < param.getParamValue().length; i++) {

                    SOAPElement symbol = bodyElement
                            .addChildElement(param.getParamName()[i]);

                    String valeu=UteisMetodos.nz(param.getParamValue()[i],"");
                    symbol.addTextNode(valeu);
                }
            }
            SOAPConnection connection = SOAPConnectionFactory
                                .newInstance().createConnection();
            SOAPMessage response = connection.call(message, param.getUrl());
            connection.close();
            
            SOAPBody responseBody = response.getSOAPBody();
            SOAPBodyElement responseElement = (SOAPBodyElement) responseBody.getChildElements().next();
            SOAPElement returnElement = (SOAPElement) responseElement.getChildElements().next();
            if (responseBody.getFault() != null) {
                param.setMsgErro(responseBody.getFault().getFaultString());
                return  null;
            } else {
                return returnElement.getValue();
            }
        } catch (SOAPException ex) {           
//            System.out.println(param.getUrl());
            System.out.println("ERRO WEBSERVICE CONNECTION");
            System.out.println(param.getNameSpace());
//            ex.printStackTrace();
        }
        return null;
    }
    /**
     * FAZ REQUISICAO DIRETA VIA URL CLASSE.JWS
     * <P>
     * ARQUIVO CLASSE.JWS DEVE EXISTIR NO WEBSERVICE
     * <P>
     * PARAMENTROS NAO PRIMITIVOS DEVEM SER ESTRUTURADOS COM 'HASHMAP'
     * <P>
     * ATRIBUTO->VALOR(KEY->VALUE)
     * <P>
     * CLASSE.JWS JA DEVE IMPLEMENTAR METODO QUE ESPERA 'HASHMAP'
     * 
     */
    public static Object getRemoteJwsMap(ParametroJWS param) {
        if (param.getMethod() == null) {
            System.out.println("method=null");
            return null;
        }
        //CLASSE POSSUE METODO DECLARADO
        String declareClass = param.getDeclaringClass();
        declareClass = declareClass.replaceAll("\\.", "/");//SEPARACAO DE PACOTES

        String methodName = param.getMethod();

        System.out.println("WEBSERVICE "+param.getUrl());
        
        try {
            Call call = new Call(param.getUrl());
            call.setTargetEndpointAddress(param.getUrl());

            //PARAM DO METODO Q SERA REQUISITADO
            Class[] paramTypes = param.getParamTypes();
            for (int i = 0; i < paramTypes.length; i++) {
                //TIPO CORRESPONDENTE JAVA/XML
                QName qParam = getQNamePrimitive(paramTypes[i]);

                //NOT isPrimitive
                if (qParam == null) {
                    qParam = XMLType.SOAP_MAP;
                    
                    Object paramValue = param.getParamValue()[i];
                    
                    //CONVERTER OBJECT/MAP
                    param.getParamValue()[i]
                            = UteisMetodos.toMap(paramValue);
                    continue;
                }

                call.addParameter(param.getParamName()[i]
                        , qParam, paramTypes[i], ParameterMode.IN);
            }
            
            //DEFINE METODO Q SERA EXEC
            call.setOperationName(new QName("urn:" + methodName, methodName));

            //DEFINE RETORNO DO METODO Q SERA EXECUTADO
            Class returnType = param.getReturnType();
            //TIPO CORRESPONDENTE JAVA/XML
            QName qReturn = getQNamePrimitive(returnType);
            if (qReturn == null) {//not isPrimitive
                registerTypeMap(call, returnType);

                qReturn = getQNameTypeMap(returnType);                
            }

            call.setReturnType(qReturn, returnType);
            
            Object retorno = call.invoke(param.getParamValue());
            
            if (retorno == null) {
                return null;
            }
           
            if (retorno instanceof byte[]) {
                //DESERIALIZABLE
                retorno = UteisMetodos.toObject((byte[]) retorno);
                
                if (retorno == null) {
                    throw new Exception("retorno not Serializable");
                }
                System.out.println("retorno ok WEBSERVICE");
                return retorno;
            }
            
            //SE HOUVER ERRO NO WEBSERVICE,retorno = Exception
            if (retorno instanceof Exception) {
                throw (Exception) retorno;
            }

            System.out.println("retorno ok WEBSERVICE");
            return retorno;

        } catch (Exception e) {
            if (e.getCause() instanceof java.net.ConnectException) {
                sendEmailOffline(param.getUrl());
            }
            e.printStackTrace();
        }finally{
//            System.out.println("WEBSERVICE "+param.getUrl());
        }
        return null;
    }

    private static QName getQNameTypeMap(Class type) {
//        String localName = Types.getLocalNameFromFullName(
//                type.getName());
        
//        QName qParam = new QName(
//                Namespaces.makeNamespace(type.getName())
//                , localName);
        QName qParam = new QName("ns:"+type.getSimpleName(),type.getSimpleName());
        
        return qParam;
    }
    
    private static void registerTypeMap(Call call,Class type)  {
//        ServiceFactory factory1 = ServiceFactory.newInstance();
        
        QName qName = getQNameTypeMap(type);

        Service serviceTickReq = call.getService();
//        Service serviceTickReq = factory1.createService(qnTick);
//        Service serviceTickReq = new org.apache.axis.client.Service();
        
        TypeMappingRegistry tmr = (TypeMappingRegistry) serviceTickReq.getTypeMappingRegistry();
        
        BeanSerializerFactory serializer = new BeanSerializerFactory(type, qName);
        BeanDeserializerFactory deserializer = 
                        new BeanDeserializerFactory(type, qName);
        
        TypeMapping tm = (TypeMapping) tmr.getDefaultTypeMapping();
//        TypeMapping tm = (TypeMapping) tmr.getOrMakeTypeMapping(
//                                "http://schemas.xmlsoap.org/soap/encoding/");
        
        if (tm.isRegistered(type, qName)) {
//            tm.removeDeserializer(type, qName);
//            tm.removeSerializer(type, qName);
            return ;
        }
        tm.register(type, qName,serializer , deserializer);
//        tmr.registerDefault(tm);
    }
    
    private static QName getQNamePrimitive(Class type) {
        if (type.equals(Boolean.class)) {
            return XMLType.XSD_BOOLEAN;
        }
        if (type.equals(boolean.class)) {
            return XMLType.XSD_BOOLEAN;
        }
        if (type.equals(Integer.class)) {
            return XMLType.XSD_INTEGER;
        }
        if (type.equals(int.class)) {
            return XMLType.XSD_INT;
        }
        if (type.equals(String.class)) {
            return XMLType.XSD_STRING;
        }
        if (type.equals(Double.class)) {
            return XMLType.XSD_DOUBLE;
        }
        if (type.equals(double.class)) {
            return XMLType.XSD_DOUBLE;
        }
        if (type.equals(Date.class)) {
            return XMLType.XSD_DATE;
        }
        if (type.equals(java.sql.Date.class)) {
            return XMLType.XSD_DATE;
        }
        if (type.equals(byte[].class)) {
            return XMLType.XSD_BYTE;
        }
        if (type.equals(byte.class)) {
            return XMLType.XSD_BYTE;
        }
        if (type.equals(Byte.class)) {
            return XMLType.XSD_BYTE;
        }

        return null;
    }

    private static void sendEmailOffline(String url) {
        Calendar c = Calendar.getInstance();
        long time = System.currentTimeMillis();

        c.setTimeInMillis(time);
        c.add(Calendar.HOUR_OF_DAY, -1);

        //SE OCORREU EM MENOS DE 1H
        if (lastEmailErro > 0 && lastEmailErro >= c.getTimeInMillis()) {
            return;
        }

        lastEmailErro = time;
        
        UteisEmail email = new UteisEmail();
        email.setMsg("WS: " + url);
        email.setAssunto("WEB SERVICE OFFLINE");
        
        try {
//            UteisEmail.sendEmailApache(email);
        } catch (Exception ex) {
        }
    }
    
    /**
     * "http://ws.sf.com.br"
     */
    public String getNameSpace(Class cls){
        return getNameSpace(cls.getPackage().getName());
    }
    public String getNameSpace(String cls){
        String[] pk = cls.split("\\.");
        
        String name="";
        
        for (int i = pk.length-1; i >=0 ; i--) {
            name+="."+pk[i];
        }
        
        return "http://"+name.replaceFirst("\\.", "")+"/";
    }
}
