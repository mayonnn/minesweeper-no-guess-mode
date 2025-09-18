public class Cell {
    private boolean hasBomb;
    private boolean isFlagged;
    private boolean isRevealed;
    private int adjacentMines;

    public Cell() {
        isFlagged = false;
        isRevealed = false;
    }

    public boolean hasBomb() {
        return hasBomb;
    }

    public void setBomb(boolean hasBomb) {
        this.hasBomb = hasBomb;
    }

    public int getAdjacentMines() {
        return adjacentMines;
    }

    public void setAdjacentMines(int adjacentMines) {
        this.adjacentMines = adjacentMines;
    }

    public boolean isRevealed() {
        return isRevealed;
    }

    public void setRevealed(boolean isRevealed) {
        this.isRevealed = isRevealed;
    }

    public void setFlagged(boolean flagged) {
        isFlagged = flagged;
    }

    public boolean isFlagged() {
        return isFlagged;
    }
}
