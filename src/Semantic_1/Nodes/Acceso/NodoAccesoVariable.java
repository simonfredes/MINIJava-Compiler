package Semantic_1.Nodes.Acceso;

import Exception.SemanticException;
import Lexical.Token;
import Semantic_1.Attribute;
import Semantic_1.Clase;
import Semantic_1.Method;
import Semantic_1.Nodes.Encadenados.Encadenado;
import Semantic_1.Nodes.Encadenados.NodoLlamadaEncadenada;
import Semantic_1.Nodes.Node;
import Semantic_1.Nodes.NodoExpresion;
import Semantic_1.Nodes.Sentencia.NodoBloque;
import Semantic_1.Parameter;
import Semantic_1.SymbolTable.SymbolTable;
import Semantic_1.Type.Type;

import java.util.ArrayList;

public class NodoAccesoVariable extends NodoAcceso {

    public boolean isMethod;
    public ArrayList<NodoExpresion> parameters = new ArrayList<>();
    public Token name;
    private boolean alreadyChecked = false;

    public Clase contentClass;

    public Method contentMethod;


    public NodoAccesoVariable(Token name, NodoBloque parentBlock, Clase contentClass, Method contentMethod) {
        this.name = name;
        this.parentBlock = parentBlock;
        isMethod = false;
        this.contentClass = contentClass;
        this.contentMethod = contentMethod;
        //        ts.parentBlock = parentBlock;
    }


    @Override
    public void setParentBlock(NodoBloque nodeBlock) {
        this.parentBlock = nodeBlock;
    }

    public NodoBloque getParentBlock() {
        return parentBlock;
    }


    public void check(SymbolTable symbolTable) throws SemanticException {
        // TODO: recuperar el tipo si es que existe Y NO SEA VOID
        // SI TIENE ENCADENADO, CHEQUEAR ENCadenado pasandole el type this, y retornarlo.
        if (isMethod) {
            //caso1: x(1) x visible en la clase que contiene la expresion
            if (!contentClass.methodTable.containsKey(name.getLexeme())) {
                throw new SemanticException(name, "Metodo no visible/encontrado");
            }

            //x(a,b,c)
            // a, b y c tiene que ser una variable //TODO: ¿esto chequea que la variable a, por ejemplo, exista, sea parametro/var local o atributo? o sea, que sea visible
            for (NodoExpresion parameter : parameters) {
                parameter.checkExpresion(symbolTable);
            }

            //chequear que cantidad de parametros coincidan con la cantidad de parametros del metodo
            if (parameters.size() != contentClass.methodTable.get(name.getLexeme()).getParametros().size()) {
                throw new SemanticException(name, "Cantidad de parametros incorrecta");
            } else if (parameters.size() == contentClass.methodTable.get(name.getLexeme()).getParametros().size()) {
                for (int i = 0; i < parameters.size(); i++) {
                    //System.out.println("Parametro " + i + ": \n" + parameters.get(i).type.toString() + " " + contentClass.methodTable.get(name.getLexeme()).getParametros().get(i).getType().nameType);
                    if (!parameters.get(i).getType().nameType.equals(contentClass.methodTable.get(name.getLexeme()).getParametros().get(i).getType().nameType)) {

                        throw new SemanticException(name, "Tipo de parametro incorrecto");
                    }
                }
            }


            //recuperar el tipo del metodo y establecer mi tipo a ese
            if (contentClass.methodTable.containsKey(name.getLexeme())) {
                this.type = contentClass.methodTable.get(name.getLexeme()).type;
            }

        } else { //SI ES UNA VARIABLE
            boolean parametroExiste = false;
            // Si la variable es tipo clase, entonces tiene que estar en la tabla de simbolos

            //Caso 3: x es un parametro, vaariable local o atributo, else error. also, estableemos tipo
            for (Parameter parametro : contentMethod.getParametros()) {
                if (parametro.getLexeme().equals(name.getLexeme())) {
                    parametroExiste = true;
                    this.type = parametro.getType();
                    break;
                }
            }
            //aca buscamos en la lista de variables Locales
            for (Attribute variable : contentMethod.bloquePrincipal.localVariables) {
                if (variable.getName().equals(name.getLexeme())) {
                    parametroExiste = true;
                    variable.esVarLocal = true;
                    this.type = variable.getType();
                    break;
                }
            }
            if (!parametroExiste) { //buscamos en la tabla de atributos
                if (!contentClass.attributeTable.containsKey(name.getLexeme())) {
                    throw new SemanticException(name, "Variable no visible/encontrada");
                } else {
                    this.type = contentClass.attributeTable.get(name.getLexeme()).getType();
                }
            }
            //else{
            //  this.type = contentClass.attributeTable.get(name.getLexeme()).getType();
            //}


        }
        //si tiene encadenado, chequeamos el encadenado y el tipo será el resultante del encadenado
        if (encadenado != null && !type.nameType.equals("void")) {
            this.type = encadenado.chequear(this.type);
        }
        alreadyChecked = true;
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
        return name;
    }

