package Semantic_1.Nodes;

import Exception.SemanticException;
import Lexical.Token;
import Semantic_1.Nodes.Sentencia.NodoBloque;
import Semantic_1.SymbolTable.SymbolTable;
import Semantic_1.Type.Type;

public class NodoLiteral extends NodoOperando {

    public Token valor;
    public boolean alredyChecked = false;

    public NodoLiteral(Token valor, NodoBloque currentBlock, SymbolTable symbolTable) {
        this.valor = valor;
        type = new Type(this.valor, symbolTable);
        this.currentBloque = currentBlock;
    }

    @Override
    public void setParentBlock(NodoBloque nodeBlock) {

    }

    // El tipo de un literal entero es int.
    //El tipo de un literal caracter es char.
    // El tipo de los literales true y false es boolean.
    // El tipo de un literal string es C(String).
    //El tipo del literal null es un tipo especial que conforma con cualquier tipo clase

    public void check(SymbolTable symbolTable) throws SemanticException {

    }

    public Type checkExpresion(SymbolTable symbolTable) throws SemanticException {
        return type;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Token getToken() {
        return type.getToken();
    }

    @Override
    public void generarCodigo(SymbolTable symbolTable) {
        switch (type.nameType) {
            case "intLiteral":
                symbolTable.writer.write("PUSH " + type.getToken().getLexeme());
                break;
            case "charLiteral":
                symbolTable.writer.write("PUSH " + (int)this.getToken().getLexeme().charAt(1));
                break;
            case "boolean":
                if (type.getToken().getLexeme().equals("true"))
                    symbolTable.writer.write("PUSH 1 ; Push TRUE");
                else
                    symbolTable.writer.write("PUSH 0 ; Push FALSE");
                break;
            case "String":
                symbolTable.writer.write(".DATA");
                String label = "string@"+ this.getToken().getRow();
                symbolTable.writer.write(label+": DW "+valor.getLexeme()+", 0");
                symbolTable.writer.write(".CODE");
                symbolTable.writer.write("PUSH "+label+" ; Pushing string");
                break;
        }
    }

    @Override
    public boolean esAsignable() {
        return false;
    }
}
