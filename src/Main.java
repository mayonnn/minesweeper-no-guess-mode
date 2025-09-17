import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame window = new JFrame("Minesweeper");
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        MainPanel panel = new MainPanel();
        window.add(panel);
        window.pack();
        window.setVisible(true);
    }
}