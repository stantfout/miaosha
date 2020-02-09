package com.usth.miaosha.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class MD5UtilTest {

    String pass = "123456";

    @Test
    public void md5() {
    }

    @Test
    public void inputPassToFormPass() {
        System.out.println(MD5Util.inputPassToFormPass("123456"));
        //d3b1294a61a07da9b49b6e22b2cbd7f9
        //d3b1294a61a07da9b49b6e22b2cbd7f9
    }

    @Test
    public void fromPassToDBPass() {
        System.out.println(MD5Util.fromPassToDBPass("d3b1294a61a07da9b49b6e22b2cbd7f9","112233"));
    }

    @Test
    public void inputPassToDBPass() {
        System.out.println(MD5Util.inputPassToDBPass(pass,"1a2b3c4d"));
    }
}