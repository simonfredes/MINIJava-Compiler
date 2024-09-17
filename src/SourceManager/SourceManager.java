package SourceManager;
//Author: Juan Dingevan

import java.io.FileNotFoundException;
import java.io.IOException;

public interface SourceManager {
    void open(String filePath) throws FileNotFoundException;

    void close() throws IOException;

    char getNextChar() throws IOException;

    int getLineNumber();

    public static final char END_OF_FILE = (char) 26;

    int getCurrentColumn();

    boolean isEOF(char currentChar);

    String getContentLine(int lineNumber);
}
