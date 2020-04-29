/**
 @classname Direct call
 @version 0.9
 */
public class DirectCallee {
    /**
    @methodname callee method
    @version 0.9
     */
    public int calleeMethod(String s, String b, DirectCallee d) {
        DirectCall dc = new DirectCall();
        dc.call();
        return 0;
    }
}