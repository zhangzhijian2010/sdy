package lock.readwrite;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhijian.zhang@chelaile.net.cn on 2019/3/22.
 */
public class ReadWriteLock2 {
    private Map<Thread, Integer> readingThreads = new HashMap<>();
    private int writers = 0;
    private int writeRequests = 0;

    public synchronized void lockRead()
            throws InterruptedException {
        Thread callingThread = Thread.currentThread();
        while (!canGrantReadAccess(callingThread)) {
            wait();
        }
        readingThreads.put(callingThread,
                (getReadAccessCount(callingThread) + 1));
    }

    public synchronized void unlockRead() {
        Thread callingThread = Thread.currentThread();
        int accessCount = getReadAccessCount(callingThread);
        if (accessCount == 1) {
            readingThreads.remove(callingThread);
        } else {
            readingThreads.put(callingThread, (accessCount - 1));
        }
        notifyAll();
    }

    private boolean canGrantReadAccess(Thread callingThread) {
        if (writers > 0) return false;
        if (isReader(callingThread)) return true;
        if (writeRequests > 0) return false;
        return true;
    }

    private int getReadAccessCount(Thread callingThread) {
        Integer accessCount = readingThreads.get(callingThread);
        if (accessCount == null) return 0;
        return accessCount.intValue();
    }

    private boolean isReader(Thread callingThread) {
        return readingThreads.get(callingThread) != null;
    }

    private Thread writingThread = null;

    public synchronized void lockWrite()
            throws InterruptedException{
        writeRequests++;
        Thread callingThread = Thread.currentThread();
        while(!canGrantWriteAccess(callingThread)){
            wait();
        }
        writeRequests--;
        //writeAccesses++;
        writingThread = callingThread;
    }

    public synchronized void unlockWrite()
            throws InterruptedException{
        //writeAccesses--;
        //if(writeAccesses == 0){
        //    writingThread = null;
        //}
        notifyAll();
    }

    private boolean canGrantWriteAccess(Thread callingThread){
        if(hasReaders()) return false;
        if(writingThread == null)    return true;
        if(!isWriter(callingThread)) return false;
        return true;
    }

    private boolean hasReaders(){
        return readingThreads.size() > 0;
    }

    private boolean isWriter(Thread callingThread){
        return writingThread == callingThread;
    }
}
