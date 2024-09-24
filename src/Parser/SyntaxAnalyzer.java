package Parser;

import Lexical.AnalizadorLexico;
import Lexical.LexicalException;
import Lexical.Token;

import java.io.IOException;

public class SyntaxAnalyzer {

    private AnalizadorLexico analizdorLexico;
    private Token tokenActual;


    public SyntaxAnalyzer(AnalizadorLexico lexicalAnalyzer) throws LexicalException, IOException {

        this.analizdorLexico = lexicalAnalyzer;
        this.tokenActual = analizdorLexico.getNextToken();

    }

    public void match(String nombreToken) throws SyntaxException, LexicalException, IOException {
        System.out.println("Entre a match con el nombre de token: "+ nombreToken);
        if (nombreToken.equals(tokenActual.getName())) {
            String oldTokenName = tokenActual.getName();
            tokenActual = analizdorLexico.getNextToken();
            System.out.println("Se hizo match con: " + oldTokenName + " | Token nuevo: " + tokenActual.getName());
        } else {
            System.out.println("Error en match");
            throw new SyntaxException(analizdorLexico.getNextToken().getRow(), nombreToken, tokenActual.getLexeme());
        }


    }

    public void inicial() throws LexicalException, SyntaxException, IOException {
        listaClases();
        match("EOF");
    }

    public void listaClases() throws LexicalException, SyntaxException, IOException {
        System.out.println("Entre a listasClases()");
        if (tokenActual.getName().equals("class")) {
            clase();
            listaClases();

        } else {
            //no hago nada por el epsilon
        }
    }

    public void clase() throws LexicalException, SyntaxException, IOException {
        System.out.println("Entre a clase()");
        match("class");
        match("idClase");
        herenciaOpcional();
        match("{");
        listaMiembros();
        match("}");
    }

    private void listaMiembros() throws LexicalException, SyntaxException, IOException {
        System.out.println("Entre a listaMiembros()");
        if (tokenActual.getName().equals("static") || tokenActual.getName().equals("void") || tokenActual.getName().equals("boolean") || tokenActual.getName().equals("char") || tokenActual.getName().equals("int") || tokenActual.getName().equals("idClase")|| tokenActual.getName().equals("public")) {
            miembro();
            listaMiembros();
        }else{
            //epsilon
        }
    }

    public void herenciaOpcional() throws LexicalException, SyntaxException, IOException {
        System.out.println("Entre a herenciaOpcional()");
        if (tokenActual.getName().equals("extends")) {
            match("extends");
            match("idClase");
        } else {
            //No hago nada por epsilon
        }
    }

    public void miembro() throws LexicalException, SyntaxException, IOException {
        System.out.println("Entre a miembro()");
        if (tokenActual.getName().equals("public")) {
            constructor();
        } else {
            estaticoOpcional();
            tipoMiembro();
            match("idMetVar");
            restoAtributoMetodo();
        }
    }

    private void restoAtributoMetodo() throws SyntaxException, LexicalException, IOException {
        if (tokenActual.getName().equals(";")){
            match(";");
        }
        else if (tokenActual.getName().equals("(")){
            argsFormales();
            bloque();
        }else {
            throw new SyntaxException(tokenActual.getRow(), "; , o (, ", tokenActual.getName());
        }
    }

    private void constructor() throws LexicalException, SyntaxException, IOException {
        System.out.println("Entre a constructor()");
        match("public");
        match("idClase");
        argsFormales();
        bloque();

    }

    private void bloque() throws LexicalException, SyntaxException, IOException {
        System.out.println("Entre a bloque()");
        match("{");
        listaSentencias();
        match("}");

    }

    private void listaSentencias() throws LexicalException, SyntaxException, IOException {
        System.out.println("Entre a listaSentencias()");
        if (tokenActual.getName().equals(";") || primerosAsignacionYLLamada(tokenActual.getName()) || tokenActual.getName().equals("var") || tokenActual.getName().equals("return") || tokenActual.getName().equals("break") || tokenActual.getName().equals("if") || tokenActual.getName().equals("while") || tokenActual.getName().equals("switch") || tokenActual.getName().equals("{")) {
            sentencia();
            listaSentencias();
        } else {
            //epsilon
        }
    }

