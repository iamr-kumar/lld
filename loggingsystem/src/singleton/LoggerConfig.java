package singleton;

import core.LogLevel;
import logger.ILogAppender;

public class LoggerConfig {
    private LogLevel logLevel;
    private ILogAppender appender;

    public LoggerConfig(LogLevel logLevel, ILogAppender appender) {
        this.logLevel = logLevel;
        this.appender = appender;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
    }

    public ILogAppender getAppender() {
        return appender;
    }

    public void setAppender(ILogAppender appender) {
        this.appender = appender;
    }
}
