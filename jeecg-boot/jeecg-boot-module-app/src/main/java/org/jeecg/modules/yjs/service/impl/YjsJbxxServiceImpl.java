package org.jeecg.modules.yjs.service.impl;

import org.jeecg.modules.yjs.entity.YjsJbxx;
import org.jeecg.modules.yjs.mapper.YjsJbxxMapper;
import org.jeecg.modules.yjs.service.IYjsJbxxService;
import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 研究生源表
 * @Author: jeecg-boot
 * @Date:   2020-08-05
 * @Version: V1.0
 */
@Service
@DS("data")
public class YjsJbxxServiceImpl extends ServiceImpl<YjsJbxxMapper, YjsJbxx> implements IYjsJbxxService {

}
