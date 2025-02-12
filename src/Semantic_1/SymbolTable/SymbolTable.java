package Semantic_1.SymbolTable;

import Generacion.Writer;
import Lexical.Token;
import Semantic_1.*;
import Exception.SemanticException;
import Semantic_1.Nodes.BloqueClaseDefault.BloqueBlockReader;
import Semantic_1.Nodes.BloqueClaseDefault.BloqueDebugPrint;
import Semantic_1.Nodes.BloqueClaseDefault.BloqueSystemPrint;
import Semantic_1.Nodes.Sentencia.NodoBloque;
import Semantic_1.Type.Type;

import java.util.*;

public class SymbolTable {
    private HashMap<String, Clase> classTable;

    private Clase currentClass;

    private Method currentMethod;

    public NodoBloque currentBlock;



    public Writer writer;

    public Method metodoMain;


    public SymbolTable(String nombreArchivo) throws Exception { //TODO CHANGE TO SemanticException
        classTable = new HashMap<>();
        currentClass = null;
        currentMethod = null;
        writer = new Writer(nombreArchivo);
        //addDefaultsClasses();
    }
    public void setMetodoMain(Method metodoMain) {
        this.metodoMain = metodoMain;
    }
    public void setCurrentBlock(NodoBloque currentBlock) {

        this.currentBlock = currentBlock;
        this.currentMethod.currentBlock = currentBlock;
    }

    public Writer getWriter() {
        return writer;
    }

    public Clase getCurrentClass() {
        return currentClass;
    }

    public void setCurrentClass(Clase currentClass) {
        this.classTable.put(currentClass.classNameToken.getLexeme(), currentClass);
        this.currentClass = currentClass;
    }


    public Method getCurrentMethod() {
        return currentMethod;
    }

    public void setCurrentMethod(Method methodToInsert) throws SemanticException {
        if (currentClass.methodTable.containsKey(methodToInsert.methodNameToken.getLexeme())) {
            throw new SemanticException(methodToInsert.methodNameToken, "Metodo duplicado");
        }
        currentClass.methodTable.put(methodToInsert.methodNameToken.getLexeme(), methodToInsert);
        currentClass.setCurrentMethod(methodToInsert);
        this.currentMethod = methodToInsert;
        methodToInsert.containerClass = currentClass;
    }

    public void insertarConstructor(String name, Constructor c) {
        currentClass.constructorTable.put(name, c);
    }

    public void setCurrentConstructor(Constructor c) throws SemanticException {
        if (currentClass.constructorTable.size() > 2) {
            throw new SemanticException(c.methodNameToken, "No se puede tener mas de un constructor");
        }
        currentClass.constructorTable.put(c.methodNameToken.getLexeme(), c);
        currentClass.setCurrentMethod(c);
        this.currentMethod = c;
        c.containerClass = currentClass;
    }

    public void insertarAtributo(String name, Attribute a) throws SemanticException {
        if (currentClass.attributeTable.containsKey(name)) {
            throw new SemanticException(a.attributeNameToken, "Atributo duplicado");
        }
        currentClass.attributeTable.put(name, a);

    }

    public void insertarClase(String name, Clase c) {
        classTable.put(name, c);
    }


