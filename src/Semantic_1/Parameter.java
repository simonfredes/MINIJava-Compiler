package Semantic_1;

import Lexical.Token;
import Semantic_1.SymbolTable.SymbolTable;
import Semantic_1.Type.Type;
import Exception.SemanticException;
public class Parameter {

    private Type type;
    private Token name;

    private Method contentMethod;

    private Clase contentClass;

    public void setType(Type type) {
        this.type = type;
    }

    public Token getName() {
        return name;
    }

    public void setName(Token name) {
        this.name = name;
    }

    public Method getContentMethod() {
        return contentMethod;
    }

    public void setContentMethod(Method contentMethod) {
        this.contentMethod = contentMethod;
    }

    public Clase getContentClass() {
        return contentClass;
    }

    public int offSet;
    public void setContentClass(Clase contentClass) {
        this.contentClass = contentClass;
    }

    public Parameter(Token name, Type type ) {
        this.type = type;
        this.name = name;
    }

    public Parameter (Token name, Type type, Clase contentClass, Method contentMethod) {
        this.type = type;
        this.name = name;
        this.contentClass = contentClass;
        this.contentMethod = contentMethod;
    }

    public void check(SymbolTable symbolTable) throws SemanticException {

    }

    public Type getType() {
        return type;
    }
    public String getLexeme(){
        return name.getLexeme();
    }
}

