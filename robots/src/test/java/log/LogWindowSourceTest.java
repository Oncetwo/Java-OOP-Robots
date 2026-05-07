package log;

import org.junit.jupiter.api.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class LogWindowSourceTest {

    @Test
    public void testAppendAndSizeLimit() {
        LogWindowSource src = new LogWindowSource(3);
        src.append(LogLevel.Debug, "one");
        src.append(LogLevel.Debug, "two");
        src.append(LogLevel.Debug, "three");
        src.append(LogLevel.Debug, "four"); // oldest must be removed, size should be 3

        assertEquals(3, src.size());
        boolean foundOne = false;
        for (LogEntry e : src.all()) {
            if ("one".equals(e.getMessage())) foundOne = true;
        }
        assertFalse(foundOne, "oldest entry should have been removed");
    }

    @Test
    public void testListenersNotifiedAndUnregister() {
        LogWindowSource src = new LogWindowSource(10);

        AtomicInteger countA = new AtomicInteger(0);
        AtomicInteger countB = new AtomicInteger(0);

        LogChangeListener a = () -> countA.incrementAndGet();
        LogChangeListener b = () -> countB.incrementAndGet();

        src.registerListener(a);
        src.registerListener(b);

        src.append(LogLevel.Debug, "msg1");
        assertEquals(1, countA.get());
        assertEquals(1, countB.get());

        src.unregisterListener(a);
        src.append(LogLevel.Debug, "msg2");
        assertEquals(1, countA.get(), "A should not be notified after unregister");
        assertEquals(2, countB.get(), "B should still be notified");
    }
}