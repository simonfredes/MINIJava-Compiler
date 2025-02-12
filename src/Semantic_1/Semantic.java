package Semantic_1;

import Lexical.AnalizadorLexico;
import Lexical.LexicalException;
import Lexical.Token;
import Parser.SyntaxException;
import Semantic_1.Nodes.*;
import Semantic_1.Nodes.Acceso.*;
import Semantic_1.Nodes.Encadenados.Encadenado;
import Semantic_1.Nodes.Encadenados.NodoLlamadaEncadenada;
import Semantic_1.Nodes.Encadenados.NodoVarEncadenada;
import Semantic_1.Nodes.Sentencia.*;
import Semantic_1.SymbolTable.SymbolTable;
import Semantic_1.Type.PrimitiveType;
import Semantic_1.Type.Type;
import Exception.*;

import java.io.IOException;
import java.util.ArrayList;


public class Semantic {

    private AnalizadorLexico analizdorLexico;
    private Token tokenActual;

    private SymbolTable TS;


    public Semantic(AnalizadorLexico lexicalAnalyzer, String nombreArchivo5) throws LexicalException, IOException, Exception {

        this.TS = new SymbolTable(nombreArchivo5);
        this.analizdorLexico = lexicalAnalyzer;
        this.tokenActual = analizdorLexico.getNextToken();
    }

    public void match(String nombreToken) throws SyntaxException, LexicalException, IOException {
        if (nombreToken.equals(tokenActual.getName())) {
            String oldTokenName = tokenActual.getName();
            tokenActual = analizdorLexico.getNextToken();
        } else {
            throw new SyntaxException(tokenActual.getRow(), nombreToken, tokenActual.getLexeme());
        }
    }

    public void inicial() throws Exception {
        listaClases();
        match("EOF");
        TS.check();
        TS.consolidate();


        TS.checkSemantico();
//       NodoSentencia nodoATest= TS.getClase("Init").methodTable.get("main").bloquePrincipal.sentences.get(0);
      //  ((NodoSentenciaLlamada) nodoATest).llamada.check(TS);
       TS.generarCodigo();
        int a= 5;
    }

    public void listaClases() throws Exception {
        if (tokenActual.getName().equals("class")) {
            clase();
            listaClases();
        } else {
            //no hago nada por el epsilon
        }
    }

    public void clase() throws Exception, SyntaxException {
        match("class");
        Token nombre = tokenActual;
        match("idClase");
        Clase c = new Clase(nombre, TS);
        if (TS.checkDuplicatedClass(c)) {
            throw new SemanticException(c.classNameToken, "Clase duplicada");
        }
        if (c.classNameToken.getLexeme().equals("Object") || c.classNameToken.getLexeme().equals("String") || c.classNameToken.getLexeme().equals("System")) {
            throw new SemanticException(c.classNameToken, "No se puede crear una clase con el nombre reservado: " + c.classNameToken.getLexeme());
        }
        TS.setCurrentClass(c);

        Token nombreAncestro = herenciaOpcional();
        TS.getCurrentClass().addHeredaDe(nombreAncestro);
        match("{");
        listaMiembros();
        match("}");
    }

    private void listaMiembros() throws LexicalException, SyntaxException, IOException, SemanticException {
        if (tokenActual.getName().equals("static") || tokenActual.getName().equals("void") || tokenActual.getName().equals("boolean") || tokenActual.getName().equals("char") || tokenActual.getName().equals("int") || tokenActual.getName().equals("idClase") || tokenActual.getName().equals("public")) {
            miembro();
            listaMiembros();
        } else {
            //epsilon
        }
    }

    public Token herenciaOpcional() throws Exception, SyntaxException {
        if (tokenActual.getName().equals("extends")) {
            match("extends");
            Token nom = tokenActual;
            match("idClase");
            return nom;
        } else if (tokenActual.getName().equals("{")) {
            return new Token("idC", "Object", 0);
        } else {
            throw new Exception("error");
        }
    }

