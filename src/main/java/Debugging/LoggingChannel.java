package Debugging;

interface LoggingChannel {
    void info(String category, String message);
    void info(String category, String message, Object... arguments);

    void warn(String category, String message);
    void warn(String category, String message, Object... arguments);

    void error(String category, String message);
    void error(String category, String message, Object... arguments);
}
