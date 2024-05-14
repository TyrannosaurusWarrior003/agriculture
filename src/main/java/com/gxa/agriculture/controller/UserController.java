package com.gxa.agriculture.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gxa.agriculture.common.BizException;
import com.gxa.agriculture.common.ErrorCode;
import com.gxa.agriculture.common.PageDto;
import com.gxa.agriculture.common.PageResults;
import com.gxa.agriculture.common.R;
import com.gxa.agriculture.entity.dto.UserLoginDto;
import com.gxa.agriculture.entity.dto.UserPageDto;
import com.gxa.agriculture.entity.dto.UserRegisterDto;
import com.gxa.agriculture.entity.pojo.User;
import com.gxa.agriculture.entity.vo.UserVo;
import com.gxa.agriculture.service.UserService;
import com.gxa.agriculture.util.PageResultsUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/*@Controller
@ResponseBody*/
@RestController
@Slf4j
@RequestMapping("/user")
@Api(value = "UserController", tags = "用户操作接口")
public class UserController {

    @Autowired
    private UserService userService;


    /**
     * 用户登录方法
     *
     * @param dto
     * @return
     */
    @ApiOperation(tags = "用户登录方法", value = "login", notes = "{\n" +
            "  \"phone\": \"133\",\n" +
            "  \"pwd\": \"123456\"\n" +
            "}")
    @PostMapping("/login")
    //这里的R可以返回R<String>或R<UserVo>取决于返回什么
    public R login(@Valid @RequestBody UserLoginDto dto) throws BizException {

        //FIXME 数据校验
        /*if (!"".equals(dto.getPhone()) && dto.getPhone() != null) {

        }
        if (!"".equals(dto.getPhone()) && dto.getPhone() != null) {

        }*/

        // 调用service
        UserVo login = userService.login(dto);

        // 返回结果给前端
        return R.success(login);


    }


    /**
     * 根据id查询
     *
     * @param id
     * @return
     * @throws BizException
     */
    @ApiOperation(tags = "根据id查询", value = "getById")
    @GetMapping("/getById")
    public R<User> getById(Integer id) throws BizException {

        //从业务层得到数据
        User user = userService.getById(id);

        //控制层加工处理
        if (user == null) {
            throw new BizException(ErrorCode.NULL_USER);
        }

        //返回给前端
        return R.success(user);
    }


    /**
     * 用户注册方法
     *
     * @param dto
     * @return
     * @throws BizException
     */
    @ApiOperation(tags = "用户注册方法", value = "register")
    @PostMapping("/register")
    public R register(@Valid @RequestBody UserRegisterDto dto) throws BizException {

        //校验数据


        //调用方法
        User user = userService.register(dto);


        //返回结果给前端
        return R.success(user);
    }

    /**
     * 简单分页查询
     *
     * @param dto
     * @return
     * @throws BizException
     */
    @ApiOperation(tags = "分页查询", value = "简单分页查询")
    @PostMapping("/page")
    public R page(@RequestBody PageDto dto) {

        Page<User> page = new Page<>(dto.getCurrent(), dto.getSize());
        Page<User> result = userService.page(page);

        //声明Map来封装total和records
       /* Map<String, Object> map = new HashMap<>();
        map.put("total",total);
        map.put("records",records);*/


        //取出目标数据并封装
        long total = result.getTotal();
        List<User> records = result.getRecords();
        //PageResults<List<User>> pageResults = PageResultsUtil.getData(total, records);

        //返回前端
        return R.success(total, records);
    }


    /**
     * 手机号模糊匹配的分页查询
     *
     * @param dto
     * @return
     * @throws BizException
     */
    @ApiOperation(tags = "分页查询", value = "手机号模糊匹配的分页查询")
    @PostMapping("/pageLikePhone")
    public R pageLikePhone(@RequestBody UserPageDto dto) {

        Page<User> page = new Page<>(dto.getPageDto().getCurrent(), dto.getPageDto().getSize());

        LambdaQueryWrapper<User> queryWrapper = Wrappers.lambdaQuery(User.class);
        queryWrapper.like(User::getPhone, dto.getPhone());

        //获取到数据库的数据
        Page<User> result = userService.page(page, queryWrapper);

        //取出目标数据并调用success的封装方法
        long total = result.getTotal();
        List<User> records = result.getRecords();
        //PageResults<List<User>> pageResults = new PageResults();
        //pageResults.setTotal(total);
        //pageResults.setRecodes(records);
        //PageResults<List<User>> pageResults = PageResultsUtil.getData(total, records);

        //将解析的数据返回给前端
        return R.success(total, records);
    }
}

