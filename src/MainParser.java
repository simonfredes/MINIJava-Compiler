import Lexical.AnalizadorLexico;
import Lexical.LexicalException;
import Lexical.Token;
import SourceManager.*;
import Parser.*;

import java.io.IOException;

public class MainParser {
    public static void main(String [] args){
        try {
            SourceManager gestorDeArchivos = new SourceManagerImpl();
            gestorDeArchivos.open(args[0]);
            AnalizadorLexico analizadorLexic = new AnalizadorLexico(gestorDeArchivos);
            SyntaxAnalyzer parser = new SyntaxAnalyzer(analizadorLexic);
            parser.inicial();

            System.out.println("[SinErrores]");
        }catch (Exception e){
            System.out.println(e.toString());
        }
    }

}
