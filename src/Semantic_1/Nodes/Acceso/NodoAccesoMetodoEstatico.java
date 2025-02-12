package Semantic_1.Nodes.Acceso;

import Lexical.Token;
import Semantic_1.Clase;
import Semantic_1.Method;
import Semantic_1.Nodes.NodoExpresion;
import Semantic_1.Nodes.Sentencia.NodoBloque;
import Semantic_1.Parameter;
import Semantic_1.SymbolTable.SymbolTable;

import java.util.ArrayList;
import Exception.SemanticException;
import Semantic_1.Type.Type;

public class NodoAccesoMetodoEstatico extends NodoAccesoVariable{

    public Token clase;

    public NodoAccesoMetodoEstatico(Token name, NodoBloque parentBlock, Token clase, Clase contentclass, Method contentMethod) {
        super(name, parentBlock, contentclass, contentMethod);
        this.clase=clase;
    }

   public void generarCodigo(SymbolTable symbolTable) {
        System.out.println("entre a generar de NodoAccesoMetodoEstatico");
        System.out.println("clase: "+clase.getLexeme());
        System.out.println("name: "+name.getLexeme());
        Method llamadaMethod = symbolTable.getClassTable().get(clase.getLexeme()).methodTable.get(name.getLexeme());

        for (NodoExpresion parameter : parameters) {
            parameter.generarCodigo(symbolTable);
        }
        symbolTable.writer.write("PUSH " + llamadaMethod.etiquetaMetodo);
        symbolTable.writer.write("CALL");


    }

    public void check(SymbolTable symbolTable) throws SemanticException {
        boolean parametroExiste = false;
        //check if the class exist in the symboltable
        if (symbolTable.checkClassExistence(clase.getLexeme())) { //chequeamos si la clase existe
            if (symbolTable.getClassTable().get(clase.getLexeme()).methodTable.get(name.getLexeme()) != null) { //chequeamos si el metodo existe

                if (symbolTable.getClassTable().get(clase.getLexeme()).methodTable.get(name.getLexeme()).isStatic != null) { //chequeamos que sea estatico
                    type = symbolTable.getClassTable().get(clase.getLexeme()).methodTable.get(name.getLexeme()).type; //el tipo será el del retorno del metodo
                }else{
                    throw new SemanticException(name, "Metodo no estatico");
                }
            }else{
                throw new SemanticException(name, "Metodo ESTATICOno visible/encontrado");
            }
            //cheequeamos si coinciden argumentos actuales con formales
            if (symbolTable.getClassTable().get(clase.getLexeme()).methodTable.get(name.getLexeme()).getParametros().size() != parameters.size()) {
                throw new SemanticException(name, "Cantidad de parametros incorrecta");
            }else{
                for (NodoExpresion parameter: parameters) {
                    parameter.checkExpresion(symbolTable);
                }
                for ( Parameter p : symbolTable.getClassTable().get(clase.getLexeme()).methodTable.get(name.getLexeme()).getParametros()) {
                    int i=0;
                    if (!p.getType().nameType.equals(parameters.get(i).getType().nameType)) {
                        throw new SemanticException(name, "Tipo de parametro incorrecto");
                    }
                    i++;
                }
            }

        }else {
            throw new SemanticException(name, "Clase no visible/encontrada");
        }


    }

    public Type checkExpresion(SymbolTable symbolTable) throws SemanticException {
        check(symbolTable);
        return type;
    }

    public void setArgsActuales(ArrayList<NodoExpresion> argsActualesList) {
        this.parameters = argsActualesList;

    }
}
