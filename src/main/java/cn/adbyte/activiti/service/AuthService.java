package cn.adbyte.activiti.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AuthService implements Serializable  {

    public List<String> getCandidateUsers() {
        List<String> result = new ArrayList<String>();
        result.add("userA");
        result.add("userB");
        return result;
    }
}
