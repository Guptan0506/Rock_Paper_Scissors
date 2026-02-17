import javax.swing.*;
import java.awt.*;

public class RockPaperScissorsFrame extends JFrame {

    // ============================
    // Game State
    // ============================
    private int wins = 0;
    private int losses = 0;
    private int ties = 0;

    // GUI components
    private JTextArea textArea;
    private JTextField winsField;
    private JTextField lossesField;
    private JTextField tiesField;

    // Strategies
    private RandomStrategy randomStrategy = new RandomStrategy();
    private CheatingStrategy cheatingStrategy = new CheatingStrategy();
    private LastUsedStrategy lastUsedStrategy = new LastUsedStrategy();

    // ============================
    // Helper class to return move + strategy
    // ============================
    private static class StrategyResult {
        String move;
        String strategy;

        StrategyResult(String move, String strategy) {
            this.move = move;
            this.strategy = strategy;
        }
    }

    // ============================
    // Constructor (Build Window)
    // ============================
    public RockPaperScissorsFrame() {
        setTitle("Rock Paper Scissors");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(createButtonPanel(), BorderLayout.NORTH);
        add(createScrollPanel(), BorderLayout.CENTER);
        add(createStatsPanel(), BorderLayout.SOUTH);

        setVisible(true);
    }

    // ============================
    // Button Panel
    // ============================
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();

        JButton rock = new JButton("Rock", new ImageIcon("src/rock.png"));
        JButton paper = new JButton("Paper", new ImageIcon("src/paper.png"));
        JButton scissors = new JButton("Scissors", new ImageIcon("src/scissors.png"));
        JButton quit = new JButton("Quit");

        rock.addActionListener(e -> handleMove("Rock"));
        paper.addActionListener(e -> handleMove("Paper"));
        scissors.addActionListener(e -> handleMove("Scissors"));
        quit.addActionListener(e -> System.exit(0));

        panel.add(rock);
        panel.add(paper);
        panel.add(scissors);
        panel.add(quit);

        return panel;
    }

    // ============================
    // Stats Panel
    // ============================
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel();

        winsField = new JTextField("0", 5);
        lossesField = new JTextField("0", 5);
        tiesField = new JTextField("0", 5);

        winsField.setEditable(false);
        lossesField.setEditable(false);
        tiesField.setEditable(false);

        panel.add(new JLabel("Wins:"));
        panel.add(winsField);
        panel.add(new JLabel("Losses:"));
        panel.add(lossesField);
        panel.add(new JLabel("Ties:"));
        panel.add(tiesField);

        return panel;
    }

    // ============================
    // Scrollable Text Area
    // ============================
    private JScrollPane createScrollPanel() {
        textArea = new JTextArea(10, 30);
        textArea.setEditable(false);
        return new JScrollPane(textArea);
    }

    // ============================
    // Handle Player Move
    // ============================
    private void handleMove(String playerMove) {

        StrategyResult resultObj = chooseStrategy(playerMove);

        String computerMove = resultObj.move;
        String strategyUsed = resultObj.strategy;

        String result = determineWinner(playerMove, computerMove);

        lastUsedStrategy.update(playerMove);
        updateStats(result);

        textArea.append(
                playerMove + " vs " + computerMove +
                        " → " + result +
                        "   [" + strategyUsed + "]\n"
        );
    }

    // ============================
    // Strategy Selection (returns move + strategy name)
    // ============================
    private StrategyResult chooseStrategy(String playerMove) {
        double r = Math.random();

        if (r < 0.1) {
            return new StrategyResult(
                    cheatingStrategy.getMove(playerMove),
                    "Cheating Strategy (10%)"
            );
        }
        if (r < 0.9) {
            return new StrategyResult(
                    randomStrategy.getMove(),
                    "Random Strategy (80%)"
            );
        }
        return new StrategyResult(
                lastUsedStrategy.getMove(),
                "Last Used Strategy (10%)"
        );
    }

    // ============================
    // Determine Winner
    // ============================
    private String determineWinner(String p, String c) {
        if (p.equals(c)) return "Tie";

        if (p.equals("Rock") && c.equals("Scissors")) return "Player";
        if (p.equals("Paper") && c.equals("Rock")) return "Player";
        if (p.equals("Scissors") && c.equals("Paper")) return "Player";

        return "Computer";
    }

    // ============================
    // Update Stats
    // ============================
    private void updateStats(String result) {
        if (result.equals("Player")) wins++;
        else if (result.equals("Computer")) losses++;
        else ties++;

        winsField.setText(String.valueOf(wins));
        lossesField.setText(String.valueOf(losses));
        tiesField.setText(String.valueOf(ties));
    }

    // ============================
    // Strategy Classes
    // ============================
    class RandomStrategy {
        public String getMove() {
            String[] moves = {"Rock", "Paper", "Scissors"};
            int index = (int) (Math.random() * moves.length);
            return moves[index];
        }
    }

    class CheatingStrategy {
        public String getMove(String playerMove) {
            if (playerMove.equals("Rock")) return "Paper";
            if (playerMove.equals("Paper")) return "Scissors";
            return "Rock";
        }
    }

    class LastUsedStrategy {
        private String lastMove = "Rock";

        public String getMove() {
            if (lastMove.equals("Rock")) return "Paper";
            if (lastMove.equals("Paper")) return "Scissors";
            return "Rock";
        }

        public void update(String move) {
            lastMove = move;
        }
    }

    // ============================
    // Main Method
    // ============================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(RockPaperScissorsFrame::new);
    }
}