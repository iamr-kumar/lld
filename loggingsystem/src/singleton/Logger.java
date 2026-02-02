package singleton;

import java.util.concurrent.ConcurrentHashMap;

import core.LogLevel;
import core.LogMessage;
import logger.ILogAppender;

public class Logger {
    private static final ConcurrentHashMap<String, Logger> instances = new ConcurrentHashMap<>();
    private LoggerConfig config;

    private Logger(LogLevel level, ILogAppender appender) {
        this.config = new LoggerConfig(level, appender);
    }

    public static Logger getInstance(LogLevel level, ILogAppender appender) {
        String key = level.name() + "_" + appender.getClass().getName();
        return instances.computeIfAbsent(key, k -> new Logger(level, appender));
    }

    public void setConfig(LoggerConfig config) {
        synchronized (Logger.class) {
            this.config = config;
        }
    }

    public void log(LogLevel level, String message) {
        if (config.getLogLevel().getValue() <= level.getValue()) {
            LogMessage logMessage = new LogMessage(level, message);
            config.getAppender().append(logMessage);
        }
    }

    public void info(String message) {
        log(LogLevel.INFO, message);
    }

    public void debug(String message) {
        log(LogLevel.DEBUG, message);
    }

    public void warn(String message) {
        log(LogLevel.WARN, message);
    }

    public void error(String message) {
        log(LogLevel.ERROR, message);
    }

}
