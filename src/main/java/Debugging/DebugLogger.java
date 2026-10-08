package Debugging;

public class DebugLogger {
    private static final boolean ENABLED = false;

    private static DebugLogger instance;

    private final LoggingChannel channel;

    private DebugLogger() {
        if (ENABLED) {
            channel = new ConsoleLoggingChannel();
        }
        else {
            channel = new NullLoggingChannel();
        }
    }


    private static void create() {
        if (instance == null) {
            instance = new DebugLogger();
        }
    }

    public static void info(String category, String message) {
        create();
        instance.channel.info(category, message);
    }

    public static void info(String category, String message, Object... arguments) {
        info(category, String.format(message, arguments));
    }

    public static void warn(String category, String message) {
        create();
        instance.channel.warn(category, message);
    }

    public static void warn(String category, String message, Object... arguments) {
        warn(category, String.format(message, arguments));
    }

    public static void error(String category, String message) {
        create();
        instance.channel.error(category, message);
    }

    public static void error(String category, RuntimeException exception) throws RuntimeException {
        error(category, exception.getMessage());
        throw exception;
    }

    public static void error(String category, String message, Object... arguments) {
        error(category, String.format(message, arguments));
    }
}
