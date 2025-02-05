package Semantic_1.Type;

import Lexical.Token;
import Semantic_1.SymbolTable.SymbolTable;

public class PrimitiveType extends Type {

    public PrimitiveType(Token token, SymbolTable ts) {
        super(token, ts);
        setIsPrimitive(true);
    }

    public boolean isPrimitive() {
        return true;
    }
}
