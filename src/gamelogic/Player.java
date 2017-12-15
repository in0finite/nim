package gamelogic;

public class Player {

    String name = "";
    boolean isAI = false;



    public Player(String name, boolean isAI) {
        this.name = name;
        this.isAI = isAI;
    }

    public String getName() {
        return name;
    }


    /// Gets name of the currently logged on user.
    public static String getCurrentUserName() {
        return System.getProperty("user.name");
    }


    /// Gets the next move for the current status of the game.
//    public  abstract    Move    getNextMove(Nim nimGame);


}
