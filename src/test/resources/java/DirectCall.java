/**
 * @classname Direct call
 * @author hijam
 * @version 1.0
 */
public class DirectCall {
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
    }

    private String call(List<String> list) {
        for(String s : list) {
            System.out.println(s);
        }

        return "OK";
    }
}
