import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

/**
 * Represents a tile in the grid.
 */
public class Tile extends StackPane {

    private Rectangle border;
    private Image tileImage;

    /**
     * Ctor.
     */
    public Tile(){

        border = new Rectangle(65, 65);
        border.setFill(Color.rgb(188, 218, 219));
        border.setStroke(Color.BLACK);

        setAlignment(Pos.CENTER);
        getChildren().add(border);
    }

    /**
     * Sets the image of a taxi inside the tile, if the tile is taken.
     * @param isTaxi
     */
    public void setTaxi(boolean isTaxi){

        if(isTaxi){

            tileImage = new Image("resources/taxi_icon.png");
            border.setFill(new ImagePattern(tileImage));
        }

        else{

            border.setFill(Color.rgb(188, 218, 219));
        }
    }

    /**
     * Sets the tile image to an obstacle.
     */
    public void setObstacle(){

        tileImage = new Image("resources/obstacle_icon.png");
        border.setFill(new ImagePattern(tileImage));

    }
}
