package threads;

import controllers.GridController;
import javafx.application.Platform;
import ui.Taxi;
import java.io.IOException;

/**
 * Contains a thread which will communicate with the server.
 */
public class Receiver implements Runnable {

    private GridController gridController;
    private String         serverResponse;
    private boolean        exitCalled;

    public Receiver(GridController gridController){

        this.gridController = gridController;
        this.exitCalled = false;
    }
    @Override
    public void run() {

        while(!this.exitCalled){

                try {


                    this.serverResponse =
                            gridController.getInFromServer().readLine();

                    Platform.runLater(() -> {
                        //Close the system by exiting thread
                        if (this.serverResponse.equals("close")) {

                            exitCalled = true;
                        }

                        //Notify about user input error.
                        else if (this.serverResponse.equals("error")) {

                            //Lock.
                            this.gridController.getLock().lock();

                            gridController.setError(true);

                            //Unlock.
                            this.gridController.getLock().unlock();
                        }

                        //Create new taxis.
                        else if (!this.serverResponse.contains(",")) {

                            //Lock.
                            this.gridController.getLock().lock();

                            createTaxis();

                            //Unlock.
                            this.gridController.getLock().unlock();
                        }

                        //Update taxis locations.
                        else {

                            //Lock.
                            this.gridController.getLock().lock();

                            updateLocations();

                            //Unlock.
                            this.gridController.getLock().unlock();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        //Close the window.
        gridController.closeWindow();
    }

    private void createTaxis(){

        String[] stringIds;
        int      taxiId;

        stringIds = this.serverResponse.split(" ");

        //Create a new taxi for every received id from the server.
        for (String string : stringIds) {

            taxiId = Integer.parseInt(string);
            this.gridController.getTaxis().add(new Taxi(taxiId));
        }
    }

    private void updateLocations(){

        this.gridController.turnImagesOff();

        String[] positions = this.serverResponse.split(" ");
        String[] indices;

        //Updates the taxis positions according to the server input.
        for (int i = 0; i < positions.length; i++) {

            indices = positions[i].split(",");
            this.gridController.getTaxis().get(i).setPosition(
                    Integer.parseInt(indices[0]),
                    Integer.parseInt(indices[1]));
        }

        //Sets all the right images.
        this.gridController.turnImagesOn();

        //Increase the time.
        this.gridController.increaseTime();
    }
}
