package main.model;
import java.util.ArrayList;
import java.util.List;
public class SurveyModel {
    private int id;
    private String question;
    private int numberOfChoices;
    private List<String> choices;
    private boolean published;
    private String category;
    private int surveyLimit;
    public SurveyModel() {
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getQuestion() {
        return question;
    }
    public void setQuestion(String question) {
        this.question = question;
    }
    public int getNumberOfChoices() {
        return numberOfChoices;
    }
    public void setNumberOfChoices(int numberOfChoices) {
        this.numberOfChoices = numberOfChoices;
    }
    public List<String> getChoices() {
        return choices != null ? choices : new ArrayList<>();
    }
    public void setChoices(List<String> choices) {
        this.choices = choices;
    }
    public boolean isPublished() {
        return published;
    }
    public void setPublished(boolean published) {
        this.published = published;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public int getSurveyLimit() {
        return surveyLimit;
    }
    public void setSurveyLimit(int surveyLimit) {
        this.surveyLimit = surveyLimit;
    }
    public SurveyModel(String question, int numberOfChoices, List<String> choices, String category, int surveyLimit) {
        this.question = question;
        this.numberOfChoices = numberOfChoices;
        this.choices = choices;
        this.category = category;
        this.surveyLimit = surveyLimit;
    }
}