    private void sentencia() throws LexicalException, SyntaxException, IOException {
        System.out.println("Entre a sentencia()");
        if (primerosAsignacionYLLamada(tokenActual.getName())) { //@TODO ver si en verdad da lo mismo llamar a asignacion o llamada (derivan exactamente lo mismo)
            expresion();
            match(";");
        } else if (tokenActual.getName().equals("var")) {
            varLocal();
            match(";");
        } else if (tokenActual.getName().equals("return")) {
            metReturn();
            match(";");

        } else if (tokenActual.getName().equals("break")) {
            metBreak();
            match(";");

        } else if (tokenActual.getName().equals("if")) {
            metIf();

        } else if (tokenActual.getName().equals("while")) {
            metWhile();
        } else if (tokenActual.getName().equals("switch")) {
            metSwitch();

        } else if (tokenActual.getName().equals("{")) {
            bloque();
        }
        else{match(";");}


    }

    private void metBreak() throws LexicalException, SyntaxException, IOException {
        System.out.println("Entre a break()");
        match("break");
    }

    private void metSwitch() throws LexicalException, SyntaxException, IOException {
        System.out.println("Entre a metSwitch()");

        match("switch");
        match("(");
        expresion();
        match(")");
        match("{");
        listaSentenciasSwitch();
        match("}");
    }

    private void listaSentenciasSwitch() throws SyntaxException, LexicalException, IOException {
        System.out.println("Entre a listaSentenciasSwitch()");

        if (tokenActual.getName().equals("case")) {
            match("case");
            literalPrimitivo();
            match(":");
            sentenciaOpcional();
        } else if (tokenActual.getName().equals("default")) {
            match("default");
            match(":");
            sentencia();
        } else {
            throw new SyntaxException(tokenActual.getRow(), "case or default", tokenActual.getName());
        }
    }

    private void sentenciaOpcional() throws LexicalException, SyntaxException, IOException {
        System.out.println("Entre a sentenciaOpcional()");

        if (tokenActual.getName().equals(";") || primerosAsignacionYLLamada(tokenActual.getName()) || tokenActual.getName().equals("var") || tokenActual.getName().equals("return") || tokenActual.getName().equals("break") || tokenActual.getName().equals("if") || tokenActual.getName().equals("while") || tokenActual.getName().equals("switch") || tokenActual.getName().equals("{")) {
            sentencia();
        } else {
            //Epsilon
        }
    }

    private void literalPrimitivo() throws LexicalException, SyntaxException, IOException {
        if (tokenActual.getName().equals("true")) { //TODO Si lo que me recomienda para factorizar tiene autobreak, es decir, entra a SOLO UN CASE.
            match("true");
        } else if (tokenActual.getName().equals("false")) {
            match("false");
        } else if (tokenActual.getName().equals("intLiteral")) {
            match("intLiteral");
        } else if (tokenActual.getName().equals("charLiteral")) {
            match("charLiteral");
        }
    }

    private void expresion() throws LexicalException, SyntaxException, IOException {
        if (tokenActual.getName().equals("+") || tokenActual.getName().equals("-") || tokenActual.getName().equals("!") || tokenActual.getName().equals("true") || tokenActual.getName().equals("false") || tokenActual.getName().equals("intLiteral") || tokenActual.getName().equals("charLiteral") || tokenActual.getName().equals("null") || tokenActual.getName().equals("stringLiteral") || tokenActual.getName().equals("this") || tokenActual.getName().equals("idMetVar") || tokenActual.getName().equals("new") || tokenActual.getName().equals("idClase") || tokenActual.getName().equals("(")) {
            expresionCompuesta();
            expresionAux();
        }
    }

    private void expresionAux() throws LexicalException, SyntaxException, IOException {
        if (tokenActual.getName().equals("=") || tokenActual.getName().equals("+=") || tokenActual.getName().equals("-=")) {
            operadorAsignacion();
            expresionCompuesta();
        } else {
            // Epsilon
        }
    }

    private void operadorAsignacion() throws LexicalException, SyntaxException, IOException {
        if (tokenActual.getName().equals("=")) {
            match("=");
        } else if (tokenActual.getName().equals("+=")) {
            match("+=");
        } else if (tokenActual.getName().equals("-=")) {
            match("-=");
        }
    }

    private void metWhile() throws LexicalException, SyntaxException, IOException {
        match("while");
        match("(");
        expresion();
        match(")");
        sentencia();
    }

    private void metIf() throws LexicalException, SyntaxException, IOException {
        match("if");
        match("(");
        expresion();
        match(")");
        sentencia();
        restoIf();
    }

    private void restoIf() throws LexicalException, SyntaxException, IOException {
        if (tokenActual.getName().equals("else")) {
            match("else");
            sentencia();
        } else {
            //epsilon
        }

    }

    private void metReturn() throws LexicalException, SyntaxException, IOException {
        match("return");
        expresionOpcional();
    }

