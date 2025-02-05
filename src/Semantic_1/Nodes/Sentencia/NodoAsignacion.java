package Semantic_1.Nodes.Sentencia;
import Exception.SemanticException;
import Lexical.Token;
import Semantic_1.Nodes.Node;
import Semantic_1.Nodes.NodoExpComp;
import Semantic_1.Nodes.NodoExpresion;
import Semantic_1.SymbolTable.SymbolTable;
import Semantic_1.Type.Type;

public class NodoAsignacion extends NodoExpComp {

    private NodoExpComp ladoIzquierdo;
    private NodoExpComp ladoDerecho;

    public Token operador;



    public NodoAsignacion(Token t, NodoExpComp ladoIzquierdo, NodoExpComp ladoDerecho) {
        this.operador = t;
        this.ladoIzquierdo = ladoIzquierdo;

        this.ladoDerecho = ladoDerecho;
    }

    @Override
    public void setParentBlock(NodoBloque nodeBlock) {
        this.parentBlock = nodeBlock;
    }


    public void check(SymbolTable symbolTable) throws SemanticException {

        Type tipoIzq = ladoIzquierdo.checkExpresion(symbolTable);
        Type tipoDer = ladoDerecho.checkExpresion(symbolTable);
        //TODO: (OBS) el tipo de la derecha DEBE SER MAS ESPECIFICO que el de la izquierda, else, error
       type= tipoIzq;
       if (tipoIzq.isPrimitive){
          if ( !tipoIzq.nameType.equals(tipoDer.nameType)){
              throw new SemanticException(getToken(), "El tipo de los lados de la asignacion no son compatibles");
          }
       }
       else if(!tipoIzq.sonCompatibles(tipoIzq, tipoDer)){
            throw new SemanticException(getToken(), "El tipo de los lados de la asignacion no son compatibles");
        }


        if (!ladoIzquierdo.esAsignable()){
            throw new SemanticException(getToken(), "El lado izquierdo de la asignacion no es asignable");
        }
    }

    @Override
    public Type checkExpresion(SymbolTable symbolTable) throws SemanticException {
        return null;
    }

    @Override
    public Type getType() {
        return type;
    } //TODO esto es el tipo del lado izq

    @Override
    public Token getToken() {
        return operador;
    }

    @Override
    public void generarCodigo(SymbolTable symbolTable) {
            ladoDerecho.generarCodigo(symbolTable);
            ladoIzquierdo.modoEscritura=true;
            ladoIzquierdo.generarCodigo(symbolTable);

    }

    @Override
    public boolean esAsignable() {
        return false;
    }
}