    @Override
    public void generarCodigo(SymbolTable symbolTable) {
        if (this.encadenado != null) {
            this.modoEscritura = false;
            encadenado.setEscrituraTrue();
        }
        if (isMethod) {
            Method llamadaMethod = contentClass.methodTable.get(name.getLexeme());

            //Si es estatico
            if (llamadaMethod.isStatic != null) {
                System.out.println("Metodo estatico" + llamadaMethod.isStatic + "nombre: "+name.getLexeme());
                for (NodoExpresion parameter : parameters) {
                    parameter.generarCodigo(symbolTable);
                }
                symbolTable.writer.write("PUSH " + llamadaMethod.etiquetaMetodo);
                symbolTable.writer.write("CALL");
            }else{ //Si el metodo es dinamico
                System.out.println("Metodo dinamico");
                if (!llamadaMethod.type.nameType.equals("void")) {
                    symbolTable.writer.write("RMEM 1 ; guardamos espacio return NodoAccesoVariable"); //guardamos espacio para return
                }
                for (NodoExpresion parameter : parameters) {
                    parameter.generarCodigo(symbolTable);
                }//una vez tenemos los parametros, y el THIS en el orden que los necesitamos,
                //lo que falta es llamar al metodo
                symbolTable.writer.write("LOAD 3 ;cargo el THIS"); //cargo el this
                symbolTable.writer.write("DUP");
                symbolTable.writer.write("LOADREF 0 ; cargamos VT");

                symbolTable.writer.write("LOADREF " + llamadaMethod.offSetMetodo);
                symbolTable.writer.write("CALL");
            }

        } else {
            Attribute variable = contentClass.attributeTable.get(name.getLexeme());
            if (variable == null) {
                //encontrar variable en variables locales haciendo for each
                for (Attribute attribute : contentMethod.bloquePrincipal.localVariables) {
                    if (attribute.getName().equals(name.getLexeme())) {
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

                } else if (contentClass.attributeTable.get(name.getLexeme()) != null) //si es un atributo
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


            }else{//si es parametro
                    Parameter parametroAcceso= null;
                    for (Parameter parameter : contentMethod.getParametros()) {
                        if (parameter.getLexeme().equals(name.getLexeme())) {
                            parametroAcceso = parameter;
                            break;
                        }
                    }
                int offSetParametro = parametroAcceso.offSet;
                if (this.modoEscritura) {
                    symbolTable.writer.write("STORE " + offSetParametro);
                }else{
                    symbolTable.writer.write("LOAD " + offSetParametro);
                }

            }


        }
        if (encadenado != null) {
            encadenado.generarCodigo(symbolTable, this.type);
        }
    }

    @Override
    public boolean esAsignable() {
        return !isMethod || encadenado.esAsignable();
    }
}
