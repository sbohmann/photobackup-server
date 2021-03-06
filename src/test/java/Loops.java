import org.junit.Test;

public class Loops {
    @Test
    public void breakOuterLoop() {
        boolean[] stop = new boolean[1];
        for (int i_ = 0; i_ < 5 && !stop[0]; i_++) {
            int i = i_;
            //noinspection TrivialFunctionalExpressionUsage
            ((Runnable) () -> {
                for (int j = 0; j < 5; j++) {
                    if (i * j > 6) {
                        System.out.println("Breaking");
                        stop[0] = true;
                        break;
                    }
                    System.out.println(i + " " + j);
                }
            }).run();
        }
        System.out.println("Done");
    }
}
