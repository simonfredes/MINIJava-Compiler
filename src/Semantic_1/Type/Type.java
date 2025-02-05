package Semantic_1.Type;

import Lexical.Token;
import Semantic_1.Clase;
import Semantic_1.SymbolTable.SymbolTable;

public  class Type {


    public Token token;
    public boolean isPrimitive;

    public String  nameType;

    public SymbolTable symbolTable;

    public Type(Token token, SymbolTable TS) {
        this.token = token;
        isPrimitive=false;
        if (token.getName().equals("intLiteral") || token.getName().equals("int") || token.getName().equals("keyword_int")) {
            this.nameType = "intLiteral";
            this.isPrimitive = true;
        } else if (token.getName().equals("charLiteral") || token.getName().equals("char") || token.getName().equals("keyword_char")) {
            this.nameType = "charLiteral";
            this.isPrimitive = true;
        } else if (token.getName().equals("boolean") || token.getName().equals("true") || token.getName().equals("false")    || token.getName().equals("keyword_boolean")) {
            this.nameType = "boolean";
            this.isPrimitive = true;
        }else if(token.getName().equals("null")) {
            this.nameType = "null";
            this.isPrimitive = false;
        }else if (token.getName().equals("String") || token.getName().equals("keyword_String") || token.getName().equals("stringLiteral")) {
            this.nameType = "String";
            this.isPrimitive = false;
        } else if (token.getName().equals("this")) {

        }
        else{
            this.nameType= token.getLexeme();
            this.isPrimitive = false;
        }

        this.symbolTable = TS;
    }


    //return true si t1 y t2 son compatibles
    //Si son de tipo clase, entonces son compatibles si T1 es ancestro de T2
    public boolean sonCompatibles(Type t1, Type t2){
        boolean toReturn = false;
        if (t1.isPrimitive && t2.isPrimitive) { //si son primitivos, entonces, son compatibles si son mismo tipo
            toReturn= t1.getName().equals(t2.getName());
        }else if (t1.isPrimitive && !t2.isPrimitive || !t1.isPrimitive && t2.isPrimitive) { //si son primitivos, entonces, son compatibles si son mismo tipo
            return false;
        }else{ //SI son de tipo clase, entonces son compatibles si T1 hereda de T2
            if (t2.nameType == "null"){

                return true;
            }
          Clase clase1 =  symbolTable.getClase(t1.getToken().getLexeme());
          Clase clase2 =  symbolTable.getClase(t2.getToken().getLexeme());

          //Chequeamos si clase1 es ANCESTRO de clase2
          toReturn =checkC1AncestroC2(clase1,clase2);
        }

        return toReturn;
    }

    private boolean checkC1AncestroC2(Clase clase1, Clase clase2) {
        if (clase1.classNameToken == clase2.classNameToken) { //si son la misma clase, son compatibles
            return true;
        }else
        if (clase2.heredaDe != null) {
            return checkC1AncestroC2(clase1,symbolTable.getClase(clase2.heredaDe.getLexeme()));

        } else {
            return false;
        }

    }

    protected void setIsPrimitive (boolean isPrimitive) {
        this.isPrimitive = isPrimitive;
    }

    public boolean getIsPrimitive() {
        return isPrimitive;
    }

    public Token getToken() {
        return token;
    }

    public String getName() {
        return token.getLexeme();
    }

    public boolean equals(Type type) {
        return type.token.getName().equals(this.token.getName());
    }

    public String toString() {

        String toReturn = "\n Type: " + token.getName() + " isPrimitive: " + isPrimitive + " nameType: " + nameType + " token: " + token.getLexeme();
        return toReturn;
    }
}