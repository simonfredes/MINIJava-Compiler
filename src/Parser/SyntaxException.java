package Parser;

public class SyntaxException extends Exception{
    private int line;
    private String lexemaEsperado, lexemaEncontrado;

    public SyntaxException(int line, String lexemaEsperado, String lexemaEncontrado){
        this.line = line;
        this.lexemaEsperado = lexemaEsperado;
        this.lexemaEncontrado = lexemaEncontrado;
    }

    public String toString(){
        String toReturn = "---------------------------------------------------------\n";
        toReturn += "Se ha producido una excepción sintactica. Se esperaba "+lexemaEsperado+" y se encontró "+lexemaEncontrado;
        toReturn += "\n\n";
        toReturn += generateErrorCode();
        toReturn += "\n---------------------------------------------------------";

        return toReturn;
    }

    private String generateErrorCode(){
        return "[Error:"+lexemaEncontrado+"|"+line+"]";
    }
}