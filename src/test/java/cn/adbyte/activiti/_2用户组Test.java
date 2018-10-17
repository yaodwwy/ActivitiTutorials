package cn.adbyte.activiti;

import org.activiti.engine.*;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.UUID;

@RunWith(SpringRunner.class)
@Import({ActivitiConfig.class})
public class _2用户组Test {


    @Autowired
    private IdentityService identityService;

    @Autowired
    private RuntimeService runService;

    @Test
    public void 创建组和用户() {

        for (int i = 0; i < 10; i++) {
            String random = UUID.randomUUID().toString().substring(0, 3);
            Group group = identityService.newGroup(random);
            group.setName("Group_" + random + i);
            group.setType("TYPE_" + random + i);
            identityService.saveGroup(group);
        }

        for (int i = 0; i < 50; i++) {
            String random = UUID.randomUUID().toString().substring(0, 3);
            User user = identityService.newUser(random);
            user.setFirstName("adam" + random + i);
            user.setLastName("yao" + random + i);
            user.setEmail("adam" + random + "@qq.com");
            user.setPassword(random);
            identityService.saveUser(user);
        }

    }

    @Test
    public void 查询组列表() {
        identityService.createGroupQuery().groupNameLike("%Group_8%").list().forEach(group -> System.out.println(group.getName()));
        System.out.println("======================");

        identityService.createGroupQuery().groupMember("0d2").list().forEach(group -> System.out.println(group.getName()));
    }

    @Test
    public void 查询用户列表() {
        identityService.createUserQuery().userLastNameLike("%adam8%").list().forEach(user -> System.out.println(user.getFirstName()));
    }

    @Test
    public void 分页查询用户列表() {
        identityService.createUserQuery().listPage(1,5).forEach(user -> System.out.println(user.getFirstName()));
    }

    @Test
    public void Sql查询用户列表() {
        identityService.createNativeUserQuery()
                .sql("SELECT * FROM ACT_ID_USER where FIRST_ LIKE #{name}")
                .parameter("name", "%adam8%")
                .list()
                .forEach(user -> System.out.println(user.getFirstName()));
    }
}
