package parser;

import java.util.ArrayList;
import syntax.function.Function;
import syntax.SyntaxTree;
import syntax.function.SpecialFunctionBehavior;

public class SpecialFunctions {
    private static ArrayList<SpecialFunctionBehavior> specialFunctions 
            = new ArrayList<SpecialFunctionBehavior>();
    
    public static void addSpecialFunction( SpecialFunctionBehavior f ) {
        specialFunctions.add(f);
    }
    
    public static void clear() {
        specialFunctions.clear();
    }
    
    public static void checkSyntaxTree(SyntaxTree tree) {
        for (SpecialFunctionBehavior f: specialFunctions) {
            tree.setSpecialFunctionBehavior(f);
        }
//        Iterator<Function> iter = specialFuncTree.iterator();
//        while ( iter.hasNext() ) {
//            Function f = iter.next();
////            if ( tree.functionsTree.contains(f) ) {
////                System.out.println(f.name);
////                Function fOld = tree.functionsTree.ceiling(f);
////                f.copyAllFields(fOld);
////                
////                tree.replaceFunction(f);
////            }
//        }
        
//        Iterator<Function> treeIter = tree.functionsTree.iterator();
//        Iterator<Function> specialIter = specialFuncTree.iterator();
//        
//        if (!treeIter.hasNext() || !specialIter.hasNext()) {
//            return;
//        }
//        Function treeFunc = treeIter.next();
//        Function specialFunc = specialIter.next();
//        while (true) {
//            int comp = treeFunc.compareTo(specialFunc);
//            if (comp == 0) {
//                treeFunc.functionSpecialTask = specialFunc.functionSpecialTask;
//                
////                System.out.println( treeFunc.name );
////                System.out.println( specialFunc.name );
////                System.out.println( specialFunc.functionSpecialTask );
//                if (!treeIter.hasNext() || !specialIter.hasNext()) {
//                    return;
//                }
//                treeFunc = treeIter.next();
//                specialFunc = specialIter.next();
//            } else if (comp < 0) {
//                if (!treeIter.hasNext()) {
//                    return;
//                }
//                treeFunc = treeIter.next();
//            } else {
//                if (!specialIter.hasNext()) {
//                    return;
//                }
//                specialFunc = specialIter.next();
//            }
//        }
    }
}
