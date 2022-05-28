/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.app.ws;

import java.util.Properties;

/**
 *
 * @author Felipe L. Garcia
 */
public class UteisProjetoWS {
    
    public static Properties getConfigProperties() {
        ParametroWS parametroRemoto = new ParametroWS();
        parametroRemoto.setMethod("getConfigProperties");
        parametroRemoto.setClasseCreate(UteisProjetoWS.class.getName());
        
//        return (Properties)  WS.getRemoteJWS(parametroJWS);
        return (Properties)  new WS().getRemoteWS(parametroRemoto);
    }
}
