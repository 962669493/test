package zz;

import com.google.common.base.Stopwatch;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangzheng
 * @date 2020/10/3
 */
public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    public static final String STR = ".";
    public static final char ESC = (char) 27;
    private static BufferedWriter bufferedWriter;
    private static File saveTargetAndFileName;
    private static String suffixes;
    private static String saveFilePath;
    private static String targetFilePath;

    static {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream("config/application.properties"));
            saveFilePath = (String) properties.get("saveFilePath");
            suffixes = (String) properties.get("suffixes");
            targetFilePath = (String) properties.get("targetFilePath");
            String saveFileName = (String) properties.get("saveFileName");
            saveTargetAndFileName = new File(saveFileName);
            saveTargetAndFileName.getParentFile().mkdirs();
            bufferedWriter = new BufferedWriter(new FileWriter(saveTargetAndFileName));
            logger.info("properties:" + properties);
        } catch (IOException e) {
            logger.info(e.getMessage(), e);
        }
    }

    public static void main(String[] args) throws IOException {
        logger.info("开始进行查找和复制");
        Stopwatch started = Stopwatch.createUnstarted();
        started.start();
        findAndCopy(new File(targetFilePath));
        bufferedWriter.flush();
        bufferedWriter.close();
        started.stop();
        logger.info("查找和复制结束，共耗时[" + started.elapsed(TimeUnit.MILLISECONDS) + "ms]");

        started.start();
        logger.info("开始修改图片");
        deal();
        started.stop();
        logger.info("修改图片结束，共耗时[" + started.elapsed(TimeUnit.MILLISECONDS) + "ms]");
    }

    public static void findAndCopy(File file) throws IOException {
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            int length = 0;
            if (files == null || (length = files.length) <= 0) {
                return;
            }
            for (int i = 0; i < length; i++) {
                findAndCopy(files[i]);
            }
        } else {
            String fileAbsolutePath = file.getAbsolutePath();
            String suffix = fileAbsolutePath.substring(fileAbsolutePath.lastIndexOf(STR) + 1);
            if (suffixes.contains(suffix)) {
                String copyFileName = System.currentTimeMillis() + STR + suffix;
                FileUtils.copyFile(file, new File(saveFilePath + File.separator + copyFileName));
                bufferedWriter.write(new TargetAndFileName(fileAbsolutePath, copyFileName).toString());
                bufferedWriter.newLine();
            }
        }
    }

    public static void deal() throws IOException {
        LineIterator lineIterator = FileUtils.lineIterator(saveTargetAndFileName);
        for (; lineIterator.hasNext(); ) {
            String targetAndFileName = lineIterator.nextLine();
            if (StringUtils.isEmpty(targetAndFileName)) {
                continue;
            }
            String[] strings = targetAndFileName.split(String.valueOf(ESC));
            String target = strings[0];
            String fileName = strings[1];
            String source = saveFilePath + File.separator + fileName;
            logger.info("开始处理[" + source + ":" + target + "]");
            ImageUtils.convert(new File(source), new File(target));
        }
    }

    static class TargetAndFileName {
        private String target;
        private String fileName;

        public TargetAndFileName(String target, String fileName) {
            this.target = target;
            this.fileName = fileName;
        }

        @Override
        public String toString() {
            return target + ESC + fileName;
        }
    }
}
