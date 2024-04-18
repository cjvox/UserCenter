package com.vox.usercenter.service;

import com.vox.usercenter.utils.AlgorithmUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

/**
 * @author VOX
 */
@SpringBootTest
public class AlgorithmUtilsTest {
    @Test
    void test(){
        String a="apple is my love";
        String b="alepp is my love";
        int i = AlgorithmUtils.minDistance(a, b);
        System.out.println(i);

        List<String> list = Arrays.asList("java", "python", "男", "大一");
        List<String> list1 = Arrays.asList("java", "c", "女", "大二");
        System.out.println(AlgorithmUtils.minDistanceTags(list1,list));
    }
}
