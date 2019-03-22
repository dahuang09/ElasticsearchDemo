package com.damon.service.user;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.damon.constansts.ESIndexContext;
import com.damon.entity.user.User;
import com.damon.repository.user.UserRepository;
import com.damon.utils.ElasticsearchUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author damon.huang
 *
 */
@Service
@Slf4j
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ElasticsearchUtil elasticsearchUtil;

    private final AtomicLong totalProcessed = new AtomicLong();
    private final AtomicLong errorCount = new AtomicLong();

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        final User user = userRepository.findByUsername(userId);
        if(user == null){
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), getAuthority());
    }

    private List getAuthority() {
        return Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    public List<User> findAll() {
        final List<User> list = new ArrayList<>();
        userRepository.findAll().iterator().forEachRemaining(list::add);
        return list;
    }

    public void index(String username) throws IOException {
        final User user = userRepository.findByUsername(username);
        if (user == null) {
            return;
        }
        final List<ESIndexContext> indexContexts = new ArrayList<>();
        final ESIndexContext indexCtx = new ESIndexContext(user.getEntityName(), user.getId(), false);
        indexCtx.setPojo(user);
        indexContexts.add(indexCtx);
        elasticsearchUtil.batchIndex(indexContexts);

    }

    public String getDocFromES(String id) throws IOException {
        return elasticsearchUtil.getDoc("User", id);
    }

    public List<String> queryFromES(String content) throws IOException {
        return elasticsearchUtil.queryFromES(content);
    }

    public String batchSyncUserToES() throws Exception {
        final List<User> users = findAll();
        log.info("batchSyncUserToES start: thread={}, total={}", 32, users.size());
        final BlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<Runnable>(1024);
        final RejectedExecutionHandler rejectedExecutionHandler = new ThreadPoolExecutor.CallerRunsPolicy();
        final ExecutorService executeService = new ThreadPoolExecutor(32, 64, 0L, TimeUnit.MILLISECONDS, blockingQueue, rejectedExecutionHandler);
        if (CollectionUtils.isEmpty(users)) {
            return "201";
        }
        final long startTime = System.currentTimeMillis();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(60 * 1000);
                    } catch (final Exception e) {
                    }
                    final long usedTime = (System.currentTimeMillis() - startTime);
                    log.info("## current used time: {}, total processed vpo: {}, avg: {} docs/ms, error: {}",
                            usedTime, totalProcessed,  totalProcessed.get()/usedTime, errorCount.get());
                }
            }
        }).start();

        for (final User user : users) {
            final Runnable runable = new Runnable() {
                @Override
                public void run() {
                    try {
                        index(user.getUsername());
                        totalProcessed.getAndAdd(1);
                    } catch (final IOException e) {
                        log.error("Fail to index user name=" + user.getUsername(), e);
                    }
                }
            };
            executeService.submit(runable);
        }
        executeService.shutdown();
        try {
            if (executeService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS)) {
                log.info("total index user={}, failure user={}", totalProcessed, errorCount);
            }
        } catch (final InterruptedException e) {
            log.error("Cannot shutdown thread pool.", e);
            throw e;
        }
        return "201";
    }



}
