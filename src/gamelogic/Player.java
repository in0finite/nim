package gamelogic;

public class Player {

    String name = "";
    boolean isAI = false;
    MoveStrategy moveStrategy = null;



    public Player(String name, boolean isAI) {
        this.name = name;
        this.isAI = isAI;
    }

    public String getName() {
        return name;
    }

    public boolean isAI() { return isAI; }

    public MoveStrategy getMoveStrategy() { return moveStrategy; }

    public void setMoveStrategy(MoveStrategy moveStrategy) { this.moveStrategy = moveStrategy; }


    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                '}';
    }


    /// Gets name of the currently logged on user.
    public static String getCurrentUserName() {
        return System.getProperty("user.name");
    }


}
