package logger;

import core.LogMessage;

public interface ILogAppender {
    void append(LogMessage logMessage);
}
