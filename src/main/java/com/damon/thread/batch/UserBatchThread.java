// Copyright (c) 1998-2019 Core Solutions Limited. All rights reserved.
// ============================================================================
// CURRENT VERSION CNT.5.0.1
// ============================================================================
// CHANGE LOG
// CNT.5.0.1 : 2019-XX-XX, damon.huang, creation
// ============================================================================
package com.damon.thread.batch;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author damon.huang
 *
 */
@Slf4j
@Data
public class UserBatchThread extends Thread {

    private volatile boolean isCancelled;
    private long startTime;
    private AtomicLong errorCount;
    private AtomicLong totalProcessed;

    public UserBatchThread(AtomicLong totalProcessed, AtomicLong errorCount) {
        this.totalProcessed = totalProcessed;
        this.errorCount = errorCount;
    }

    /* (non-Javadoc)
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        while (!isCancelled) {
            try {
                Thread.sleep(60 * 1000);
            } catch (final Exception e) {
            }
            final long usedTime = (System.currentTimeMillis() - startTime);
            log.info("## current used time: {}ms, total processed user: {}, avg: {} ms, error: {}",
                    usedTime, totalProcessed,  totalProcessed.get()/usedTime, errorCount);
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Thread#start()
     */
    @Override
    public synchronized void start() {
        startTime = System.currentTimeMillis();
        super.start();
    }
}
