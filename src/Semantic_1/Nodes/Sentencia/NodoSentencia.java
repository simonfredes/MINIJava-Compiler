package Semantic_1.Nodes.Sentencia;

import Lexical.Token;
import Semantic_1.Nodes.Node;
import Semantic_1.SymbolTable.SymbolTable;
import Exception.SemanticException;
import Semantic_1.Type.Type;

public interface NodoSentencia extends Node {



    void setParentBlock(NodoBloque nodeBlock);

    NodoBloque getParentBlock();
    public void check(SymbolTable symbolTable) throws SemanticException;
    public Type getType();

    public Token getToken();


    void generarCodigo(SymbolTable symbolTable);
}
