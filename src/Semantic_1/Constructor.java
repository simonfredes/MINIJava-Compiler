package Semantic_1;

import Lexical.Token;
import Semantic_1.Type.Type;

public class Constructor extends Method{

    public Constructor(Token methodNameToken, Type tipo, Clase containerClass) {
        super(methodNameToken, tipo, containerClass, null);
        this.etiquetaMetodo="lblMet"+this.containerClass.classNameToken.getLexeme();

    }




}
