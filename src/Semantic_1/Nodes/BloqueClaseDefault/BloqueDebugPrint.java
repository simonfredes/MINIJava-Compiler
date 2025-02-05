package Semantic_1.Nodes.BloqueClaseDefault;

import Lexical.Token;
import Semantic_1.Clase;
import Semantic_1.Method;
import Semantic_1.Nodes.Sentencia.NodoBloque;
import Semantic_1.Parameter;
import Semantic_1.SymbolTable.SymbolTable;

import java.util.ArrayList;

public class BloqueDebugPrint extends NodoBloque {

    public BloqueDebugPrint(Token initialToken, Clase currentClass, Method currentMethod, NodoBloque parentBlock, ArrayList<Parameter> methodParameters) {
        super(initialToken, currentClass, currentMethod, parentBlock, methodParameters);
    }

    public void generarCodigo(SymbolTable symbolTable) {
        symbolTable.writer.write("LOAD 3 ; carga el parametro");
        symbolTable.writer.write("IPRINT");
    }

}
