/*
 * Copyright (c) caihongwen.cn 2017.
 */

package cn.cherish.learn.quartz_web.dal.mapper;

import cn.cherish.learn.quartz_web.dal.entity.TaskConfig;
import org.apache.ibatis.annotations.Mapper;

import java.io.Serializable;
import java.util.List;

/**
 * @author Cherish
 * @version 1.0
 * @date 2017/9/5 20:25
 */
@Mapper
public interface TaskConfigMapper {

    int insert(TaskConfig entity);

    int deleteById(Serializable id);

    int updateById(TaskConfig entity);

    TaskConfig findById(Serializable id);

    List<TaskConfig> findAll();

}
