package controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import threads.Receiver;
import ui.Taxi;
import ui.Tile;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.locks.ReentrantLock;

public class GridController implements Initializable {

    @FXML
    private Button    sendBtn;
    @FXML
    private Button    addBtn;
    @FXML
    private Label     timeLbl;
    @FXML
    private VBox      rows;
    @FXML
    private TextField commandTxt;
    @FXML
    private TextField driversTxt;
    @FXML
    private Label     errorLbl;

    private ReentrantLock    lock;
    private String           ip;
    private int              port;
    private int              gridHeight;
    private int              gridWidth;
    private Socket           clientSocket;
    private BufferedReader   inFromUser;
    private BufferedReader   inFromServer;
    private DataOutputStream outToServer;
    private String           command;
    private Tile[][]         map;
    private ArrayList<Taxi>  taxis;
    private int              timeCounter;
    private Runnable         receiver;

    /**
     * Initializes the window.
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //TODO change the textField of the drivers into a textArea, and check
        // that it works with the server.
        //TODO check that the received height and width are correct

        String   rawGridParameters;
        String[] gridParameters;

        this.taxis = new ArrayList<>();
        this.timeCounter = 0;
        this.lock = new ReentrantLock();

        this.inFromUser = new BufferedReader(new InputStreamReader(System.in));
        this.clientSocket = null;

        try {

            this.clientSocket = new Socket(this.ip, this.port);
            this.outToServer =
                    new DataOutputStream(clientSocket.getOutputStream());
            this.inFromServer =
                    new BufferedReader(new
                            InputStreamReader(clientSocket.getInputStream()));

            // sentence = inFromUser.readLine();
            // outToServer.writeBytes(sentence + '\n');

            rawGridParameters = this.inFromServer.readLine();
            gridParameters = rawGridParameters.split(" ");

            //Sets the map height and width.
            this.gridHeight = Integer.parseInt(gridParameters[0]);
            this.gridWidth = Integer.parseInt(gridParameters[1]);

            //Creates the map of tiles.
            buildMap();

            //Sets onAction methods to buttons.
            this.sendBtn.setOnAction(e -> sendBtnClicked());
            this.addBtn.setOnAction(e -> addBtnClicked());

            //Turn thread on.
            this.receiver = new Receiver(this);
            new Thread(this.receiver).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the socket's parameters.
     *
     * @param ip
     * @param port
     */
    public void setSocket(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    private void sendBtnClicked() {

        setError(false);
        this.command = commandTxt.getText();
        commandTxt.setText("");

        try {

            this.outToServer.writeBytes(this.command + '\n');

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The method the add button will perform.
     */
    private void addBtnClicked() {

        String driverRequest;

        driverRequest = driversTxt.getText();
        driversTxt.setText("");

        try {

            this.outToServer.writeBytes(driverRequest + '\n');

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BufferedReader getInFromServer() {
        return inFromServer;
    }

    /**
     * Sets the error message in the error label.
     *
     * @param turnOn
     */
    public void setError(boolean turnOn) {

        if (turnOn) {

            this.errorLbl.setText("Error");
        } else {

            this.errorLbl.setText("No errors");
        }
    }

    /**
     * Closes the window.
     */
    public void closeWindow() {
        Platform.exit();
    }

    /**
     * Builds the map of tiles.
     */
    private void buildMap() {

        this.map = new Tile[this.gridHeight][this.gridWidth];

        for (int i = this.gridHeight - 1; i >= 0; i--) {

            //Creates a row that will hold tiles.
            HBox row = new HBox();
            row.setSpacing(4);
            row.setAlignment(Pos.CENTER);

            for (int j = 0; j < this.gridWidth; ++j) {

                //Populates the row with tiles.
                Tile tile = new Tile();
                map[i][j] = tile;
                row.getChildren().add(tile);
            }

            //Adds the the new row to the existing rows
            this.rows.getChildren().add(row);
        }
    }

    /**
     * Removes all the taxi images from the map.
     */
    public void turnImagesOff() {

        for (Taxi taxi : this.taxis) {

            this.map[taxi.getxPosition()][taxi.getyPosition()].setImage(false);
        }

    }

    /**
     * Sets all the required taxi images on the map.
     */
    public void turnImagesOn() {

        for (Taxi taxi : this.taxis) {

            this.map[taxi.getxPosition()][taxi.getyPosition()].setImage(true);
        }
    }

    public ArrayList<Taxi> getTaxis() {
        return taxis;
    }

    /**
     * Increases the time counter and updates the time label.
     */
    public void increaseTime() {

        ++this.timeCounter;
        this.timeLbl.setText(Integer.toString(this.timeCounter));
    }

    /**
     * Returns the lock
     *
     * @return lock
     */
    public ReentrantLock getLock() {
        return this.lock;
    }
}


