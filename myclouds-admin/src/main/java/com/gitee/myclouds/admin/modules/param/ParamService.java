package com.gitee.myclouds.admin.modules.param;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.gitee.myclouds.admin.domain.myparam.MyParamEntity;
import com.gitee.myclouds.admin.domain.myparam.MyParamMapper;
import com.gitee.myclouds.toolbox.wrap.Dto;
import com.gitee.myclouds.toolbox.wrap.Dtos;

/**
 * 键值参数服务
 * 
 * @author xiongchun
 *
 */
@Service
public class ParamService {
	
	@Autowired
	private MyParamMapper myParamMapper;
	
	/**
	 * 查询参数列表
	 * 
	 * @param inDto
	 * @return
	 */
	public String listAll(Dto inDto){
		Dto outDto = Dtos.newDto();
		List<MyParamEntity> myParamEntities = myParamMapper.list(inDto);
		outDto.put("data", myParamEntities);
		outDto.put("recordsTotal", myParamEntities.size());
		outDto.put("recordsFiltered", myParamEntities.size());
		return JSON.toJSONString(outDto);
	}
	}
