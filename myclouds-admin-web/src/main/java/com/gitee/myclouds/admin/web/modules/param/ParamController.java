package com.gitee.myclouds.admin.web.modules.param;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gitee.myclouds.toolbox.wrap.Dto;
import com.gitee.myclouds.toolbox.wrap.Dtos;

/**
 * 键值参数管理
 * 
 * @author xiongchun
 *
 */
@Controller
@RequestMapping("sys/param")
public class ParamController {

	@Autowired
	private ParamService paramService;

	@RequestMapping("init")
	public String init(ModelMap map) {

		return "modules/sys/param";
	}

	/**
	 * 查询列表
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "list", method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json")
	@ResponseBody
	public String listParams(HttpServletRequest request) {
		Dto inDto = Dtos.newDto(request);
		return paramService.list(inDto);
	}

	/**
	 * 新增
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "save", method = { RequestMethod.POST }, produces = "application/json")
	@ResponseBody
	public Dto saveParam(HttpServletRequest request) {
		Dto inDto = Dtos.newDto(request);
		return Dtos.newDto(paramService.save(inDto));
	}

	/**
	 * 删除
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "delete", method = { RequestMethod.POST }, produces = "application/json")
	@ResponseBody
	public Dto deleteParam(HttpServletRequest request) {
		Dto inDto = Dtos.newDto(request);
		return Dtos.newDto(paramService.delete(inDto));
	}

}
