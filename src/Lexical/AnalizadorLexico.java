package Lexical;

import java.io.IOException;

import TablaHash.KeyW_Table;
import SourceManager.*;

public class AnalizadorLexico {
    private KeyW_Table keywords;
    private String lexeme;
    private SourceManager fileManager;
    private char currentChar;

    public AnalizadorLexico(SourceManager fileManager) {
        this.fileManager = fileManager;
        lexeme = "";
        initializeKeywords();
        currentChar = ' ';
    }

    public char getCurrentChar(){
        return currentChar;
    }
    private void initializeKeywords() {
        keywords = new KeyW_Table();
    }

    private Token returnResult(Token tokenToReturn) throws LexicalException, IOException {
        String tokenName = tokenToReturn.getName();
        String tokenLexeme = tokenToReturn.getLexeme();

        if (tokenName == "intLiteral" && tokenLexeme.length() > 9) {
            throw new LexicalException(tokenLexeme, fileManager.getLineNumber(), fileManager.getCurrentColumn(), "No se permiten literales enteros de mas de 9 dígitos.", fileManager.getContentLine(fileManager.getLineNumber()));
        }
        if (tokenName == "floatLiteral"){
            checkFloat(tokenToReturn);
        }
        if (keywords.exist(tokenLexeme)) {
            return new Token(tokenLexeme, tokenLexeme, fileManager.getLineNumber());
        } else if (tokenName == "idClase" || tokenName == "idMetVar" || tokenName == "stringLiteral" || tokenName == "intLiteral" || tokenName == "floatLiteral" || tokenName == "charLiteral" || tokenName == "EOF") {
            return tokenToReturn;
        } else return new Token(tokenLexeme, tokenLexeme, fileManager.getLineNumber());
    }

    private void checkFloat(Token tokenToReturn) throws IOException, LexicalException {
        String tokenLexeme = tokenToReturn.getLexeme();
        boolean isZero = false;

        if (tokenLexeme.contains("e")){
            String[] splitted = tokenLexeme.split("e");
            if (Double.parseDouble(splitted[0]) == 0){
                isZero = true;
            }
        }
        if (tokenLexeme.contains("E")){
            String[] splitted = tokenLexeme.split("E");
            if (Double.parseDouble(splitted[0]) == 0){
                isZero = true;
            }
        }

        try {
            double num = Double.parseDouble(tokenLexeme);
            if (!isZero && num < Float.MIN_VALUE){
                throw new LexicalException(tokenLexeme, fileManager.getLineNumber(), fileManager.getCurrentColumn(),"El literal es mas pequeño que el menor float permitido.", fileManager.getContentLine(fileManager.getLineNumber()));
            }else if (num > Float.MAX_VALUE){
                throw new LexicalException(tokenLexeme, fileManager.getLineNumber(), fileManager.getCurrentColumn(),"El literal es mas grande que el mayor float permitido.", fileManager.getContentLine(fileManager.getLineNumber()));
            }
        }catch (NumberFormatException exception){
            throw new LexicalException(tokenLexeme, fileManager.getLineNumber(), fileManager.getCurrentColumn(),"El literal esta fuera de los rangos permitidos por los floats.", fileManager.getContentLine(fileManager.getLineNumber()));
        }

    }

    private void updateLexeme() {
        lexeme += currentChar;
    }

    public Token getNextToken() throws LexicalException, IOException {
        return returnResult(e0());
    }

