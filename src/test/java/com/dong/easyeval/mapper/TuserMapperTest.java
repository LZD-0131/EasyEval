package com.dong.easyeval.mapper;

import com.dong.easyeval.entity.TUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Rollback
public class TuserMapperTest {

    @Autowired
    private TUserMapper tUserMapper;

    @Test
    public void testSelect() {
        System.out.println("----- selectAll method test ------");
        List<TUser> userList = tUserMapper.selectList(null);
        userList.forEach(System.out::println);
    }

    @Test
    public void testInsert() {
        System.out.println("----- insert method test ------");
        TUser user = TUser.builder()
                .wechatUserId("test_openid_" + System.currentTimeMillis())
                .username("娴嬭瘯鐢ㄦ埛")
                .wechatNickname("灏忔槑")
                .build();

        int result = tUserMapper.insert(user);
        System.out.println("褰卞搷琛屾暟: " + result);
        System.out.println("鎻掑叆鍚庣殑鐢ㄦ埛ID: " + user.getUserId());
    }
}