    public void miembro() throws LexicalException, SyntaxException, IOException, SemanticException {
        if (tokenActual.getName().equals("public")) {
            constructor();
        } else { //SI no es un constructor, entonces puede ser un tipo primitivo, idClase, void, es decir, un metodo o un atributo
            Token tokenEstatico = estaticoOpcional();
            Type tipo = tipoMiembro();
            Token tokenNombre = tokenActual;
            match("idMetVar");
            restoAtributoMetodo(tokenEstatico, tipo, tokenNombre);
        }
    }

    private void restoAtributoMetodo(Token tokenEstatico, Type tipo, Token tokenNombre) throws SyntaxException, LexicalException, IOException, SemanticException {

        if (tokenActual.getName().equals(";")) {
            match(";");
            Attribute a = new Attribute(tokenNombre, tipo, TS.getCurrentClass(), tokenEstatico);
            TS.insertarAtributo(tokenNombre.getLexeme(), a);
        } else if (tokenActual.getName().equals("(")) {
            Method m = new Method(tokenNombre, tipo, TS.getCurrentClass(), tokenEstatico);

            TS.setCurrentMethod(m);
            argsFormales();
            m.bloquePrincipal= bloque();
        } else {
            throw new SyntaxException(tokenActual.getRow(), "; , o (, ", tokenActual.getName());
        }
    }

    private void constructor() throws LexicalException, SyntaxException, IOException, SemanticException {
        match("public");
        Token nombre = tokenActual;  //guardo el idClase del constructor
        match("idClase");
        Constructor c = new Constructor(nombre, new Type(nombre, TS), TS.getCurrentClass());
        TS.setCurrentConstructor(c);
        argsFormales();
        c.bloquePrincipal=bloque();
    }

    private NodoBloque bloque() throws LexicalException, SyntaxException, IOException, SemanticException {
        NodoBloque bloqueActual;
        Token initialToken = tokenActual;
        match("{");
        if (TS.currentBlock == null) {
            bloqueActual = new NodoBloque(initialToken, TS.getCurrentClass(), TS.getCurrentMethod(), null, TS.getCurrentMethod().getParametros());
            TS.setCurrentBlock(bloqueActual);
        } else {
            bloqueActual = new NodoBloque(initialToken, TS.getCurrentClass(), TS.getCurrentMethod(), TS.currentBlock, TS.getCurrentMethod().getParametros());
            TS.setCurrentBlock(bloqueActual);//todo: ver por que queda desactualizado el bloque, ej { {1}  } el bloque de adentro queda como el bloque de afuera, es decir, misma row
        }

        listaSentencias();
        Token endToken = tokenActual;
        match("}");
        TS.setCurrentBlock(bloqueActual.parentBlock);
        bloqueActual.endToken = endToken;
        return bloqueActual;
    }

    private void listaSentencias() throws LexicalException, SyntaxException, IOException, SemanticException {
        if (tokenActual.getName().equals(";") || primerosAsignacionYLLamada(tokenActual.getName()) || tokenActual.getName().equals("var") || tokenActual.getName().equals("return") || tokenActual.getName().equals("break") || tokenActual.getName().equals("if") || tokenActual.getName().equals("while") || tokenActual.getName().equals("switch") || tokenActual.getName().equals("{")) {
            NodoSentencia s = sentencia();
            TS.currentBlock.addSentence(s);
            listaSentencias();
        } else {
            //epsilon
        }
    }

    private NodoSentencia sentencia() throws LexicalException, SyntaxException, IOException, SemanticException {
        NodoSentencia sentencia = null;

        if (primerosAsignacionYLLamada(tokenActual.getName())) {
            sentencia = accesoOAsignacionOOpUnaria();
            match(";");
        } else if (tokenActual.getName().equals("var") ) {
            sentencia = varLocal();
            match(";");
        } else if (tokenActual.getName().equals("return")) {
            sentencia = metReturn();
            match(";");
        } else if (tokenActual.getName().equals("break")) {
            sentencia = metBreak();
            match(";");
        } else if (tokenActual.getName().equals("if")) {
            sentencia = metIf();

        } else if (tokenActual.getName().equals("while")) {
            sentencia = metWhile();
        } else if (tokenActual.getName().equals("switch")) {
            sentencia = metSwitch();

        } else if (tokenActual.getName().equals("{")) {
            sentencia = bloque();
        } else {
            match(";");
            sentencia =new  NodoSentenciaVacia();
        }

        return sentencia;
    }

