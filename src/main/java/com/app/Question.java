/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app;

import java.util.Date;

/**
 *
 * @author Felipe L. Garcia
 */
public class Question {
    private int question_id;
    private String question;
    private Date published_at;
    private String url;
    private Choice[] choice;

    public Question() {
    }

    @Override
    public String toString() {
        return PollsService.getAtributosValue(this);
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public int getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(int question_id) {
        this.question_id = question_id;
    }

    public Date getPublished_at() {
        return published_at;
    }

    public void setPublished_at(Date published_at) {
        this.published_at = published_at;
    }

    public Choice[] getChoice() {
        return choice;
    }

    public void setChoice(Choice... choice) {
        this.choice = choice;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
}

