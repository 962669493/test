package zz;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author zhangzheng
 * @date 2020/9/11
 */
public class InvokeAnyTest implements Callable<Integer> {
    private int param;

    public InvokeAnyTest(int param) {
        this.param = param;
    }

    @Override
    public Integer call() throws Exception {
        if (param != 4) {
            throw new InterruptedException("123456");
        }
        return param;
    }

    public static void main(String[] args) {
        List<Callable<Integer>> tasks = new ArrayList<Callable<Integer>>();
        tasks.add(new InvokeAnyTest(1));
        tasks.add(new InvokeAnyTest(2));
        tasks.add(new InvokeAnyTest(3));
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(0);
        Integer integer = null;
        try {
            integer = scheduledExecutorService.invokeAny(tasks);
        } catch (InterruptedException e) {
            System.out.println(1);
            e.printStackTrace();
        } catch (ExecutionException e) {
            System.out.println(2);
        }
        System.out.println(integer);
    }
}
