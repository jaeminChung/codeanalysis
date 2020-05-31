import jdk.jfr.internal.Logger;

import java.io.Serializable;
import java.util.List;
import java.io.File;

/**
 * @classname Direct call
 * @author jmchung
 * @version 1.0
 */
public class DirectCall implements Serializable, CallInterface{
    String globalB = "global b";
    /*
     * @methodname call
     * @return void
     * @param void
     * @version 1.0
     */
    public void call() {
        DirectCallee callee = new DirectCallee();
        String s = "abc";
        String methodName = "foo"
        s = s + methodName;
        int i = callee.calleeMethod(s, globalB, new DirectCallee());
        i = Integer.valueOf("3");
        {
            String s1 = "진짜 블럭";
        }
    }

    private String call(List<String> list) {
        for( ; ; ) {
            //Nothing
        }
        for(String s : list.subList(0, 1)) {
            System.out.println(s);
        }
        for (int a = 0, b[] = {1}, c[][] = {{1}, {2}}; a < list.size(); a++) {
            System.out.println(String.valueOf(a));
        }
        for (String s : list)
            System.out.println("블럭 없는 For문");


        return "OK";
    }

    private void NestedCall() {
        DirectCallee callee = new DirectCallee();
        int i = callee.calleeMethod(callee.getString(4L), callee.getString(500L), new DirectCallee("s/ddd/llll"));
        String s = callee.getString(4L).toString();
    }

    private void checkIfStatement(String check) {
        if("if".equals(check)) {
            String sIf = check;
        } else if("else if".equals(check)) {
            String sElseIf = check;
        } else {
            String sElse = check;
        }
    }

    private void checkCaseStatement(String condition) throws Exception {
        switch (condition) {
            case "aa" :
            case "bb" :
                String b = "bbb";
                System.err.println(b);
                break;
            case "cc":
            default:
                throws new Exception("exception");
                break;
        }
    }

    private void catchException() {
        try(File f = new File("")) {
            checkCaseStatement("");
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("OK");
        }

        try {
            String noresource;
        } catch (Exception e) {
            String withoutFinally;
        }
    }
}
