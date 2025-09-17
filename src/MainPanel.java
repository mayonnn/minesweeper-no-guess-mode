import javax.swing.*;
import java.awt.*;

public class MainPanel extends JPanel {
    private int numberOfMines = 20;
    private int width = 10;
    private int height = 10;

    private Cell[][] cellField = new Cell[width][height];
    private JButton[][] buttons = new JButton[width][height];

    private Color[] colors = {Color.blue, Color.green, Color.red, Color.magenta, Color.pink, Color.cyan, Color.yellow, Color.black};

    //TODO first click guarantee of not being a bomb
    //private boolean firstClick = true;

    public MainPanel() {
        setPreferredSize(new Dimension(600, 600));
        setLayout(new GridLayout(height, width));
        set();
    }

    private void initiateCells() {
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                cellField[row][col] = new Cell();
            }
        }

        int i = 0;
        while(i < numberOfMines) {
            int row = (int) (Math.random() * height);
            int col = (int) (Math.random() * width);

            if (!cellField[row][col].hasBomb()) {
                cellField[row][col].setBomb(true);
                i++;
            }
        }

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                cellField[row][col].setAdjacentMines(countAdjacentMines(row, col));
            }
        }
    }

    private int countAdjacentMines(int row, int col) {
        int count = 0;

        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if (i == row && j == col) continue;

                if (i >= 0 && i < height && j >= 0 && j < width) {
                    if (cellField[i][j].hasBomb()) count++;
                }
            }
        }
        return count;
    }

    private void createButtons() {
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                JButton button = new JButton();
                buttons[row][col] = button;

                int finalCol = col;
                int finalRow = row;

                button.addActionListener(e -> revealCell(finalRow, finalCol));

                add(button);
            }
        }
    }

    private void revealCell(int row, int col) {
        JButton button = buttons[row][col];
        Cell cell = cellField[row][col];
        if (cell.isRevealed()) {
            return;
        }

        if (cell.hasBomb()) {
            button.setBackground(Color.red);
            button.setText("💣");
            
        } else if (cell.getAdjacentMines() == 0){
            clearOpenField(row, col);
        } else {
            button.setBackground(Color.white);
            button.setForeground(colors[cell.getAdjacentMines() - 1]);
            button.setText(String.valueOf(cell.getAdjacentMines()));
        }

        cell.setRevealed(true);

    }

    private void clearOpenField(int row, int col) {
        if (cellField[row][col].isRevealed()) return;
        cellField[row][col].setRevealed(true);

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

    private void printArray(){
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                System.out.print(cellField[row][col].hasBomb() + "/" + cellField[row][col].getAdjacentMines() + "    ");
            }
            System.out.println();
        }
    }

    private void set() {
        initiateCells();
        createButtons();
        printArray();
    }

    private void reset() {
        clearButtons();
        set();
    }

    private void clearButtons() {
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                JButton button = buttons[row][col];
                remove(button);
            }
        }
    }
}
