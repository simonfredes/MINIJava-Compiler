package Semantic_1.Nodes.Sentencia;

import Lexical.Token;
import Exception.SemanticException;
import Semantic_1.Nodes.Acceso.*;
import Semantic_1.Nodes.NodoExpresion;
import Semantic_1.SymbolTable.SymbolTable;
import Semantic_1.Type.Type;

public class NodoSentenciaLlamada implements NodoSentencia {

    public NodoExpresion llamada;


    public NodoSentenciaLlamada(NodoExpresion e) {
        llamada = e;
    }

    @Override
    public void setParentBlock(NodoBloque nodeBlock) {

    }

    @Override
    public NodoBloque getParentBlock() {
        return null;
    }

    @Override
    public void check(SymbolTable symbolTable) throws SemanticException {
        llamada.checkExpresion(symbolTable);
        if (!(llamada instanceof NodoAcceso) ){
            throw new SemanticException(llamada.getToken() , "La expresion no es un acceso");

        }

        //llamada.check(symbolTable);
    }
    public NodoExpresion getLlamada(){
        return llamada;
    }
    @Override
    public Type getType() {
        return null;
    }

    @Override
    public Token getToken() {
        return null;
    }

    @Override
    public void generarCodigo(SymbolTable symbolTable) {
        llamada.generarCodigo(symbolTable);
        if (!(llamada.type.nameType.equals("void"))){
            symbolTable.writer.write("POP");
        }
    }
}
