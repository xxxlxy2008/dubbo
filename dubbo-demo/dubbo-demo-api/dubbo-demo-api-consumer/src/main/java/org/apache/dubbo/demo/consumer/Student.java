package org.apache.dubbo.demo.consumer;

import java.io.Serializable;

/**
 * Created on 2020-07-19
 */
public class Student implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private int age;

    private transient StudentUtil studentUtil;
}