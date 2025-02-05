package Semantic_1.Nodes.Sentencia;
import Exception.SemanticException;
import Lexical.Token;
import Semantic_1.Nodes.NodoExpresion;
import Semantic_1.SymbolTable.SymbolTable;
import Semantic_1.Type.Type;

public class NodoIfSolo implements NodoSentencia {

    public NodoExpresion nodoExpresion;
    public NodoSentencia nodoSentencia;
    public NodoBloque nodoBloquePadre;
    public Token tokenIf;

    public Type tipo;


    public NodoIfSolo(NodoExpresion nodoExpresion, NodoSentencia ns) {
        this.nodoExpresion = nodoExpresion;
        this.nodoSentencia = ns;

    }

    @Override
    public void setParentBlock(NodoBloque nodeBlock) {
        this.nodoBloquePadre = nodeBlock;
    }

    @Override
    public NodoBloque getParentBlock() {
        return nodoBloquePadre;
    }

    @Override
    public void check(SymbolTable symbolTable) throws SemanticException {
        nodoExpresion.checkExpresion(symbolTable);
            if (!nodoExpresion.getType().nameType.equals("boolean")){
                throw new SemanticException(nodoExpresion.getToken(), "La condicion del if debe ser de tipo boolean");
            }
            this.nodoSentencia.check(symbolTable);


    }

    @Override
    public Type getType() {
        return tipo;
    }

    @Override
    public Token getToken() {
        return tokenIf;
    }

    public void setToken(Token token){
        this.tokenIf = token;
    }

    @Override
    public void generarCodigo(SymbolTable symbolTable) {

        this.nodoExpresion.generarCodigo(symbolTable);
        symbolTable.writer.write("BF endIF@"+ this.getToken().getLexeme()+ this.getToken().getRow());
        this.nodoSentencia.generarCodigo(symbolTable);

        symbolTable.writer.write("endIF@"+ this.getToken().getLexeme()+ this.getToken().getRow() + ": NOP");


    }

    public String toString(){
        String toReturn ="";
        toReturn += "if: "+nodoExpresion.toString();
        toReturn += "then: "+nodoSentencia.toString();
        return toReturn;
    }
}
