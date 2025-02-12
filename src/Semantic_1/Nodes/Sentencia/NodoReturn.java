package Semantic_1.Nodes.Sentencia;

import Lexical.Token;
import Semantic_1.Method;
import Semantic_1.Nodes.Node;
import Semantic_1.Nodes.NodoExpresion;
import Semantic_1.SymbolTable.SymbolTable;
import Exception.SemanticException;
import Semantic_1.Type.Type;

public class NodoReturn implements NodoSentencia {
    public Token returnTok;

    public NodoExpresion expression;
    private Type type;
    private boolean alreadyChecked = false;
    private NodoBloque parentBlock;

    public Method currentMethod;

    public NodoReturn(Token returnTok, NodoExpresion expression, Method currentMethod, NodoBloque parentBlock) {
        this.returnTok = returnTok;
        this.expression = expression;
        this.currentMethod = currentMethod;
        this.parentBlock = parentBlock;
    }


    @Override
    public void setParentBlock(NodoBloque nodeBlock) {
            this.parentBlock=nodeBlock;
    }

    @Override
    public NodoBloque getParentBlock() {
        return this.parentBlock;
    }

    @Override
    public void check(SymbolTable symbolTable) throws SemanticException {
        if (!alreadyChecked) {
            if (currentMethod.type.nameType.equals("void") && expression != null) {
                throw new SemanticException(returnTok, "Un metodo void no puede tener un return + expression");
            }
            if (expression != null) {
                expression.checkExpresion(symbolTable);
                if (currentMethod.type.nameType.equals(expression.getType().nameType)) {
                    type = currentMethod.type;
                }
                else {
                    throw new SemanticException(returnTok, "El return debe ser de tipo " + currentMethod.type.getName());
                }
            }

            alreadyChecked = true;
        }
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
        if (expression != null) {
            expression.generarCodigo(symbolTable);
            //en este momento, en el tope de la pila está el resultado de la expresion
            int offSet = currentMethod.getParametros().size() + 3 + (currentMethod.isStatic!=null ? 0 : 1);
            symbolTable.writer.write("STORE " + offSet + "\n");
        }
        int cantVarLocales =   currentMethod.bloquePrincipal.localVariables.size() * -1;
       symbolTable.writer.write("FMEM "+ cantVarLocales + "; Libero el espacio de memoria de las variables locales");

        //aca lo que falta es que se haga un storeFP y un ret n
        symbolTable.writer.write("STOREFP ");
        symbolTable.writer.write("RET " + String.valueOf(currentMethod.getParametros().size() + (currentMethod.isStatic!=null ? 0 : 1)) + "\n");

    }
}