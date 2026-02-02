package logger;

import core.LogMessage;

public class ConsoleAppender implements ILogAppender {
    @Override
    public void append(LogMessage logMessage) {
        System.out.println("Console Log - Level: " + logMessage.getLevel() +
                ", Message: " + logMessage.getMessage() +
                ", Timestamp: " + logMessage.getTimestamp());
    }

}
