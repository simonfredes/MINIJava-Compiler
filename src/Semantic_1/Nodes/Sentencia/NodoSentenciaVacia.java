package Semantic_1.Nodes.Sentencia;

import Lexical.Token;
import Semantic_1.SymbolTable.SymbolTable;
import Semantic_1.Type.Type;
import Exception.SemanticException;
public class NodoSentenciaVacia implements NodoSentencia{
    @Override
    public void setParentBlock(NodoBloque nodeBlock) {

    }

    @Override
    public NodoBloque getParentBlock() {
        return null;
    }

    @Override
    public void check(SymbolTable symbolTable) throws SemanticException {

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

    }
}
