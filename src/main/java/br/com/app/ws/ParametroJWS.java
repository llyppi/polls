/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.app.ws;

import java.io.Serializable;

/**
 *
 * @author Felipe L. Garcia
 */
public class ParametroJWS implements Serializable{
    private String url;
    private String method;
    private Class returnType;
    private String declaringClass;
    private String nameSpace;
    private Object[] paramValue;
    private Class[] paramTypes;
    private String[] paramName;
    private String msgErro;

    public ParametroJWS(String url) {
        this.url = url;
    }

    public String getMsgErro() {
        return msgErro;
    }

    public void setMsgErro(String msgErro) {
        this.msgErro = msgErro;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    public Class[] getParamTypes() {
        return paramTypes;
    }

    public void setParamTypes(Class... paramTypes) {
        this.paramTypes = paramTypes;
    }

    public String getDeclaringClass() {
        return declaringClass;
    }

    public void setDeclaringClass(String declaringClass) {
        this.declaringClass = declaringClass;
    }

    public Class getReturnType() {
        return returnType;
    }

    public void setReturnType(Class returnType) {
        this.returnType = returnType;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object[] getParamValue() {
        return paramValue;
    }

    public void setParamValue(Object... paramValue) {
        this.paramValue = paramValue;
    }

    public String[] getParamName() {
        return paramName;
    }

    public void setParamName(String... paramName) {
        this.paramName = paramName;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }
    
}
