package zz;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author zhangzheng
 * @date 2020/10/3
 */
public class Main {
    private static final Logger LOG= LogManager.getLogger(Main.class);
    public static void main(String[] args) {
        System.out.println(LOG.isInfoEnabled());
        System.out.println(LOG.isWarnEnabled());
        if(LOG.isDebugEnabled()){
            LOG.debug("123456");
        }

    }
}
