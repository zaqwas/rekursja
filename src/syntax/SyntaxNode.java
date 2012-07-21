package syntax;

import interpreter.Instance;
import java.util.ArrayList;
import stringcreator.StringCreator;

public abstract class SyntaxNode {
    public SyntaxNode jump;
    
    public abstract SyntaxNode commit( Instance instance );
    
    public boolean isStopNode() {
        return false;
    }
    public boolean isStatisticsNode() {
        return false;
    }
    public StringCreator getStatusCreator(Instance instance) {
        return null;
    }
    
    public interface Committer {
        public SyntaxNode commit(Instance instance);
    }
    
    public abstract void printDebug();
    public static void printDebugList(ArrayList<SyntaxNode> list) {
        for (SyntaxNode sn:list) {
            sn.printDebug();
        }
    }
}

/*
 * type:
 * 0  !
 * 1  - (unar)
 * 2  *
 * 3  /
 * 4  %
 * 5  + (binar)
 * 6  -
 * 7  <
 * 8  >
 * 9  <=
 * 10 >=
 * 11 ==
 * 12 !=
 * 13 &&
 * 14 ||
 * 15 ( (temp)
 * 
 * 20 call
 * 21 var
 * 22 const
 * 23 string
 * 
 * 30 =
 * 31 *=
 * 32 /=
 * 33 %=
 * 34 &=
 * 35 |=
 * 36 +=
 * 37 -=
 * 
 * 40 ++
 * 41 --
 * 
 * 50 for
 * 51 while
 * 52 do while
 * 53 if
 * 
 * 60 return
 * 61 continue
 * 62 break
 * 
 * 70 function
 * 
*/