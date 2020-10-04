package zz.job;

import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * @author zhangzheng
 * @date 2020/9/20
 */
@Configuration
public class ProductOperate {
    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private StepBuilderFactory steps;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Bean
    public Job productOperateJob(@Qualifier("masterProductOperateStep") Step step) {
        return jobs.get("productOperateJob")
                .incrementer(new RunIdIncrementer())
                .start(step)
                .build();
    }

    @Bean
    public Step masterProductOperateStep(@Qualifier("slaveProductOperateStep") Step slaveProductOperateStep) {
        return steps.get("masterProductOperateStep")
                .partitioner(slaveProductOperateStep.getName(), new CommonPartitioner() {
                    @Override
                    public int getCount() {
                        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM product", Integer.class);
                    }
                })
                .gridSize(20)
                .step(slaveProductOperateStep)
                .taskExecutor(new SimpleAsyncTaskExecutor())
                .build();
    }

    @Bean
    public Step slaveProductOperateStep(@Qualifier("productOperateItemReader") ItemReader productOperateItemReader) {
        return steps.get("slaveProductOperateStep")
                .<Object, Object>chunk(200)
                .reader(productOperateItemReader)
                .processor(new ItemProcessor<Object, Object>() {
                    @Override
                    public Object process(Object item) throws Exception {
                        return item;
                    }
                })
                .writer(new ItemWriter<Object>() {
                    @Override
                    public void write(List<?> items) throws Exception {}
                })
                .build();
    }

    @Bean
    @StepScope
    public ItemReader productOperateItemReader(@Value("#{stepExecutionContext[" + CommonPartitioner._MINRECORD + "]}") int minRecord,
                                               @Value("#{stepExecutionContext[" + CommonPartitioner._PAGESIZE + "]}") int pageSize) {
        return new ItemReader<Object>() {
            @Override
            public Object read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                List<Object> list = jdbcTemplate.query("SELECT id, name, `desc` FROM product LIMIT ?, ?", new Object[]{minRecord, pageSize}, new RowMapper<Object>() {
                    @Override
                    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return rs.getString(1);
                    }
                });
                System.out.println(Thread.currentThread().getName() + ":" + list);
                return list;
            }
        };
    }

    @Component
    public class MyCommandLineRunner implements CommandLineRunner {
        @Autowired
        private JobLauncher jobLauncher;
        @Autowired
        @Qualifier("productOperateJob")
        private Job productOperateJob;

        @Override
        public void run(String... args) throws Exception {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addDate("date", new Date())
                    .toJobParameters();
            jobLauncher.run(productOperateJob, jobParameters);
        }
    }
}
