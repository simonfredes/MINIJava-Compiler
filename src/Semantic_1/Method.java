package Semantic_1;

import Lexical.Token;
import Semantic_1.Nodes.Sentencia.NodoBloque;
import Semantic_1.SymbolTable.SymbolTable;
import Semantic_1.Type.Type;
import Exception.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Method {

    //private HashMap<String,Attribute> attributeTable;
    public Token isStatic;
    public String etiquetaMetodo;
    public boolean isChecked;
    public Token methodNameToken;
    public Clase containerClass;
    public ArrayList<Attribute> consolidatedAttributes;

    public Type type;

    public int getOffSetMetodo() {
        return offSetMetodo;
    }

    public int offSetMetodo;
    public boolean esMetodoHeredado=true;
    private ArrayList<Parameter> parametros;

    public NodoBloque currentBlock;

    public NodoBloque bloquePrincipal;


    public Method(Token methodNameToken, Type tipo, Clase containerClass, Token isStatic) {
        this.methodNameToken = methodNameToken;
        this.containerClass = containerClass;
        //  attributeTable = new HashMap<>();
        this.isStatic = isStatic;
        consolidatedAttributes = new ArrayList<>();
        parametros = new ArrayList<>();
        type = tipo;

    }

    public void insertarParametro(Parameter p) throws SemanticException {
        if (checkDuplicatedParameterName(p))
            throw new SemanticException(p.getName(), "Parametro duplicado");

        this.parametros.add(p);
    }

    public void check() throws SemanticException {

        if (!this.type.getIsPrimitive() && !this.type.token.getLexeme().equals("void")){
            boolean existClass=  this.containerClass.getSymbolTable().checkClassExistence(this.type.token.getLexeme());
            if (!existClass)
                throw new SemanticException(this.type.token, "La clase del tipo de objeto retorno no existe");
        }
        //Check metodo sobrecargado
        SymbolTable tablaSimbolos = this.containerClass.getSymbolTable();
        Method metodoPadre = null;
        //Si el metodo esta duplicado, chequeamos que tenga misma cantidad de parametros, mismo tipo de parametro y mismo tipo de retorno.
        if (tablaSimbolos.getClase(this.containerClass.heredaDe.getLexeme()).methodTable.containsKey(this.methodNameToken.getLexeme())) {

            metodoPadre = tablaSimbolos.getClase(this.containerClass.heredaDe.getLexeme()).methodTable.get(this.methodNameToken.getLexeme());

            if (metodoPadre.getParametros().size() != this.parametros.size()) {
                throw new SemanticException(this.methodNameToken, "Metodo de clase padre y metodo de clase hija poseen mismo nombre pero la cantidad de metodos difiere, ");
            }
            if (! metodoPadre.type.token.getLexeme().equals(this.type.token.getLexeme())) {
                throw new SemanticException(this.methodNameToken, "Metodo de clase padre y metodo de clase hija poseen mismo nombre pero el tipo de retorno difiere, ");
            }
        }




        for (Parameter p : this.parametros) {
            if (!p.getType().getIsPrimitive() && !p.getType().token.getLexeme().equals("void") ) {

                boolean existClass = this.containerClass.getSymbolTable().checkClassExistence(p.getType().token.getLexeme());
                if (!existClass)
                    throw new SemanticException(p.getType().token, "La clase del tipo de objeto de parametro no existe");
            }
        }

    }
    public void consolidar() {
        //Check when method return object of class C, class C exist.

    }

    public boolean checkDuplicatedParameterName(Parameter p) {
        boolean toReturn = false;
        for (Parameter parameter : this.parametros) {
            if (parameter.getName().getLexeme().equals(p.getName().getLexeme())) {
                toReturn = true;
            }
        }

        return toReturn;
    }

    public ArrayList<Parameter> getParametros() {
        return this.parametros;
    }

    public void checkSemantico(SymbolTable symbolTable) throws SemanticException {
        if (!isChecked) {
            bloquePrincipal.check(symbolTable);
        }
        this.etiquetaMetodo= "lblMet"+this.methodNameToken.getLexeme() + "@" +this.containerClass.classNameToken.getLexeme();
        this.isChecked=true;

        setOffsetParametros();
    }

    private void setOffsetParametros() {
        int offSet = parametros.size() + (isStatic == null ? 3 : 2);
        for (int i = 0; i < this.parametros.size(); i++) {
            parametros.get(i).offSet= offSet-i;
        }
    }


    public void generarCodigo(SymbolTable symbolTable) {
        String textoMetodo= "";
        textoMetodo+=this.etiquetaMetodo +": LOADFP \n";
        textoMetodo += "LOADSP \n";
        textoMetodo += "STOREFP" + "\n";


        symbolTable.writer.write(textoMetodo);
        this.bloquePrincipal.generarCodigo(symbolTable);




        textoMetodo= "STOREFP \n";
        textoMetodo+= "RET " + String.valueOf(parametros.size() + (isStatic==null ? 1 : 0)) + "\n";
        symbolTable.writer.write(textoMetodo);

    }


}
