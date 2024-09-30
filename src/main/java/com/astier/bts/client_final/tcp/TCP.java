/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.astier.bts.client_final.tcp;
import com.astier.bts.client_final.DiffieHelleman.DiffieHelleman;
import com.astier.bts.client_final.HelloController;
import com.astier.bts.client_final.aes.Aes_cbc;
import javafx.application.Platform;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static javafx.scene.paint.Color.RED;

/**
 * @author Michael
 */
public class TCP extends Thread {
    int port;
    InetAddress serveur;
    public Socket socket;
    public boolean marche = false;
    public OutputStream outBin;
    public InputStream inBin;
    HelloController fxmlCont;
    byte[] bufferEntreeBin = new byte[65535];
    public  Aes_cbc aesCbc;
    DiffieHelleman clefIV;
    byte [] paramclefIV;
    public TCP() {
    }

    public TCP(InetAddress serveur, int port, HelloController fxmlCont) {
        this.port = port;
        this.serveur = serveur;
        this.fxmlCont = fxmlCont;
        System.out.println("@ serveur: " + serveur + " port: " + port);




    }

    public void connection() {
        if (!this.isAlive()) {
            try {
                try {
                    this.socket = new Socket(this.serveur, this.port);
                } catch (IOException ex) {
                }
                outBin = socket.getOutputStream();
                inBin = socket.getInputStream();
                clefIV = new DiffieHelleman(this, 1024);
                paramclefIV = clefIV.recuperationParam();
                byte[]clef = Arrays.copyOfRange(paramclefIV,1,17);
                byte[]IV = Arrays.copyOfRange(paramclefIV,17,33);
                aesCbc=new Aes_cbc(clef,IV);
                // fin recuper la clef et le verteru iv avec l'algorithme de Diffie-Hellman
                this.start();    //lance un thread par la methode run qui est la methode du thread d'écoute
                marche = true;
            } catch (IOException ex) {
            }
        }
    }

    public void deconnection() throws InterruptedException {
        if (this.isAlive()) {
            try {
                if (socket != null && !socket.isClosed()) {
                    fxmlCont.voyant.setFill(RED);
                    if (outBin != null) {
                        outBin.write(aesCbc.cryptage("exit".getBytes(StandardCharsets.UTF_8)));
                    }
                    Thread.sleep(500);
                    if (outBin != null) outBin.close();
                    if (inBin != null) inBin.close();

                    if (socket != null && !socket.isClosed()) {
                        socket.close();
                    }
                    Platform.runLater(() -> fxmlCont.voyant.setFill(RED));

                    fxmlCont.deconnecter.fire();
                    marche = false;
                    System.out.println("Déconnexion réussie.");
                }
            } catch (IOException | InterruptedException e) {
                System.err.println("Erreur lors de la déconnexion: " + e.getMessage());
            }
        }
    }

    public void requette(String laRequette) throws IOException {
        outBin.write(aesCbc.cryptage(laRequette.getBytes(StandardCharsets.UTF_8)));
        System.out.println("la requette " + laRequette);
    }

    public void run() {
        String message;
        while (marche) {
            try {
                int nbLusBin = inBin.read(bufferEntreeBin);
                byte[] trameUtile= Arrays.copyOfRange(bufferEntreeBin, 0, nbLusBin);

                message = new String(aesCbc.decryptage(trameUtile));
                if (message.length() != 0) {
                    System.out.println("new String(aesCbc.decryptage(trameUtile))"+new String(aesCbc.decryptage(trameUtile)));
                    System.out.println("    MESSAGE SERVEUR >  \n      " + message + "\n");
                    updateMessage(message);
                }

            } catch (Exception ex) {

            }
        }
    }


    /*
    Pour déclencher une opération graphique en dehors du thread graphique  utiliser
    javafx.application.Platform.runLater(java.lang.Runnable)
    Cette méthode permet d'éxécuter le code du runnable par le thread graphique de JavaFX.
    */
    protected void updateMessage(String message) {
        Platform.runLater(() -> fxmlCont.textAreaReponses.appendText("    MESSAGE SERVEUR >  \n      " + message + "\n"));
    }
}