    private void expresionOpcional() throws LexicalException, SyntaxException, IOException {
        if (tokenActual.getName().equals("+") || tokenActual.getName().equals("-") || tokenActual.getName().equals("!") || tokenActual.getName().equals("true") || tokenActual.getName().equals("false") || tokenActual.getName().equals("intLiteral") || tokenActual.getName().equals("charLiteral") || tokenActual.getName().equals("null") || tokenActual.getName().equals("string")) {
            expresion();
        } else {
            //epsilon
        }
    }

    private void varLocal() throws LexicalException, SyntaxException, IOException {
        match("var");
        match("idMetVar");
        match("=");
        expresionCompuesta();
    }

    private void expresionCompuesta() throws SyntaxException, LexicalException, IOException {
        if (tokenActual.getName().equals("+") || tokenActual.getName().equals("-") || tokenActual.getName().equals("!") || tokenActual.getName().equals("true") || tokenActual.getName().equals("false") || tokenActual.getName().equals("intLiteral") || tokenActual.getName().equals("charLiteral") || tokenActual.getName().equals("null") || tokenActual.getName().equals("stringLiteral") || tokenActual.getName().equals("this") || tokenActual.getName().equals("idMetVar") || tokenActual.getName().equals("new") || tokenActual.getName().equals("idClase") || tokenActual.getName().equals("(")) {
            expresionBasica();
            expresionCompuestaAUX();
        } else {
            throw new SyntaxException(tokenActual.getRow(), "operador", tokenActual.getName());
        }
    }

    private void expresionCompuestaAUX() throws LexicalException, SyntaxException, IOException {
        if (tokenActual.getName().equals("||") || tokenActual.getName().equals("&&") || tokenActual.getName().equals("==") ||tokenActual.getName().equals("!=") ||tokenActual.getName().equals("<")
                ||tokenActual.getName().equals(">") ||tokenActual.getName().equals(">=") ||tokenActual.getName().equals("<=") ||tokenActual.getName().equals("+")
                ||tokenActual.getName().equals("-") ||tokenActual.getName().equals("*") ||tokenActual.getName().equals("/") ||tokenActual.getName().equals("%")){
            operadorBinario();
            expresionBasica();
            expresionCompuestaAUX();
        }else{
            //EPSILON musk
        }
    }

    private void operadorBinario() throws LexicalException, SyntaxException, IOException {
        if (tokenActual.getName().equals("||")) {
            match("||");
        } else if (tokenActual.getName().equals("&&")) {
            match("&&");
        } else if (tokenActual.getName().equals("==")) {
            match("==");
        } else if (tokenActual.getName().equals("!=")) {
            match("!=");
        } else if (tokenActual.getName().equals("<")) {
            match("<");
        } else if (tokenActual.getName().equals(">")) {
            match(">");
        } else if (tokenActual.getName().equals(">=")) {
            match(">=");
        } else if (tokenActual.getName().equals("<=")) {
            match("<=");
        } else if (tokenActual.getName().equals("+")) {
            match("+");
        } else if (tokenActual.getName().equals("-")) {
            match("-");
        } else if (tokenActual.getName().equals("*")) {
            match("*");
        } else if (tokenActual.getName().equals("/")) {
            match("/");
        } else if (tokenActual.getName().equals("%")) {
            match("%");
        }
    }


    private void expresionBasica() throws SyntaxException, LexicalException, IOException {
        if (tokenActual.getName().equals("+") || tokenActual.getName().equals("-") || tokenActual.getName().equals("!")) {
            operadorUnario();
            operando();
        } else if (tokenActual.getName().equals("true") || tokenActual.getName().equals("false") || tokenActual.getName().equals("intLiteral") || tokenActual.getName().equals("charLiteral") || tokenActual.getName().equals("null") || tokenActual.getName().equals("stringLiteral") || tokenActual.getName().equals("this") || tokenActual.getName().equals("idMetVar") || tokenActual.getName().equals("new") || tokenActual.getName().equals("idClase") || tokenActual.getName().equals("(")) {
            operando();
        } else {
            throw new SyntaxException(tokenActual.getRow(), "operando", tokenActual.getName());
        }
    }

    private void operadorUnario() throws LexicalException, SyntaxException, IOException {
        if (tokenActual.getName().equals("+")){
            match("+");
        }else if (tokenActual.getName().equals("-")){
            match("-");
        }
        else if (tokenActual.getName().equals("!")){
            match("!");
        }
        else{
            throw new SyntaxException(tokenActual.getRow(), "+, -, !", tokenActual.getName());
        }
    }

