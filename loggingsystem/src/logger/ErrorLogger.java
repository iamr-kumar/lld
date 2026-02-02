package logger;

public class ErrorLogger extends LogHandler {
    public ErrorLogger(int level, ILogAppender appender) {
        super(level, appender);
    }

}
