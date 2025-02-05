package Semantic_1.Nodes.Sentencia;
import Exception.SemanticException;
import Lexical.Token;
import Semantic_1.SymbolTable.SymbolTable;
import Semantic_1.Type.Type;

public class NodoSentenciaAsignacion implements NodoSentencia {

    NodoAsignacion asignacion;
    public NodoSentenciaAsignacion(NodoAsignacion expresion) {
        asignacion = expresion;
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
           this.asignacion.check(symbolTable);
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
        asignacion.generarCodigo(symbolTable);
    }
}
