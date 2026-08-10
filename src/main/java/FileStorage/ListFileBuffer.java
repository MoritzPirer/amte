package FileStorage;

import Shared.Position;

import java.util.ArrayList;
import java.util.List;

public class ListFileBuffer implements FileBuffer {
    private final List<String> paragraphs;

    public ListFileBuffer() {
        paragraphs = new ArrayList<>();
        paragraphs.add("");
    }

    private void checkPosition(Position position) throws InvalidPosition {
        if (position.row() < 0 || position.row() > paragraphs.size()) {
            throw new InvalidPosition("row " + position.row() + " is out of range");
        }
        if (position.column() < 0 || position.column() > paragraphs.get(position.row()).length()) {
            throw new InvalidPosition("column " + position.column() + " is out of range");
        }
    }

    @Override
    public void insert(String content, Position position) throws InvalidPosition {
        checkPosition(position);

        String currentLine = paragraphs.get(position.row());
        String beforeInsert = currentLine.substring(0, position.column());
        String afterInsert = currentLine.substring(position.column());

        String[] newParagraphs = content.split("\n");
        if (newParagraphs.length == 1) {
            //inline insert
            paragraphs.set(position.row(), beforeInsert + newParagraphs[0] + afterInsert);
        }
        else {
            newParagraphs[0] = beforeInsert + newParagraphs[0];
            newParagraphs[newParagraphs.length - 1]  = newParagraphs[newParagraphs.length - 1] + afterInsert;

            paragraphs.remove(position.row());
            int offset = 0;
            for (String paragraph : newParagraphs) {
                paragraphs.add(position.row() + offset, paragraph);
                offset++;
            }
        }
    }

    @Override
    public void delete(Position start, Position end) throws InvalidPosition {
        checkPosition(start);
        if (!end.isAfter(start)) {
            throw new InvalidPosition("start is after end");
        }
        
        String beforeDelete = paragraphs.get(start.row()).substring(0, start.column());

        Position deletionEnd = getDeletionEnd(end);

        int rowsToDelete = deletionEnd.row() - start.row() + 1;
        String afterDelete = paragraphs.get(deletionEnd.row()).substring(deletionEnd.column());
        System.out.println("!!!" + afterDelete);
        
        for (int i = 0; i < rowsToDelete; i++) {
            paragraphs.remove(start.row());
        }

        if (!beforeDelete.isEmpty() || !afterDelete.isEmpty()) {
            paragraphs.add(start.row(), beforeDelete + afterDelete);
        }
    }

    private Position getDeletionEnd(Position end) {
        Position deletionEnd;
        if (end.row() >= paragraphs.size()) {
            // snap end to last char
            deletionEnd = new Position(
                    paragraphs.size() - 1,
                    paragraphs.getLast().length() - 1
            );
        }
        else if (end.column() >= paragraphs.get(end.row()).length()) {
            // snap end to last char of row
            deletionEnd = new Position(end.row(), paragraphs.get(end.row()).length());
        }
        else {
            deletionEnd = end;
        }
        return deletionEnd;
    }

    @Override
    public List<String> readRange(Position start, Position end) throws InvalidPosition {
        checkPosition(start);
        if (!end.isAfter(start)) {
            throw new InvalidPosition("start is after end");
        }

        List<String> result = new ArrayList<>();

        for (int row = start.row(); row <= end.row(); row++) {
            if (row >= paragraphs.size()) {
                break;
            }

            int startIndex = row == start.row()? start.column(): 0;
            int endIndex = row == end.row()? end.column(): paragraphs.get(row).length();

            result.add(paragraphs.get(row).substring(startIndex, endIndex));
        }

        return result;
    }

    @Override
    public String readLine(int row) throws InvalidPosition {
        if (row < 0 || row >= paragraphs.size()) {
            throw new InvalidPosition("row " + row + " is out of range");
        }
        
        return paragraphs.get(row);
    }

}
