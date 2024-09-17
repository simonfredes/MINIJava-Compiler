package TablaHash;

import java.util.HashSet;
import java.util.Set;

public class KeyW_Table {
    private Set<String> keywords;

    public KeyW_Table(){
        keywords = new HashSet<>();
        keywords.add("class");
        keywords.add("public");
        keywords.add("extends");
        keywords.add("static");
        keywords.add("void");
        keywords.add("boolean");
        keywords.add("char");
        keywords.add("int");
        keywords.add("if");
        keywords.add("else");
        keywords.add("while");
        keywords.add("return");
        keywords.add("var");
        keywords.add("switch");
        keywords.add("case");
        keywords.add("break");
        keywords.add("null");
        keywords.add("this");
        keywords.add("new");
        keywords.add("true");
        keywords.add("false");
        keywords.add("float");

    }

    public boolean exist(String value){
        return keywords.contains(value);
    }

}