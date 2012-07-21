package syntax.function;

import interpreter.accessvar.VariableType;
import java.util.Comparator;

public class FunctionComparator implements Comparator<FunctionDeclaration> {

    @Override
    public int compare(FunctionDeclaration f1, FunctionDeclaration f2) {
        boolean t1 = f1.getName().equals("write") || f1.getName().equals("writeln");
        boolean t2 = f2.getName().equals("write") || f2.getName().equals("writeln");
        if (t1 || t2) {
            return t1 && t2 ? 0
                    : (t1 && !t2 ? -1 : 1);
        }
        int comp = f1.getName().compareTo(f2.getName());
        if (comp != 0) {
            return comp;
        }
        comp = f1.getArgumentsLength() - f2.getArgumentsLength();
        if (comp != 0) {
            return comp;
        }
        int size = f1.getArgumentsLength();
        for (int i = 0; i < size; i++) {
            t1 = f1.getArgumentType(i) != VariableType.ARRAY;
            t2 = f2.getArgumentType(i) != VariableType.ARRAY;
            if (t1 && !t2) {
                return -1;
            }
            if (!t1 && t2) {
                return 1;
            }
        }
        return 0;
    }

}