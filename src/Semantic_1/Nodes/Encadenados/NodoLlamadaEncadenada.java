package Semantic_1.Nodes.Encadenados;

import Lexical.Token;
import Semantic_1.Clase;
import Semantic_1.Method;
import Semantic_1.Nodes.Acceso.NodoAccesoVariable;
import Semantic_1.Nodes.NodoExpresion;
import Semantic_1.Nodes.Sentencia.NodoBloque;
import Semantic_1.SymbolTable.SymbolTable;
import Semantic_1.Type.Type;

import java.util.ArrayList;

import Exception.SemanticException;

public class NodoLlamadaEncadenada extends NodoAccesoVariable implements Encadenado {


    public ArrayList<NodoExpresion> parametros;
    public Encadenado encadenado;

    public Type tipo;
    public Type tipoAnteriorEncadenado;
    public boolean modoEscritura = false;
    public SymbolTable TS;

    public NodoLlamadaEncadenada(Token nombreMetVar, NodoBloque bloqueActual, SymbolTable ts, Clase contentClass, Method contentMethod) {
        super(nombreMetVar, bloqueActual, contentClass, contentMethod);
        this.TS = ts;

        isMethod = true;
    }

    @Override
    public Type chequear(Type t) throws SemanticException {

        //x.metodo(1,2); tengo que chequear que x tenga en su methodTable el metodo metodo
        this.tipoAnteriorEncadenado = t;
        if (t.isPrimitive || t.getName().equals("void")) {
            throw new SemanticException(getToken(), "No se puede llamar a un metodo de un tipo primitivo");
        }
        //Si no existe el metodo

        else if (!TS.getClase(t.nameType).methodTable.containsKey(getToken().getLexeme())) {
            throw new SemanticException(getToken(), "No se encuentra el metodo " + getToken().getLexeme() + " en la clase " + contentClass.classNameToken.getLexeme());
        } //Si los argumentos actuales != de argumentos formales
        else if (TS.getClase(t.nameType).methodTable.get(getToken().getLexeme()).getParametros().size() != parametros.size()) {
            throw new SemanticException(getToken(), "Cantidad los argumentos actuales son diferentes de los argumentos formales de la llamada al metodo " + getToken().getLexeme());
        }
        for (NodoExpresion e : parametros) {
            e.checkExpresion(TS);
        }
        //TS.getClase(t.nameType).methodTable.get(getToken().getLexeme()).getParametros().get(0).getType().nameType.equals(parametros.get(i).getType().nametype)
        for (int i = 0; i < parametros.size(); i++) {
            if (!TS.getClase(t.nameType).methodTable.get(getToken().getLexeme()).getParametros().get(i).getType().nameType.equals(parametros.get(i).getType().nameType)) {
                throw new SemanticException(getToken(), "Los argumentos actuales son diferentes a los argumentos formales de la llamada al metodo " + getToken().getLexeme());
            }
        }

        if (encadenado == null) {
            type = TS.getClase(t.nameType).methodTable.get(getToken().getLexeme()).type;
        } else {
            type = encadenado.chequear(TS.getClase(t.nameType).methodTable.get(getToken().getLexeme()).type);
        }
        return type;
    }

    @Override
    public void setEscrituraTrue() {
        this.modoEscritura = true;
    }

    @Override
    public void setEscrituraFalse() {
        this.modoEscritura = false;
    }


    public void addParameter(NodoExpresion p) {
        this.parametros.add(p);
    }

    @Override
    public Type checkExpresion(SymbolTable symbolTable) throws SemanticException {
        return null;
    }

    public void setEncadenado(Encadenado e) {
        this.encadenado = e;
    }

    public void generarCodigo(SymbolTable symbolTable, Type tipoAnterior) {

        //si NO es estatico, entonces:

        if (symbolTable.getClassTable().get(tipoAnteriorEncadenado.nameType).methodTable.get(this.name.getLexeme()).isStatic == null) {

            if (!symbolTable.getClassTable().get(tipoAnteriorEncadenado.nameType).methodTable.get(this.name.getLexeme()).type.nameType.equals("void")) { //si el metodo tiene retorno, guardamos espacio para el retorno
                symbolTable.writer.write("RMEM 1"); //guardamos espacio para el retorno del metodo al que llamaremos
                symbolTable.writer.write("SWAP");
            }

            for (NodoExpresion e : this.parametros) {
                e.generarCodigo(symbolTable);
                symbolTable.writer.write("SWAP");
            }
            symbolTable.writer.write("DUP");
            symbolTable.writer.write("LOADREF 0 ; Cargamos la VT en el stack");
            //System.out.println( symbolTable.getClassTable().get(type.nameType).methodTable.get(this.name.getLexeme()).offSetMetodo);
            symbolTable.writer.write("LOADREF " + symbolTable.getClassTable().get(tipoAnteriorEncadenado.nameType).methodTable.get(this.name.getLexeme()).offSetMetodo); // symbolTable.getClassTable().get(tipoAnteriorEncadenado.nameType).methodTable.get(this.name.getLexeme()).offSetMethod
            symbolTable.writer.write("CALL ");

        } else {//Si el metodo es estatico
            symbolTable.writer.write("POP ;eliminamos el this, ya que es estatico");
            if (!symbolTable.getClassTable().get(tipoAnteriorEncadenado.nameType).methodTable.get(this.name.getLexeme()).type.nameType.equals("void")) { //si el metodo tiene retorno, guardamos espacio para el retorno
                symbolTable.writer.write("RMEM 1 ; guardamos espacio para el retorno del metodo estatico + " + this.name.getLexeme()); //guardamos espacio para el retorno del metodo al que llamaremos
            }

            for (NodoExpresion e : this.parametros) {
                e.generarCodigo(symbolTable);
            }
            //Pusheamos la etiqueta del metodo y la llamamos
            symbolTable.writer.write("PUSH " + symbolTable.getClassTable().get(tipoAnteriorEncadenado.nameType).methodTable.get(this.name.getLexeme()).etiquetaMetodo);
            symbolTable.writer.write("CALL");
        }
        if (encadenado != null) {
            encadenado.generarCodigo(symbolTable, this.type);
        }
    }

    public void setArgsActuales(ArrayList<NodoExpresion> listaArgs) {
        this.parametros = listaArgs;
    }

    @Override
    public boolean esAsignable() {
        return encadenado != null && encadenado.esAsignable();
    }
}
