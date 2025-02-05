package Semantic_1.Nodes.Sentencia;
import Exception.SemanticException;
import Lexical.Token;
import Semantic_1.Attribute;
import Semantic_1.Clase;
import Semantic_1.Method;
import Semantic_1.Nodes.Node;
import Semantic_1.Parameter;
import Semantic_1.SymbolTable.SymbolTable;
import Semantic_1.Type.Type;

import java.util.ArrayList;

public class NodoBloque implements NodoSentencia {

    public Token initialToken;
    public Token endToken;
    public int flagBreak;
    public ArrayList<NodoSentencia> sentences = new ArrayList<>();
    public ArrayList<Parameter> methodParameters = new ArrayList<>();
    public ArrayList<Attribute> localVariables = new ArrayList<>();

    public NodoBloque parentBlock;

    public Clase currentClass;
    public Method currentMethod;


    public NodoBloque(Token initialToken, Clase currentClass, Method currentMethod, NodoBloque parentBlock, ArrayList<Parameter> methodParameters) {
        this.initialToken = initialToken;
        this.currentClass = currentClass;
        this.currentMethod = currentMethod;
        this.parentBlock = parentBlock;
        this.methodParameters = methodParameters;
    }

    public void addSentence(NodoSentencia sentence){
        sentence.setParentBlock(this);
        sentences.add(sentence);
    }

    public NodoBloque getParentBlock() {
        return parentBlock;
    }
    @Override
    public void setParentBlock(NodoBloque nodeBlock) {
        this.parentBlock = nodeBlock;
    }

    public int cantVarLocales(){
        return localVariables.size();
    }
    @Override
    public void check(SymbolTable symbolTable) throws SemanticException {
        //Una sentencia de bloque es correcta si y s´olo si todas sus sub-sentencias son correctas. Cuando se
        //comienza el control de correctitud de un bloque es conveniente indicar que es el bloque actual de
        //an´alisis.
        symbolTable.currentBlock= this;
        if (this.parentBlock != null)
            this.localVariables.addAll(parentBlock.localVariables);

        for (NodoSentencia sentence: sentences) {
            sentence.check(symbolTable);
        }

        symbolTable.currentBlock= this.parentBlock;

    }

    @Override
    public Type getType() {
        return null;
    }

    @Override
    public Token getToken() {
        return null;
    }

    public void generarCodigo(SymbolTable symbolTable) {
        String toAdd= "";
        for (NodoSentencia sentence: sentences) {
            sentence.generarCodigo(symbolTable);
        }
        if (this.parentBlock!=null){
            toAdd+= "FMEM " + String.valueOf(this.cantVarLocales()- this.parentBlock.cantVarLocales()) + "\n";

        }else{
            toAdd+= "FMEM " + String.valueOf(this.cantVarLocales()) + "\n";
        }

        symbolTable.writer.write(toAdd);

    }

   /* public String toString(){
        String toReturn = "";
        for (NodoSentencia sentence: sentences){
            toReturn += sentence.toString();
        }
        return toReturn;
    }*/

}
