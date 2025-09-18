import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainPanel extends JPanel {
    private int numberOfMines = 20;
    private int width = 10;
    private int height = 10;
    private int unrevealedCells; //unrevealed cells with no bombs

    private Cell[][] cellField = new Cell[width][height];
    private JButton[][] buttons = new JButton[width][height];

    private Color[] colors = {Color.blue, Color.green, Color.red, Color.magenta, Color.pink, Color.cyan, Color.yellow, Color.black};

    private JPanel gridPanel;

    private JButton topButton = new JButton();
    private JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

    private boolean clickable = true;

    public MainPanel() {
        setPreferredSize(new Dimension(600, 650));
        setLayout(new BorderLayout());

        topButton.setPreferredSize(new Dimension(50, 50));
        topPanel.add(topButton);
        add(topPanel, BorderLayout.NORTH);

        gridPanel = new JPanel(new GridLayout(height, width));
        add(gridPanel, BorderLayout.CENTER);

        set();

        topButton.addActionListener(e -> reset());
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
                            if (pressed & clickable) {
                                if (cell.isFlagged()) {
                                    button.setText("");
                                    cell.setFlagged(false);
                                } else {
                                    button.setText("\uD83D\uDEA9");
                                    cell.setFlagged(true);
                                }
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
        if (cell.isRevealed() || !clickable || cell.isFlagged()) {
            return;
        }

        if (cell.hasBomb()) {
            button.setBackground(Color.red);
            button.setText("💣");
            topButton.setText("😞");
            clickable = false;
        } else if (cell.getAdjacentMines() == 0){
            clearOpenField(row, col);
        } else {
            button.setBackground(Color.white);
            button.setForeground(colors[cell.getAdjacentMines() - 1]);
            button.setText(String.valueOf(cell.getAdjacentMines()));
            unrevealedCells--;
        }

        if (unrevealedCells <= 0) {
            topButton.setText("\uD83D\uDE0E");
            JOptionPane.showMessageDialog(null, "You won!");
            clickable = false;
        }
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
        clickable = true;
        topButton.setText("\uD83D\uDE00");
        unrevealedCells = width * height - numberOfMines;
    }

    private void reset() {
        gridPanel.removeAll();
        set();
        gridPanel.revalidate();
        gridPanel.repaint();
    }
}
