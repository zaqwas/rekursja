package syntax;

import interpreter.accessvar.AccessVar;

public class VarDeclaration {

    private String name;
    private AccessVar accessVar;
    public VarDeclaration(String name, AccessVar accessVar) {
        this.name = name;
        this.accessVar = accessVar;
    }

    public VarDeclaration(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public AccessVar getAccessVar() {
        return accessVar;
    }

}