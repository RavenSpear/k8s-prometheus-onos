package cn.seu.p4virtex.nvh;

import org.onosproject.core.CoreServiceAdapter;
import org.onosproject.core.IdGenerator;

import java.util.concurrent.atomic.AtomicLong;

class TestCoreService extends CoreServiceAdapter {

    @Override
    public IdGenerator getIdGenerator(String topic) {
        return new IdGenerator() {
            private AtomicLong counter = new AtomicLong(100);

            @Override
            public long getNewId() {
                return counter.getAndIncrement();
            }
        };
    }
}