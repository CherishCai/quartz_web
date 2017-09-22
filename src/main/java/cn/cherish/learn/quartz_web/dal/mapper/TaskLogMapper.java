/*
 * Copyright (c) caihongwen.cn 2017.
 */

package cn.cherish.learn.quartz_web.dal.mapper;

import cn.cherish.learn.quartz_web.dal.entity.TaskLog;
import org.apache.ibatis.annotations.Mapper;

import java.io.Serializable;

/**
 * @author Cherish
 * @version 1.0
 * @date 2017/9/5 20:25
 */
@Mapper
public interface TaskLogMapper {

    int insert(TaskLog entity);

    int deleteById(Serializable id);

    int updateById(TaskLog entity);

    TaskLog findById(Serializable id);

}
