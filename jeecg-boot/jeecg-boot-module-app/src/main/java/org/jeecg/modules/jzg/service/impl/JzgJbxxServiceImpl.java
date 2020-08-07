package org.jeecg.modules.jzg.service.impl;

import org.jeecg.modules.jzg.entity.JzgJbxx;
import org.jeecg.modules.jzg.mapper.JzgJbxxMapper;
import org.jeecg.modules.jzg.service.IJzgJbxxService;
import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 教职工源表
 * @Author: jeecg-boot
 * @Date:   2020-08-05
 * @Version: V1.0
 */
@Service
@DS("data")
public class JzgJbxxServiceImpl extends ServiceImpl<JzgJbxxMapper, JzgJbxx> implements IJzgJbxxService {

}
