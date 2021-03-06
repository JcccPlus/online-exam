package cn.edu.hstc.controller;

import cn.edu.hstc.framework.AjaxResult;
import cn.edu.hstc.framework.Convert;
import cn.edu.hstc.framework.page.PageDomain;
import cn.edu.hstc.framework.page.TableDataInfo;
import cn.edu.hstc.framework.page.TableSupport;
import cn.edu.hstc.framework.util.DateUtils;
import cn.edu.hstc.framework.util.ServletUtils;
import cn.edu.hstc.framework.util.SqlUtil;
import cn.edu.hstc.framework.util.StringUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * web层通用数据处理
 */
public class BaseController {
    protected final Logger logger = LoggerFactory.getLogger(BaseController.class);

    /**
     * 将前台传递过来的日期格式的字符串，自动转化为Date类型
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // Date 类型转换
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(DateUtils.parseDate(text));
            }
        });
    }

    /**
     * 设置请求分页数据
     */
    protected void startPage(Integer pageNum) {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        if(pageNum==null){
            pageNum = pageDomain.getPageNum();
        }
        Integer pageSize = pageDomain.getPageSize();
        String orderBy = (String) ServletUtils.getRequest().getAttribute("orderBy");
        //String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
        //如果页码及单页数量为空，则设置为默认值
        if (StringUtils.isNull(pageNum)) {
            pageNum = 1;
        }
        if(StringUtils.isNull(pageSize)){
            pageSize = 10;
        }
        PageHelper.startPage(pageNum, pageSize, orderBy);
    }

    /**
     * 获取request
     */
    public HttpServletRequest getRequest() {
        return ServletUtils.getRequest();
    }

    /**
     * 获取response
     */
    public HttpServletResponse getResponse() {
        return ServletUtils.getResponse();
    }

    /**
     * 获取session
     */
    public HttpSession getSession() {
        return getRequest().getSession();
    }

    /**
     * 响应请求分页数据
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected TableDataInfo getDataTable(List<?> list, int code) {
        TableDataInfo rspData = new TableDataInfo();
        //rspData.setCode(0);
        rspData.setCode(code);
        rspData.setRows(list);
        if (ServletUtils.getRequest().getAttribute("pageNum") != null) {
            int pageNum = Convert.toInt(ServletUtils.getRequest().getAttribute("pageNum"));
            int pages = new PageInfo(list).getPages();
            if (pageNum > pages) {
                rspData.setRows(new ArrayList<>());
            }
        }
        rspData.setTotal(new PageInfo(list).getTotal());
        return rspData;
    }

    /**
     * 响应返回结果
     *
     * @param rows 影响行数
     * @return 操作结果
     */
    protected AjaxResult toAjax(int rows) {
        return rows > 0 ? success() : error();
    }

    /**
     * 响应返回结果
     *
     * @param result 结果
     * @return 操作结果
     */
    protected AjaxResult toAjax(boolean result) {
        return result ? success() : error();
    }

    /**
     * 返回成功
     */
    public AjaxResult success() {
        return AjaxResult.success();
    }

    /**
     * 返回失败消息
     */
    public AjaxResult error() {
        return AjaxResult.error();
    }

    /**
     * 返回成功消息
     */
    public AjaxResult success(String message) {
        return AjaxResult.success(message);
    }

    /**
     * 返回失败消息
     */
    public AjaxResult error(String message) {
        return AjaxResult.error(message);
    }

    /**
     * 返回错误码消息
     */
    public AjaxResult error(AjaxResult.Type type, String message) {
        return new AjaxResult(type, message);
    }

    /**
     * 页面跳转
     */
    public String redirect(String url) {
        return StringUtils.format("redirect:{}", url);
    }
}
