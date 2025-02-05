package Semantic_1.Nodes.Encadenados;

import Lexical.Token;
import Semantic_1.SymbolTable.SymbolTable;
import Semantic_1.Type.Type;
import Exception.SemanticException;
public interface Encadenado {



    public Type chequear (Type t) throws SemanticException;

    public void setEscrituraTrue();
    public void setEscrituraFalse();
    void setEncadenado(Encadenado encadenado);

    boolean esAsignable();

    void generarCodigo(SymbolTable symbolTable, Type tipoAnterior);
}
