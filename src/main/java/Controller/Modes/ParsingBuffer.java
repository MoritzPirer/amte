package Controller.Modes;

class ParsingBuffer {
    private final StringBuilder builder = new StringBuilder();
    private final int maxBufferSize;

    public ParsingBuffer(int maxBufferSize) {
        this.maxBufferSize = maxBufferSize;
    }

    public void add(char c) {
        if (builder.length() == maxBufferSize) {
            clear();
        }
        builder.append(c);
    }

    public String get() {
        return builder.toString();
    }

    public void clear() {
        builder.delete(0, builder.length());
    }

    public int length() {
        return builder.length();
    }
}
