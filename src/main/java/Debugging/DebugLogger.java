package Debugging;

import java.util.Set;

public class DebugLogger {
    private static final boolean ENABLED = true;

    private static DebugLogger instance;

    private final LoggingChannel channel;
    private final Set<String> ignoredCategories;

    private DebugLogger() {
        if (ENABLED) {
            channel = new ConsoleLoggingChannel();
        }
        else {
            channel = new NullLoggingChannel();
        }

        ignoredCategories = Set.of("TESTING");
    }


    private static void create() {
        if (instance == null) {
            instance = new DebugLogger();
        }
    }

    public static void info(String category, String message) {
        create();
        if (!instance.ignoredCategories.contains(category)) {
            instance.channel.info(category, message);
        }
    }

    public static void info(String category, String message, Object... arguments) {
        info(category, String.format(message, arguments));
    }

    public static void warn(String category, String message) {
        create();
        if (!instance.ignoredCategories.contains(category)) {
            instance.channel.warn(category, message);
        }
    }

    public static void warn(String category, String message, Object... arguments) {
        warn(category, String.format(message, arguments));
    }

    public static void error(String category, String message) {
        create();
        if (!instance.ignoredCategories.contains(category)) {
            instance.channel.error(category, message);
        }
    }

    public static void error(String category, RuntimeException exception) throws RuntimeException {
        error(category, exception.getMessage());
        throw exception;
    }

    public static void error(String category, String message, Object... arguments) {
        error(category, String.format(message, arguments));
    }
}
