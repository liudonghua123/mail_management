package org.jeecg.modules.bks.service.impl;

import org.jeecg.modules.bks.entity.BksJbxx;
import org.jeecg.modules.bks.mapper.BksJbxxMapper;
import org.jeecg.modules.bks.service.IBksJbxxService;
import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 本科生源表
 * @Author: jeecg-boot
 * @Date:   2020-08-05
 * @Version: V1.0
 */
@Service
@DS("data")
public class BksJbxxServiceImpl extends ServiceImpl<BksJbxxMapper, BksJbxx> implements IBksJbxxService {

}
