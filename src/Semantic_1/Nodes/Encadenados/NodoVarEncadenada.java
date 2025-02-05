package Semantic_1.Nodes.Encadenados;

import Lexical.Token;
import Semantic_1.Attribute;
import Semantic_1.Clase;
import Semantic_1.Method;
import Semantic_1.Parameter;
import Semantic_1.SymbolTable.SymbolTable;
import Semantic_1.Type.Type;
import Exception.SemanticException;

public class NodoVarEncadenada implements Encadenado {

    public Encadenado encadenado;

    public Token nombreVariable;

    public SymbolTable symbolTable;

    public Attribute atributoEnTS;
    public Type tipoAnteriorEncadenado;

    public boolean modoEscritura = false;
    public boolean isMethod = false;
    public Method contentMethod;


    public NodoVarEncadenada(Token nombreVariable, SymbolTable ts) {
        this.nombreVariable = nombreVariable;
        this.symbolTable = ts;
    }

    @Override
    public Type chequear(Type t) throws SemanticException {
        this.tipoAnteriorEncadenado = t;

        if (!t.isPrimitive && symbolTable.getClase(t.getToken()
                .getLexeme()).attributeTable.containsKey(this.nombreVariable.getLexeme())) {
            this.atributoEnTS = symbolTable.getClase(t.getToken().getLexeme()).attributeTable.get(this.nombreVariable.getLexeme());
        } else {
            throw new SemanticException(this.nombreVariable, "Atributo no encontrado o tipo primitivo");
        }

        if (encadenado != null) {
            return encadenado.chequear(this.atributoEnTS.getType());
        } else {
            return this.atributoEnTS.getType();
        }
    }

    @Override
    public void setEscrituraTrue() {
        this.modoEscritura = true;
    }

    @Override
    public void setEscrituraFalse() {
        this.modoEscritura = false;
    }

    public boolean esAsignable() {

        return encadenado == null || encadenado.esAsignable();

    }

    @Override
    public void generarCodigo(SymbolTable symbolTable, Type tipoAnterior) {
        Clase contentClass = symbolTable.getClase(tipoAnterior.getToken().getLexeme());
        String name = this.nombreVariable.getLexeme();
        if (this.encadenado != null) {
            this.modoEscritura = false;
            encadenado.setEscrituraTrue();
        }
        Attribute variable = contentClass.attributeTable.get(name);
        if (variable == null) {
            //encontrar variable en variables locales haciendo for each
            for (Attribute attribute : contentMethod.bloquePrincipal.localVariables) {
                if (attribute.getName().equals(name)) {
                    variable = attribute;
                    break;
                }
            }
        }
        if (variable != null) { // Si la variable de acceso es una varLocal
            if (variable.esVarLocal) {

                if (this.modoEscritura) {
                    symbolTable.writer.write("STORE " + variable.getOffSet());
                } else
                    symbolTable.writer.write("LOAD " + variable.getOffSet());

            } else if (contentClass.attributeTable.get(name) != null) //si es un atributo
            {
                if (this.modoEscritura) {
                    //LOAD 3 (para obtener referencia al this) (y luego obtener referencia al atributo)
                    //Swap, ya que el STOREREF necesita el valor ultimo y el lugar donde se almacenará arriba
                    //STOREREF ATRIBUTO.GETOFFSET
                    symbolTable.writer.write("LOAD 3");
                    symbolTable.writer.write("SWAP");
                    symbolTable.writer.write("STOREREF " + variable.offSet);
                } else { // Si es modo lectura, solamente cargamos el atributo
                    symbolTable.writer.write("LOAD 3");
                    symbolTable.writer.write("LOADREF " + variable.offSet);
                }
            }


        } else {//si es parametro
            Parameter parametroAcceso = null;
            for (Parameter parameter : contentMethod.getParametros()) {
                if (parameter.getLexeme().equals(name)) {
                    parametroAcceso = parameter;
                    break;
                }
            }
            int offSetParametro = parametroAcceso.offSet;
            if (this.modoEscritura) {
                symbolTable.writer.write("STORE " + offSetParametro);
            } else {
                symbolTable.writer.write("LOAD " + offSetParametro);
            }

        }


        if (encadenado != null) {
            encadenado.generarCodigo(symbolTable, this.atributoEnTS.getType());
        }
    }


    public void generarCodigo(SymbolTable symbolTable) {

    }

    public void setEncadenado(Encadenado e) {
        this.encadenado = e;
    }

}
