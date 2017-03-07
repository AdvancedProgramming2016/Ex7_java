import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The main class of the application.
 */
public class Main extends Application {

    /**
     * The method creates the stage.
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("resources/grid.fxml"));
        primaryStage.setTitle("Taxi Center");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    /**
     * The main method.
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }
}
