/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.app.ws;

/**
 *
 * @author Felipe L. Garcia
 */
public class MonitorAppWS extends WS{

    public MonitorAppWS() {
        
    }
    
    public boolean registerIP(String ip){       
        ParametroJWS param = new ParametroJWS(url);

        param.setDeclaringClass(getClass().getName());
        param.setMethod("registerIP");
        param.setParamValue(ip);
        param.setParamName("arg0");
        param.setParamTypes(String.class);
        param.setReturnType(Boolean.class);
        param.setNameSpace(getNameSpace(this.getClass()));

        Object rt = getRemoteSOAP(param);
        super.setMsgErro(param.getMsgErro());
        
        if(rt==null){
            return false;
        }
        
        return Boolean.valueOf((String)rt);
    }        
}