    private NodoSentencia accesoOAsignacionOOpUnaria() throws LexicalException, SyntaxException, IOException {
        NodoSentencia toReturn = null;
        NodoExpresion e = expresion();
        if(e instanceof NodoAsignacion){
           return new NodoSentenciaAsignacion((NodoAsignacion)e);
        }else {
            return new NodoSentenciaLlamada(e);
        }
    }


    private NodoBreak metBreak() throws LexicalException, SyntaxException, IOException {
        NodoBreak metBreak = new NodoBreak(tokenActual);

        match("break");

        return metBreak;
    }

    private NodoSwitch metSwitch() throws LexicalException, SyntaxException, IOException, SemanticException {
        NodoSwitch toReturn = null;
        NodoExpresion expresionSwitch; //TODO Esto es nodoSwitch
        ArrayList<NodoSentencia> sentenciasSwitch = new ArrayList<>();
        match("switch");
        match("(");
        expresionSwitch = expresion();
        match(")");

        match("{");

        listaSentenciasSwitch(sentenciasSwitch);
        match("}");
        System.out.println(sentenciasSwitch.toString());
        toReturn = new NodoSwitch(tokenActual, expresionSwitch, sentenciasSwitch, TS.currentBlock);
        return toReturn;
    }

    private void listaSentenciasSwitch(ArrayList<NodoSentencia> sentenciasSwitch) throws SyntaxException, LexicalException, IOException, SemanticException {

        if (tokenActual.getName().equals("case")) {
            match("case");
            literalPrimitivo();
            match(":");
            sentenciasSwitch.add(sentenciaOpcional());
            listaSentenciasSwitch(sentenciasSwitch); //TODO REVISAR DESPUES

        } else if (tokenActual.getName().equals("default")) {
            match("default");
            match(":");
            sentenciasSwitch.add(sentencia());
        } else {
            //Espsilon
        }
    }

    private NodoSentencia sentenciaOpcional() throws LexicalException, SyntaxException, IOException, SemanticException {
        NodoSentencia toReturn = null;
        if (tokenActual.getName().equals(";") || primerosAsignacionYLLamada(tokenActual.getName()) || tokenActual.getName().equals("var") || tokenActual.getName().equals("return") || tokenActual.getName().equals("break") || tokenActual.getName().equals("if") || tokenActual.getName().equals("while") || tokenActual.getName().equals("switch") || tokenActual.getName().equals("{")) {
            toReturn = sentencia();
        } else {
            //Epsilon
        }

        return toReturn;
    }

    private void literalPrimitivo() throws LexicalException, SyntaxException, IOException {
        if (tokenActual.getName().equals("true")) {
            match("true");
        } else if (tokenActual.getName().equals("false")) {
            match("false");
        } else if (tokenActual.getName().equals("intLiteral")) {
            match("intLiteral");
        } else if (tokenActual.getName().equals("charLiteral")) {
            match("charLiteral");
        }

    }

    private NodoExpComp expresion() throws LexicalException, SyntaxException, IOException {
        NodoExpComp NE = null;
        NodoExpComp expresionCompuestaNode = null;

        expresionCompuestaNode = expresionCompuesta();
        NE = expresionAux(expresionCompuestaNode);
        return NE;
    }

    private NodoExpComp expresionAux(NodoExpComp NECLadoIzq) throws LexicalException, SyntaxException, IOException {
        if (tokenActual.getName().equals("=") || tokenActual.getName().equals("+=") || tokenActual.getName().equals("-=")) {
            Token t = operadorAsignacion();
            NodoExpComp NECLadoDer = expresionCompuesta();
            return new NodoAsignacion(t, NECLadoIzq, NECLadoDer);
        } else {
            // Epsilon
            return NECLadoIzq;
        }

    }

