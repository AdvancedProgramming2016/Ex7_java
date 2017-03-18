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

import java.io.*;
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

    private ReentrantLock      lock;
    private String             ip;
    private String             clientPath;
    private int                port;
    private int                gridHeight;
    private int                gridWidth;
    private Socket             clientSocket;
    private BufferedReader     inFromUser;
    private BufferedReader     inFromServer;
    private OutputStreamWriter outToServer;
    private String             command;
    private String             obstacles;
    private Tile[][]           map;
    private ArrayList<Taxi>    taxis;
    private int                timeCounter;
    private Runnable           receiver;

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
        //TODO make a valid. check that a number of drivers is selected before pressing the add button on the gui

        String   rawGridParameters;
        String[] gridParameters;

        this.taxis = new ArrayList<>();
        this.timeCounter = 0;
        this.lock = new ReentrantLock();

        //Sets the client file path
        this.clientPath = "./client.out";

        this.inFromUser = new BufferedReader(new InputStreamReader(System.in));
        this.clientSocket = null;

        try {

            this.clientSocket = new Socket(this.ip, this.port);
            this.outToServer =
                    new OutputStreamWriter(clientSocket.getOutputStream());
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
            this.buildMap();

            //Sets the obstacles on the map.
            this.obstacles = this.inFromServer.readLine();
            this.setObstacles();

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

    /**
     * The method the send button will perform.
     */
    private void sendBtnClicked() {

        setError(false);
        this.command = commandTxt.getText();
        commandTxt.setText("");

        try {

            this.lock.lock();
            System.out.println(command);
            this.outToServer.write(this.command + '\0');
            this.outToServer.flush();
            this.lock.unlock();

        } catch (IOException e) {
            e.printStackTrace();
        }

        if(command.equals("9")){

            increaseTime();
        }
    }

    /**
     * The method the add button will perform.
     */
    private void addBtnClicked() {

        String driverRequest;

        driverRequest = driversTxt.getText();
        driversTxt.setText("");

        //this.lock.lock();
        //System.out.println(driverRequest);

        //Opens a new client
        openClient(driverRequest);

        //TODO send to driver client instead
        // this.outToServer.write(driverRequest + '\0');
        //this.outToServer.flush();
        //this.lock.unlock();

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

            this.map[taxi.getxPosition()][taxi.getyPosition()].setTaxi(false);
        }

    }

    /**
     * Sets all the required taxi images on the map.
     */
    public void turnImagesOn() {

        for (Taxi taxi : this.taxis) {

            this.map[taxi.getxPosition()][taxi.getyPosition()].setTaxi(true);
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

    public void setObstacles() {

        String[] positions = this.obstacles.split(" ");
        String[] indices;

        if(positions[0].equals("")){

            return;
        }

        //Updates the taxis positions according to the server input.
        for (int i = 0; i < positions.length; i++) {

            indices = positions[i].split(",");
            this.map[Integer.parseInt(indices[0])][Integer.parseInt(indices[1])]
                    .setObstacle();
        }
    }

    /**
     * Returns the Ip string.
     * @return
     */
    public String getIp() {
        return ip;
    }

    /**
     * Returns the port number.
     * @return
     */
    public int getPort() {
        return port;
    }

    /**
     * Returns the client file path.
     * @return String
     */
    public String getClientPath() {
        return clientPath;
    }

    /**
     * Opens a client process.
     */
    public void openClient(String driverParams){

        try {

//            String runtimeArgs[] = new String[8];
//
//            //Create the runtime arguments
//            runtimeArgs[0] = "./client.out";
//            runtimeArgs[1] = this.getIp().replace("/", "");
//            runtimeArgs[2] = Integer.toString(getPort());
//            for (int i = 1; i < 6; i++) {
//                runtimeArgs[2 + i] = driverParams.split(",")[i - 1];
//            }
//
//            //Run a client with the runtime arguments
//            Runtime.getRuntime().exec(runtimeArgs);

            String[] params = new String[] {this.getClientPath(), this.getIp(), Integer.toString(this.getPort()), driverParams};

            //Runtime.getRuntime().exec(params);

            Process p = Runtime.getRuntime().exec(params);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


