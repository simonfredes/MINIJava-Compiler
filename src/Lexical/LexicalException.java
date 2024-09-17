package Lexical;

public class LexicalException extends Exception {
    String lexeme, error, lineContent;
    int line,column;


    public LexicalException(String lexeme, int line, int column, String error, String lineContent) {
        this.lexeme = lexeme;
        this.line = line;
        this.column = column;
        this.error = error;
        this.lineContent = lineContent;
    }

    public String toString(){
        String toReturn = "---------------------------------------------------------\n";
        toReturn += "Se ha producido un error léxico. "+error+" \nLexema: "+lexeme+" En la línea: "+line+", columna: "+column;
        toReturn += "\n\n";
        toReturn += lineContent;
        toReturn += "\n";
        for (int i=0; i<column-1; i++){
            toReturn += " ";
        }
        toReturn += "↑\n";
        toReturn += "[Error:"+lexeme+"|"+line+"]\n\n";
        toReturn += "---------------------------------------------------------";

        return toReturn;
    }
}