    private Token operadorAsignacion() throws LexicalException, SyntaxException, IOException {
        Token tokenRetorno = tokenActual;
        if (tokenActual.getName().equals("=")) {
            match("=");
        } else if (tokenActual.getName().equals("+=")) {
            match("+=");
        } else if (tokenActual.getName().equals("-=")) {
            match("-=");
        }
        return tokenRetorno;
    }

    private NodoWhile metWhile() throws LexicalException, SyntaxException, IOException, SemanticException {
        NodoExpresion expresionWhile;
        Token whileToken = tokenActual;
        match("while");
        match("(");
        expresionWhile=expresion();
        match(")");
        NodoSentencia sentenciaWhile = sentencia();

        return new NodoWhile(expresionWhile, sentenciaWhile, whileToken, TS.currentBlock);
    }

    private NodoIfSolo metIf() throws LexicalException, SyntaxException, IOException, SemanticException {
        boolean tieneElse=false;
        Token tokenIf = tokenActual;
        match("if");
        match("(");
        NodoExpresion NE = expresion();
        match(")");
        NodoSentencia NS = sentencia();

        NodoIfElse restoIF=  restoIf(NE,NS);
        if (restoIF == null) {
            NodoIfSolo ifSolo = new NodoIfSolo(NE, NS);
            ifSolo.setToken(tokenIf);
            return ifSolo;

        }else{
            restoIF.setToken(tokenIf);
            return restoIF;
        }
    }

    private NodoIfElse restoIf(NodoExpresion NE, NodoSentencia NS ) throws LexicalException, SyntaxException, IOException, SemanticException {
        NodoIfElse toReturn=null;
        if (tokenActual.getName().equals("else")) {
            match("else");

            NodoSentencia sentenciaElse = sentencia();
            toReturn= new NodoIfElse(NE, NS, sentenciaElse);
        } else {
            //epsilon
        }
        return toReturn;

    }

    private NodoReturn metReturn() throws LexicalException, SyntaxException, IOException {
        Token tokenReturn = tokenActual;
        match("return");
        NodoReturn NodoReturn = new NodoReturn(tokenActual, expresionOpcional(), TS.getCurrentMethod(), TS.currentBlock);
        return NodoReturn;
    }

    private NodoExpresion expresionOpcional() throws LexicalException, SyntaxException, IOException {
        NodoExpresion expresionOpcional = null;
        if (tokenActual.getName().equals("+") || tokenActual.getName().equals("-") || tokenActual.getName().equals("!") || tokenActual.getName().equals("true") || tokenActual.getName().equals("false") || tokenActual.getName().equals("intLiteral") || tokenActual.getName().equals("charLiteral") || tokenActual.getName().equals("null") || tokenActual.getName().equals("string") || tokenActual.getName().equals("idMetVar") || tokenActual.getName().equals("this") || tokenActual.getName().equals("new") || tokenActual.getName().equals("(") || tokenActual.getName().equals("idClase")) {
            expresionOpcional = expresion();
        } else {
            //epsilon
        }
        return expresionOpcional;
    }

    private NodoVarDeclaracion varLocal() throws LexicalException, SyntaxException, IOException, SemanticException {
        match("var");
        Token name = tokenActual;
        match("idMetVar");
        match("=");
        NodoExpComp expresion = expresionCompuesta();
        NodoVarDeclaracion retorno = new NodoVarDeclaracion(name, expresion, TS);
        //Attribute varLocalTipoCompatible = new Attribute(name, new Type(name, TS), TS.getCurrentClass(), null);
        //TS.currentBlock.localVariables.add(varLocalTipoCompatible);
        retorno.setParentBlock(TS.currentBlock);

        return retorno;
    }

