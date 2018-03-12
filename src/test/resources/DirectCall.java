/*
 * @classname Direct call
 * @author hijam
 * @version 1.0
 */
public class DirectCall {
    /*
     * @methodname call
     * @return void
     * @param void
     * @version 1.0
     */
    public void call() {
        DirectCallee callee = new DirectCallee();
        int i = callee.calleeMethod();
        i = Integer.valueOf("3");
    }
}