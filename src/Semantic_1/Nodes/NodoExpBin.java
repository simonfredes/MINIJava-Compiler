package Semantic_1.Nodes;

import Lexical.Token;
import Semantic_1.Clase;
import Semantic_1.Nodes.Sentencia.NodoBloque;
import Semantic_1.SymbolTable.SymbolTable;
import Exception.SemanticException;
import Semantic_1.Type.Type;

public class NodoExpBin extends NodoExpComp {

    public Token operator;
    public NodoExpComp left;
    public NodoExpComp right;

    public Type type;

    public NodoExpBin(NodoExpComp left, Token operator, NodoExpComp right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public void setParentBlock(NodoBloque nodeBlock) {


    }


    //TODO: check con gotti
    public Type checkExpresion(SymbolTable symbolTable) throws SemanticException {
        Type ladoIzquierdoExpresion = left.checkExpresion(symbolTable);
        Type ladoDerechoExpresion = right.checkExpresion(symbolTable);

        //if the operator is + - * / % only int  are allowed (check with the lexeme of the token)
        if (operator.getLexeme().equals("+") || operator.getLexeme().equals("-") || operator.getLexeme().equals("*") || operator.getLexeme().equals("/") || operator.getLexeme().equals("%")) {
            if (ladoIzquierdoExpresion.nameType.equals("intLiteral") && ladoDerechoExpresion.nameType.equals("intLiteral")) {
                type = new Type(new Token("intLiteral", "int", operator.getRow()), symbolTable);
                return type;
            } else {
                throw new SemanticException(operator, "El operador " + operator.getLexeme() + " solo se puede usar con enteros");

            }
        }

        //Los operadores booleanos && y || s´olo trabajan con
        //subexpresiones de tipo boolean y devuelven resultados de tipo boolean
        if (operator.getLexeme().equals("&&") || operator.getLexeme().equals("||")) {
            if (ladoIzquierdoExpresion.nameType.equals("boolean") && ladoDerechoExpresion.nameType.equals("boolean")) {
                type = new Type(new Token("boolean", "boolean", operator.getRow()), symbolTable);
                return type;
            } else {
                throw new SemanticException(operator, "El operador " + operator.getLexeme() + " solo se puede usar con booleanos");
            }
        }


        //if the operator is == != only conform types are allowed (check with the lexeme of the token)
        //if the type of the right conform with the left one, then the type will be boolean, otherwise error
        if (operator.getLexeme().equals("!=") || operator.getLexeme().equals("==")) {
            if (ladoIzquierdoExpresion.nameType.equals("intLiteral") || ladoIzquierdoExpresion.getName().equals("float") || ladoIzquierdoExpresion.getName().equals("charLiteral") || ladoIzquierdoExpresion.getName().equals("boolean")) {
                if (!ladoIzquierdoExpresion.nameType.equals(ladoDerechoExpresion.nameType)) {
                    throw new SemanticException(operator, "Binary operation between different types");
                }
                type = new Type(new Token("boolean", "boolean", operator.getRow()), symbolTable);
            } else { //si no son tipos de clase primitivos, entonces cheqqueamos si son compatibles
                //TODO: check w/ gotti
                Clase clase1 = symbolTable.getClase(ladoIzquierdoExpresion.getName());
                Clase clase2 = symbolTable.getClase(ladoDerechoExpresion.getName());
                if (clase1 != null && clase2 != null) { //si no son primitivos, entonces, chequeamos compatibilidad
                    if (ladoIzquierdoExpresion.sonCompatibles(ladoIzquierdoExpresion, ladoDerechoExpresion)) {
                        type = new Type(new Token("boolean", "boolean", operator.getRow()), symbolTable);
                    } else {
                        throw new SemanticException(operator, "Binary operation between different types");
                    }
                }
            }
        }

        // Los operadores relacionales <, <=,
        //>= y >, s´olo trabajan con subexpresiones de tipo int y devuelven resultado de tipo boolean.
        if (operator.getLexeme().equals("<") || operator.getLexeme().equals("<=") || operator.getLexeme().equals(">=") || operator.getLexeme().equals(">")) {
            if (ladoIzquierdoExpresion.nameType.equals("intLiteral") && ladoDerechoExpresion.nameType.equals("intLiteral")) {
                type = new Type(new Token("boolean", "boolean", operator.getRow()), symbolTable);
                return type;
            } else
                throw new SemanticException(operator, "El operador " + operator.getLexeme() + " solo se puede usar con enteros");
        }

        return type;
    }


    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Token getToken() {
        return type.getToken();
    }

    @Override
    public void generarCodigo(SymbolTable symbolTable) {
        left.generarCodigo(symbolTable);
        right.generarCodigo(symbolTable);

        switch (operator.getLexeme()) { //TODO:GENERACIONCHECK
            case "+" -> symbolTable.writer.write("add");
            case "-" -> symbolTable.writer.write("sub");
            case "*" -> symbolTable.writer.write("mul");
            case "/" -> symbolTable.writer.write("div");
            case "&&" -> symbolTable.writer.write("and");
            case "||" -> symbolTable.writer.write("or");
            case "==" -> symbolTable.writer.write("eq");
            case "!=" -> symbolTable.writer.write("ne");
            case ">" -> symbolTable.writer.write("gt");
            case ">=" -> symbolTable.writer.write("ge");
            case "<" -> symbolTable.writer.write("lt");
            case "<=" -> symbolTable.writer.write("le");
        }

    }

    @Override
    public boolean esAsignable() {
        return false;
    }

}
