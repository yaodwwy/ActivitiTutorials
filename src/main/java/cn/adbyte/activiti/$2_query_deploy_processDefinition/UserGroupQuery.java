package cn.adbyte.activiti.$2_query_deploy_processDefinition;

import java.util.List;
import java.util.UUID;

import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.identity.Group;

public class UserGroupQuery {

    public static void main(String[] args) {
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        IdentityService is = engine.getIdentityService();

        //初始数据
        for (int i = 0; i < 10; i++) {
            Group group = is.newGroup(UUID.randomUUID().toString());
            group.setName("Group_" + i);
            group.setType("TYPE_" + i);
            is.saveGroup(group);
        }
        //FieldQuery
        List<Group> groups = is.createGroupQuery().groupName("Group_1").groupType("TYPE_1").list();
        for (Group g : groups) {
            System.out.println(g.getId());
        }
        //NativeQuery
        groups = is.createNativeGroupQuery()
                .sql("SELECT * FROM ACT_ID_GROUP where NAME_ = #{name}")
                .parameter("name", "Group_2").list();
        for (Group g : groups) {
            System.out.println(g.getId());
        }

        //TestSort
        groups = is.createGroupQuery().orderByGroupId().desc().orderByGroupName().asc().list();
        for (Group g : groups) {
            System.out.println(g.getId());
        }

        //TestSingle
        Group g = is.createGroupQuery().groupName("Group_0").singleResult();
        System.out.println(g.getId());

        //TestList
        groups = is.createGroupQuery().list();
        for (Group group : groups) {
            System.out.println(group.getId() + "---" + group.getName() + "---" + group.getType());
        }

        long size = is.createGroupQuery().count();
        System.out.println(size);

        //TestListPage
        groups = is.createGroupQuery().listPage(1, 5);
        for(Group group : groups) {
            System.out.println(group.getId() + "---" + group.getName() + "---" + group.getType());
        }

    }

}
