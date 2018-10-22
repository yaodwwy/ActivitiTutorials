package cn.adbyte.activiti.pojo;

import lombok.Data;

/**
 * @author Adam
 */
@Data
public class Member {

    private String identity;
    /** 消费金额 */
    private int amount;
    /** 规则计算后的金额 */
    private double result;
    /** 优惠后的金额 */
    private int afterDiscount;

}
