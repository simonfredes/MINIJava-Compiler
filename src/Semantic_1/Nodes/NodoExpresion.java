package Semantic_1.Nodes;

import Lexical.Token;
import Exception.SemanticException;
import Semantic_1.Nodes.Encadenados.Encadenado;
import Semantic_1.Nodes.Sentencia.NodoBloque;
import Semantic_1.SymbolTable.SymbolTable;
import Semantic_1.Type.Type;

public  abstract class NodoExpresion{

    public Encadenado encadenado;
    public NodoBloque currentBloque;

    public Type type;

    public boolean esAsignable;

    public void setParentBlock(NodoBloque nodeBlock) {
        this.currentBloque.parentBlock = nodeBlock;
    }


   // public abstract void check(SymbolTable symbolTable) throws SemanticException;
    //TODO: check w gotti ¿que pasa aca? esto creo q deberia ser abstract.
    //TODO: este metodo no tendria que tener implementación
    public abstract Type checkExpresion(SymbolTable symbolTable) throws SemanticException;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public abstract Token getToken() ;


    public void setEncadenado(Encadenado encadenado) {
        this.encadenado = encadenado;
    }

    public abstract void generarCodigo(SymbolTable symbolTable);
}
