package com.lte.building.controller;

import com.jeecg.demo.entity.JeecgDemoEntity;
import com.jeecg.p3.demo.entity.JeecgP3demoEntity;
import com.jeecg.p3.demo.service.JeecgP3demoService;
import com.lte.building.entity.LteBuildingEntity;
import com.lte.building.service.LteBuildingServiceI;
import org.apache.log4j.Logger;
import org.jeecgframework.core.beanvalidator.BeanValidators;
import org.jeecgframework.core.common.controller.BaseController;
import org.jeecgframework.core.common.exception.BusinessException;
import org.jeecgframework.core.common.hibernate.qbc.CriteriaQuery;
import org.jeecgframework.core.common.model.json.AjaxJson;
import org.jeecgframework.core.common.model.json.DataGrid;
import org.jeecgframework.core.constant.Globals;
import org.jeecgframework.core.util.ExceptionUtil;
import org.jeecgframework.core.util.MyBeanUtils;
import org.jeecgframework.core.util.ResourceUtil;
import org.jeecgframework.core.util.StringUtil;
import org.jeecgframework.minidao.pojo.MiniDaoPage;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.entity.vo.NormalExcelConstants;
import org.jeecgframework.tag.core.easyui.TagUtil;
import org.jeecgframework.web.system.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author onlineGenerator
 * @version V1.0
 * @Title: Controller
 * @Description: lte_building
 * @date 2017-11-18 15:59:59
 */
