package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import ui.Tile;

import java.net.URL;
import java.util.ResourceBundle;

public class GridController implements Initializable {

    @FXML
    private Button sendBtn;
    @FXML
    private Button addBtn;
    @FXML
    private Label timeLbl;
    @FXML
    private VBox rows;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //Creates the map of tiles.
        for (int i = 0; i < 10; i++) {

            //Creates a row that will hold tiles.
            HBox row = new HBox();
            row.setSpacing(4);
            row.setAlignment(Pos.CENTER);

            for (int j = 0; j < 10; j++) {

                //Populates the row with tiles.
                Tile tile = new Tile();
                row.getChildren().add(tile);
            }

            //Adds the the new row to the existing rows
            this.rows.getChildren().add(row);
        }
    }

    public void changeText() {
        timeLbl.setText("Changed");
    }
}
