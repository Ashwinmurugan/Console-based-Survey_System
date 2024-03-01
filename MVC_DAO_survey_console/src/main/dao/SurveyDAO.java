package main.dao;
import main.model.SurveyModel;

import java.util.List;
public interface SurveyDAO {
    boolean incrementChoiceCount(int surveyId, List<Integer> choiceIndexes);
    boolean incrementChoiceCount(int surveyId, String userVote);
    List<SurveyModel> getSurveysByCategory(String category);
    boolean addSurvey(SurveyModel survey);
    boolean removeSurvey(int surveyId);
    List<SurveyModel> getAllSurveys();
    SurveyModel getSurveyById(int surveyId);
    boolean publishSurvey(int surveyId);
    List<String> getSurveyChoices(int surveyId);
    String getSurveyCategory(int surveyId);
    List<String> getUniqueSurveyCategories();
    int getSurveyIdByCategory(String selectedCategory);
    List<SurveyModel> getAllSurveysWithChoices();
    List<SurveyModel> getAllSurveysWithChoices(String category);
    int getChoiceCount(int surveyId, String choice);
    int getSurveyLimit(int surveyId);
    List<SurveyModel> getSurveysWithReports();
    int getTotalChoiceCount(int surveyId);
}
