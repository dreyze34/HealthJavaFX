package com.example.healthjavafx;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.util.Duration;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.json.JSONObject;

public class HelloController {

    @FXML
    private LineChart<Number, Number> glucoseChart;
    @FXML
    private NumberAxis timeAxis;

    @FXML
    private LineChart<Number, Number> realtimeChart;
    @FXML
    private NumberAxis realtimeXAxis;
    @FXML
    private NumberAxis realtimeYAxis;

    private XYChart.Series<Number, Number> dailySeries = new XYChart.Series<>();
    private XYChart.Series<Number, Number> realtimeSeries = new XYChart.Series<>();

    private List<Double> lastTenMinuteValues = new ArrayList<>();
    private int secondCounter = 0;
    private int realtimeCounter = 0;

    private HttpClient httpClient = HttpClient.newHttpClient();

    @FXML
    protected void initialize() {
        glucoseChart.getData().add(dailySeries);
        realtimeChart.getData().add(realtimeSeries);

        configureAxes();
        loadDailyData();
        showDailyData();

        Timeline realtimeTimeline = new Timeline(new KeyFrame(Duration.millis(100), event -> fetchRealTimeGlucoseData()));
        realtimeTimeline.setCycleCount(Timeline.INDEFINITE);
        realtimeTimeline.play();
    }

    private void configureAxes() {
        realtimeXAxis.setAutoRanging(false);
        realtimeXAxis.setLowerBound(0);
        realtimeXAxis.setUpperBound(60);
        realtimeYAxis.setAutoRanging(false);
        realtimeYAxis.setLowerBound(70);
        realtimeYAxis.setUpperBound(180);

        timeAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(timeAxis) {
            @Override
            public String toString(Number value) {
                int minutes = value.intValue();
                if (minutes % 60 == 0) {
                    return minutes + " (" + (minutes / 60) + "h)";
                } else {
                    return String.valueOf(minutes);
                }
            }
        });
    }

    @FXML
    protected void showDailyData() {
        dailySeries.setName("Données journalières");
        timeAxis.setLabel("Temps (minutes)");
        timeAxis.setLowerBound(0);
        timeAxis.setUpperBound(1440);
        timeAxis.setTickUnit(10);
        glucoseChart.setData(FXCollections.observableArrayList(dailySeries));
    }

    private void loadDailyData() {
        // Méthode pour charger les données journalières initiales (ici pour l'exemple).
        Random random = new Random();
        for (int i = 0; i < 1440; i += 10) {
            dailySeries.getData().add(new XYChart.Data<>(i, 70 + random.nextDouble() * 50));
        }
    }

    private void fetchRealTimeGlucoseData() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://st5-capteurs-440708.ew.r.appspot.com/get")) // Remplacez l'URL ici
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(this::processRealTimeData)
                .exceptionally(ex -> {
                    System.out.println("Erreur lors de la récupération des données : " + ex.getMessage());
                    return null;
                });
    }

    private void processRealTimeData(String responseBody) {
        try {
            JSONObject json = new JSONObject(responseBody);
            double glucoseLevel = json.getDouble("taux");
            long time = json.getLong("date");

            updateRealTimeChart(glucoseLevel);
            updateDailyData(glucoseLevel, time);
        } catch (Exception e) {
            System.out.println("Erreur lors du traitement des données : " + e.getMessage());
        }
    }

    private void updateRealTimeChart(double glucoseLevel) {
        realtimeSeries.getData().add(new XYChart.Data<>(realtimeCounter, glucoseLevel));
        realtimeCounter++;

        if (realtimeCounter > 60) {
            realtimeXAxis.setLowerBound(realtimeCounter - 60);
            realtimeXAxis.setUpperBound(realtimeCounter);
            if (realtimeSeries.getData().size() > 60) {
                realtimeSeries.getData().remove(0);
            }
        }
    }

    private void updateDailyData(double glucoseLevel, long timestamp) {
        lastTenMinuteValues.add(glucoseLevel);
        secondCounter++;

        if (secondCounter >= 600) { // Toutes les 10 minutes
            double tenMinuteAverage = lastTenMinuteValues.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            int minutesOfDay = (int) ((timestamp / 1000) % 86400) / 60; // Minutes écoulées dans la journée

            dailySeries.getData().add(new XYChart.Data<>(minutesOfDay, tenMinuteAverage));
            lastTenMinuteValues.clear();
            secondCounter = 0;
        }
    }
}




