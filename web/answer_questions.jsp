<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
    <link rel="stylesheet" href="css/bootstrap.min.css">
    <link rel="stylesheet" href="css/add_question.css">
    <script src="js/jquery.js"></script>
    <script src="js/bootstrap.min.js"></script>
</head>
<body>
<center><h1 value="${requestScope.sex}" class="h1">健康状况调查表</h1></center><hr style="width: 80%"/>
<div class="content" style="padding:10px;cursor:pointer;width:774px;margin: 0 auto">
    <c:forEach items="${requestScope.allQuestions}" var="question">
        <c:if test="${question.sex!=requestScope.sex}">
            <div class="will_blue" style="width:100%;overflow: auto;">
                    ${question.format}
            </div>
            <hr/>
        </c:if>
    </c:forEach>
    <button type="hidden" style="display: none" id="sid" value="${requestScope.sid}"/>
    <button type="hidden" style="display: none" id="hid" value="${sessionScope.user.id}"/>
    <div class="form-group">
        <button type="button" onclick="save()" id="save" style="cursor: pointer;outline: none;" class="btn btn-success btn-group-sm">
            保存已答题目
        </button>
        <button type="button" onclick="sub()" id="create_table" style="cursor: pointer;outline: none;margin-left: 10px" class="btn btn-success btn-group-sm">
            提交调查表
        </button>
    </div>
</div>
<script>
    var questions = document.getElementsByClassName("question");
    for(let i=0;i<questions.length;i++){
        questions[i].style.border = "none";
        var height = questions[i].style.height.split("px")[0];
        questions[i].parentNode.style.height = (parseInt(height)+3)+"px";
    }
    var answers = [];
    function getAnswers() {
        var inputs = document.getElementsByTagName("input");
        for(let i=0;i<inputs.length;i++){
            if(inputs[i].value!=""){
                var a = [i,inputs[i].value];
                answers.push(a);
            }
        }
    }

    function sub() {
        var inputs = document.getElementsByTagName("input");
        var sign=0;
        for(let i=0;i<inputs.length;i++){
            if(inputs[i].value.indexOf(",")!=-1||inputs[i].value.indexOf(";")!=-1){
                alert("问卷中不得出现\",\";\"字符!");
                return;
            }
            if(inputs[i].value==""){
                if(confirm("您有部分项未填写,确认提交么?")){
                    sign=1;
                    getAnswers();
                    break;
                }else {
                    return;
                }
            }
        }
        if(sign==0){
            getAnswers();
        }
        var value = document.getElementsByTagName("h1")[0].getAttribute("value");
        if(value==1) value=2;
        else value = 1;
        var content = document.getElementsByClassName("content")[0].outerHTML;
        var userId = document.getElementById("hid").value;
        var sid = document.getElementById("sid").value;
        $.post("/addAnswer.action",{surveyId:sid,userId:userId,sex:value,answer:answers.join(";"),format:content,status:1},function (data,textStatus) {
            answers=[];
            alert("提交成功.");
        });
    }
    function save() {
        var inputs = document.getElementsByTagName("input");
        var sign=0;
        for(let i=0;i<inputs.length;i++) {
            if (inputs[i].value.indexOf(",") != -1 || inputs[i].value.indexOf(";") != -1) {
                alert("问卷中不得出现\",\";\"字符!");
                return;
            }
        }
        if(sign==0){
            getAnswers();
        }
        var value = document.getElementsByTagName("h1")[0].getAttribute("value");
        if(value==1) value=2;
        else value = 1;
        var userId = document.getElementById("hid").value;
        var sid = document.getElementById("sid").value;

        var input = document.getElementsByTagName("input");
        for (var i=0;i<input.length;i++){
            input[i].setAttribute("value",input[i].value);
        }
        var content = document.getElementsByClassName("content")[0].outerHTML;
        $.post("/addAnswer.action",{surveyId:sid,userId:userId,sex:value,answer:answers.join(";"),format:content,status:0},function (data,textStatus) {
            answers=[];
            alert("保存成功.");
        });
    }
</script>
</body>
</html>