    private NodoExpComp expresionCompuesta() throws SyntaxException, LexicalException, IOException {
        NodoExpComp retorno = null;
        if (tokenActual.getName().equals("+") || tokenActual.getName().equals("-") || tokenActual.getName().equals("!") || tokenActual.getName().equals("true") || tokenActual.getName().equals("false") || tokenActual.getName().equals("intLiteral") || tokenActual.getName().equals("charLiteral") || tokenActual.getName().equals("null") || tokenActual.getName().equals("stringLiteral") || tokenActual.getName().equals("this") || tokenActual.getName().equals("idMetVar") || tokenActual.getName().equals("new") || tokenActual.getName().equals("idClase") || tokenActual.getName().equals("(")) {
            NodoExpComp leftExpresion = expresionBasica();
            retorno =expresionCompuestaAUX(leftExpresion);
        } else {
            throw new SyntaxException(tokenActual.getRow(), "operador", tokenActual.getName());
        }
        return retorno;
    }

    private NodoExpComp expresionCompuestaAUX(NodoExpComp leftExpresion) throws LexicalException, SyntaxException, IOException {
        NodoExpComp toReturn = null;
        if (tokenActual.getName().equals("||") || tokenActual.getName().equals("&&") || tokenActual.getName().equals("==") || tokenActual.getName().equals("!=") || tokenActual.getName().equals("<")
                || tokenActual.getName().equals(">") || tokenActual.getName().equals(">=") || tokenActual.getName().equals("<=") || tokenActual.getName().equals("+")
                || tokenActual.getName().equals("-") || tokenActual.getName().equals("*") || tokenActual.getName().equals("/") || tokenActual.getName().equals("%")) {
            Token binaryOperator = operadorBinario();
            NodoExpComp rightExpresion = expresionBasica();

            NodoExpBin binaryExpresion = new NodoExpBin(leftExpresion, binaryOperator, rightExpresion);
            toReturn = (NodoExpComp) expresionCompuestaAUX(binaryExpresion);
        } else {
            //epsilon
            return leftExpresion;
        }
        return toReturn;
    }

    private Token operadorBinario() throws LexicalException, SyntaxException, IOException {
        Token tokenToReturn = tokenActual;
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
        return tokenToReturn;
    }


    private NodoExpComp expresionBasica() throws SyntaxException, LexicalException, IOException {
        NodoExpComp toReturn = null;
        if (tokenActual.getName().equals("+") || tokenActual.getName().equals("-") || tokenActual.getName().equals("!")) {
            Token operador = operadorUnario();
            NodoExpresion expression = operando();
            toReturn = new NodoExpUnaria(operador, expression, TS.currentBlock);
        } else if (tokenActual.getName().equals("true") || tokenActual.getName().equals("false") || tokenActual.getName().equals("intLiteral") || tokenActual.getName().equals("charLiteral") || tokenActual.getName().equals("null") || tokenActual.getName().equals("stringLiteral") || tokenActual.getName().equals("this") || tokenActual.getName().equals("idMetVar") || tokenActual.getName().equals("new") || tokenActual.getName().equals("idClase") || tokenActual.getName().equals("(")) {
            toReturn = operando();
        } else {
            throw new SyntaxException(tokenActual.getRow(), "operando", tokenActual.getName());
        }
        return toReturn;
    }

    private Token operadorUnario() throws LexicalException, SyntaxException, IOException {
        Token toRet = tokenActual;
        if (tokenActual.getName().equals("+")) {
            match("+");
        } else if (tokenActual.getName().equals("-")) {
            match("-");
        } else if (tokenActual.getName().equals("!")) {
            match("!");
        } else {
            throw new SyntaxException(tokenActual.getRow(), "+, -, !", tokenActual.getName());
        }
        return toRet;
    }

    private NodoExpComp operando() throws SyntaxException, LexicalException, IOException {
        if (tokenActual.getName().equals("true") || tokenActual.getName().equals("false") || tokenActual.getName().equals("intLiteral") || tokenActual.getName().equals("charLiteral") || tokenActual.getName().equals("null") || tokenActual.getName().equals("stringLiteral")) {
            return literal();
        } else if (tokenActual.getName().equals("this") || tokenActual.getName().equals("idMetVar") || tokenActual.getName().equals("new") || tokenActual.getName().equals("(") || tokenActual.getName().equals("idClase")) {
            return acceso();
        } else {
            throw new SyntaxException(tokenActual.getRow(), "literal o acceso ", tokenActual.getName());
        }
    }

