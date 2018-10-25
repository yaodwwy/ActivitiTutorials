package cn.adbyte.activiti.feign.api;

import lombok.Data;

@Data
public class Results<T> {

    private Integer total;
    private Integer start;
    private String sort;
    private String order;
    private Integer size;
    private T[] data;
}
