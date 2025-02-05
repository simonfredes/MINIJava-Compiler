package Semantic_1.Nodes.Acceso;

import Lexical.Token;
import Semantic_1.Clase;
import Semantic_1.Method;
import Semantic_1.Nodes.Sentencia.NodoBloque;
import Semantic_1.SymbolTable.SymbolTable;
import Exception.SemanticException;
import Semantic_1.Type.Type;

public class NodoAccesoThis extends NodoAcceso {

    public Token tokenAccesso;
    public Clase currentClass;

    public Method currentMethod;

    public NodoAccesoThis(Token tokenAcceso) {
        this.tokenAccesso = tokenAcceso;
    }

    public NodoAccesoThis(Token tokenAcceso, NodoBloque currentBlock, Clase currentClass, Method currentMethod) {
        this.tokenAccesso = tokenAcceso;
        this.parentBlock = currentBlock;
        this.currentClass = currentClass;
        this.currentMethod = currentMethod;
    }

    @Override
    public void setParentBlock(NodoBloque nodeBlock) {
        this.parentBlock = nodeBlock;
    }


    public void check(SymbolTable symbolTable) throws SemanticException {
        //chequear que el metodo en el que estoy, no sea statico //TODO: chequear
        if (currentMethod.isStatic != null) {
            throw new SemanticException(getToken(), "No se puede acceder a this en un metodo statico");
        }
        // Si tiene encadenado, chequear encadenado pasandole el type this, y retornarlo.
        if (encadenado!=null){
            //TODO: Pero... que tipo soy?
            //this.type = currentClass.
           this.type= new Type( currentClass.classNameToken, symbolTable);
            this.type=encadenado.chequear(type);
        }else{
            this.type= new Type(tokenAccesso, symbolTable);
        }
        //else, retorno el tipo this

    }

    @Override
    public Type checkExpresion(SymbolTable symbolTable) throws SemanticException {
        check(symbolTable);
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
        symbolTable.writer.write("LOAD 3");
    }

    @Override
    public boolean esAsignable() {
        return encadenado!= null && encadenado.esAsignable();
    }
}
