package com.demo.wyd.framedemo.tester;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

/**
 * Description:Rules注解,可以通过该注解在测试用例中模拟我们需要的行为
 * Rule实现了TestRule接口
 * Created by wyd on 2017/3/21.
 */

public class RuleTester {
    private static File mFile;

    @Rule
    //TemporaryFolder 测试过程中创建文件夹的类，当测试结束之后，文件夹会自动删除
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setUp() throws Exception {
        System.out.println("method:setUp()\n" + mFile);
        Assert.assertNull(mFile);
    }

    @Test
    public void test() {
        try {
            mFile = folder.newFile("RuleTester.txt");
            System.out.println("method:test()\n" + mFile);
            Assert.assertNotNull(mFile);
            boolean flag = mFile.exists();
            Assert.assertTrue(flag);
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @AfterClass
    public static void tearDown() throws Exception {
        System.out.println("method:tearDown()\n" + mFile);
        boolean flag = mFile.exists();
        Assert.assertFalse(flag);
    }
}
