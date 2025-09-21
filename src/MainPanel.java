import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Predicate;

public class MainPanel extends JPanel {
    private int numberOfMines = 20;
    private int width = 10;
    private int height = 10;
    private int unrevealedCells; // unrevealed non-bomb cells
    private int numberOfFlags;

    private Cell[][] cellField;
    private JButton[][] buttons;

    private final Color[] colors = {
            Color.blue, Color.green, Color.red, Color.magenta,
            Color.pink, Color.cyan, Color.yellow, Color.black
    };

    private JPanel gridPanel;
    private JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    private JButton topButton = new JButton();
    private JLabel numberOfRemainingMinesLabel = new JLabel();

    private boolean clickable = true;
    private boolean clearingAdjacentCells = false;

    public MainPanel() {
        setPreferredSize(new Dimension(600, 650));
        setLayout(new BorderLayout());
        setupUI();
        reset();
    }

    private void setupUI() {
        topButton.setPreferredSize(new Dimension(50, 50));
        topButton.addActionListener(e -> reset());

        topPanel.add(topButton);
        topPanel.add(numberOfRemainingMinesLabel);
        add(topPanel, BorderLayout.NORTH);

        gridPanel = new JPanel(new GridLayout(height, width));
        add(gridPanel, BorderLayout.CENTER);
    }

    private void reset() {
        gridPanel.removeAll();

        cellField = new Cell[height][width];
        buttons = new JButton[height][width];

        initiateCells();
        createButtons();

        gridPanel.revalidate();
        gridPanel.repaint();

        topButton.setText("😀");
        numberOfFlags = 0;
        unrevealedCells = width * height - numberOfMines;
        numberOfRemainingMinesLabel.setText(String.valueOf(numberOfMines));
        clickable = true;

        printArray();
    }

    private void initiateCells() {
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                cellField[row][col] = new Cell();
            }
        }

        int placed = 0;
        while (placed < numberOfMines) {
            int row = (int) (Math.random() * height);
            int col = (int) (Math.random() * width);

            if (!cellField[row][col].hasBomb()) {
                cellField[row][col].setBomb(true);
                placed++;
            }
        }

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                cellField[row][col].setAdjacentMines(countAdjacentMines(row, col));
            }
        }
    }

    private void createButtons() {
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                JButton button = new JButton();
                buttons[row][col] = button;
                Cell cell = cellField[row][col];

                int finalCol = col;
                int finalRow = row;

                button.addActionListener(e -> revealCell(finalRow, finalCol));
                button.addMouseListener(new MouseAdapter() {
                    boolean pressed = false;

                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (e.getButton() == MouseEvent.BUTTON3) {
                            pressed = true;
                        }
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        if (e.getButton() == MouseEvent.BUTTON3) {
                            if (pressed && clickable && !cell.isRevealed()) {
                                if (cell.isFlagged()) {
                                    button.setText("");
                                    cell.setFlagged(false);
                                    numberOfFlags--;
                                } else {
                                    button.setText("\uD83D\uDEA9");
                                    cell.setFlagged(true);
                                    numberOfFlags++;
                                }
                                numberOfRemainingMinesLabel.setText(String.valueOf(numberOfMines - numberOfFlags));

                            }
                        }
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        pressed = false;
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        pressed = true;
                    }
                });

                gridPanel.add(button);
            }
        }
    }

    private void revealCell(int row, int col) {
        JButton button = buttons[row][col];
        Cell cell = cellField[row][col];
        if (!clickable || cell.isFlagged()) {
            return;
        }

        if (cell.isRevealed()) {
            tryChord(row, col);
        } else if (cell.hasBomb()) {
            showBomb(button);
        } else if (cell.getAdjacentMines() == 0){
            clearOpenField(row, col);
        } else {
            showNumber(button, cell.getAdjacentMines());
        }

        checkWin();

        cell.setRevealed(true);
    }

    private void clearOpenField(int row, int col) {
        if (cellField[row][col].isRevealed()) return;
        cellField[row][col].setRevealed(true);
        unrevealedCells--;

        JButton button = buttons[row][col];
        button.setBackground(Color.white);

        if (cellField[row][col].getAdjacentMines() > 0) {
            button.setForeground(colors[cellField[row][col].getAdjacentMines() - 1]);
            buttons[row][col].setText(String.valueOf(cellField[row][col].getAdjacentMines()));
            return;
        }

        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if (i >= 0 && i < height && j >= 0 && j < width) {
                    clearOpenField(i, j);
                }
            }
        }
    }

    private void tryChord(int row, int col) {
        if (clearingAdjacentCells) return;
        if (countAdjacentMines(row, col) == countAdjacentFlags(row, col)) {
            clearingAdjacentCells = true;
            for (int i = row - 1; i <= row + 1; i++) {
                for (int j = col - 1; j <= col + 1; j++) {
                    if (i == row && j == col) continue;
                    if (i >= 0 && i < height && j >= 0 && j < width) {
                        revealCell(i, j);
                    }
                }
            }
            clearingAdjacentCells = false;
        }
    }

    private void showBomb(JButton button) {
        button.setBackground(Color.red);
        button.setText("💣");
        topButton.setText("😞");
        clickable = false;
    }

    private void showNumber(JButton button, int number) {
        button.setBackground(Color.white);
        button.setForeground(colors[number - 1]);
        button.setText(String.valueOf(number));
        unrevealedCells--;
    }

    private void checkWin() {
        if (unrevealedCells <= 0 && !clearingAdjacentCells) {
            topButton.setText("😎");
            JOptionPane.showMessageDialog(null, "You won!");
            clickable = false;
        }
    }

    private void printArray(){
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                System.out.print(cellField[row][col].hasBomb() + "/" + cellField[row][col].getAdjacentMines() + "    ");
            }
            System.out.println();
        }
    }

    private int countAdjacent(int row, int col, Predicate<Cell> condition) {
        int count = 0;
        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if (i == row && j == col) continue;

                if (i >= 0 && i < height && j >= 0 && j < width) {
                    if (condition.test(cellField[i][j])) count++;
                }
            }
        }
        return count;
    }

    private int countAdjacentMines(int row, int col) {
        return countAdjacent(row, col, Cell::hasBomb);
    }

    private int countAdjacentFlags(int row, int col) {
        return countAdjacent(row, col, Cell::isFlagged);
    }

}
