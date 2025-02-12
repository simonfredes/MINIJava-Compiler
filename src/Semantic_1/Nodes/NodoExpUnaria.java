package Semantic_1.Nodes;

import Lexical.Token;
import Semantic_1.Nodes.Sentencia.NodoBloque;
import Semantic_1.SymbolTable.SymbolTable;
import Exception.SemanticException;
import Semantic_1.Type.Type;

public class NodoExpUnaria extends NodoExpComp {

    public Token operador;
    public NodoExpresion expression;
    public NodoBloque bloqueActual;

    public NodoExpUnaria(Token operador, NodoExpresion expression, NodoBloque currentBlock) {
        this.operador = operador;
        this.expression = expression;
        this.bloqueActual = currentBlock;
    }

    @Override
    public void setParentBlock(NodoBloque nodeBlock) {
        bloqueActual.parentBlock = nodeBlock;
    }


    @Override
    public Type checkExpresion(SymbolTable symbolTable) throws SemanticException {
        // Asumiendo que `expression.check(symbolTable)` inicializa `type`
        Type tipoExpresion = expression.checkExpresion(symbolTable); // Chequea y asigna el tipo de la expresión en `type`

        switch (operador.getLexeme()) {
            case "!":
                if (tipoExpresion.nameType.equals("boolean")) {
                    this.type = tipoExpresion;
                    return tipoExpresion; // Retorna si es boolean
                } else {
                    throw new SemanticException(operador, "El operador ! solo se puede usar en booleanos");
                }

            case "+":
            case "-":
                if (tipoExpresion.nameType.equals("intLiteral")) {
                    this.type = tipoExpresion;
                    return tipoExpresion; // Retorna si es entero
                } else {
                    throw new SemanticException(operador, "El operador " + operador.getLexeme() + " solo se puede usar en enteros");
                }

            default:
                throw new SemanticException(operador, "Operador desconocido: " + operador.getLexeme());
        }
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
        this.expression.generarCodigo(symbolTable);

        switch (operador.getLexeme()) {
            case "!":
                symbolTable.writer.write("NOT");
                break;
            case "+":
                symbolTable.writer.write("POS");
                break;
            case "-":
                symbolTable.writer.write("NEG");
        }
    }

    @Override
    public boolean esAsignable() {
        return false;
    }
}
