package Semantic_1.Nodes.Sentencia;

import Lexical.Token;
import Semantic_1.Attribute;
import Semantic_1.Nodes.Node;
import Semantic_1.Nodes.NodoExpComp;
import Semantic_1.Nodes.Sentencia.NodoBloque;
import Semantic_1.SymbolTable.SymbolTable;
import Exception.SemanticException;
import Semantic_1.Type.Type;

public class NodoVarDeclaracion implements NodoSentencia {

    public Type tipo;
    public Token name;
    public NodoExpComp expresion;
    public Attribute varLocalTipoCompatible;

    private NodoBloque parentBlock;

    public NodoVarDeclaracion(Token name, NodoExpComp expresion, SymbolTable symbolTable) throws SemanticException {
        this.name = name;
        this.expresion = expresion;
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
        //La sentencia de declaraci´on de variables locales es var id = e1 son correctas si y s´olo si
    System.out.println("entre a check de NodoVarDeclaración");
        tipo= expresion.checkExpresion(symbolTable);
        System.out.println("El resultado de TIPO de chequear la expresion es: " + tipo.nameType);

       varLocalTipoCompatible = new Attribute(name,tipo, symbolTable.getCurrentClass(), null);
        if (symbolTable.currentBlock.localVariables.stream().anyMatch(a -> a.getName().equals(name.getLexeme()))) {
            throw new SemanticException(name, "La variable local " + name.getLexeme() + " ya ha sido declarada");
        }
        varLocalTipoCompatible.setOffSet(-symbolTable.currentBlock.localVariables.size());

        symbolTable.currentBlock.localVariables.add(varLocalTipoCompatible);
        //parentBlock.localVariables.add(new Attribute(name,tipo, parentBlock.currentClass,null));


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
        symbolTable.writer.write("RMEM 1");
        expresion.generarCodigo(symbolTable);
        symbolTable.writer.write("STORE "+ varLocalTipoCompatible.getOffSet());


    }
}
