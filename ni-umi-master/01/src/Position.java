public class Position {
    private int x; // == col
    private int y; // == row
    private boolean checked = false;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public int distanceTo(Position secondPos) {
        return Math.abs(this.x - secondPos.x) + Math.abs(this.y - secondPos.y);
    }
}
