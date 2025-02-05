package Exception;

import Lexical.Token;

public class SemanticException extends Exception{
    private Token token;
    private String message;

    public SemanticException(Token token, String message){
        this.token = token;
        this.message = message;
    }

    public String toString(){
        String toReturn = "---------------------------------------------------------\n";
        toReturn += "Se ha producido una excepción semantica. "+message;
        toReturn += "\n\n";
        toReturn += generateErrorCode();
        toReturn += "\n---------------------------------------------------------";

        return toReturn;
    }

    private String generateErrorCode(){
        return "[Error:"+token.getLexeme()+"|"+token.getRow()+"]";
    }
}