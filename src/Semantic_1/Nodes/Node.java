package Semantic_1.Nodes;

import Lexical.Token;
import Semantic_1.Nodes.Sentencia.NodoBloque;
import Semantic_1.SymbolTable.SymbolTable;
import Exception.SemanticException;
import Semantic_1.Type.Type;

//This interface will provide the methods that all the nodes in the AST trees will have
public interface Node {


    void setParentBlock(NodoBloque nodeBlock);

    public void check(SymbolTable symbolTable) throws SemanticException;
    public Type getType();
    public Token getToken();

    //public void generarCodigo();
}