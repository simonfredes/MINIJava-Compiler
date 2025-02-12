package Semantic_1.Nodes.Sentencia;

import Semantic_1.Nodes.NodoExpresion;
import Semantic_1.SymbolTable.SymbolTable;
import Exception.SemanticException;
public class NodoIfElse extends NodoIfSolo{

    private NodoSentencia sentenciaELSE;
    public NodoIfElse(NodoExpresion nodoExpresion, NodoSentencia ns, NodoSentencia sentenciaELSE ) {
        super(nodoExpresion, ns);
        this.sentenciaELSE= sentenciaELSE;
    }

    public String toString(){
        String toReturn ="";
        toReturn += super.toString();
        toReturn += "else: "+sentenciaELSE.toString();
        return toReturn;
    }

    public void check(SymbolTable symbolTable) throws SemanticException {
        super.check(symbolTable);
        sentenciaELSE.check(symbolTable);
    }

    public void generarCodigo(SymbolTable symbolTable) {
        // Generar el código de la expresión (condición del if)
        this.nodoExpresion.generarCodigo(symbolTable);

        // Instrucción para saltar al bloque ELSE si la condición es falsa
        symbolTable.writer.write("BF elseIf@" + this.getToken().getLexeme() + this.getToken().getRow());

        // Generar código para la sentencia del IF
        this.nodoSentencia.generarCodigo(symbolTable);

        // Si existe un bloque ELSE, saltar al final después de ejecutar el IF
        if (this.sentenciaELSE != null) {
            symbolTable.writer.write("JUMP endIF@" + this.getToken().getLexeme() + this.getToken().getRow());
        }

        // Etiqueta del bloque ELSE
        symbolTable.writer.write("elseIf@" + this.getToken().getLexeme() + this.getToken().getRow() + ": NOP");

        // Generar código para el bloque ELSE, si existe
        if (this.sentenciaELSE != null) {
            this.sentenciaELSE.generarCodigo(symbolTable);
        }

        // Etiqueta del final del if-else
        symbolTable.writer.write("endIF@" + this.getToken().getLexeme() + this.getToken().getRow() + ": NOP");


    }

    @Override
    public NodoBloque getParentBlock() {
        return this.nodoBloquePadre;
    }

    @Override
    public void setParentBlock(NodoBloque nodeBlock) {
        this.nodoBloquePadre = nodeBlock;
    }
}