    private NodoExpComp acceso() throws SyntaxException, LexicalException, IOException {
        NodoExpComp toReturn = null;
        if (tokenActual.getName().equals("this") || tokenActual.getName().equals("idMetVar") || tokenActual.getName().equals("new") || tokenActual.getName().equals("(") || tokenActual.getName().equals("idClase")) {
            toReturn = primario();
            toReturn.setEncadenado(encadenadoOpcional());
        } else {
            throw new SyntaxException(tokenActual.getRow(), "acceso", tokenActual.getName());
        }
        return toReturn;
    }

    private Encadenado encadenadoOpcional() throws LexicalException, SyntaxException, IOException {
        Encadenado toReturn = null;

        if (tokenActual.getName().equals(".")) {
            Encadenado chain = null;
            match(".");
            Token nombreMetodoVar = tokenActual;
            match("idMetVar");
            toReturn = encadenadoOpcionalAUX(nombreMetodoVar);
        } else {
            return toReturn;
            //epsilon
        }
        return toReturn;
    }

    private Encadenado encadenadoOpcionalAUX(Token nombreMetVar) throws LexicalException, SyntaxException, IOException {

        if (tokenActual.getName().equals("(")) {
            NodoLlamadaEncadenada chain = null;

            chain = new NodoLlamadaEncadenada(nombreMetVar, TS.currentBlock, TS, TS.getCurrentClass(), TS.getCurrentMethod());
            ArrayList<NodoExpresion> listaArgs = argsActuales();
            chain.setEncadenado(encadenadoOpcional());
            chain.setArgsActuales(listaArgs);
            return chain;

        } else {
            NodoVarEncadenada chain = null;
            chain = new NodoVarEncadenada(nombreMetVar, TS);
            chain.contentMethod = TS.getCurrentMethod();
            chain.setEncadenado(encadenadoOpcional());
            return chain;
        }

    }

    private NodoExpComp primario() throws LexicalException, SyntaxException, IOException {
        NodoExpComp toReturn = null;
        if (tokenActual.getName().equals("this")) {
            toReturn = accesoThis();
        } else if (tokenActual.getName().equals("idMetVar")) {
            toReturn = accesoVarMet();
        } else if (tokenActual.getName().equals("new")) {
            toReturn = accesoConstructor();
        } else if (tokenActual.getName().equals("(")) {
            toReturn = expresionParentizada();
        } else if (tokenActual.getName().equals("idClase")) {
            toReturn = accesoMetodoEstatico();
        }
        return toReturn;
    }

    private NodoAccesoMetodoEstatico accesoMetodoEstatico() throws LexicalException, SyntaxException, IOException {
        Token nombreClase = tokenActual;
        match("idClase");
        match(".");
        Token nombreMetVar = tokenActual;
        match("idMetVar");
        NodoAccesoMetodoEstatico toReturn = new NodoAccesoMetodoEstatico(nombreMetVar, TS.currentBlock, nombreClase, TS.getCurrentClass(), TS.getCurrentMethod());
        ArrayList<NodoExpresion> argsActualesList = new ArrayList<>();
        argsActualesList = argsActuales();
        toReturn.setArgsActuales(argsActualesList);
        return toReturn;
    }

    private ArrayList<NodoExpresion> argsActuales() throws LexicalException, SyntaxException, IOException {
        //TODO: Retornar lista de argumentos actuales y quitar parametro
        //TODO: Crear lista y modificar el hilo hacia abajo, es decir, meterme en lsitaExpsOpcional y armar la lista
        ArrayList<NodoExpresion> toReturn = new ArrayList<>();
        match("(");
        listaExpsOpcional(toReturn);
        match(")");

        return toReturn;
    }

