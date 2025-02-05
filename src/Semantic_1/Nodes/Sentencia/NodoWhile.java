package Semantic_1.Nodes.Sentencia;

import Lexical.Token;
import Semantic_1.Nodes.Node;
import Semantic_1.Nodes.NodoExpresion;
import Semantic_1.SymbolTable.SymbolTable;
import Exception.SemanticException;
import Semantic_1.Type.Type;

public class NodoWhile implements NodoSentencia {

    private NodoBloque parentBlock;

    public NodoExpresion expresionWhile;
    public NodoSentencia sentenciaWhile;
    public Token whileToken;

    public NodoWhile(NodoExpresion expresionWhile, NodoSentencia sentenciaWhile, Token whileToken, NodoBloque parentBlock) {
        this.expresionWhile = expresionWhile;
        this.sentenciaWhile = sentenciaWhile;
        this.whileToken = whileToken;
        this.parentBlock = parentBlock;

        if (sentenciaWhile instanceof NodoBloque) {
           //TODO: descomentar y arreglar anhidamientos de break  ((NodoBloque) sentenciaWhile).flagBreak= true;

        }

    }

    @Override
    public void setParentBlock(NodoBloque nodeBlock) {
        parentBlock = nodeBlock;
        //Si mi padre es un bloque que puede contener break, entonces, el bloque que contenga la sentencia del while, tambien
       // if (parentBlock.flagBreak == true &&  sentenciaWhile instanceof NodoBloque) {
        //TODO: descomentar y chequear anhidamientos     ((NodoBloque) sentenciaWhile).flagBreak = true;
       // }
    }

    @Override
    public NodoBloque getParentBlock() {
       return parentBlock;
    }

    @Override
    public void check(SymbolTable symbolTable) throws SemanticException {
        //Una sentencia de la forma while(e) S es correcta si y s´olo si e es una expresi´on correctamente tipada
        //de tipo boolean y S es una sentencia correcta.

        Type tipoExpresionWhile = expresionWhile.checkExpresion(symbolTable);
        if (!tipoExpresionWhile.nameType.equals("boolean")) {
            throw new SemanticException( expresionWhile.getToken(),"La condicion de un while debe ser de tipo boolean");
        }
        sentenciaWhile.check(symbolTable);

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
        String start = "start@" + whileToken.getLexeme() + whileToken.getRow();
        String endWhile= "endWhile@" + whileToken.getLexeme() + whileToken.getRow();


        symbolTable.writer.write(start+ ":NOP");
        this.expresionWhile.generarCodigo(symbolTable);
        symbolTable.writer.write("BF " + endWhile);


        this.sentenciaWhile.generarCodigo(symbolTable);



        symbolTable.writer.write("JUMP " +start);
        symbolTable.writer.write(endWhile + ":NOP ; fin del while");




    }
}
