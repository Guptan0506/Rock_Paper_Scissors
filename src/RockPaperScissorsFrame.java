import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class RockPaperScissorsFrame extends JFrame {

    // GUI components
    private JButton rockButton;
    private JButton paperButton;
    private JButton scissorsButton;
    private JButton quitButton;

    private JTextField playerWinsField;
    private JTextField computerWinsField;
    private JTextField tiesField;

    private JTextArea resultsArea;

    // Game state
    private int playerWins = 0;
    private int computerWins = 0;
    private int ties = 0;

    private int playerRockCount = 0;
    private int playerPaperCount = 0;
    private int playerScissorsCount = 0;

    private String lastPlayerMove = null;

    private final Random random = new Random();

    // Strategies
    private final Strategy cheatStrategy = new Cheat();
    private final Strategy randomStrategy = new RandomStrategy();
    private final Strategy leastUsedStrategy = new LeastUsedStrategy();
    private final Strategy mostUsedStrategy = new MostUsedStrategy();
    private final Strategy lastUsedStrategy = new LastUsedStrategy();

    public RockPaperScissorsFrame() {
        super("Rock Paper Scissors Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);

        initComponents();
        layoutComponents();
    }

    private void initComponents() {
        rockButton = new JButton("Rock", new ImageIcon("src/rock.png"));
        paperButton = new JButton("Paper", new ImageIcon("src/paper.png"));
        scissorsButton = new JButton("Scissors", new ImageIcon("src/scissors.png"));
        quitButton = new JButton("Quit");

        // Stats fields
        playerWinsField = new JTextField("0", 5);
        playerWinsField.setEditable(false);

        computerWinsField = new JTextField("0", 5);
        computerWinsField.setEditable(false);

        tiesField = new JTextField("0", 5);
        tiesField.setEditable(false);

        // Results area
        resultsArea = new JTextArea(15, 40);
        resultsArea.setEditable(false);

        // Single listener for R/P/S
        ActionListener moveListener = new MoveButtonListener();
        rockButton.addActionListener(moveListener);
        paperButton.addActionListener(moveListener);
        scissorsButton.addActionListener(moveListener);

        quitButton.addActionListener(e -> System.exit(0));
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());

        // Top panel: buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(new TitledBorder("Choose your move"));
        buttonPanel.add(rockButton);
        buttonPanel.add(paperButton);
        buttonPanel.add(scissorsButton);
        buttonPanel.add(quitButton);

        // Stats panel
        JPanel statsPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        statsPanel.setBorder(new TitledBorder("Stats"));

        statsPanel.add(new JLabel("Player Wins:"));
        statsPanel.add(playerWinsField);

        statsPanel.add(new JLabel("Computer Wins:"));
        statsPanel.add(computerWinsField);

        statsPanel.add(new JLabel("Ties:"));
        statsPanel.add(tiesField);

        // Results panel
        JScrollPane scrollPane = new JScrollPane(resultsArea);
        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBorder(new TitledBorder("Game Results"));
        resultsPanel.add(scrollPane, BorderLayout.CENTER);

        add(buttonPanel, BorderLayout.NORTH);
        add(statsPanel, BorderLayout.WEST);
        add(resultsPanel, BorderLayout.CENTER);
    }

    private class MoveButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String playerMove;
            if (e.getSource() == rockButton) {
                playerMove = "R";
                playerRockCount++;
            } else if (e.getSource() == paperButton) {
                playerMove = "P";
                playerPaperCount++;
            } else {
                playerMove = "S";
                playerScissorsCount++;
            }

            // Choose strategy based on probability
            int p = random.nextInt(100) + 1;
            Strategy chosenStrategy;
            String strategyName;

            if (p >= 1 && p <= 10) {
                chosenStrategy = cheatStrategy;
                strategyName = "Cheat";
            } else if (p <= 30) {
                chosenStrategy = leastUsedStrategy;
                strategyName = "Least Used";
            } else if (p <= 50) {
                chosenStrategy = mostUsedStrategy;
                strategyName = "Most Used";
            } else if (p <= 70) {
                chosenStrategy = lastUsedStrategy;
                strategyName = "Last Used";
            } else {
                chosenStrategy = randomStrategy;
                strategyName = "Random";
            }

            String computerMove = chosenStrategy.getMove(playerMove);
            String resultLine = determineResultLine(playerMove, computerMove, strategyName);

            // Update last player move
            lastPlayerMove = playerMove;

            // Append to text area
            resultsArea.append(resultLine + "\n");

            // Update stats fields
            playerWinsField.setText(String.valueOf(playerWins));
            computerWinsField.setText(String.valueOf(computerWins));
            tiesField.setText(String.valueOf(ties));
        }
    }

    // Inner strategy: Least Used
    private class LeastUsedStrategy implements Strategy {
        @Override
        public String getMove(String playerMove) {
            // Find least used player symbol
            int r = playerRockCount;
            int p = playerPaperCount;
            int s = playerScissorsCount;

            // Default to Rock if all zero
            String leastUsed = "R";
            int min = r;

            if (p < min) {
                min = p;
                leastUsed = "P";
            }
            if (s < min) {
                min = s;
                leastUsed = "S";
            }

            // Computer plays the move that beats the least used
            switch (leastUsed) {
                case "R":
                    return "P"; // Paper beats Rock
                case "P":
                    return "S"; // Scissors beats Paper
                case "S":
                    return "R"; // Rock beats Scissors
                default:
                    return "R";
            }
        }
    }

    // Inner strategy: Most Used
    private class MostUsedStrategy implements Strategy {
        @Override
        public String getMove(String playerMove) {
            int r = playerRockCount;
            int p = playerPaperCount;
            int s = playerScissorsCount;

            String mostUsed = "R";
            int max = r;

            if (p > max) {
                max = p;
                mostUsed = "P";
            }
            if (s > max) {
                max = s;
                mostUsed = "S";
            }

            // Computer plays the move that beats the most used
            switch (mostUsed) {
                case "R":
                    return "P";
                case "P":
                    return "S";
                case "S":
                    return "R";
                default:
                    return "R";
            }
        }
    }

    // Inner strategy: Last Used
    private class LastUsedStrategy implements Strategy {
        @Override
        public String getMove(String playerMove) {
            // If no last move yet, fall back to random
            if (lastPlayerMove == null) {
                return randomStrategy.getMove(playerMove);
            }

            // Assume player will repeat last move; computer plays the move that beats it
            switch (lastPlayerMove) {
                case "R":
                    return "P";
                case "P":
                    return "S";
                case "S":
                    return "R";
                default:
                    return randomStrategy.getMove(playerMove);
            }
        }
    }

    private String determineResultLine(String playerMove, String computerMove, String strategyName) {
        String playerWord = moveToWord(playerMove);
        String computerWord = moveToWord(computerMove);

        if (playerMove.equals(computerMove)) {
            ties++;
            return playerWord + " vs " + computerWord + ". (Tie! Computer: " + strategyName + ")";
        }

        // Determine winner and rule text
        String ruleText;
        boolean playerWinsRound;

        if (playerMove.equals("R") && computerMove.equals("S")) {
            ruleText = "Rock breaks scissors";
            playerWinsRound = true;
        } else if (playerMove.equals("S") && computerMove.equals("R")) {
            ruleText = "Rock breaks scissors";
            playerWinsRound = false;
        } else if (playerMove.equals("P") && computerMove.equals("R")) {
            ruleText = "Paper covers rock";
            playerWinsRound = true;
        } else if (playerMove.equals("R") && computerMove.equals("P")) {
            ruleText = "Paper covers rock";
            playerWinsRound = false;
        } else if (playerMove.equals("S") && computerMove.equals("P")) {
            ruleText = "Scissors cuts paper";
            playerWinsRound = true;
        } else { // player P, computer S
            ruleText = "Scissors cuts paper";
            playerWinsRound = false;
        }

        if (playerWinsRound) {
            playerWins++;
            return ruleText + ". (Player wins! Computer: " + strategyName + ")";
        } else {
            computerWins++;
            return ruleText + ". (Computer wins! Computer: " + strategyName + ")";
        }
    }

    private String moveToWord(String move) {
        switch (move) {
            case "R":
                return "Rock";
            case "P":
                return "Paper";
            case "S":
                return "Scissors";
            default:
                return "Unknown";
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RockPaperScissorsFrame frame = new RockPaperScissorsFrame();
            frame.setVisible(true);
        });
    }
}