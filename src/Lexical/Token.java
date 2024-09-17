package Lexical;

public class Token {
    private String name, lexeme;
    private int row;

    public Token(String name, String lexeme, int row) {
        this.name = name;
        this.lexeme = lexeme;
        this.row = row;
    }

    public String getName() {
        return name;
    }

    public String getLexeme() {
        return lexeme;
    }

    public int getRow() {
        return row;
    }

    public String toString(){
        return "("+name+","+lexeme+","+row+")";
    }

}