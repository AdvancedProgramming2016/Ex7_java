import controllers.GridController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The main class of the application.
 */
public class Main extends Application {

    public static String[] arguments;

    /**
     * The method creates the stage.
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader fxmlLoader = new
                FXMLLoader(getClass().getResource("resources/grid.fxml"));

        GridController gridController = new GridController();
        gridController.setSocket(arguments[0], Integer.parseInt(arguments[1]));
        fxmlLoader.setController(gridController);
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("Taxi Center");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    /**
     * The main method.
     * @param args
     */
    public static void main(String[] args) throws  Exception {

       arguments = args;

        launch(args);
    }
}