@Controller
@RequestMapping("/lteBuildingController")
public class LteBuildingController extends BaseController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(LteBuildingController.class);

    @Autowired
    private LteBuildingServiceI lteBuildingService;
    @Autowired
    private SystemService systemService;
    @Autowired
    private Validator validator;


    /**
     * lte_building列表 页面跳转
     *
     * @return
     */
    @RequestMapping(params = "list")
    public ModelAndView list(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("com/lte/building/lteBuildingList");
        return modelAndView;
    }

    /**
     * easyui AJAX请求数据
     *
     * @param request
     * @param response
     * @param dataGrid
     * @param user
     */

    @RequestMapping(params = "datagrid")
    public void datagrid(LteBuildingEntity lteBuilding, HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
        CriteriaQuery cq = new CriteriaQuery(LteBuildingEntity.class, dataGrid);
        //查询条件组装器
        org.jeecgframework.core.extend.hqlsearch.HqlGenerateUtil.installHql(cq, lteBuilding, request.getParameterMap());
        try {
            //自定义追加查询条件
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
        cq.add();
        this.lteBuildingService.getDataGridReturn(cq, true);
        TagUtil.datagrid(response, dataGrid);
    }

    /**
     * 下拉列表
     *
     * @return
     */
    @RequestMapping(params = "listSel")
    @ResponseBody
    public AjaxJson listSel() {
        AjaxJson ajaxJson = new AjaxJson();
        List<LteBuildingEntity> lteBuildingServiceList = lteBuildingService.getList(LteBuildingEntity.class);
        ajaxJson.setObj(lteBuildingServiceList);
        return ajaxJson;
    }

    /**
     * 删除lte_building
     *
     * @return
     */
    @RequestMapping(params = "doDel")
    @ResponseBody
    public AjaxJson doDel(LteBuildingEntity lteBuilding, HttpServletRequest request) {
        String message = null;
        AjaxJson j = new AjaxJson();
        lteBuilding = systemService.getEntity(LteBuildingEntity.class, lteBuilding.getId());
        message = "lte_building删除成功";
        try {
            lteBuildingService.delete(lteBuilding);
            systemService.addLog(message, Globals.Log_Type_DEL, Globals.Log_Leavel_INFO);
        } catch (Exception e) {
            e.printStackTrace();
            message = "lte_building删除失败";
            throw new BusinessException(e.getMessage());
        }
        j.setMsg(message);
        return j;
    }

    /**
     * 批量删除lte_building
     *
     * @return
     */
    @RequestMapping(params = "doBatchDel")
    @ResponseBody
    public AjaxJson doBatchDel(String ids, HttpServletRequest request) {
        String message = null;
        AjaxJson j = new AjaxJson();
        message = "lte_building删除成功";
        try {
            for (String id : ids.split(",")) {
                LteBuildingEntity lteBuilding = systemService.getEntity(LteBuildingEntity.class,
                        Integer.parseInt(id)
                );
                lteBuildingService.delete(lteBuilding);
                systemService.addLog(message, Globals.Log_Type_DEL, Globals.Log_Leavel_INFO);
            }
        } catch (Exception e) {
            e.printStackTrace();
            message = "lte_building删除失败";
            throw new BusinessException(e.getMessage());
        }
        j.setMsg(message);
        return j;
    }


    /**
     * 添加lte_building
     *
     * @param ids
     * @return
     */
    @RequestMapping(params = "doAdd")
    @ResponseBody
    public AjaxJson doAdd(LteBuildingEntity lteBuilding, HttpServletRequest request) {
        String message = null;
        AjaxJson j = new AjaxJson();
        message = "lte_building添加成功";
        try {
            lteBuildingService.save(lteBuilding);
            systemService.addLog(message, Globals.Log_Type_INSERT, Globals.Log_Leavel_INFO);
        } catch (Exception e) {
            e.printStackTrace();
            message = "lte_building添加失败";
            throw new BusinessException(e.getMessage());
        }
        j.setMsg(message);
        return j;
    }

    /**
     * 更新lte_building
     *
     * @param ids
     * @return
     */
    @RequestMapping(params = "doUpdate")
    @ResponseBody
    public AjaxJson doUpdate(LteBuildingEntity lteBuilding, HttpServletRequest request) {
        String message = null;
        AjaxJson j = new AjaxJson();
        message = "lte_building更新成功";
        LteBuildingEntity t = lteBuildingService.get(LteBuildingEntity.class, lteBuilding.getId());
        try {
            MyBeanUtils.copyBeanNotNull2Bean(lteBuilding, t);
            lteBuildingService.saveOrUpdate(t);
            systemService.addLog(message, Globals.Log_Type_UPDATE, Globals.Log_Leavel_INFO);
        } catch (Exception e) {
            e.printStackTrace();
            message = "lte_building更新失败";
            throw new BusinessException(e.getMessage());
        }
        j.setMsg(message);
        return j;
    }


    /**
     * lte_building新增页面跳转
     *
     * @return
     */
    @RequestMapping(params = "goAdd")
    public ModelAndView goAdd(LteBuildingEntity lteBuilding, HttpServletRequest req) {
        if (StringUtil.isNotEmpty(lteBuilding.getId())) {
            lteBuilding = lteBuildingService.getEntity(LteBuildingEntity.class, lteBuilding.getId());
            req.setAttribute("lteBuildingPage", lteBuilding);
        }
        return new ModelAndView("com/lte/building/lteBuilding-add");
    }

    /**
     * lte_building编辑页面跳转
     *
     * @return
     */
    @RequestMapping(params = "goUpdate")
    public ModelAndView goUpdate(LteBuildingEntity lteBuilding, HttpServletRequest req) {
        if (StringUtil.isNotEmpty(lteBuilding.getId())) {
            lteBuilding = lteBuildingService.getEntity(LteBuildingEntity.class, lteBuilding.getId());
            req.setAttribute("lteBuildingPage", lteBuilding);
        }
        return new ModelAndView("com/lte/building/lteBuilding-update");
    }

    /**
     * 导入功能跳转
     *
     * @return
     */
    @RequestMapping(params = "upload")
    public ModelAndView upload(HttpServletRequest req) {
        req.setAttribute("controller_name", "lteBuildingController");
        return new ModelAndView("common/upload/pub_excel_upload");
    }

    /**
     * 导出excel
     *
     * @param request
     * @param response
     */
    @RequestMapping(params = "exportXls")
    public String exportXls(LteBuildingEntity lteBuilding, HttpServletRequest request, HttpServletResponse response
            , DataGrid dataGrid, ModelMap modelMap) {
        CriteriaQuery cq = new CriteriaQuery(LteBuildingEntity.class, dataGrid);
        org.jeecgframework.core.extend.hqlsearch.HqlGenerateUtil.installHql(cq, lteBuilding, request.getParameterMap());
        List<LteBuildingEntity> lteBuildings = this.lteBuildingService.getListByCriteriaQuery(cq, false);
        modelMap.put(NormalExcelConstants.FILE_NAME, "lte_building");
        modelMap.put(NormalExcelConstants.CLASS, LteBuildingEntity.class);
        modelMap.put(NormalExcelConstants.PARAMS, new ExportParams("lte_building列表", "导出人:" + ResourceUtil.getSessionUser().getRealName(),
                "导出信息"));
        modelMap.put(NormalExcelConstants.DATA_LIST, lteBuildings);
        return NormalExcelConstants.JEECG_EXCEL_VIEW;
    }

    /**
     * 导出excel 使模板
     *
     * @param request
     * @param response
     */
    @RequestMapping(params = "exportXlsByT")
    public String exportXlsByT(LteBuildingEntity lteBuilding, HttpServletRequest request, HttpServletResponse response
            , DataGrid dataGrid, ModelMap modelMap) {
        modelMap.put(NormalExcelConstants.FILE_NAME, "lte_building");
        modelMap.put(NormalExcelConstants.CLASS, LteBuildingEntity.class);
        modelMap.put(NormalExcelConstants.PARAMS, new ExportParams("lte_building列表", "导出人:" + ResourceUtil.getSessionUser().getRealName(),
                "导出信息"));
        modelMap.put(NormalExcelConstants.DATA_LIST, new ArrayList());
        return NormalExcelConstants.JEECG_EXCEL_VIEW;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(params = "importExcel", method = RequestMethod.POST)
    @ResponseBody
    public AjaxJson importExcel(HttpServletRequest request, HttpServletResponse response) {
        AjaxJson j = new AjaxJson();

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 获取上传文件对象
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<LteBuildingEntity> listLteBuildingEntitys = ExcelImportUtil.importExcel(file.getInputStream(), LteBuildingEntity.class, params);
                for (LteBuildingEntity lteBuilding : listLteBuildingEntitys) {
                    lteBuildingService.save(lteBuilding);
                }
                j.setMsg("文件导入成功！");
            } catch (Exception e) {
                j.setMsg("文件导入失败！");
                logger.error(ExceptionUtil.getExceptionMessage(e));
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return j;
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<LteBuildingEntity> list() {
        List<LteBuildingEntity> listLteBuildings = lteBuildingService.getList(LteBuildingEntity.class);
        return listLteBuildings;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> get(@PathVariable("id") String id) {
        LteBuildingEntity task = lteBuildingService.get(LteBuildingEntity.class, id);
        if (task == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(task, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> create(@RequestBody LteBuildingEntity lteBuilding, UriComponentsBuilder uriBuilder) {
        //调用JSR303 Bean Validator进行校验，如果出错返回含400错误码及json格式的错误信息.
        Set<ConstraintViolation<LteBuildingEntity>> failures = validator.validate(lteBuilding);
        if (!failures.isEmpty()) {
            return new ResponseEntity(BeanValidators.extractPropertyAndMessage(failures), HttpStatus.BAD_REQUEST);
        }

        //保存
        try {
            lteBuildingService.save(lteBuilding);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        //按照Restful风格约定，创建指向新任务的url, 也可以直接返回id或对象.
        String id = lteBuilding.getId().toString();
        URI uri = uriBuilder.path("/rest/lteBuildingController/" + id).build().toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(uri);

        return new ResponseEntity(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@RequestBody LteBuildingEntity lteBuilding) {
        //调用JSR303 Bean Validator进行校验，如果出错返回含400错误码及json格式的错误信息.
        Set<ConstraintViolation<LteBuildingEntity>> failures = validator.validate(lteBuilding);
        if (!failures.isEmpty()) {
            return new ResponseEntity(BeanValidators.extractPropertyAndMessage(failures), HttpStatus.BAD_REQUEST);
        }

        //保存
        try {
            lteBuildingService.saveOrUpdate(lteBuilding);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }

        //按Restful约定，返回204状态码, 无内容. 也可以返回200状态码.
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") String id) {
        lteBuildingService.deleteEntityById(LteBuildingEntity.class, id);
    }
}
