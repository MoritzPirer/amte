package Shared;

public record Position(int row, int column) {

    public boolean isBefore(Position position) {
        if (row < position.row()) {
            return true;
        }
        if (row > position.row()) {
            return false;
        }
        return column < position.column();
    }

    public boolean isAfter(Position position) {
        if (row > position.row()) {
            return true;
        }
        if (row < position.row()) {
            return false;
        }
        return column > position.column();
    }

    public boolean equals(Position position) {
        return row == position.row() && column == position.column();
    }
}
