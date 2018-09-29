package com.app;
/**
 *
 * @author Felipe L. Garcia
 */
public class Question {
    private int question_id;
    private String question;
    private String published_at;
    private String url;
    private Choice[] choices;

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

    public String getPublished_at() {
        return published_at;
    }

    public void setPublished_at(String published_at) {
        this.published_at = published_at;
    }

    public Choice[] getChoices() {
        return choices;
    }

    public void setChoices(Choice... choices) {
        this.choices = choices;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
}

