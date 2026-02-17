public interface Strategy {
    /**
     * Returns the computer's move: "R", "P", or "S".
     *
     * @param playerMove the player's move ("R", "P", or "S")
     * @return the computer's move
     */
    String getMove(String playerMove);
}