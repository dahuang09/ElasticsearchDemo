package com.damon.repository.user;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.StringUtils;
import org.springframework.stereotype.Component;

import com.damon.entity.user.User;

/**
 * @author damon.huang
 *
 */
@Component
public class UserRepository {

    public User findByUsername(String userId) {
        final List<User> users = new ArrayList<>();
        final User user1 = new User();
//        https://www.devglan.com/online-tools/bcrypt-hash-generator
//      core@123 -> $2a$04$HPTPbK.1Ec58yka1QQBzYeH11NgJT0coSonNY85fqTcFgmWT9hxVm
        user1.setPassword("$2a$04$HPTPbK.1Ec58yka1QQBzYeH11NgJT0coSonNY85fqTcFgmWT9hxVm");
        user1.setId("123");
        user1.setUsername("damon1");
        final User user2 = new User();
//        core@1234 -> $2a$04$GG0U/gaoGWoqexSoOSC46O.zjSoRofsOX2vOkwfFd4N5Hxb/bt31q
        user2.setPassword("$2a$04$GG0U/gaoGWoqexSoOSC46O.zjSoRofsOX2vOkwfFd4N5Hxb/bt31q");
        user2.setId("1234");
        user2.setUsername("damon2");
        users.add(user1);
        users.add(user2);
        for (final User user : users) {
            if (StringUtils.equals(userId, user.getUsername())) {
                return user;
            }
        }
        return null;
    }

    public List<User> findAll() {
        final List<User> users = new ArrayList<>();
        for (int i=1; i<=10000; i++) {
            final User user = new User();
            final String id = "DM" + i;
            final String username = "damon" + i;
            user.setId(id);
            user.setUsername(username);
            users.add(user);
        }
        return users;
    }
}
