package Semantic_1.Nodes;

import Semantic_1.Nodes.Sentencia.NodoBloque;

public abstract class NodoExpComp extends NodoExpresion{

    protected NodoBloque parentBlock;
    public boolean modoEscritura = false;

    public abstract boolean esAsignable();



}
