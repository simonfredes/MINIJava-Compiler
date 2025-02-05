package Semantic_1.Nodes.BloqueClaseDefault;

import Lexical.Token;
import Semantic_1.Clase;
import Semantic_1.Method;
import Semantic_1.Nodes.Sentencia.NodoBloque;
import Semantic_1.Parameter;
import Semantic_1.SymbolTable.SymbolTable;

import java.util.ArrayList;

public class BloqueSystemPrint extends NodoBloque {
    private boolean enter;
    private String type;


    public BloqueSystemPrint(Token initialToken, Clase currentClass, Method currentMethod, NodoBloque parentBlock, ArrayList< Parameter > methodParameters , String type, boolean enter){
        super( initialToken,  currentClass,  currentMethod,  parentBlock,  methodParameters);

        this.enter = enter;
        this.type = type;
    }

    public void generarCodigo(SymbolTable symbolTable) {

        switch (type) {
            case "int":
                symbolTable.writer.write("LOAD 3 ; carga el parametro");
                symbolTable.writer.write("IPRINT ; imprime el entero");
                break;
            case "bool":
                symbolTable.writer.write("LOAD 3 ; carga el parametro");
                symbolTable.writer.write("BPRINT ; imprime el booleano");
                break;
            case "string":
                symbolTable.writer.write("LOAD 3 ; carga el parametro");
                symbolTable.writer.write("SPRINT ; imprime el string");
                break;
            case "char":
                symbolTable.writer.write("LOAD 3 ; carga el parametro");
                symbolTable.writer.write("CPRINT ; imprime el caracter");
                break;
            default:
                break;
        }

        if(enter){
            symbolTable.writer.write("PRNLN ; imprime el salto de linea");
        }
    }
}
