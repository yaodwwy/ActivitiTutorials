package cn.adbyte.activiti.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Adam
 */
@Data
public class Member implements Serializable {

    private String identity;
    /**
     * 消费金额
     */
    private int amount;
    /**
     * 规则计算后的金额
     */
    private double result;
    /**
     * 优惠后的金额
     */
    private int afterDiscount;

    public double doDiscount(double discount) {
        return this.amount * discount;
    }
}
