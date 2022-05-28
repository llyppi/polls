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
public class ParametroWS implements Serializable{
    private String classeCreate;
    private Class[] paramConstrType;
    private Object[] paramConstrValue;
    
    private String method;
    private Object[] paramMetodo;

    public Class[] getParamConstrType() {
        return paramConstrType;
    }

    public void setParamConstrType(Class... paramClasse) {
        this.paramConstrType = paramClasse;
    }

    public Object[] getParamConstrValue() {
        return paramConstrValue;
    }

    public void setParamConstrValue(Object... paramConstr) {
        this.paramConstrValue = paramConstr;
    }
    
    public String getClasseCreate() {
        return classeCreate;
    }

    public void setClasseCreate(String classeCreate) {
        this.classeCreate = classeCreate;
    }
    
    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object[] getParamMetodo() {
        return paramMetodo;
    }

    public void setParamMetodo(Object... paramMetodo) {
        this.paramMetodo = paramMetodo;
    }
    
}
