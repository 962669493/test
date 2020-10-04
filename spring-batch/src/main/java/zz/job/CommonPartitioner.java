package zz.job;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangzheng
 * @date 2020/9/20
 */
public abstract class CommonPartitioner implements Partitioner {
    public static final String _MINRECORD = "_minRecord";
    public static final String _PAGESIZE = "_pageSize";

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        Map<String, ExecutionContext> result = new HashMap(2);
        Integer count = getCount();
        double partition = Math.ceil(count / gridSize);
        for (int i = 0; i < partition; i++) {
            ExecutionContext context = new ExecutionContext();
            context.put(_MINRECORD, i * gridSize);
            context.put(_PAGESIZE, gridSize);
            result.put("partition" + i, context);
        }
        return result;
    }

    /**
     * @return
     */
    public abstract int getCount();
}
