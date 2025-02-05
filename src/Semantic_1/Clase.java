package Semantic_1;

import Generacion.Writer;
import Lexical.Token;
import Semantic_1.SymbolTable.SymbolTable;
import Exception.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.Collectors;

public class Clase {
    public
    HashMap<String, Constructor> constructorTable;
    public HashMap<String, Method> methodTable;

    public ArrayList<Method> methodsOrdenados = new ArrayList<>();
    public HashMap<String, Attribute> attributeTable;
    private Method currentMethod;
    public Token classNameToken;
    public Token heredaDe;


    private SymbolTable TS;
    private ArrayList<Clase> herenciaDeUnaClase = new ArrayList<>();
    public boolean consolidatedAttributes, consolidatedMethods, isConsolidated;


    public Clase(Token classNameToken, SymbolTable TS) throws SemanticException {
        this.TS = TS;
        this.classNameToken = classNameToken;
        constructorTable = new HashMap<>();
        methodTable = new HashMap<>();
        attributeTable = new HashMap<>();
        consolidatedAttributes = false;
        consolidatedMethods = false;
        isConsolidated = false;
    }


    public void addHeredaDe(Token heredaDe) {

        this.heredaDe = heredaDe;
    }


    public void setCurrentMethod(Method currentMethod) {
        this.currentMethod = currentMethod;
    }

    public void consolidar(Clase clase) throws SemanticException {
        if (!this.isConsolidated) {
            consolidarAtributos(clase);
            consolidarMetodos(clase);
        }
        clase.isConsolidated = true;
    }

    private void consolidarAtributos(Clase clase) throws SemanticException {
        if (!clase.consolidatedAttributes) {
            if (!TS.getClase(clase.heredaDe.getLexeme()).consolidatedAttributes) {
                consolidar(TS.getClase(clase.heredaDe.getLexeme()));
            }
            //For each attribute in the parent class, add attribute to this class attribute table
            for (Attribute a : TS.getClase(clase.heredaDe.getLexeme()).attributeTable.values()) {
                if (!attributeTable.containsKey(a.getName())) {
                    attributeTable.put(a.getName(), a);
                } else {
                    throw new SemanticException(a.attributeNameToken, "Atributo heredado duplicado");
                }
            }
            clase.consolidatedAttributes = true;

        }
    }


    private void consolidarMetodos(Clase clase) throws SemanticException {
        //TODO: Para completar el arrayList de methoddos ordenados, primero recorro del final al inicio los metodos que heredo ed la clase pardre, luego los agrego a mi lista
        //TODO: Luego, recorro al reves mis methodos y los agrego
        ArrayList<Method> methodsOrdenadosAUX = new ArrayList<Method>();
        for (Method m : this.methodTable.values()) {
            if (m.isStatic == null) {
                methodsOrdenadosAUX.add(m);

            }

        }

        if (!clase.consolidatedMethods) {
            if (!TS.getClase(clase.heredaDe.getLexeme()).consolidatedMethods) {
                consolidar(TS.getClase(clase.heredaDe.getLexeme()));

            } //Una vez que mi paddre se consolidó
            for (Method m : TS.getClase(clase.heredaDe.getLexeme()).methodsOrdenados) {
                if (m.isStatic == null) {
                    if (methodTable.containsKey(m.methodNameToken.getLexeme())) {
                        methodsOrdenados.add(methodTable.get(m.methodNameToken.getLexeme()));
                        methodsOrdenadosAUX.remove((methodTable.get(m.methodNameToken.getLexeme())));
                    }
                    else{
                        methodsOrdenados.add(m);
                    }
                }

            }
            methodsOrdenados.addAll(methodsOrdenadosAUX);
            for (int i=0; i<methodsOrdenados.size();i++){
                //set offset of each methodd
                methodsOrdenados.get(i).offSetMetodo=i;
            }
            //for each method in methoddOrdenados, print name
            for (Method m : methodsOrdenados) {
                System.out.println(m.methodNameToken.getLexeme() + "con su offset: "+m.offSetMetodo);
            }

            for (Method m : TS.getClase(clase.heredaDe.getLexeme()).methodTable.values()) {
                if (!methodTable.containsKey(m.methodNameToken.getLexeme())) {

                    methodTable.put(m.methodNameToken.getLexeme(), m);
                    //isStatic no deberia nunca ser null, deberia ser o true o false.
                } else if (methodTable.get(m.methodNameToken.getLexeme()).isStatic != null && m.isStatic != null || methodTable.get(m.methodNameToken.getLexeme()).isStatic == null && m.isStatic == null) {
                    if (methodTable.containsKey(m.methodNameToken.getLexeme()) && methodTable.get(m.methodNameToken.getLexeme()).getParametros().equals(m.getParametros()) && methodTable.get(m.methodNameToken.getLexeme()).type.token.getLexeme().equals(m.type.token.getLexeme())) {
                    }


                } else {
                    throw new SemanticException(m.methodNameToken, "Metodo de clase padre y metodo de clase hija poseen mismo nombre pero la cantidad/tipo de parametros difiere, ");
                }
            }
            clase.consolidatedMethods = true;
        }
    }


