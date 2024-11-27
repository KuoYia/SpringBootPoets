package clover.controller;
import clover.pojo.Poet;
import clover.service.PoetService;
import clover.util.BeanUtil;
import org.springframework.web.bind.annotation.*;

import java.beans.IntrospectionException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Map;

@RestController
@RequestMapping("/poets2")
public class Test2 {

    private final PoetService poetService;

    public Test2(PoetService poetService) {
        this.poetService = poetService;
    }

    // 测试BeanUtil.convert方法
    @PostMapping("/create")
    public Poet createPoet(@RequestParam Map<String, String> params) {
        Poet poet = BeanUtil.convert(Poet.class, params);
        poetService.insert(poet); // 假设PoetService有一个insert方法
        return poet;
    }

    // 测试BeanUtil.populate方法
    @PostMapping("/populate")
    public Poet populatePoet(@RequestParam Map<String, String> params) {
        Poet poet = new Poet();
        BeanUtil.populate(poet, params);
        return poet;
    }

    // 测试BeanUtil.copyProperties方法
    @PostMapping("/copy")
    public Poet copyPoet(@RequestParam Map<String, String> params) throws IntrospectionException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Poet sourcePoet = BeanUtil.convert(Poet.class, params);
        Poet targetPoet = new Poet();
        BeanUtil.copyProperties(targetPoet, sourcePoet);
        return targetPoet;
    }

    // 测试BeanUtil.setProperty方法
    @PostMapping("/set")
    public Poet setPoetProperty(@RequestParam Map<String, String> params) {
        Poet poet = new Poet();
        String propertyName = params.get("propertyName");
        String propertyValue = params.get("propertyValue");

        // 直接使用BeanUtil.convert进行类型转换和属性设置
        BeanUtil.setProperty(poet, propertyName, propertyValue);
        return poet;
    }

}