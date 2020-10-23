<%@ include file="/systeminfo/init_wev8.jsp" %>
<html>
<head>
    <meta charset="utf-8">
    <title>预算信息查询</title>
    <!-- 引入 echarts.js -->
    <%--    <script src="../js/jquery-3.2.1.min.js"></script>--%>
    <link type="text/css" href="/js/tabs/css/e8tabs1_wev8.css" rel="stylesheet"/>
    <link rel="stylesheet" href="/css/ecology8/request/searchInput_wev8.css" type="text/css"/>
    <script type="text/javascript" src="/js/ecology8/request/searchInput_wev8.js"></script>
    <link rel="stylesheet" href="/css/ecology8/request/seachBody_wev8.css" type="text/css"/>
    <link rel="stylesheet" href="/css/ecology8/request/hoverBtn_wev8.css" type="text/css"/>
    <link rel="stylesheet" href="../css/index.min.css" type="text/css"/>
    <script type="text/javascript" src="/js/ecology8/request/hoverBtn_wev8.js"></script>
    <script type="text/javascript" src="/js/ecology8/request/titleCommon_wev8.js"></script>

    <script src="../js/echarts.min.js"></script>
    <script src="../js/china.js"></script>
    <script src="../js/ecStat.min.js"></script>
    <script src="../js/dataTool.js"></script>
    <script src="../js/vue.js"></script>
    <script src="../js/util/myAjax.js"></script>
    <script language=javascript src="/js/ecology8/request/e8.browser_wev8.js"></script>
    <style>
        * {
            margin: 0;
            padding: 0;
        }

        #main {
            width: 1000px;
            margin: auto;
        }

        .chart {
            width: 246px;
            height: 200px;
            float: left;
            margin-top: 30px;
        }

        .top {
            width: 800px;
            margin: auto;
            height: 60px;
            padding: 20px 0;

        }

        .hr {
            border-bottom: 1px solid #dfdfdf;
            width: 100%;
            height: 1px;
        }

        .top_box {
            float: left;
            width: 260px;
        }

        .top_box > div:nth-child(1) {
            float: left;
            margin-right: 50px;
        }

        .top_box > button > span {
            background: none;
            padding: 0;
        }
    </style>
</head>
<body>
<div class="top">
    <div class="top_box">
        <div>成本中心</div>
        <div id="bm" style="width: 100px;height: 50px;float: left;"></div>
    </div>
    <div class="top_box">
        <div>年份</div>
        <div id="year" style="width: 100px;height: 50px;float: left;"></div>
    </div>
    <div class="top_box">
        <button type="button" title="查询" class="ant-btn ant-btn-primary" onclick="initData()">
            <span>查询</span>
        </button>
    </div>
</div>
<div class="hr"></div>
<div id="main" style="clear: both;">
</div>
<div class="ant-table-placeholder">
    <span><i class="anticon anticon-frown"></i>暂无数据</span>
</div>
<!-- 为ECharts准备一个具备大小（宽高）的Dom -->

<script type="text/javascript">
    function createChart(data) {
        var dom = "<div id=\"" + data.idName + "\" class=\"chart\"></div>";
        $("#main").append(dom);
        var myChart = echarts.init(document.getElementById(data.idName));
        var option = {
            title: {
                text: data.title,
                left: 'center',
                textStyle: {
                    fontSize: 12
                }
            },
            tooltip: {
                trigger: 'item',
                formatter: '{a} <br/>{b} : {c} ({d}%)'
            },
            series: [
                {
                    name: data.title,
                    type: 'pie',
                    radius: '25%',
                    center: ['50%', '50%'],
                    data: data.data,
                    emphasis: {
                        itemStyle: {
                            shadowBlur: 10,
                            shadowOffsetX: 0,
                            shadowColor: 'rgba(0, 0, 0, 0.5)'
                        }
                    }
                }
            ]
        };
        myChart.setOption(option);
    }

    // initData();
    //初始化数据
    function initData() {
        var requestData = {
            bm: $("#bmBrowser").val(),
            year: $("#yearBrowser").val()
        }
        if (requestData.bm == "") {
            alert("请选择成本中心！");
            return false;
        }
        if (requestData.year == "") {
            alert("请选择年份！");
            return false;
        }
        var responseData = myAjax("/msdev/jhswyy/sap/ys/jsp/buget.jsp", "POST", requestData);
        if (responseData.code == 0) {
            $("#main").html("");
            responseData.data.forEach(function (item) {
                createChart(item);
            })
            if (responseData.data.length > 0) {
                $(".ant-table-placeholder").css("display", "none");
            } else {
                $(".ant-table-placeholder").css("display", "block");
            }
        } else {
            alert(responseData.message);
        }
    }

    createBm();

    function createBm() {
        $("#bm").e8Browser({ //docLastModUser 显示浏览框的的span或者div的id
            name: "bmBrowser",//浏览按钮的name
            viewType: "0",
            browserValue: "0",//浏览按钮的值
            isMustInput: "1",//是否必填  2为必填
            browserSpanValue: "",//浏览按钮显示的值
            hasInput: true,//是否有输入框
            linkUrl: "#",//链接地址
            isSingle: true,//是否是单选
            completeUrl: "/data.jsp?type=4",//自动联想获取数据的地址
            browserUrl: "/systeminfo/BrowserMain.jsp?url=/hrm/company/MultiDepartmentBrowserByOrder.jsp",//浏览框打开的地址
            width: "150px",//宽度
            hasAdd: false,//是否显示添加按钮
            isSingle: true,//是否单选
            _callback: 'aa', //回调方法
            _callbackParams: ''//回调方法传入的参数
        });
    }

    function aa() {
        var bmName = $("#bmBrowserspan").children("a").eq(0).html();
        var bmNameArr = bmName.split(",");
        bmNameArr.forEach(function (value, index) {
            $("#bmBrowserspan").children("a").eq(index).html(value);
        })
    }

    function bb() {

    }

    createYear();

    function createYear() {
        var date = new Date();
        var year = date.getFullYear();
        $("#year").e8Browser({ //docLastModUser 显示浏览框的的span或者div的id
            name: "yearBrowser",//浏览按钮的name
            viewType: "0",
            browserValue: "" + year,//浏览按钮的值
            isMustInput: "1",//是否必填  2为必填
            browserSpanValue: "" + year,//浏览按钮显示的值
            hasInput: true,//是否有输入框
            linkUrl: "#",//链接地址
            isSingle: true,//是否是单选
            completeUrl: "/data.jsp",//自动联想获取数据的地址
            browserUrl: "/systeminfo/BrowserMain.jsp?url=/workflow/field/Workflow_FieldYearBrowser.jsp",//浏览框打开的地址
            width: "150px",//宽度
            hasAdd: false,//是否显示添加按钮
            isSingle: true,//是否单选
            _callback: 'bb', //回调方法
            _callbackParams: ''//回调方法传入的参数
        });
    }
</script>
</body>
</html>