import core.LogLevel;
import logger.ConsoleAppender;
import logger.DebugLogger;
import logger.ErrorLogger;
import logger.ILogAppender;
import logger.InfoLogger;
import logger.LogHandler;
import logger.WarnLogger;
import singleton.Logger;
import singleton.LoggerConfig;

public class Main {
    public static LogHandler getLogHandlerChain(ILogAppender appender) {
        LogHandler infoLogger = new InfoLogger(LogHandler.INFO, appender);
        LogHandler debugLogger = new DebugLogger(LogHandler.DEBUG, appender);
        LogHandler warnLogger = new WarnLogger(LogHandler.WARN, appender);
        LogHandler errorLogger = new ErrorLogger(LogHandler.ERROR, appender);

        infoLogger.setNextHandler(debugLogger);
        debugLogger.setNextHandler(warnLogger);
        warnLogger.setNextHandler(errorLogger);

        return infoLogger;
    }

    public static void main(String[] args) {
        ILogAppender consoleAppender = new ConsoleAppender();
        LogHandler loggerChain = getLogHandlerChain(consoleAppender);

        // Example log messages
        loggerChain.logMessage(LogHandler.INFO, "This is an info message.");
        loggerChain.logMessage(LogHandler.DEBUG, "This is a debug message.");
        loggerChain.logMessage(LogHandler.WARN, "This is a warning message.");
        loggerChain.logMessage(LogHandler.ERROR, "This is an error message.");

        // Using singleton Logger
        Logger logger = Logger.getInstance(LogLevel.INFO, consoleAppender);
        logger.info("Singleton Logger - Info message");
        logger.debug("Singleton Logger - Debug message");
        logger.warn("Singleton Logger - Warning message");
        logger.setConfig(new LoggerConfig(LogLevel.ERROR, consoleAppender));
        logger.error("Singleton Logger - Error message");
    }
}
