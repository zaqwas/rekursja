package interpreter;

import codeeditor.CodeEditor;
import instanceframe.InstanceFrame;
import instancetree.TreeOfInstances;
import java.math.BigInteger;
import lesson.Lesson;
import mainclass.MainClass;
import stack.StackOfInstances;
import statistics.Statistics;
import stringcreator.StringCreator;
import syntax.SyntaxNode;
import syntax.SyntaxNodeIndexed;
import syntax.SyntaxTree;
import syntax.expression.Call;
import syntax.function.Function;

public class InterpreterThread extends Thread {

    private static enum RequestStatus {
        RUN, PAUSE, STEP_INTO, STEP_OVER, STEP_OUT, FAST, STOP
    }
    private static enum RunStatus {
        PAUSED, RUNNING, STOPPED, DELAY;
    }
    
    private RunStatus runStatus;
    private RequestStatus reqStatus;
    private int delayTime = 1000;
    private SyntaxTree syntaxTree;
    
    private Instance mainInstance;
    private Instance currentInstance;
    private Instance stopInstance;
    
    private MainClass mainClass;
    private CodeEditor editorMgr;
    private InstanceFrame instanceFrame;
    private StackOfInstances stack;
    private TreeOfInstances tree;
    private Statistics statistics;
    private Lesson lesson;
    
    
    //<editor-fold defaultstate="collapsed" desc="Get state of thread methods">
    public boolean isStopped() {
        return runStatus == RunStatus.STOPPED;
    }
    public boolean isPaused() {
        return runStatus == RunStatus.PAUSED;
    }
    public boolean isRunning() {
        return runStatus == RunStatus.RUNNING;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Request functions">
    public void requestRun() {
        reqStatus = RequestStatus.RUN;
    }
    public void requestPause() {
        reqStatus = RequestStatus.PAUSE;
    }
    public void requestStepInto() {
        reqStatus = RequestStatus.STEP_INTO;
    }
    public void requestStepOver() {
        reqStatus = RequestStatus.STEP_OVER;
        stopInstance = currentInstance;
    }
    public void requestStepOut() {
        reqStatus = RequestStatus.STEP_OUT;
        stopInstance = currentInstance.getParentInstance();
    }
    public void requestFast() {
        reqStatus = RequestStatus.FAST;
    }
    public void requestStop() {
        reqStatus = RequestStatus.STOP;
    }
    //</editor-fold>
    

    public InterpreterThread(SyntaxTree syntaxTree, MainClass mainClass) {
        this.syntaxTree = syntaxTree;
        this.mainClass = mainClass;
        
        editorMgr = mainClass.getEditor();
        instanceFrame = mainClass.getInstanceFrame();
        tree = mainClass.getTreeOfInstances();
        stack = mainClass.getStackOfInstances();
        statistics = mainClass.getStatistics();
        lesson = mainClass.getLesson();
        
        syntaxTree.initGlobalVars();
        mainInstance = new Instance(syntaxTree.main);

        currentInstance = mainInstance;
        reqStatus = RequestStatus.PAUSE;
        runStatus = RunStatus.PAUSED;
    }
    
    private boolean checkPauseStatus() {
        if (reqStatus == RequestStatus.PAUSE) {
            runStatus = RunStatus.PAUSED;
            return true;
        }
        if (reqStatus == RequestStatus.RUN && delayTime>0 ) {
            runStatus = RunStatus.DELAY;
            return true;
        }
        return false;
    }

    @Override
    public void run() {
        SyntaxNode nextNode = mainInstance.function;
        SyntaxNode prevNode = null;
        
        Instance topStackInstance = mainInstance;
        Instance realInstance = mainInstance;
        
        boolean newInstance = false;
        boolean markTopStackInstance = false;
        StringCreator statusCreator = null;
        

        lesson.threadStart(this);
        instanceFrame.start(syntaxTree, mainInstance);
        stack.start(mainInstance);
        tree.start(mainInstance);
        statistics.start();

        while (currentInstance != null) {
            if (reqStatus == RequestStatus.STOP) {
                currentInstance = null;
                break;
            }
            
            //<editor-fold defaultstate="collapsed" desc="Pause">
            if (runStatus == RunStatus.DELAY || runStatus == RunStatus.PAUSED) {
                if (prevNode instanceof SyntaxNodeIndexed) {
                    SyntaxNodeIndexed idx = (SyntaxNodeIndexed) prevNode;
                    editorMgr.paintStatement(idx.getLeftIndex(), idx.getRightIndex());
                }
                if ( statusCreator!=null ) {
                    mainClass.setStatus(statusCreator);
                    statusCreator = null;
                }
                statistics.update();
                instanceFrame.update(topStackInstance);
                tree.update(topStackInstance);
                stack.update(topStackInstance);
                if ( markTopStackInstance ) {
                    tree.mark();
                    stack.mark();
                }
                lesson.pauseStart(prevNode, delayTime);
                if ( runStatus == RunStatus.DELAY ) {
                    try {
                        sleep(delayTime);
                    } catch (InterruptedException ex) {
                    }
                    if (reqStatus == RequestStatus.PAUSE) {
                        runStatus = RunStatus.PAUSED;
                    } else {
                        runStatus = RunStatus.RUNNING;
                    }
                }
                if ( runStatus == RunStatus.PAUSED ) {
                    while (reqStatus == RequestStatus.PAUSE) {
                        try {
                            sleep(10);
                        } catch (InterruptedException ex) {
                        }
                    }
                    runStatus = RunStatus.RUNNING;
                }
                lesson.pauseStop(prevNode);
                editorMgr.clrearStatement();
                if (markTopStackInstance) {
                    tree.unmark();
                    stack.unmark();
                    markTopStackInstance = false;
                }
                if (reqStatus == RequestStatus.STOP) {
                    continue;
                }
            }
            //</editor-fold>
            
            if ( topStackInstance != currentInstance ) {
                if ( newInstance ) {
                    currentInstance = topStackInstance;
                    newInstance = false;
                } else {
                    topStackInstance = currentInstance;
                }
            }
            if (reqStatus == RequestStatus.STEP_OVER && currentInstance == stopInstance) {
                reqStatus = RequestStatus.PAUSE;
            }
            if (reqStatus == RequestStatus.STEP_INTO) {
                reqStatus = RequestStatus.PAUSE;
            }

            if (nextNode == null) {
                
                //<editor-fold defaultstate="collapsed" desc="Remove instance from stack">
                Call call = realInstance.getCallNode();
                Function function = realInstance.getFunction();
                BigInteger returnedValue = realInstance.getReturnedValue();
                realInstance.removeFromStack();
                realInstance = realInstance.getParentInstance();
                currentInstance = realInstance;
                if (realInstance == null) {
                    break;
                }
                if ( !function.isVoid() ) {
                    if ( returnedValue==null ) {
                        //TODO error
                    }
                    realInstance.pushStack(returnedValue);
                }
                if ( (reqStatus == RequestStatus.STEP_OVER || reqStatus == RequestStatus.STEP_OUT) &&
                        realInstance == stopInstance) {
                    reqStatus = RequestStatus.PAUSE;
                }
                if ( function.isStoppedAfterCall() ) {
                    if ( checkPauseStatus() ) {
                        statusCreator = function.getStatusCreatorAfterCall(realInstance);
                        markTopStackInstance = function.isAddedToHistory();
                    }
                }
                prevNode = call;
                nextNode = prevNode.commit(realInstance);
                //</editor-fold>
                
            } else if (nextNode instanceof Call) {
                
                //<editor-fold defaultstate="collapsed" desc="Add instance to stack">
                Call call = (Call) nextNode;
                Function function = call.getFunction();
                boolean historyFunction = function.isAddedToHistory();
                realInstance = new Instance(realInstance, call, historyFunction);
                
                if ( historyFunction ) {
                    topStackInstance = realInstance;
                    newInstance = true;
                }
                if ( function.isStopedBeforeCall() ) {
                    if ( checkPauseStatus() ) {
                        statusCreator = function.getStatusCreatorBeforeCall(realInstance);
                        markTopStackInstance = historyFunction;
                    }
                }
                if ( function.isAddedToStatistics() ) {
                    statistics.addFunciton(function);
                }
                prevNode = nextNode;
                nextNode = function;
                //</editor-fold>
                
            } else {
                if (nextNode.isStopNode()) {
                    boolean pause = checkPauseStatus();
                    if ( pause ) {
                        //statusCreator = nextNode.getStatusCreator(realInstance);
                    }
                }
                if ( nextNode.isStatisticsNode() ) {
                    statistics.addSyntaxNode(nextNode);
                }
                prevNode = nextNode;
                nextNode = nextNode.commit(realInstance);
            }
        }
        
        
        runStatus = RunStatus.STOPPED;
        mainClass.setStatus( new StringCreator() {
            @Override
            public String getString(int maxWidth) {
                assert fontMetrics!=null : "FontMetrics not initialized.";
                return "Program zatrzymany";
            }
        });
        tree.update(mainInstance);
        stack.clear();
        statistics.update();
        lesson.threadStop();
        mainClass.clearThread();
    }

}