    private void listaExpsOpcional(ArrayList<NodoExpresion> toReturn) throws LexicalException, SyntaxException, IOException {
        if (tokenActual.getName().equals("+") || tokenActual.getName().equals("-") || tokenActual.getName().equals("!") || tokenActual.getName().equals("true") || tokenActual.getName().equals("false") || tokenActual.getName().equals("intLiteral") || tokenActual.getName().equals("charLiteral") || tokenActual.getName().equals("null") || tokenActual.getName().equals("stringLiteral") || tokenActual.getName().equals("this") || tokenActual.getName().equals("idMetVar") || tokenActual.getName().equals("new") || tokenActual.getName().equals("(") || tokenActual.getName().equals("idClase")) {
            listaExps(toReturn);
        } else {
            //Epsilon
        }
    }

    private void listaExps(ArrayList<NodoExpresion> toReturn) throws SyntaxException, LexicalException, IOException {

        if (tokenActual.getName().equals("+") || tokenActual.getName().equals("-") || tokenActual.getName().equals("!") || tokenActual.getName().equals("true") || tokenActual.getName().equals("false") || tokenActual.getName().equals("intLiteral") || tokenActual.getName().equals("charLiteral") || tokenActual.getName().equals("null") || tokenActual.getName().equals("stringLiteral") || tokenActual.getName().equals("this") || tokenActual.getName().equals("idMetVar") || tokenActual.getName().equals("new") || tokenActual.getName().equals("(") || tokenActual.getName().equals("idClase")) {
            NodoExpresion exp = expresion();
            toReturn.add(exp);
            listaExpsAUX(toReturn);
        } else {
            throw new SyntaxException(tokenActual.getRow(), "expresion", tokenActual.getName());
        }
    }

    private NodoExpComp expresionParentizada() throws LexicalException, SyntaxException, IOException {
        NodoExpComp toReturn = null;
        match("(");
        toReturn = expresion();
        match(")");
        return toReturn;
    }

    private NodoAccesoConstructor accesoConstructor() throws LexicalException, SyntaxException, IOException {
        NodoAccesoConstructor toReturn = null;
        Token tok_new = tokenActual;
        match("new");
        Token tipo = tokenActual;
        match("idClase"); //TODO: en el toReturn = new NodoAccesoConstructor de abbajo, necesito enviar un TYPE, no un TOKEN
        toReturn = new NodoAccesoConstructor(tok_new, TS.currentBlock, TS.getCurrentClass(), TS.getCurrentMethod(), tipo);
        ArrayList<NodoExpresion> argsActuales = argsActuales();
        toReturn.setArgsActuales(argsActuales);
        return toReturn;
    }

    private NodoAccesoVariable accesoVarMet() throws LexicalException, SyntaxException, IOException {
        Token idMetVar = tokenActual;
        NodoAccesoVariable toReturn = new NodoAccesoVariable(idMetVar, TS.currentBlock, TS.getCurrentClass(), TS.getCurrentMethod());
        match("idMetVar");
        accesoVarMetAUX(toReturn);

        return toReturn;
    }

    private void accesoVarMetAUX(NodoAccesoVariable toReturn) throws LexicalException, SyntaxException, IOException {

        if (tokenActual.getName().equals("(")) {
            toReturn.isMethod=true;
            toReturn.parameters = argsActuales();
        } else {
            toReturn.isMethod=false;
            //no hago nada, epsilon
        }
    }

    private NodoAcceso accesoThis() throws LexicalException, SyntaxException, IOException {
        Token tokenAcceso = tokenActual;
        match("this");
        return new NodoAccesoThis(tokenAcceso, TS.currentBlock, TS.getCurrentClass(), TS.getCurrentMethod());
    }