    private void updateChar() {
        try {
            currentChar = fileManager.getNextChar();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Token e0() throws LexicalException, IOException {
        lexeme = "";
        while (currentChar == ' ' || currentChar == '\n' || currentChar == '\r' || currentChar == '\t') {
            updateChar();
        }
        if (Character.isDigit(currentChar)) {
            updateLexeme();
            updateChar();
            return eLitInt();

        } else if (Character.isLowerCase(currentChar)) {
            updateLexeme();
            updateChar();
            return eIDMetVar();

        } else if (Character.isUpperCase(currentChar)) {
            updateLexeme();
            updateChar();
            return eIDClass();

        } else if (currentChar == '>' || currentChar == '<' || currentChar == '!') {
            updateLexeme();
            updateChar();
            return eOperatorsWithPossibleEquals();

        } else if (currentChar == '=') {
            updateLexeme();
            updateChar();
            return eOnlyEquals();

        } else if (currentChar == '*' || currentChar == '%') {
            updateLexeme();
            updateChar();
            return eOperators();

        } else if (currentChar == '/') {
            updateLexeme();
            updateChar();
            return eCommentsOrOperator();

        } else if (currentChar == '+' || currentChar == '-') {
            updateLexeme();
            updateChar();
            return eOperatorsOrAssignment();

        } else if (currentChar == '(' || currentChar == ')' || currentChar == '{' || currentChar == '}' || currentChar == ';' || currentChar == ':' || currentChar == ',') {
            updateLexeme();
            updateChar();
            return ePuntuacion();

        } else if (currentChar == '.') {
            updateLexeme();
            updateChar();
            return ePunto();

        } else if (currentChar == '&') {
            updateLexeme();
            updateChar();
            return eAmpersand();

        } else if (currentChar == '|') {
            updateLexeme();
            updateChar();
            return eBarraVertical();

        } else if (currentChar == '"') {
            updateLexeme();
            updateChar();
            return eString1();

        } else if (currentChar == '\'') {
            updateLexeme();
            updateChar();
            return eChar1();

        } else if (fileManager.isEOF(currentChar)) {
            return eFinal();

        } else {
            updateLexeme();
            updateChar();
            throw new LexicalException(lexeme, fileManager.getLineNumber(), fileManager.getCurrentColumn(), "Caractér inválido.", fileManager.getContentLine(fileManager.getLineNumber()));
        }
    }

    private Token ePunto() throws LexicalException, IOException {
        if (Character.isDigit(currentChar)) {
            updateLexeme();
            updateChar();
            return eFloat1();
        } else {
            return ePuntuacion();
        }
    }

    private Token eFinal() {
        updateLexeme();
        return new Token("EOF", "", fileManager.getLineNumber());
    }

    private Token eString1() throws LexicalException, IOException {
        if (fileManager.isEOF(currentChar)) {
            throw new LexicalException(lexeme, fileManager.getLineNumber(), fileManager.getCurrentColumn(), "Se esperaba \" pero se encontro EOF.", fileManager.getContentLine(fileManager.getLineNumber()));

        } else if (currentChar == '\"') {
            updateLexeme();
            updateChar();
            return new Token("stringLiteral", lexeme, fileManager.getLineNumber());

        } else if (currentChar == '\\') {
            updateLexeme();
            updateChar();
            return eString2();

        } else if (currentChar == '\n' || currentChar == '\r') {
            throw new LexicalException(lexeme, fileManager.getLineNumber(), fileManager.getCurrentColumn(), "Se esperaba \" pero se encontro un salto de linea.", fileManager.getContentLine(fileManager.getLineNumber()));

        } else {
            updateLexeme();
            updateChar();
            return eString1();
        }
    }

    private Token eString2() throws LexicalException, IOException {
        if (fileManager.isEOF(currentChar)) {
            throw new LexicalException(lexeme, fileManager.getLineNumber(), fileManager.getCurrentColumn(), "Se esperaba \" o un caracter pero se encontro EOF.", fileManager.getContentLine(fileManager.getLineNumber()));

        } else if (currentChar == '\n' || currentChar == '\r') {
            throw new LexicalException(lexeme, fileManager.getLineNumber(), fileManager.getCurrentColumn(), "Se esperaba \" o un caracter pero se encontro un salto de linea.", fileManager.getContentLine(fileManager.getLineNumber()));

        } else {
            updateLexeme();
            updateChar();
            return eString1();
        }
    }

    private Token eChar1() throws LexicalException, IOException {
        if (fileManager.isEOF(currentChar)) {
            throw new LexicalException(lexeme, fileManager.getLineNumber(), fileManager.getCurrentColumn(), "Se esperaba un caractér pero se encontro EOF.", fileManager.getContentLine(fileManager.getLineNumber()));

        } else if (currentChar == '\n' || currentChar== '\r') {
            updateChar();
            throw new LexicalException(lexeme, fileManager.getLineNumber(), fileManager.getCurrentColumn(), "Se esperaba un caractér pero se encontro un salto de línea.", fileManager.getContentLine(fileManager.getLineNumber()));

        } else if (currentChar == '\'') {
            updateChar();
            throw new LexicalException(lexeme, fileManager.getLineNumber(), fileManager.getCurrentColumn(), "No se permiten caracteres vacíos.", fileManager.getContentLine(fileManager.getLineNumber()));

        } else if (currentChar == '\\') {
            updateLexeme();
            updateChar();
            return eChar2();

        } else {
            updateLexeme();
            updateChar();
            return eChar3();
        }
    }

    private Token eChar2() throws LexicalException, IOException {
        if (fileManager.isEOF(currentChar)) {
            updateChar();
            throw new LexicalException(lexeme, fileManager.getLineNumber(), fileManager.getCurrentColumn(), "Se esperaba un caractér pero se encontro EOF.", fileManager.getContentLine(fileManager.getLineNumber()));

        } else {
            updateLexeme();
            updateChar();
            return eChar3();
        }
    }

    private Token eChar3() throws LexicalException, IOException {
        if (fileManager.isEOF(currentChar)) {
            throw new LexicalException(lexeme, fileManager.getLineNumber(), fileManager.getCurrentColumn(), "Se esperaba \' pero se encontro EOF.", fileManager.getContentLine(fileManager.getLineNumber()));

        } else if (currentChar == '\'') {
            updateLexeme();
            updateChar();
            return new Token("charLiteral", lexeme, fileManager.getLineNumber());

        } else {
            updateChar();
            throw new LexicalException(lexeme, fileManager.getLineNumber(), fileManager.getCurrentColumn(), "Se esperaba \'.", fileManager.getContentLine(fileManager.getLineNumber()));
        }
    }

    private Token eOnlyEquals() {
        if (currentChar == '=') {
            updateLexeme();
            updateChar();
            return eOperators();

        } else {
            return new Token("assignment", lexeme, fileManager.getLineNumber());

        }
    }

    //eLitInt literales enteros
    private Token eLitInt() throws LexicalException, IOException {
        if (Character.isDigit(currentChar)) {
            updateLexeme();
            updateChar();
            return eLitInt();

        } else if (currentChar == '.') {
            updateLexeme();
            updateChar();
            return eFloat1();

        } else if (currentChar == 'e' || currentChar == 'E') {
            updateLexeme();
            updateChar();
            return eFloat2();

        } else {
            return new Token("intLiteral", lexeme, fileManager.getLineNumber());
        }
    }

    //punto del float
    private Token eFloat1() throws LexicalException, IOException {
        if (Character.isDigit(currentChar)) {
            updateLexeme();
            updateChar();
            return eFloat1();

        } else if (currentChar == 'e' || currentChar == 'E') {
            updateLexeme();
            updateChar();
            return eFloat2();

        } else {
            return new Token("floatLiteral", lexeme, fileManager.getLineNumber());
        }
    }

    private Token eFloat2() throws IOException, LexicalException {
        if (currentChar == '+' || currentChar == '-') {
            updateLexeme();
            updateChar();
            return eFloat3();

        } else if (Character.isDigit(currentChar)) {
            updateLexeme();
            updateChar();
            return eFloat4();

        } else {
            throw new LexicalException(lexeme, fileManager.getLineNumber(), fileManager.getCurrentColumn(), "Se esperaba un digito o un signo.", fileManager.getContentLine(fileManager.getLineNumber()));
        }
    }

    private Token eFloat3() throws IOException, LexicalException {
        if (Character.isDigit(currentChar)) {
            updateLexeme();
            updateChar();
            return eFloat4();

        } else {
            throw new LexicalException(lexeme, fileManager.getLineNumber(), fileManager.getCurrentColumn(), "Se esperaba un dígito.", fileManager.getContentLine(fileManager.getLineNumber()));
        }
    }

    private Token eFloat4() {
        if (Character.isDigit(currentChar)) {
            updateLexeme();
            updateChar();
            return eFloat4();

        } else {
            return new Token("floatLiteral", lexeme, fileManager.getLineNumber());
        }
    }

    //eIDMetVar identificadores de metodos o variables
    private Token eIDMetVar() {
        if (Character.isDigit(currentChar) || Character.isLowerCase(currentChar) || Character.isUpperCase(currentChar) || currentChar == '_') {
            updateLexeme();
            updateChar();
            return eIDMetVar();

        } else {
            return new Token("idMetVar", lexeme, fileManager.getLineNumber());

        }
    }

    //eIDClass identificadores de clases
    private Token eIDClass() {
        if (Character.isDigit(currentChar) || Character.isLowerCase(currentChar) || Character.isUpperCase(currentChar) || currentChar == '_') {
            updateLexeme();
            updateChar();
            return eIDClass();

        } else {
            return new Token("idClase", lexeme, fileManager.getLineNumber());

        }
    }

    //ePuntuacion puntuadores, (){};,:
    private Token ePuntuacion() {

        return new Token("punctuator", lexeme, fileManager.getLineNumber());
    }

    private Token eAmpersand() throws LexicalException, IOException {
        if (currentChar == '&') {
            updateLexeme();
            updateChar();
            return eOperators();

        } else {
            throw new LexicalException(lexeme, fileManager.getLineNumber(), fileManager.getCurrentColumn(), "Se esperaba un andpersand doble.", fileManager.getContentLine(fileManager.getLineNumber()));
        }
    }

    private Token eBarraVertical() throws LexicalException, IOException {
        if (currentChar == '|') {
            updateLexeme();
            updateChar();
            return eOperators();

        } else {
            throw new LexicalException(lexeme, fileManager.getLineNumber(), fileManager.getCurrentColumn(), "Se esperaba una linea vertical doble.", fileManager.getContentLine(fileManager.getLineNumber()));
        }
    }

    private Token eOperators() {
        return new Token("operator", lexeme, fileManager.getLineNumber());
    }

    //En el else, no actualicé el lexema ni el char, por lo tanto, retornaía solamente mayor, menor ó "!"
    private Token eOperatorsWithPossibleEquals() {
        if (currentChar == '=') {
            updateLexeme();
            updateChar();
            return eOperators();

        } else {
            return eOperators();
        }
    }

    //si el siguiente caracter, no es un igual, terminó nuestro lexema y retornamos lo que ingresó (+ o -)
    private Token eOperatorsOrAssignment() {
        if (currentChar == '=') {
            updateLexeme();
            updateChar();
            return new Token("assignment", lexeme, fileManager.getLineNumber());

        } else {
            return eOperators();

        }
    }

    //eComments inicio de comentarios y operadores unicamente /, /
    private Token eCommentsOrOperator() throws LexicalException, IOException {
        if (currentChar == '*') {
            lexeme = "";
            updateLexeme();
            updateChar();
            return eMultilnComments1();

        } else if (currentChar == '/') {
            updateLexeme();
            updateChar();
            return eUnilnComments1();

        } else {
            return eOperators();

        }
    }

    private Token eUnilnComments1() throws LexicalException, IOException {
        if (currentChar == '\n') {
            updateLexeme();
            updateChar();
            return e0();

        } else if (fileManager.isEOF(currentChar)) {
            updateLexeme();
            return eFinal();
        } else {
            updateLexeme();
            updateChar();
            return eUnilnComments1();
        }
    }

    private Token eMultilnComments1() throws LexicalException, IOException {
        if (currentChar == '*') {
            lexeme = "";
            updateLexeme();
            updateChar();
            return eMultilnComments2();

        } else if (fileManager.isEOF(currentChar)) {
            throw new LexicalException(lexeme, fileManager.getLineNumber(), fileManager.getCurrentColumn(), "Comentario multilinea no cerrado.", fileManager.getContentLine(fileManager.getLineNumber()));

        } else {
            lexeme = "";
            updateLexeme();
            updateChar();
            return eMultilnComments1();

        }
    }

    private Token eMultilnComments2() throws LexicalException, IOException {
        if (currentChar == '/') {
            updateChar();
            return e0();

        } else if (fileManager.isEOF(currentChar)) {
            throw new LexicalException(lexeme, fileManager.getLineNumber(), fileManager.getCurrentColumn(), "Comentario multilinea no cerrado.", fileManager.getContentLine(fileManager.getLineNumber()));

        } else if (currentChar == '*') {
            lexeme = "";
            updateLexeme();
            updateChar();
            return eMultilnComments2();

        } else {
            updateLexeme();
            updateChar();
            return eMultilnComments1();

        }
    }
}