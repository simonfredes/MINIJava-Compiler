package Semantic_1;

import Lexical.Token;
import Semantic_1.Type.Type;

import java.util.ArrayList;

import Exception.*;

public class Attribute {
    private Clase containerClass;
    private Token isStatic;

    public boolean esVarLocal;

    public Token attributeNameToken;

    public Type type;



    public int offSet=-1;


    public Attribute(Token token, Type type, Clase clase, Token isStatic) {
        this.isStatic = isStatic;
        attributeNameToken = token;
        containerClass = clase;
        this.type = type;
    }

    public Clase getContainerClass() {
        return containerClass;
    }


    public int getOffSet() {
        return offSet;
    }

    public void setOffSet(int offSet) {
        this.offSet = offSet;
    }

    public boolean equals(Attribute attribute) {
        if (this.getType().equals(attribute.getType()) && this.getName().equals(attribute.getName())) {
            return true;
        } else {
            return false;
        }
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return attributeNameToken.getLexeme();
    }

    public Token getToken() {
        return attributeNameToken;
    }


    public void check() throws SemanticException {
        checkInheritAttr(containerClass);
        checkTypeAttributeNotVoid();
        checkVariablesTipoClase();

    }

    private void checkTypeAttributeNotVoid() throws SemanticException {
        if (this.getType().getToken().getLexeme().equals("void")) {
            throw new SemanticException(this.attributeNameToken, "El tipo de la variable: " + this.attributeNameToken.getLexeme() + " no puede ser void");
        }
    }

    private void checkVariablesTipoClase() throws SemanticException {
        if (this.getType().isPrimitive == false)
            //For each class in TS, check if class exists
            if (!this.containerClass.getSymbolTable().getClassTable().containsKey(this.getType().getToken().getLexeme()))
                throw new SemanticException(this.getType().getToken(), "El tipo del atributo: " + this.getType().getToken().getLexeme() + ", no existe");
    }
            //if (!this.containerClass.getSymbolTable().getClassTable().containsKey(this.attributeNameToken.getLexeme()))
              //  throw new SemanticException(this.getType().getToken(), "El tipo del atributo: " + this.getType().getToken().getLexeme() + ", no existe");
   // }

    private void checkInheritAttr(Clase clase) throws SemanticException {
        if (clase.heredaDe.getLexeme().equals("Object")) {
            return;
        } else {
            Clase ancestro = clase.getSymbolTable().getClase(clase.heredaDe.getLexeme());
            for (Attribute a : ancestro.attributeTable.values()) {
                if (a.equals(this)) {
                    throw new SemanticException(this.attributeNameToken, "Atributo presente en ancestro");
                }
                checkInheritAttr(ancestro);
            }

        }
    }
}