package com.github.jvm.concurrent;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * 实现的简易共享锁
 *
 * @author : Crab2Died
 * 2018/03/06  11:41:49
 * @see java.util.concurrent.Semaphore
 */
public class SharedLock {

    private final Sync sync;

    public SharedLock(int count) {
        this.sync = new Sync(count);
    }

    public SharedLock() {
        this.sync = new Sync(1);
    }

    static final class Sync extends AbstractQueuedSynchronizer {

        Sync(int count) {
            if (count <= 0)
                throw new IllegalArgumentException("count must large 0");
            setState(count);
        }

        @Override
        protected int tryAcquireShared(int arg) {

            for (; ; ) {
                int current = getState();
                int newCount = current - arg;
                if (newCount < 0 || compareAndSetState(current, newCount))
                    return newCount;
            }
        }

        @Override
        protected boolean tryReleaseShared(int arg) {

            for (; ; ) {
                int current = getState();
                int newCount = current + arg;
                if (compareAndSetState(current, newCount))
                    return true;
            }
        }
    }

    public void lock() {
        sync.acquireShared(1);
    }

    public void unlock() {
        sync.releaseShared(1);
    }

}
