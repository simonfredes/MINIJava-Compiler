package Semantic_1.Nodes.Acceso;

import Semantic_1.Nodes.Encadenados.Encadenado;
import Semantic_1.Nodes.NodoExpComp;
import Semantic_1.Nodes.NodoOperando;
import Semantic_1.Nodes.Sentencia.NodoBloque;
import Semantic_1.Nodes.NodoExpresion;
import Semantic_1.SymbolTable.SymbolTable;
import Semantic_1.Type.Type;
import Exception.SemanticException;

public abstract class NodoAcceso extends NodoOperando {



    @Override
    public void setParentBlock(NodoBloque nodeBlock) {
        this.parentBlock = nodeBlock;
    }

    public Type getType() {
        return type;

    }

    public abstract void check(SymbolTable symbolTable) throws SemanticException;


}
