package Debugging;

class NullLoggingChannel implements LoggingChannel {
    @Override
    public void info(String category, String message) { }

    @Override
    public void info(String category, String message, Object... arguments) { }

    @Override
    public void warn(String category, String message) { }

    @Override
    public void warn(String category, String message, Object... arguments) { }

    @Override
    public void error(String category, String message) { }

    @Override
    public void error(String category, String message, Object... arguments) { }
}