    //EL problema es que chequea herencia Circular con el tipo C, y C no existe.
    private void checkCircularInheritance(Clase clase) throws SemanticException {
        if (!this.classNameToken.getLexeme().equals("Object")) {
            if (!clase.heredaDe.getLexeme().equals("Object")) {
                if (herenciaDeUnaClase.contains(clase)) {
                    throw new SemanticException(clase.classNameToken, "Clase heredada circularmente");
                } else {
                    herenciaDeUnaClase.add(clase);
                    if (TS.getClase(clase.heredaDe.getLexeme()) != null) //Que sucederia si es nulo? ERROR?
                        checkCircularInheritance(TS.getClase(clase.heredaDe.getLexeme()));
                    else
                        throw new SemanticException(clase.heredaDe, "Clase heredada no existente!");
                }
            }
        }
    }


    public SymbolTable getSymbolTable() {
        return TS;
    }


    public void check() throws SemanticException {
        checkCircularInheritance(this);
        for (Method m : constructorTable.values()) {
            if (!(m.methodNameToken.getLexeme().equals("default") || m.methodNameToken.getLexeme().equals(classNameToken.getLexeme()))) {
                throw new SemanticException(m.methodNameToken, "El nombre del constructor no se corresponde con el nombre de la clase");
            }
        }
        for (Method m : methodTable.values()) {
            m.check();
        }

        for (Attribute a : attributeTable.values()) {
            a.check();
        }

        setOffsetAtributos();
    }

    private void setOffsetAtributos() {
        int contador = 1;
        int cantidadAtributosPadre = 0;
        if (TS.getClase(this.heredaDe.getLexeme()) != null) {
            cantidadAtributosPadre = TS.getClase(this.heredaDe.getLexeme()).attributeTable.size();
        }
        for (Attribute a : attributeTable.values()) {
            if (a.offSet == -1) {
                a.offSet = cantidadAtributosPadre + contador;
                contador++;
            }
        }
    }

    public String toString() {
        String toReturn = "";
        if (!this.classNameToken.getLexeme().equals("Object") && !this.classNameToken.getLexeme().equals("String") && !this.classNameToken.getLexeme().equals("System") && !this.classNameToken.getLexeme().equals("String")) {
            for (Method m : methodTable.values()) {
                toReturn += "\n" + ((m.toString()));

            }
            toReturn += "\n";
            for (Constructor c : constructorTable.values()) {
                toReturn += "\n" + ((c.toString()));
            }
        }
        return toReturn;
    }

    public void checkSemantico(SymbolTable symbolTable) throws SemanticException {
        for (Method m : methodTable.values()) {
            m.checkSemantico(symbolTable);
        }
    }

    public void generarCodigo(SymbolTable symbolTable) {

        generarVT();
        TS.writer.write(".CODE");
        for (Method m : methodTable.values()) {
            if (m.containerClass == this) {
                m.generarCodigo(symbolTable);
            }
        }
        for (Constructor c : constructorTable.values()) {
            c.generarCodigo(symbolTable);
        }

    }

    private void generarVT() {
        String DW = "DW ";
        int contador=0;
        Collection<Method> dynamicMethods = methodTable.values().stream().filter(m -> m.isStatic == null).toList();
        System.out.println(dynamicMethods);
        //now, sort DynamicMethods by method.offset
        dynamicMethods = dynamicMethods.stream().sorted(Comparator.comparing(Method::getOffSetMetodo)).toList();

        for (Method m : dynamicMethods) {
            if (contador>0) {
                DW +="," +m.etiquetaMetodo ;
            }
            else {
                DW += m.etiquetaMetodo + " ";
            }
            contador++;
        }

        if (dynamicMethods.size() == 0) {
            DW = "NOP";
        }

        TS.writer.write(".DATA ; VirtualTable");
        TS.writer.write("lblVT" + this.classNameToken.getLexeme() + ": " + DW);

    }
}
