package clover.controller;
import clover.pojo.Poet;
import clover.service.PoetService;
import clover.util.BeanUtil;
import org.springframework.web.bind.annotation.*;


import java.util.Map;

/*
@RestController
@RequestMapping("/poets2")
public class TestController {

        @PostMapping("/create")
        public Poet createPoet(@RequestParam Map<String, String> params) {
            return BeanUtil.convert(Poet.class, params);

        }
    }

*/


@RestController
@RequestMapping("/poets2")
public class TestController {

    private final PoetService poetService; // 假设你有一个PoetService类

    public TestController(PoetService poetService) {
        this.poetService = poetService;
    }

    @PostMapping("/create")
    public Poet createPoet(@RequestParam Map<String, String> params) {
        Poet poet = BeanUtil.convert(Poet.class, params);
        int result = poetService.insert(poet);
        return poet;
    }
}

