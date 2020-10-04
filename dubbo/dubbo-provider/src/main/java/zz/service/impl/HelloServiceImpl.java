package zz.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.stereotype.Component;
import zz.service.HelloService;

@Service
@Component
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String name) {
        int count = 100;
        for (int i = 0; i < count; i++) {

        }
        return "Hello" + name;
    }
}