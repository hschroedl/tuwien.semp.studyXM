package at.ac.tuwien.sepm.ss16.qse18.gui.exam;

import at.ac.tuwien.sepm.ss16.qse18.domain.Exam;
import at.ac.tuwien.sepm.ss16.qse18.domain.ExerciseExam;
import at.ac.tuwien.sepm.ss16.qse18.domain.Topic;
import at.ac.tuwien.sepm.ss16.qse18.gui.BaseController;
import at.ac.tuwien.sepm.ss16.qse18.service.ServiceException;
import at.ac.tuwien.sepm.ss16.qse18.service.impl.ExamServiceImpl;
import at.ac.tuwien.sepm.ss16.qse18.service.impl.ExerciseExamServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author  Zhang Haixiang on 02.06.2016.
 */
@Component public class ShowResultController extends BaseController{
    @Autowired private ExerciseExamServiceImpl exerciseExamService;
    @FXML public PieChart pieChart;
    @FXML public BarChart barChart;

    @FXML public Button buttonFinish;

    @FXML public Label labelGrade;

    @FXML public CategoryAxis xAxis = new CategoryAxis();
    @FXML public NumberAxis yAxis = new NumberAxis();



    public ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

    @FXML public void initialize(ExerciseExam exerciseExam){
        pieChart.getData().clear();
        pieChartData.clear();

        String[] result = new String[3];

        Map<Topic, String[]> topicResult = new HashMap<>();


        try {
            result = exerciseExamService.gradeExam(exerciseExam);
            labelGrade.setText(result[2]);
            pieChartData.add(new PieChart.Data("correct", Double.parseDouble(result[0])));
            pieChartData.add(new PieChart.Data("false", Double.parseDouble(result[1])));
            pieChart.setTitle("correct/false answers");
            pieChart.setTitleSide(Side.BOTTOM);
            pieChart.setData(pieChartData);
            pieChart.setLegendVisible(false);
            pieChartData.get(0).getNode().setStyle("-fx-pie-color: " + "limegreen");
            pieChartData.get(1).getNode().setStyle("-fx-pie-color: " + "firebrick");


            yAxis.setLabel("answered questions");
            barChart.setTitle("performance in subject areas");
            barChart.setTitleSide(Side.BOTTOM);


            topicResult = exerciseExamService.topicGrade(exerciseExam);
            XYChart.Series series1 = new XYChart.Series();
            series1.setName("false");

            for(Map.Entry<Topic, String[]> m: topicResult.entrySet()){
                XYChart.Data data = new XYChart.Data(m.getKey().getTopic(), Double.parseDouble(m.getValue()[1]));
                //data.getNode().setStyle("-fx-bar-fill: " + "limegreen"); funktioniert iwie nicht
                series1.getData().add(data);
            }


            XYChart.Series series2 = new XYChart.Series();
            series2.setName("correct");

            for(Map.Entry<Topic, String[]> f: topicResult.entrySet()){
                series2.getData().add(new XYChart.Data(f.getKey().getTopic(),  Double.parseDouble(f.getValue()[0])));
            }

            barChart.getData().addAll(series1, series2);


        }catch (ServiceException e){
            showError(e);
        }
    }

    public void finish(){
        mainFrameController.handleHome();
    }

}
