package loggingsystem.src.logger;

import loggingsystem.src.core.LogMessage;

public interface ILogAppender {
    void append(LogMessage logMessage);
}