    public String toString() {
        StringBuilder toReturn = new StringBuilder();

        for (Clase c : classTable.values()) {
            toReturn.append("Class: ").append(c.classNameToken.getLexeme()).append("\n");
            if (c.heredaDe!= null && c.heredaDe.getLexeme() != "Object")
                toReturn.append("├─ Extends: ").append(c.heredaDe.getLexeme()).append("\n");
            //toReturn.append("├─ Constructor:\n");
            //toReturn.append("│   └─ ").append(c.constructorTable.get("default").methodNameToken.getLexeme()).append(" : ").append(c.constructorTable.get("default").type.token.getLexeme()).append("\n");


            if (c.attributeTable.isEmpty())
                toReturn.append("├─ No attributes\n");
            else
                toReturn.append("├─ Attributes:\n");

            for (Attribute a : c.attributeTable.values()) {
                toReturn.append("│   ├─ ").append(a.attributeNameToken.getLexeme()).append(" : ").append(a.type.token.getLexeme()).append("\n");
            }

            if (c.methodTable.isEmpty())
                toReturn.append("└─ No methods\n");
            else {
                toReturn.append("└─ Methods:\n");

                for (Method m : c.methodTable.values()) {
                    toReturn.append("    ├─ ").append(m.methodNameToken.getLexeme()).append(" : ").append(m.type.token.getLexeme()).append("\n");
                    if (m.isStatic != null)
                        toReturn.append("    │   ├─ Static: ").append(m.isStatic.getLexeme()).append("\n");
                    toReturn.append("    │   └─ Parameters:\n");
                    /*for (Attribute p : m.parametros.values()) {
                        if (m.parametros.values().toArray()[m.parametros.size() - 1] == p)
                            toReturn.append("    │       └─ ").append(p.attributeNameToken.getLexeme()).append(" : ").append(p.type.token.getLexeme()).append("\n");
                        else
                            toReturn.append("    │       ├─ ").append(p.attributeNameToken.getLexeme()).append(" : ").append(p.type.token.getLexeme()).append("\n");
                    }*/
                    for (int i = 0; i < m.getParametros().size(); i++) {
                        Parameter p = m.getParametros().get(i);

                        // Comprobar si es el último elemento
                        if (i == m.getParametros().size() - 1) {
                            toReturn.append("    │       └─ ")
                                    .append(p.getName().getLexeme())
                                    .append(" : ")
                                    .append(p.getType().getToken().getLexeme())
                                    .append("\n");
                        } else {
                            toReturn.append("    │       ├─ ")
                                    .append(p.getName().getLexeme())
                                    .append(" : ")
                                    .append(p.getType().getToken().getLexeme())
                                    .append("\n");
                        }
                    }
                }
            }
            toReturn.append("\n");
         //   toReturn.append(c.toString());

            toReturn.append("--------------------------------\n");
        }

        return toReturn.toString();
    }

    public Clase getClase(String name) {
        //if first and last char are '"' then return classType
        if (name.charAt(0) == '"' && name.charAt(name.length()-1) == '"')
            return classTable.get("String");
        else
            return classTable.get(name);


    }

    public void check() throws SemanticException {
        addDefaultsClasses();
        int cantMain = 0;
        for (Clase c : classTable.values()) {
            if (c.classNameToken.getLexeme().equals("Object"))
                continue;
            c.check();
            if (c.methodTable.get("main") != null && c.methodTable.get("main").isStatic.getName().equals("static") && c.methodTable.get("main").type.getName().equals("void") && c.methodTable.get("main").getParametros().isEmpty()){
                cantMain++;
                setMetodoMain(c.methodTable.get("main"));
            }
        }
        if (cantMain == 0 || cantMain > 1) {
            throw new SemanticException(new Token("errorGeneral", "errorGeneral", 0), "Clase sin main");
        }

    }

    public void consolidate() throws SemanticException {
        for (Clase c : classTable.values()) {
            if (c.constructorTable.size() == 0) {
                Constructor defaultConstructor =  new Constructor(new Token("idMetVar", c.classNameToken.getLexeme(), 0), new Type(c.classNameToken, this), c);
                defaultConstructor.bloquePrincipal = new NodoBloque(new Token("idMetVar", c.classNameToken.getLexeme(), 0), c, null, null, null);
                insertarConstructor(c.classNameToken.getLexeme(),defaultConstructor);

            }

            c.consolidar(c);
        }

    }

    public boolean checkDuplicatedClass(Clase c) {
        if (classTable.containsKey(c.classNameToken.getLexeme())) {
            return true;
        } else
            return false;
    }

