package com.damon.service.user;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.damon.constansts.ESIndexContext;
import com.damon.entity.user.User;
import com.damon.repository.user.UserRepository;
import com.damon.utils.ElasticsearchUtil;

/**
 * @author damon.huang
 *
 */
@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ElasticsearchUtil elasticsearchUtil;

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

    public List findAll() {
        final List list = new ArrayList<>();
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



}