    private NodoLiteral literal() throws LexicalException, SyntaxException, IOException {
        NodoLiteral toReturnLiteral = null;
        Token tokenActualLiteral = tokenActual;
        if (tokenActual.getName().equals("true") || tokenActual.getName().equals("false") || tokenActual.getName().equals("intLiteral") || tokenActual.getName().equals("charLiteral")) {
            literalPrimitivo();
        } else if (tokenActual.getName().equals("null") || tokenActual.getName().equals("stringLiteral")) {
            literalObjeto();
        } else {
            throw new SyntaxException(tokenActual.getRow(), "literal", tokenActual.getName());
        }
        if (TS.getCurrentMethod() != null) { //TODO: por que? creo que si es nulo, es un atributo
            toReturnLiteral = new NodoLiteral(tokenActualLiteral, TS.currentBlock, TS);
        }
        return toReturnLiteral;
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

    private void argsFormales() throws LexicalException, SyntaxException, IOException, SemanticException {
        match("(");
        listaArgsFormalesOpcional();
        match(")");
    }

    private void listaArgsFormalesOpcional() throws LexicalException, SyntaxException, IOException, SemanticException {
        if (tokenActual.getName().equals("boolean") || tokenActual.getName().equals("char") || tokenActual.getName().equals("int") || tokenActual.getName().equals("idClase")) {
            listaArgsFormales();
        } else {
            //epsilon
        }
    }

    private void listaArgsFormales() throws LexicalException, SyntaxException, IOException, SemanticException {
        if (tokenActual.getName().equals("boolean") || tokenActual.getName().equals("char") || tokenActual.getName().equals("int") || tokenActual.getName().equals("idClase")) {
            Parameter parametroActual = argFormal();
            Token nombreParametro = parametroActual.getName();
            if (parametroActual != null) {
                TS.getCurrentMethod().insertarParametro(new Parameter(nombreParametro, parametroActual.getType(), TS.getCurrentClass(), TS.getCurrentMethod()));
            }
            restoListaArgFormal();
        }
    }

    private void restoListaArgFormal() throws LexicalException, SyntaxException, IOException, SemanticException {
        if (tokenActual.getName().equals(",")) {
            match(",");
            listaArgsFormales();
        } else {
            //epsilon
        }
    }

    private Parameter argFormal() throws LexicalException, SyntaxException, IOException {
        if (tokenActual.getName().equals("boolean") || tokenActual.getName().equals("char") || tokenActual.getName().equals("int") || tokenActual.getName().equals("idClase")) {
            Type tipo = tipo();
            Token nombre = tokenActual;
            match("idMetVar");
            return new Parameter(nombre, tipo, TS.getCurrentClass(), TS.getCurrentMethod());
        }
        return null;
    }

    private Type tipoMiembro() throws LexicalException, SyntaxException, IOException {
        Type toReturn = new Type(tokenActual, TS);
        if (tokenActual.getName().equals("boolean") || tokenActual.getName().equals("char") || tokenActual.getName().equals("int") || tokenActual.getName().equals("idClase")) {
            toReturn = tipo();
        } else {
            match("void");
        } //TODO: else{throw new SyntaxException}
        return toReturn;
    }

    private Type tipoPrimitivo() throws SyntaxException, LexicalException, IOException {
        Token actual = tokenActual;
        if (tokenActual.getName().equals("boolean"))
            match("boolean");
        else if (tokenActual.getName().equals("char")) {
            match("char");
        } else if (tokenActual.getName().equals("int")) {
            match("int");
        } else {
            throw new SyntaxException(tokenActual.getRow(), "char, int o boolean", tokenActual.getName());
        }
        Type type = new PrimitiveType(actual, TS);
        return type;
    }


    private Token estaticoOpcional() throws LexicalException, SyntaxException, IOException {
        Token toReturn = tokenActual;
        if (tokenActual.getName().equals("static")) {
            match("static");
            return toReturn;
        } else {
            //epsilongo
            return null;
        }
    }


    private void listaExpsAUX(ArrayList<NodoExpresion> toReturn) throws LexicalException, SyntaxException, IOException {
        if (tokenActual.getName().equals(",")) {
            match(",");
            listaExps(toReturn);
        } else {
            //Epsilon
        }
    }


    private Type tipo() throws SyntaxException, LexicalException, IOException {
        if (tokenActual.getName().equals("boolean") || tokenActual.getName().equals("char") || tokenActual.getName().equals("int")) {
            return tipoPrimitivo();
        } else if (tokenActual.getName().equals("idClase")) {
            Token idClase = tokenActual;
            match("idClase");
            return new Type(idClase, TS);
        } else {
            throw new SyntaxException(tokenActual.getRow(), "tipo", tokenActual.getName());
        }
    }

    @Override
    public String toString() {
        return TS.toString();
    }
}
