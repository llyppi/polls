/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.app.ws;

import br.com.utilitarios.UteisFile;
import java.io.File;

/**
 *
 * @author Felipe L. Garcia
 */
public class ConnTestWS extends WS{

    public ConnTestWS() {

    }
    
    public static void main(String[] args) {
        String result = new ConnTestWS().test();
        
        System.out.println(result);
    }
    
    public String getTest(int cliente){
        ParametroJWS param = new ParametroJWS(url);

        param.setDeclaringClass(getClass().getName());
        param.setMethod("getTest");
        param.setParamValue(cliente);
        param.setParamName("arg0");
        param.setParamTypes(int.class);
        param.setReturnType(String.class);
        param.setNameSpace(getNameSpace(this.getClass()));

        Object rt = getRemoteSOAP(param);
        super.setMsgErro(param.getMsgErro());
        
        if(rt==null){
            return null;
        }
        
        return (String) rt;
    }
    
    public String test(){
         System.setProperty("java.net.useSystemProxies", "false");
//        System.setProperty("http.proxyHost", "192.168.5.1");
//        System.setProperty("http.proxyPort", "3128");
//        System.setProperty("https.proxyHost", "192.168.5.1");
//        System.setProperty("https.proxyPort", "3128");
//        System.setProperty("socksProxyHost", "192.168.5.1");
//        System.setProperty("socksProxyPort", "3128");
//        
//        if(UteisMetodos.isOnline("https://webservices.producaorestrita.esocial.gov.br"
//                + "/servicos/empregador/enviarloteeventos/WsEnviarLoteEventos.svc")){
//        if(!UteisMetodos.isOnlineNetwork()){
//            return "OFFLINE";
//        }
        
        File f = new File("C:\\Users\\Administrator\\Desktop\\XMLEsocial.xml");
        String xml = null;
        try {
            xml = UteisFile.read(f);
        } catch (Exception ex) {
        }
        
        ParametroJWS param = new ParametroJWS(
                "https://webservices.producaorestrita.esocial.gov.br"
                        + "/servicos/empregador"
                        + "/enviarloteeventos/WsEnviarLoteEventos.svc");

        param.setDeclaringClass(getClass().getName());
        param.setMethod("EnviarLoteEventos");
        param.setParamValue(xml);
        param.setParamName("arg0");
        param.setParamTypes(String.class);
        param.setReturnType(String.class);
        param.setNameSpace("http://www.esocial.gov.br/schema/lote/eventos/envio/v2_4_02/");

        Object rt = getRemoteSOAP(param);
        super.setMsgErro(param.getMsgErro());

        if (rt == null) {
            return null;
        }

        return (String) rt;   
    }
}
