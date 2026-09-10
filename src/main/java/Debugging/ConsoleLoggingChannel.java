package Debugging;

class ConsoleLoggingChannel implements LoggingChannel {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";

    @Override
    public void info(String category, String message) {
        System.out.println("[INFO] " + category + ": " + message);
    }

    @Override
    public void info(String category, String message, Object... arguments) {
        info(category, String.format(message, arguments));
    }

    @Override
    public void warn(String category, String message) {
        System.out.println(ANSI_YELLOW + "[WARN] " + category + ": " + message + ANSI_RESET);
    }

    @Override
    public void warn(String category, String message, Object... arguments) {
        warn(category, String.format(message, arguments));
    }

    @Override
    public void error(String category, String message) {
        System.out.println(ANSI_RED + "[ERROR] " + category + ": " + message + ANSI_RESET);
    }

    @Override
    public void error(String category, String message, Object... arguments) {
        error(category, String.format(message, arguments));
    }
}
