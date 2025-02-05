package Semantic_1.Nodes.Acceso;

import Lexical.Token;
import Semantic_1.Clase;
import Semantic_1.Method;
import Semantic_1.Nodes.NodoExpresion;
import Semantic_1.Nodes.Sentencia.NodoBloque;
import Semantic_1.SymbolTable.SymbolTable;
import Exception.SemanticException;
import Semantic_1.Type.Type;

import java.util.ArrayList;

public class NodoAccesoConstructor extends NodoAccesoVariable{


    public Token idClaseConstructor;


    public NodoAccesoConstructor(Token tokNew, NodoBloque currentBlock, Clase containterClass, Method containerMethod, Token idClase) {
         super(tokNew, currentBlock, containterClass, containerMethod);
         this.idClaseConstructor = idClase;
         isMethod=true;



    }

    @Override
    public void check(SymbolTable symbolTable) throws SemanticException {
        if (encadenado != null) {
            throw new SemanticException(name, "No se puede acceder a un constructor con encadenamiento");
        }
            for (NodoExpresion arg : parameters) {
                arg.checkExpresion(symbolTable);
            }
            System.out.println("Constructor: " + this.idClaseConstructor.getLexeme());
            //chequear si la cantidad de argumentos formales es igual a la cantidad de argumentos actuales
               //If de arriba deberia ser: si parameters.size (((args actuales))) != symbolTable.getClase(this.idClaseConstructor.getLexeme()).constructorTable.get(this.getToken().getName()).getParametros().size
              //Si la clase symbolTable.getClase(this.idClaseConstructor.getLexeme()) no tiene constructores, entonces, tiene el default (tengo que chequear que los argumentos actuales sean 0 si o si)
                if (symbolTable.getClase(this.idClaseConstructor.getLexeme()).constructorTable.isEmpty()){
                    if (parameters.isEmpty()){
                        type = new Type(idClaseConstructor, symbolTable);
                    }
                    else{
                        throw new SemanticException(name, "Cantidad de argumentos incorrecta");
                    }
                }else if (parameters.size() != symbolTable.getClase(this.idClaseConstructor.getLexeme()).constructorTable.get(this.getToken().getName()).getParametros().size()){
                    throw new SemanticException(name, "Cantidad de argumentos incorrecta");
                }
                        //Como no es vacio, entonces, tengo que chequear si la cantidad de argumentos actuales es igual a la cantidad de argumentos formales)


            for (int i = 0; i < parameters.size(); i++) {
                if (!parameters.get(i).getType().nameType.equals(contentMethod.getParametros().get(i).getType().nameType)) {
                    throw new SemanticException(name, "Tipo de argumento incorrecto");
                }
            }
            //TODO: chequear que los argumentos sean del mismo tipo que los formales
       /* if (encadenado == null) {
            type = new Type(idClaseConstructor, symbolTable);

        }
        else {
            type = encadenado.chequear(type);
        }*/
        type = new Type(idClaseConstructor, symbolTable);


    }


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
        return idClaseConstructor;
    }

    public void generarCodigo(SymbolTable symbolTable) {
        symbolTable.writer.write("RMEM 1  ; Reservamos memoria para el resultado del malloc (la referencia al nuevo CIR de"+this.idClaseConstructor.getLexeme()+")");
        symbolTable.writer.write("PUSH "+(symbolTable.getClassTable().get(idClaseConstructor.getLexeme()).attributeTable.size()+1)+"  ;  Apilo la cantidad de var de instancia del CIR de "+this.idClaseConstructor.getLexeme()+" +1 por VT");
        symbolTable.writer.write("PUSH simple_malloc  ; La dirección de la rutina para alojar memoria en el heap");
        symbolTable.writer.write("CALL  ; Llamo a malloc");
        symbolTable.writer.write("DUP  ; Para no perder la referencia al nuevo CIR");
        symbolTable.writer.write("PUSH lblVT"+symbolTable.getClassTable().get(idClaseConstructor.getLexeme()).classNameToken.getLexeme()+"  ; Apilamos la dirección del comienzo de la VT de la clase "+this.idClaseConstructor.getLexeme()+"");
        symbolTable.writer.write("STOREREF 0  ; Guardamos la Referencia a la VT en el CIR que creamos");
        symbolTable.writer.write("DUP");
        for (NodoExpresion parameter : parameters) {
            parameter.generarCodigo(symbolTable);
            symbolTable.writer.write("SWAP");
        }
        symbolTable.writer.write("PUSH lblMet"+this.idClaseConstructor.getLexeme()+"");
        symbolTable.writer.write("CALL  ; Llamo al constructor "+this.idClaseConstructor.getLexeme()+ "");

    }

    public void setArgsActuales(ArrayList<NodoExpresion> argsActuales) {
        this.parameters = argsActuales;
    }
}