    private void operando() throws SyntaxException, LexicalException, IOException {
        if (tokenActual.getName().equals("true") || tokenActual.getName().equals("false") || tokenActual.getName().equals("intLiteral") || tokenActual.getName().equals("charLiteral") || tokenActual.getName().equals("null") || tokenActual.getName().equals("stringLiteral")) {
            literal();
        } else if (tokenActual.getName().equals("this") || tokenActual.getName().equals("idMetVar") || tokenActual.getName().equals("new") || tokenActual.getName().equals("(") || tokenActual.getName().equals("idClase")) {
            acceso();
        } else {
            throw new SyntaxException(tokenActual.getRow(), "literal o acceso ", tokenActual.getName());
        }
    }

    private void acceso() throws SyntaxException, LexicalException, IOException {
        if (tokenActual.getName().equals("this") || tokenActual.getName().equals("idMetVar") || tokenActual.getName().equals("new") || tokenActual.getName().equals("(") || tokenActual.getName().equals("idClase")) {
            primario();
            encadenadoOpcional();
        } else {
            throw new SyntaxException(tokenActual.getRow(), "acceso", tokenActual.getName());
        }
    }

    private void encadenadoOpcional() throws LexicalException, SyntaxException, IOException {
        if (tokenActual.getName().equals(".")) {
            match(".");
            match("idMetVar");
            encadenadoOpcionalAUX();
        } else {
            //epsilon
        }
    }

    private void encadenadoOpcionalAUX() throws LexicalException, SyntaxException, IOException {
        if (tokenActual.getName().equals(".")) {
            encadenadoOpcional();
        } else if (tokenActual.getName().equals("(")) {
            argsActuales();
            encadenadoOpcional();
        }//TODO agregar else{//epsilong}
    }

    private void primario() throws LexicalException, SyntaxException, IOException {
        if (tokenActual.getName().equals("this")) {
            accesoThis();
        } else if (tokenActual.getName().equals("idMetVar")) {
            accesoVarMet();
        } else if (tokenActual.getName().equals("new")) {
            accesoConstructor();
        } else if (tokenActual.getName().equals("(")) {
            expresionParentizada();
        } else if (tokenActual.getName().equals("idClase")) {
            accesoMetodoEstatico();
        }
    }

    private void accesoMetodoEstatico() throws LexicalException, SyntaxException, IOException {
        match("idClase");
        match(".");
        match("idMetVar");
        argsActuales();
    }

    private void argsActuales() throws LexicalException, SyntaxException, IOException {
        match("(");
        listaExpsOpcional();
        match(")");
    }

    private void listaExpsOpcional() throws LexicalException, SyntaxException, IOException {
        if (tokenActual.getName().equals("+") || tokenActual.getName().equals("-") || tokenActual.getName().equals("!") || tokenActual.getName().equals("true") || tokenActual.getName().equals("false") || tokenActual.getName().equals("intLiteral") || tokenActual.getName().equals("charLiteral") || tokenActual.getName().equals("null") || tokenActual.getName().equals("stringLiteral") || tokenActual.getName().equals("this") || tokenActual.getName().equals("idMetVar") || tokenActual.getName().equals("new") || tokenActual.getName().equals("(") || tokenActual.getName().equals("idClase")) {
            listaExps();
        } else {
            //Epsilon
        }
    }

    private void listaExps() throws SyntaxException, LexicalException, IOException {
        if (tokenActual.getName().equals("+") || tokenActual.getName().equals("-") || tokenActual.getName().equals("!") || tokenActual.getName().equals("true") || tokenActual.getName().equals("false") || tokenActual.getName().equals("intLiteral") || tokenActual.getName().equals("charLiteral") || tokenActual.getName().equals("null") || tokenActual.getName().equals("stringLiteral") || tokenActual.getName().equals("this") || tokenActual.getName().equals("idMetVar") || tokenActual.getName().equals("new") || tokenActual.getName().equals("(") || tokenActual.getName().equals("idClase")) {
            expresion();
            listaExpsAUX();
        } else {
            throw new SyntaxException(tokenActual.getRow(), "expresion", tokenActual.getName());
        }
    }

    private void expresionParentizada() throws LexicalException, SyntaxException, IOException {
        match("(");
        expresion();
        match(")");
    }

    private void accesoConstructor() throws LexicalException, SyntaxException, IOException {
        match("new");
        match("idClase");
        argsActuales();
    }

    private void accesoVarMet() throws LexicalException, SyntaxException, IOException {
        match("idMetVar");
        accesoVarMetAUX();
    }

    private void accesoVarMetAUX() throws LexicalException, SyntaxException, IOException {
        if (tokenActual.getName().equals("(")) {
            argsActuales();
        } else {
            //no hago nada, epsilon
        }
    }

