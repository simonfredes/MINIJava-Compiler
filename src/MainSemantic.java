import Lexical.AnalizadorLexico;
import Semantic_1.Semantic;
import SourceManager.SourceManager;
import SourceManager.*;

public class MainSemantic {
    public static void main(String[] args) {
        try {
            if (args.length == 1 || args.length == 2) {

                SourceManager gestorDeArchivos = new SourceManagerImpl();
                gestorDeArchivos.open(args[0]);
                AnalizadorLexico analizadorLexic = new AnalizadorLexico(gestorDeArchivos);
                if (args.length == 1){

                }
                Semantic parser = new Semantic(analizadorLexic, args.length == 2 ? args[1] : "hola.out");
                parser.inicial();
                System.out.println(parser.toString());

                System.out.println("[SinErrores]");

            }
            else{
                throw new Exception ("Cantidad de archivos incorrectos");
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

}
