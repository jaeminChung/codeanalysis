/**
 @classname Direct call
 @version 0.9
 */
public class DirectCallee {
    private String name;

    public DirectCallee(String s) {
        this.name = s;
    }
    /**
    @methodname callee method
    @version 0.9
     */
    public int calleeMethod(String s, String b, DirectCallee d) {
        DirectCall dc = new DirectCall();
        dc.call();
        return 0;
    }

    public String getString(long loong) {
        return String.valueOf(loong);
    }
}