    public HashMap<String, Clase> getClassTable() {
        return classTable;
    }

    public boolean checkClassExistence(String lexeme) {
        boolean toReturn = false;
        if (classTable.containsKey(lexeme)) {
            toReturn = true;
        }
        return toReturn;
    }


    public void addDefaultsClasses() throws SemanticException {
        Clase object = new Clase(new Token("Object", "Object", -1), this);
        object.consolidatedMethods = true;
        object.consolidatedAttributes = true;
        object.heredaDe = null;
        addObjectMethods(object);
        insertarClase("Object", object);

        Clase system = new Clase(new Token("System", "System", -1), this);
        system.heredaDe = (new Token("Object", "Object", -1));
        addSystemMethods(system);
        insertarClase("System", system);
        Clase string = new Clase(new Token("String", "String", -1), this);
        string.heredaDe = (new Token("Object", "Object", -1));
        insertarClase("String", string);
    }

    private void addSystemMethods(Clase system) throws SemanticException {
        Method method = new Method(new Token("idMetVar", "read", -1), new Type(new Token("keyword_int", "int", -1), this), system, new Token("keyword_static", "static", -1));
        system.setCurrentMethod(method);
        method.bloquePrincipal = new BloqueBlockReader(new Token("read", "read", -1), system, method, null, null);
        system.methodTable.put("read", method);


        method = new Method(new Token("idMetVar", "printB", -1), new Type(new Token("keyword_void", "void", -1), this), system, new Token("keyword_static", "static", -1));
        method.insertarParametro(new Parameter(new Token("idMetVar", "b", -1), new Type(new Token("keyword_boolean", "boolean", -1), this), system, method));
        method.bloquePrincipal = new BloqueSystemPrint(new Token("printB", "printB", -1), system, method,null, method.getParametros(), "bool", false );
        system.setCurrentMethod(method);
        system.methodTable.put("printB", method);


        method = new Method(new Token("idMetVar", "printC", -1), new Type(new Token("keyword_void", "void", -1), this), system, new Token("keyword_static", "static", -1));
        method.insertarParametro(new Parameter(new Token("idMetVar", "c", -1), new Type(new Token("keyword_char", "char", -1), this), system, method));
        method.bloquePrincipal = new BloqueSystemPrint(new Token("printC", "printC", -1), system, method,null, method.getParametros(), "char", false );
        system.setCurrentMethod(method);
        system.methodTable.put("printC", method);


        method = new Method(new Token("idMetVar", "printI", -1), new Type(new Token("keyword_void", "void", -1),this), system, new Token("keyword_static", "static", -1));
        method.insertarParametro(new Parameter(new Token("idMetVar", "i", -1), new Type(new Token("keyword_int", "int", -1),this), system, method));
        method.bloquePrincipal = new BloqueSystemPrint(new Token("printI", "printI", -1), system, method,null, method.getParametros(), "int", false );
        system.setCurrentMethod(method);
        system.methodTable.put("printI", method);


        method = new Method(new Token("idMetVar", "printS", -1), new Type(new Token("keyword_void", "void", -1),this), system, new Token("keyword_static", "static", -1));
        method.insertarParametro(new Parameter(new Token("idMetVar", "s", -1), new Type(new Token("keyword_String", "String", -1),this), system, method));
        method.bloquePrincipal = new BloqueSystemPrint(new Token("printS", "printS", -1), system, method,null, method.getParametros(), "string", false );
        system.setCurrentMethod(method);
        system.methodTable.put("printS", method);


        method = new Method(new Token("idMetVar", "println", -1), new Type(new Token("keyword_void", "void", -1),this), system, new Token("keyword_static", "static", -1));
        system.setCurrentMethod(method);
        method.bloquePrincipal = new BloqueSystemPrint(new Token("println", "println", -1), system, method,null, method.getParametros(), "asdsd", true );
        system.setCurrentMethod(method);
        system.methodTable.put("println", method);


        method = new Method(new Token("idMetVar", "printBln", -1), new Type(new Token("keyword_void", "void", -1),this), system, new Token("keyword_static", "static", -1));
        method.insertarParametro(new Parameter(new Token("idMetVar", "b", -1), new Type(new Token("keyword_boolean", "boolean", -1),this), system, method));
        method.bloquePrincipal = new BloqueSystemPrint(new Token("printBln", "printBln", -1), system, method,null, method.getParametros(), "bool", true );
        system.setCurrentMethod(method);
        system.methodTable.put("printBln", method);


        method = new Method(new Token("idMetVar", "printCln", -1), new Type(new Token("keyword_void", "void", -1),this), system, new Token("keyword_static", "static", -1));
        method.insertarParametro(new Parameter(new Token("idMetVar", "c", -1), new Type(new Token("keyword_char", "char", -1),this), system, method));
        method.bloquePrincipal = new BloqueSystemPrint(new Token("printCln", "printCln", -1), system, method,null, method.getParametros(), "char", true );
        system.setCurrentMethod(method);
        system.methodTable.put("printCln", method);


        method = new Method(new Token("idMetVar", "printIln", -1), new Type(new Token("keyword_void", "void", -1),this), system, new Token("keyword_static", "static", -1));
        method.insertarParametro(new Parameter(new Token("idMetVar", "i", -1), new Type(new Token("keyword_int", "int", -1),this), system, method));
        method.bloquePrincipal = new BloqueSystemPrint(new Token("printIln", "printIln", -1), system, method,null, method.getParametros(), "int", true );
        system.setCurrentMethod(method);
        system.methodTable.put("printIln", method);


        method = new Method(new Token("idMetVar", "printSln", -1), new Type(new Token("keyword_void", "void", -1),this), system, new Token("keyword_static", "static", -1));
        method.insertarParametro(new Parameter(new Token("idMetVar", "s", -1), new Type(new Token("keyword_String", "String", -1),this), system, method));
        method.bloquePrincipal = new BloqueSystemPrint(new Token("printSln", "printSln", -1), system, method,null, method.getParametros(), "string", true );
        system.setCurrentMethod(method);
        system.methodTable.put("printSln", method);
    }