    private void accesoThis() throws LexicalException, SyntaxException, IOException {
        match("this");
    }

    private void literal() throws LexicalException, SyntaxException, IOException {
        if (tokenActual.getName().equals("true") || tokenActual.getName().equals("false") || tokenActual.getName().equals("intLiteral") || tokenActual.getName().equals("charLiteral")) {
            literalPrimitivo();
        } else if (tokenActual.getName().equals("null") || tokenActual.getName().equals("stringLiteral")) {
            literalObjeto();
        } else {
            throw new SyntaxException(tokenActual.getRow(), "literal", tokenActual.getName());
        }
    }

    private void literalObjeto() throws SyntaxException, LexicalException, IOException {
        if (tokenActual.getName().equals("null")) {
            match("null");
        } else if (tokenActual.getName().equals("stringLiteral")) {
            match("stringLiteral");
        } else {
            throw new SyntaxException(tokenActual.getRow(), "literalfi", tokenActual.getName());
        }
    }


    private boolean primerosAsignacionYLLamada(String nombreTokenActual) {
        return nombreTokenActual.equals("+") || nombreTokenActual.equals("-") ||
                nombreTokenActual.equals("!") || nombreTokenActual.equals("true") ||
                nombreTokenActual.equals("false") || nombreTokenActual.equals("intLiteral") || nombreTokenActual.equals("charLiteral") || nombreTokenActual.equals("null") || nombreTokenActual.equals("stringLiteral") || nombreTokenActual.equals("this") || nombreTokenActual.equals("idMetVar") || nombreTokenActual.equals("new") || nombreTokenActual.equals("(") || nombreTokenActual.equals("idClase");
    }

    private void argsFormales() throws LexicalException, SyntaxException, IOException {
        match("(");
        listaArgsFormalesOpcional();
        match(")");
    }

    private void listaArgsFormalesOpcional() throws LexicalException, SyntaxException, IOException {
        if (tokenActual.getName().equals("boolean") || tokenActual.getName().equals("char") || tokenActual.getName().equals("int") || tokenActual.getName().equals("idClase")) {
            listaArgsFormales();
        } else {
            //epsilon
        }
    }

    private void listaArgsFormales() throws LexicalException, SyntaxException, IOException {
        if (tokenActual.getName().equals("boolean") || tokenActual.getName().equals("char") || tokenActual.getName().equals("int") || tokenActual.getName().equals("idClase")) {
            argFormal();
            restoListaArgFormal();
        }
    }

    private void restoListaArgFormal() throws LexicalException, SyntaxException, IOException {
       if (tokenActual.getName().equals(",")){
        match(",");
        listaArgsFormales();}
       else {
           //epsilon
       }
    }

    private void argFormal() throws LexicalException, SyntaxException, IOException {
        if (tokenActual.getName().equals("boolean") || tokenActual.getName().equals("char") || tokenActual.getName().equals("int") || tokenActual.getName().equals("idClase")) {
            tipo();
            match("idMetVar");
        }
    }

    private void tipoMiembro() throws LexicalException, SyntaxException, IOException {
        if (tokenActual.getName().equals("boolean") || tokenActual.getName().equals("char") || tokenActual.getName().equals("int") || tokenActual.getName().equals("idClase")) {
            tipo();
        } else {
            match("void");
        } //TODO: else{throw new SyntaxException}
    }

    private void tipoPrimitivo() throws SyntaxException, LexicalException, IOException {
        if (tokenActual.getName().equals("boolean"))
            match("boolean");
        else if (tokenActual.getName().equals("char")) {
            match("char");
        } else if (tokenActual.getName().equals("int")) {
            match("int");
        } else {
            throw new SyntaxException(tokenActual.getRow(), "char, int o boolean", tokenActual.getName());
        }
    }


    private void estaticoOpcional() throws LexicalException, SyntaxException, IOException {
        if (tokenActual.getName().equals("static")) {
            match("static");
        } else {
            //epsilongo
        }
    }


    private void listaExpsAUX() throws LexicalException, SyntaxException, IOException {
        if (tokenActual.getName().equals(",")) {
            match(",");
            listaExps();
        } else {
            //Epsilon
        }
    }



    private void tipo() throws SyntaxException, LexicalException, IOException {
        if (tokenActual.getName().equals("boolean") || tokenActual.getName().equals("char") || tokenActual.getName().equals("int")) {
            tipoPrimitivo();
        } else if (tokenActual.getName().equals("idClase")) {
            match("idClase");
        } else {
            throw new SyntaxException(tokenActual.getRow(), "tipo", tokenActual.getName());
        }
    }
}
