package org.easyframework.easyadmin.web.controllers;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.easyframework.easyadmin.common.util.DhtmlXTreeUtils;
import org.easyframework.easyadmin.common.viewmodel.DhtmlXTreeNode;
import org.easyframework.easyadmin.common.viewmodel.JsonResult;
import org.easyframework.easyadmin.common.viewmodel.ParamJsonResult;
import org.easyframework.easyadmin.po.ModulePo;
import org.easyframework.easyadmin.service.ModuleService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/membership/module")
public class ModuleController extends AbstractController {
	@Resource
	private ModuleService moduleService;

	@RequestMapping(value = { "", "/", "/index" })
	public String index() {
		return "membership/module";
	}

	@RequestMapping(value = "/refresh")
	@ResponseBody
	public JsonResult refresh() {
		JsonResult result = new JsonResult(false, "刷新模块列表失败！");
		try {
			this.moduleService.reloadCache();
			this.setSuccessResult(result, "刷新模块列表成功！");
		} catch (Exception ex) {
			this.setExceptionResult(result, ex);
		}
		return result;
	}

	@RequestMapping(value = "/getModuleById")
	@ResponseBody
	public ModulePo getModule(String id) {
		return this.moduleService.getById(id);
	}

	@RequestMapping(value = "/list")
	@ResponseBody
	public DhtmlXTreeNode list(Integer id)
	{
		refresh();
		int moduleId = (id == null ? 0 : id);
		List<ModulePo> modules = this.moduleService.getChildModules(moduleId);
		List<DhtmlXTreeNode> nodes = modules.stream().map(x -> {
			DhtmlXTreeNode node = new DhtmlXTreeNode();
			node.setId(String.valueOf(x.getModuleId()));
			node.setChild(x.isLeaf() ? 0 : 1);
			node.setText(x.getName());
			node.setTooltip(x.getName());
			node.setUserdata("meta", x);
			return node;
		}).collect(Collectors.toList());

		return DhtmlXTreeUtils.getRootNode(String.valueOf(moduleId), nodes);
	}

	@RequestMapping(value = "/add")
	@ResponseBody
	public ParamJsonResult<ModulePo> add(ModulePo po) {
		ParamJsonResult<ModulePo> result = new ParamJsonResult<ModulePo>(false, "添加模块失败！");

		if (StringUtils.isBlank(po.getUrl())) {
			this.setFailureResult(result, "url不能为空！");
			return result;
		}
		try {
			po.setCreateTime(Calendar.getInstance().getTime());
			this.moduleService.addModule(po);
			this.setSuccessResult(result, "添加模块成功！");
			this.moduleService.reloadCache();
		} catch (Exception ex) {
			this.setExceptionResult(result, ex);
		}

		return result;
	}

	@RequestMapping(value = "/edit")
	@ResponseBody
	public ParamJsonResult<ModulePo> edit(ModulePo po) {
		ParamJsonResult<ModulePo> result = new ParamJsonResult<ModulePo>(false, "更新模块失败！");
		String[] names = new String[] {
				ModulePo.Name, ModulePo.Code, ModulePo.Status, ModulePo.Sequence,
				ModulePo.Comment, ModulePo.Url, ModulePo.Icon, ModulePo.LinkType,
				ModulePo.Target, ModulePo.Params
		};

		try {
			this.moduleService.edit(po, po.getModuleId(), names);
			this.setSuccessResult(result, "更新模块成功！");
			this.moduleService.reloadCache();
		} catch (Exception ex) {
			this.setExceptionResult(result, ex);
		}
		return result;
	}

	@RequestMapping(value = "/removeById")
	@ResponseBody
	public ParamJsonResult<ModulePo> remove(String id) {
		ParamJsonResult<ModulePo> result = new ParamJsonResult<ModulePo>(false, "删除模块失败");

		try {
			ModulePo module = this.moduleService.getById(id, ModulePo.ParentId);
			this.moduleService.remove(Integer.parseInt(id), module.getParentId());
			this.setSuccessResult(result, "删除模块成功！");
			this.moduleService.reloadCache();
		} catch (Exception ex) {
			this.setExceptionResult(result, ex);
		}

		return result;
	}
}