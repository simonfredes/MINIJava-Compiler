import Lexical.AnalizadorLexico;
import Lexical.LexicalException;
import Lexical.Token;
import Semantic_1.Semantic;
import SourceManager.*;
import Parser.*;

import java.io.IOException;

public class MainParser {
    public static void main(String [] args){
        try {
            SourceManager gestorDeArchivos = new SourceManagerImpl();
            gestorDeArchivos.open(args[0]);
            AnalizadorLexico analizadorLexic = new AnalizadorLexico(gestorDeArchivos);
            Semantic parser = new Semantic(analizadorLexic, args[1]);
            parser.inicial();
            System.out.println(parser.toString());

            System.out.println("[SinErrores]");
        }catch (Exception e){
            System.out.println(e.toString());
        }
    }

}
