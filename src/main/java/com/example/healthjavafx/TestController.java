package com.example.healthjavafx;

import javafx.fxml.FXML;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TestController {

    @FXML
    protected void initialize() {
        // Initialisation si nécessaire
    }

    public String fetchJsonData() {
        // URL de l'API à appeler
        String url = "https://st5-capteurs-440708.ew.r.appspot.com/get"; // Remplacez par votre URL

        // Création du client HTTP
        HttpClient client = HttpClient.newHttpClient();

        // Création de la requête
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET() // Méthode GET
                .build();

        // Envoi de la requête et traitement de la réponse
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body(); // Retourne la réponse JSON
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Retourne null en cas d'erreur
        }
    }

    public static void main(String[] args) {
        System.out.println(new TestController().fetchJsonData());
    }
}
