package loggingsystem.src.logger;

import loggingsystem.src.core.LogLevel;
import loggingsystem.src.core.LogMessage;

public abstract class LogHandler {
    public static final int INFO = 1;
    public static final int DEBUG = 2;
    public static final int WARN = 3;
    public static final int ERROR = 4;

    protected int level;
    protected LogHandler nextHandler;
    protected ILogAppender appender;

    public LogHandler(int level, ILogAppender appender) {
        this.level = level;
        this.appender = appender;
    }

    public void setNextHandler(LogHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    public void logMessage(int level, String message) {
        if (this.level >= level) {
            LogLevel logLevel = intToLogLevel(level);
            LogMessage logMessage = new LogMessage(logLevel, message);
            if (appender != null) {
                appender.append(logMessage);
            } else {
                System.out.println("No appender set for this handler.");
            }
        } else if (nextHandler != null) {
            nextHandler.logMessage(level, message);
        }
    }

    public LogLevel intToLogLevel(int level) {
        switch (level) {
            case INFO:
                return LogLevel.INFO;
            case DEBUG:
                return LogLevel.DEBUG;
            case WARN:
                return LogLevel.WARN;
            case ERROR:
                return LogLevel.ERROR;
            default:
                return LogLevel.INFO;
        }
    }

}
