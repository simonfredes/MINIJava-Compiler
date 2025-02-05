package Semantic_1.Nodes.Sentencia;

import Lexical.Token;
import Semantic_1.SymbolTable.SymbolTable;
import Exception.SemanticException ;
import Semantic_1.Type.Type;

public class NodoBreak implements NodoSentencia {
    public Token tokenActual;
    private NodoBloque parentBlock;

    public NodoBreak(Token tokenActual) {
        this.tokenActual = tokenActual;
    }

    @Override
    public void setParentBlock(NodoBloque nodeBlock) {
        this.parentBlock = nodeBlock;
    }

    @Override
    public NodoBloque getParentBlock() {
        return parentBlock;
    }

    @Override
    public void check(SymbolTable symbolTable) throws SemanticException {
       if(parentBlock.flagBreak >0){
           return;
       }
       else{
           throw new SemanticException(tokenActual, "No se puede usar break fuera de un ciclo o switch");
       }
    }

    @Override
    public Type getType() {
        return null;
    }

    @Override
    public Token getToken() {
        return tokenActual;
    }

    @Override
    public void generarCodigo(SymbolTable symbolTable) {
        //symbolTable.writer.write("JUMP "+ parentBlock);
    }

    public String toString() {
        return "Break: " + tokenActual.getLexeme() + "\n";
    }
}
