package Controller.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public record Line(TextSpan[] textSpans) {
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (TextSpan span : textSpans) {
            sb.append(span.toString());
        }
        return sb.toString();
    }
    /**
     * returns a Line that is has the same contents as this, except that the first n characters are removed.
     * @param n the number of characters to remove. May not be negative. If there are <= n characters, the returned object will be empty
     * @return a new Line object without the first n characters, or this if n == 0
     */
    public Line dropFirstNCharacters(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("n should be greater than 0");
        }

        if (n == 0) {
            return this;
        }

        int totalLength = length();
        if (n > totalLength) {
            return new Line(new TextSpan[0]);
        }

        int skipped = 0;
        for (int i = 0; i < textSpans.length; i++) {
            TextSpan span = textSpans[i];
            if (skipped + span.text().length() < n) {
                skipped += span.text().length();
                continue;
            }
            if (skipped == n) {
                return new Line(Arrays.copyOfRange(textSpans, i + 1, textSpans.length));
            }

            int charsNeededFromCurrent = n - skipped;

            TextSpan split = new TextSpan(
                span.style(),
                span.text().substring(span.text().length() - charsNeededFromCurrent)
            );

            TextSpan[] remaining = Arrays.copyOfRange(textSpans, i, textSpans.length);
            remaining[i] =  split;

            return new Line(remaining);
        }

        throw new IllegalStateException("Function should have returned before this point!");
    }

    public Line[] splitIntoChunks(int maxChunkLength) {
        List<Line> lines = new ArrayList<>();
        List<TextSpan> currentLine = new ArrayList<>();

        int currentLength = 0;
        for (TextSpan span : textSpans) {
            String text = span.text();
            Style style = span.style();

            int index = 0;
            while (index < text.length()) {
                if (currentLength == maxChunkLength) {
                    lines.add(new Line(currentLine.toArray(new TextSpan[0])));
                    currentLine.clear();
                    currentLength = 0;
                }

                int remainingSpace = maxChunkLength - currentLength;
                int take = Math.min(remainingSpace, text.length() - index);

                String chunk = text.substring(index, index + take);
                currentLine.add(new TextSpan(style, chunk));

                currentLength += take;
                index += take;
            }
        }

        if (!currentLine.isEmpty()) {
            lines.add(new Line(currentLine.toArray(new TextSpan[0])));
        }

        return lines.toArray(new Line[0]);
    }

    public int length() {
        int sum = 0;
        for (TextSpan span : textSpans) {
            sum += span.text().length();
        }

        return sum;
    }
}
