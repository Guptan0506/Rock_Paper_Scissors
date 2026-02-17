public class Cheat implements Strategy {

    @Override
    public String getMove(String playerMove) {
        String computerMove;
        switch (playerMove) {
            case "R":
                computerMove = "P"; // Paper beats Rock
                break;
            case "P":
                computerMove = "S"; // Scissors beats Paper
                break;
            case "S":
                computerMove = "R"; // Rock beats Scissors
                break;
            default:
                computerMove = "R";
                break;
        }
        return computerMove;
    }
}