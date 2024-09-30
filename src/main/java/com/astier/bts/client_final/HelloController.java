package com.astier.bts.client_final;

import com.astier.bts.client_final.tcp.TCP;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.shape.Circle;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javafx.scene.paint.Color.*;

public class HelloController implements Initializable {
    public Button button;
    public Button connecter;
    public Button deconnecter;
    public TextField textFieldIP;
    public TextField textFieldPort;
    public TextField textFieldRequette;
    public Circle voyant;
    public TextArea textAreaReponses;
    public TCP tcp;
    static boolean enRun = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        button.setDisable(true);
        deconnecter.setDisable(true);
        voyant.setFill(RED);

        button.setOnAction(event -> {
            try {
                envoyer();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });


        connecter.setOnAction(event -> {
            try {
                connecter();
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        });


        deconnecter.setOnAction(event -> {
            try {
                deconnecter();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }


    private void envoyer() throws InterruptedException {
        if (!enRun) {
            System.out.println("Impossible d'envoyer : Vous n'êtes pas connecté.");
            return;
        }

        String requette = textFieldRequette.getText();
        if (requette.equalsIgnoreCase("exit")) {
            deconnecter();
        } else if (!requette.isEmpty()) {
            try {
                tcp.requette(requette);
            } catch (IOException ex) {
                Logger.getLogger(HelloController.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Erreur lors de l'envoi de la requête : " + ex.getMessage());
            }
        }
    }


    private void deconnecter() throws InterruptedException {
        if (enRun) {
            try {
                tcp.deconnection();
                enRun = false;


                button.setDisable(true);
                connecter.setDisable(false);
                deconnecter.setDisable(true);
                voyant.setFill(RED);

                System.out.println("Déconnexion réussie.");
            } catch (Exception ex) {
                Logger.getLogger(HelloController.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Erreur lors de la déconnexion : " + ex.getMessage());
            }
        } else {
            System.out.println("Aucune connexion active à déconnecter.");
        }
    }


    private void connecter() throws UnknownHostException {
        String adresseDuServeur = textFieldIP.getText();
        String portDuServeur = textFieldPort.getText();

        if (!adresseDuServeur.isEmpty() && !portDuServeur.isEmpty()) {
            if (!enRun) {
                try {

                    tcp = new TCP(InetAddress.getByName(adresseDuServeur), Integer.parseInt(portDuServeur), this);
                    tcp.connection();


                    voyant.setFill(LIME);
                    enRun = true;

                    connecter.setDisable(true);
                    deconnecter.setDisable(false);
                    button.setDisable(false);

                    System.out.println("Connexion réussie.");
                } catch (IOException ex) {
                    Logger.getLogger(HelloController.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("Erreur lors de la connexion : " + ex.getMessage());
                }
            }
        } else {
            System.out.println("Veuillez fournir une adresse IP et un port.");
        }
    }
}
