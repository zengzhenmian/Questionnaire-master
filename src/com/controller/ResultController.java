package com.controller;


import com.models.Result;
import com.models.User;
import com.service.ResultService;
import com.service.SurveyService;
import com.service.UserService;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ResultController {
    @Autowired
    private ResultService resultService;
    @Autowired
    private SurveyService surveyService;
    @Autowired
    private UserService userService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setResultService(ResultService resultService) {
        this.resultService = resultService;
    }

    public ResultService getResultService() {
        return resultService;
    }

    public void setSurveyService(SurveyService surveyService) {
        this.surveyService = surveyService;
    }

    public SurveyService getSurveyService() {
        return surveyService;
    }

    @RequestMapping("/addAnswer")
    public void addAnswer(Result result, HttpServletResponse response){
        Result result1 = resultService.checkStatus(result.getSurveyId(),result.getUserId());
        if(result1==null) {
            resultService.addResult(result);
        }else {
            if(result.getStatus()==1){
                result1.setStatus(1);
                resultService.update(result1);
            }else {
                result1.setStatus(0);
                resultService.update(result1);
            }
        }
    }

    @RequestMapping("/findResult")
    public String findResult(@RequestParam("name")String name, HttpServletRequest request){
        List<Result> list = resultService.getResultByIds(userService.getUserByName(name));
        List<String[]> s = new ArrayList<>();
        for(int i=0;i<list.size();i++){
            String result = list.get(i).getAnswer();
            String[] ss = new String[result.split(";").length];
            for(int j=0;j<result.split(";").length;j++){
                ss[j] = result.split(";")[j];
            }
            s.add(ss);
        }
        request.setAttribute("results",list);
        request.setAttribute("answerString",s);
        return "print_survey.jsp";
    }

    @RequestMapping("/getAllResult")
    public String getAllResult(HttpServletRequest request){
        List<Result> list = resultService.getAllResults();
        int[] userIds = new int[list.size()];
        for (int i=0;i<userIds.length;i++){
            userIds[i] = list.get(i).getUserId();
        }
        String[] snames = userService.getNames(userIds);
        request.setAttribute("snames",snames);
        List<String[]> s = new ArrayList<>();
        for(int i=0;i<list.size();i++){
            String result = list.get(i).getAnswer();
            String[] ss = new String[result.split(";").length];
            for(int j=0;j<result.split(";").length;j++){
                ss[j] = result.split(";")[j];
            }
            s.add(ss);
        }
        request.setAttribute("results",list);
        request.setAttribute("answerString",s);
        return "print_survey.jsp";
    }

    @RequestMapping("/getCurrentResult")
    public String getCurrentResult(@RequestParam("id") String id,HttpServletRequest request){
        Result result = resultService.getResultById(Integer.parseInt(id));
        request.setAttribute("result",result);
        return "getCurrentResult.jsp";
    }

    @RequestMapping("/printSurvey")
    public void printSurvey(HttpServletResponse response) throws Exception{
        List<Result> list = resultService.getAllResults();
        int[] userIds = new int[list.size()];
        for (int i=0;i<userIds.length;i++){
            userIds[i] = list.get(i).getUserId();
        }
        String[] snames = userService.getNames(userIds);
        List<String[]> s = new ArrayList<>();
        for(int i=0;i<list.size();i++){
            String result = list.get(i).getAnswer();
            String[] ss = new String[result.split(";").length];
            for(int j=0;j<result.split(";").length;j++){
                ss[j] = result.split(";")[j];
            }
            s.add(ss);
        }
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet=wb.createSheet("Survey Report");
        //???sheet???????????????????????????????????????(excel??????)????????????0???65535?????????????????????
        HSSFRow row1=sheet.createRow(0);
        //??????????????????excel?????????????????????????????????????????????0???255?????????????????????
        HSSFCell cell=row1.createCell(0);
        //?????????????????????
        cell.setCellValue("??????/??????/(??????,??????)");
        //???????????????CellRangeAddress???????????????????????????????????????????????????????????? ?????????
        sheet.addMergedRegion(new CellRangeAddress(0,0,0,3));

        for(int i=0;i<list.size();i++){
            HSSFRow row=sheet.createRow(i+1);
            row.createCell(0).setCellValue(snames[i]);
            if(list.get(i).getSex()==1){
                row.createCell(1).setCellValue("???");
            }else {
                row.createCell(1).setCellValue("???");
            }
            for(int j=0;j<s.size();j++){
                if(i==j){
                    for(int k=0;k<s.get(j).length;k++){
                        row.createCell(k+2).setCellValue(s.get(j)[k]);
                    }
                }
            }
        }

        OutputStream output=response.getOutputStream();
        response.reset();
        response.setHeader("Content-disposition", "attachment; filename=Survey Report.xls");
        response.setContentType("application/msexcel");
        wb.write(output);
        output.close();
    }

    @RequestMapping("/checkStatus")
    public String checkStatus(@RequestParam("surveyId")String surveyId, HttpSession session,HttpServletRequest request){
        User user = (User)session.getAttribute("user");
        int id = user.getId();
        Result result = resultService.checkStatus(Integer.parseInt(surveyId),id);
        if(result==null){
            request.setAttribute("sid",surveyId);
            return "select_sex.jsp";
        }else {
            return "/toSave.action?id="+result.getId();
        }
    }

    @RequestMapping("/toSave")
    public String toSave(@RequestParam("id") String id,HttpServletRequest request){
        Result result = resultService.getResultById(Integer.parseInt(id));
        request.setAttribute("result",result);
        return "answer_savequestions.jsp";
    }
}
