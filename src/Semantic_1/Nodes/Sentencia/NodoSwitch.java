package Semantic_1.Nodes.Sentencia;

import Lexical.Token;
import Semantic_1.Nodes.NodoExpresion;
import Semantic_1.SymbolTable.SymbolTable;
import Exception.SemanticException;
import Semantic_1.Type.Type;

import java.util.ArrayList;

public class NodoSwitch implements NodoSentencia {

    private NodoBloque parentBlock;

    public Token tokenActual;
    public NodoExpresion expresionSwitch;
    public ArrayList<NodoSentencia> sentenciasSwitch;

    public NodoSwitch(Token tokenActual, NodoExpresion expresionSwitch, ArrayList<NodoSentencia> sentenciasSwitch, NodoBloque parentBlock) {
        this.expresionSwitch = expresionSwitch;
        this.sentenciasSwitch = sentenciasSwitch;
        this.tokenActual = tokenActual;
        this.parentBlock = parentBlock;


    }

    @Override
    public void setParentBlock(NodoBloque nodeBlock) {
        this.parentBlock = nodeBlock;
    }

    @Override
    public NodoBloque getParentBlock() {
        return this.parentBlock;
    }

    @Override
    public void check(SymbolTable symbolTable) throws SemanticException {
        //Una sentencia de la forma switch(e) {...} es correcta si y solo si e es una expresion correctamente
        //tipada de tipo boolean, int o char

        Type tipoExpresionSwitch = expresionSwitch.checkExpresion(symbolTable);
        if (!tipoExpresionSwitch.getToken().getName().equals("boolean") && !tipoExpresionSwitch.getToken().getName().equals("int") && !tipoExpresionSwitch.getToken().getName().equals("char")) {
            throw new SemanticException(tokenActual, "La expresion del switch debe ser de tipo boolean, int o char");
        }
        //  {...} contiene sentencias correctamente tipadas.
        for (NodoSentencia sentenciaSwitch : sentenciasSwitch) {
            sentenciaSwitch.check(symbolTable);
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
        // Etiqueta de finalización del switch
        String endLabel = "endSwitch@" + this.tokenActual.getLexeme() + this.tokenActual.getRow();

        // Generar código para la expresión del switch
        this.expresionSwitch.generarCodigo(symbolTable);

        // Generar etiquetas y condiciones para cada caso
        for (int i = 0; i < this.sentenciasSwitch.size(); i++) {
            NodoSentencia sentencia = this.sentenciasSwitch.get(i);
            String caseLabel = "case@" + i + "@" + this.tokenActual.getLexeme() + this.tokenActual.getRow();

            // Etiqueta para el caso
            symbolTable.writer.write(caseLabel + ": NOP");

            // Generar código para la sentencia
            sentencia.generarCodigo(symbolTable);

            // Si la sentencia tiene un `break`, saltar al final del switch
            if (sentencia instanceof NodoBreak) {
                symbolTable.writer.write("JUMP " + endLabel);
            }
        }

        // Etiqueta de finalización
        symbolTable.writer.write(endLabel + ": NOP");

    }
}
