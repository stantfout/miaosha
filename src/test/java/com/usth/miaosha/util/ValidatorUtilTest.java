package com.usth.miaosha.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class ValidatorUtilTest {

    @Test
    public void isMobile() {
        System.out.println(ValidatorUtil.isMobile("18706685190"));
        System.out.println(ValidatorUtil.isMobile("1870668510"));
    }
}