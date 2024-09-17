package SourceManager;
//Author: Juan Dingevan

import java.io.*;
import java.nio.charset.StandardCharsets;

public class SourceManagerImpl implements SourceManager {
    private BufferedReader reader;
    private String currentLine;
    private int lineNumber;
    private int lineIndexNumber;

    private int columnNumber;
    private boolean mustReadNextLine;


    public SourceManagerImpl() {
        currentLine = "";
        lineNumber = 0;
        lineIndexNumber = 0;
        columnNumber=0;
        mustReadNextLine = true;
    }

    @Override
    public void open(String filePath) throws FileNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(filePath);
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);

        reader = new BufferedReader(inputStreamReader);
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

    @Override
    public char getNextChar() throws IOException {
        char currentChar = ' ';

        if (mustReadNextLine) {
            currentLine = reader.readLine();
            lineNumber++;
            lineIndexNumber = 0;
            columnNumber = 0;     // Reinicia el número de columna.
            mustReadNextLine = false;
        }

        if (lineIndexNumber < currentLine.length()) {
            currentChar = currentLine.charAt(lineIndexNumber);
            lineIndexNumber++;
            columnNumber++;       // Incrementa el número de columna.
        } else if (reader.ready()) {
            currentChar = '\n';
            mustReadNextLine = true;
            columnNumber = 0;     // Reinicia el número de columna después del salto de línea.

        } else {
            currentChar = END_OF_FILE;
        }

        return currentChar;
    }
    public int getCurrentColumn(){
        return columnNumber;
    }

    @Override
    public boolean isEOF(char currentChar) {
        return currentChar == (char) 26;

    }

    @Override
    public String getContentLine(int lineNumber) {
        return currentLine;
    }

    @Override
    public int getLineNumber() {
        return lineNumber;
    }

}
