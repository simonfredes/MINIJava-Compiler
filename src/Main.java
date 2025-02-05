import SourceManager.*;
import Lexical.AnalizadorLexico;
import Lexical.LexicalException;
import Lexical.Token;

import java.io.IOException;
import java.util.*;

class Main {
    public static void main(String[] args) {
        if (args.length == 0){
            System.out.println("No se encontró ningun archivo, especifique la ruta correctamente.");
        }
        else{
            ArrayList<LexicalException> erroresEncontrados = new ArrayList<>();
            try {
                SourceManagerImpl fileManipulator = new SourceManagerImpl();
                fileManipulator.open(args[0]);
                AnalizadorLexico analizadorLexico = new AnalizadorLexico(fileManipulator);

                Token current = new Token(null,null,0);

                while (current.getName() != "EOF"){
                    try {
                        current = analizadorLexico.getNextToken();
                        System.out.println(current);
                    }catch (LexicalException e){
                        erroresEncontrados.add(e);
                    }
                }


            } catch (IOException e) {
                System.out.println("Error al abrir o leer el archivo.");
            }

            if (erroresEncontrados.isEmpty()){
                System.out.println("\n---------------------------------------------------------\n\n[SinErrores]");
            }else {
                for (LexicalException lexicalError : erroresEncontrados) {
                    System.out.println(lexicalError.toString());
                }
                System.out.println("\n Se encontraron: "+erroresEncontrados.size()+" errores");
            }
        }
    }
}