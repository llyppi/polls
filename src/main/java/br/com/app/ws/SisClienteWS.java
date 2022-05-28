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
public class SisClienteWS extends WS{

    public SisClienteWS() {
    }
    
    public boolean excluirRSA(int cliente){       
        ParametroJWS param = new ParametroJWS(url);

        param.setDeclaringClass(getClass().getName());
        param.setMethod("excluirRSA");
        param.setParamValue(cliente);
        param.setParamName("arg0");
        param.setParamTypes(int.class);
        param.setReturnType(Boolean.class);
        param.setNameSpace(getNameSpace(this.getClass()));

        Object rt = getRemoteSOAP(param);
        super.setMsgErro(param.getMsgErro());
        
        if(rt==null){
            return false;
        }
        
        return Boolean.valueOf((String)rt);
    }
    
    public String getRSA(int cliente){       
        ParametroJWS param = new ParametroJWS(url);

        param.setDeclaringClass(getClass().getName());
        param.setMethod("getRSA");
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
    
    public boolean gerarRSA(int cliente,int days,String key){
        ParametroJWS param = new ParametroJWS(url);

        param.setDeclaringClass(getClass().getName());
        param.setMethod("gerarRSA");
        param.setParamValue(cliente,days,key);
        param.setParamName("arg0","arg1","arg2");
        param.setParamTypes(int.class,int.class,String.class);
        param.setReturnType(Boolean.class);
        param.setNameSpace(getNameSpace(this.getClass()));

        Object rt = getRemoteSOAP(param);
        super.setMsgErro(param.getMsgErro());
        
        if(rt==null){
            return false;
        }
        
        return Boolean.valueOf((String)rt);
    }
    
    public boolean gerarAppRSA(int appID, int days){
        ParametroJWS param = new ParametroJWS(url);

        param.setDeclaringClass(getClass().getName());
        param.setMethod("gerarAppRSA");
        param.setParamValue(appID,days);
        param.setParamName("arg0","arg1");
        param.setParamTypes(int.class,int.class);
        param.setReturnType(Boolean.class);
        param.setNameSpace(getNameSpace(this.getClass()));

        Object rt = getRemoteSOAP(param);
        super.setMsgErro(param.getMsgErro());
        
        if(rt==null){
            return false;
        }
        
        return Boolean.valueOf((String)rt);
    }
    public boolean excluirAppRSA(int appID){
        ParametroJWS param = new ParametroJWS(url);

        param.setDeclaringClass(getClass().getName());
        param.setMethod("excluirAppRSA");
        param.setParamValue(appID);
        param.setParamName("arg0");
        param.setParamTypes(int.class,int.class);
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
