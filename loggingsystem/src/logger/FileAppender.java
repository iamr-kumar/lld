package logger;

import core.LogMessage;

public class FileAppender implements ILogAppender {
    private String filePath;

    public FileAppender(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void append(LogMessage logMessage) {
        System.out.println("Writing to file: " + filePath + " Message: " + logMessage.getMessage()
                + " Level: " + logMessage.getLevel() + " Timestamp: " + logMessage.getTimestamp());
    }
}
