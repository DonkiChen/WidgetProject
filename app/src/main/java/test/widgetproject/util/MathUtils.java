package test.widgetproject.util;

/**
 * Created on 2018/4/23.
 *
 * @author Administrator
 */
public class MathUtils {
    public static boolean between(double number, double small, double big) {
        return small <= number && number <= big;
    }
}
