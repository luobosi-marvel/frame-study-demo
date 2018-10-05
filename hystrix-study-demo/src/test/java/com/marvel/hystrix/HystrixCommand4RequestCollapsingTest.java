package com.marvel.hystrix;

import com.netflix.hystrix.HystrixEventType;
import com.netflix.hystrix.HystrixInvokableInfo;
import com.netflix.hystrix.HystrixRequestLog;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import org.junit.Test;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * 相邻两个请求可以自动合并的前提是两者足够“近”：启动执行的间隔时间足够小，默认10ms
 * todo: 将请求合并
 */
public class HystrixCommand4RequestCollapsingTest {
        @Test
        public void testCollapser() throws Exception {
            HystrixRequestContext context = HystrixRequestContext.initializeContext();
            try {
            	
                Future<String> f1 = new HystrixCollapserDemo(1).queue();
                Future<String> f2 = new HystrixCollapserDemo(2).queue();
//                System.out.println(new HystrixCollapserDemo(1).execute());	// 这条很可能会合并到f1和f2的批量请求中
//                System.out.println(new HystrixCollapserDemo(1).execute());	// 由于上面有IO打印，这条很可能不会合并到f1和f2的批量请求中
                Future<String> f3 = new HystrixCollapserDemo(3).queue();
                Future<String> f4 = new HystrixCollapserDemo(4).queue();

                Future<String> f5 = new HystrixCollapserDemo(5).queue();
                // f5和f6，如果sleep时间够小则会合并，如果sleep时间够大则不会合并，默认10ms
                TimeUnit.MILLISECONDS.sleep(13);
                Future<String> f6 = new HystrixCollapserDemo(6).queue();              
          
                System.out.println(f1.get());
                System.out.println(f2.get());
                System.out.println(f3.get());
                System.out.println(f4.get());
                System.out.println(f5.get());
                System.out.println(f6.get());
                // 下面3条都不在一个批量请求中
//                System.out.println(new HystrixCollapserDemo(7).execute());
//                System.out.println(new HystrixCollapserDemo(8).queue().get());
//                System.out.println(new HystrixCollapserDemo(9).queue().get());

                // note：numExecuted表示共有几个命令执行，1个批量多命令请求算一个，这个实际值可能比代码写的要多，因为due to non-determinism of scheduler since this example uses the real timer
                int numExecuted = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size();
                System.out.println("num executed: " + numExecuted);
                int numLogs = 0;
                for (HystrixInvokableInfo<?> command : HystrixRequestLog.getCurrentRequest().getAllExecutedCommands()) {
                    numLogs++;
                    
                    // assert the command is the one we're expecting
//                    assertEquals("CollepsingKey", command.getCommandKey().name());

                    System.err.println(command.getCommandKey().name() + " => command.getExecutionEvents(): " + command.getExecutionEvents());

                    // confirm that it was a COLLAPSED command execution
//                    assertTrue(command.getExecutionEvents().contains(HystrixEventType.COLLAPSED));
                    assertTrue(command.getExecutionEvents().contains(HystrixEventType.SUCCESS));
                }
                assertEquals(numExecuted, numLogs);
            } finally {
                context.shutdown();
            }
        }
}