public class DirectCall {
    public void call() {
        DirectCallee callee = new DirectCallee();
        int i = callee.calleeMethod();
        i = Integer.valueOf("3");
    }
}