    private void addObjectMethods(Clase object) throws SemanticException {
        Method method = new Method(new Token("idMetVar", "debugPrint", -1), new Type(new Token("keyword_void", "void", -1),this), object, new Token("keyword_static", "static", -1));
        method.insertarParametro(new Parameter(new Token("idMetVar", "i", -1), new Type(new Token("int", "int", -1),this), object, method));
        method.bloquePrincipal= new BloqueDebugPrint(new Token("debugPrint", "debugPrint", -1), object, method,null, new ArrayList<Parameter>() );
        object.isConsolidated=true;
        object.setCurrentMethod(method);
        object.methodTable.put("debugPrint", method);


    }

    public void checkSemantico() throws SemanticException {
        for (Clase c : this.classTable.values()) {
            c.checkSemantico(this);
        }
    }

    public void inicializarArchivo(){
        writer.write(".CODE");
        writer.write ("PUSH simple_heap_init");
        writer.write ("CALL");

        writer.write("PUSH "+this.metodoMain.etiquetaMetodo);
        writer.write("CALL");
        writer.write("HALT");

        writer.write("simple_heap_init: "+ "RET 0 \n simple_malloc: LOADFP \n LOADSP \n STOREFP \n LOADHL \n DUP \n PUSH 1 \nADD \n STORE 4 \n LOAD 3 \n ADD \nSTOREHL \nSTOREFP \nRET 1 \n\n");

    }
    public void generarCodigo(){
        inicializarArchivo();
        for (Clase c : this.classTable.values()) {
            c.generarCodigo(this);
        }
        writer.closeArchivo();
    }
}

