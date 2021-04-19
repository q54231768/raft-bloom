var form = layui.form;
var fileToSend = null;

function showStatusTable() {
    var mainArea = $('#mainArea');
    mainArea.empty();
    var tableTemplate = "<table class=\"layui-table\" style=\"width: 900px;\">\n" +
        "    <colgroup>\n" +
        "      <col width=\"180\">\n" +
        "      <col width=\"180\">\n" +
        "      <col width=\"180\">\n" +
        "      <col width=\"180\">\n" +
        "      <col width=\"180\">\n" +
        "      <col>\n" +
        "    </colgroup>\n" +
        "    <thead>\n" +
        "      <tr>\n" +
        "        <th>当前位图大小</th>\n" +
        "        <th>当前哈希方法个数</th>\n" +
        "        <th>当前插入次数</th>\n" +
        "        <th>当前冲突次数</th>\n" +
        "        <th>当前碰撞率</th>\n" +
        "      </tr> \n" +
        "    </thead>\n" +
        "    <tbody>\n" +
        "      <tr>\n" +
        "        <td  id=\"bitSizeTd\"></td>\n" +
        "        <td  id=\"hashNumTd\"></td>\n" +
        "        <td  id=\"insertCountTd\"></td>\n" +
        "        <td id=\"collideCountTd\"></td>\n" +
        "        <td id=\"collisionProbabilityTd\"></td>\n" +
        "      </tr>\n" +
        "    </tbody>\n" +
        "  </table>";
    mainArea.append(tableTemplate);
    getBloomFilterStatus();
}


function getBloomFilterStatus() {
    $.ajax({
        url: 'getBloomFilterStatus',
        type: 'get',
        success: function (data) {
            showBloomFilterStatus(data);
        },
        error: function (data) {
            alert(data);
        }

    });
}


function showBloomFilterStatus(bloomStatus) {
    var bitSizeTd = document.getElementById("bitSizeTd");
    var hashNumTd = document.getElementById("hashNumTd");
    var insertCountTd = document.getElementById("insertCountTd");
    var collideCountTd = document.getElementById("collideCountTd");
    var collisionProbabilityTd = document.getElementById("collisionProbabilityTd");
    bitSizeTd.innerHTML = bloomStatus.bitSize;
    hashNumTd.innerHTML = bloomStatus.hashNum;
    insertCountTd.innerHTML = bloomStatus.insertCount;
    collideCountTd.innerHTML = bloomStatus.collideCount;
    collisionProbabilityTd.innerHTML = bloomStatus.collisionProbability;
}


function showRestForm() {
    var mainArea = $('#mainArea');
    mainArea.empty();
    var formTemplate = "<form class=\"layui-form\" action=\"\">\n" +
        "    <div class=\"layui-form-item\">\n" +
        "      <label class=\"layui-form-label\">预计插入量:</label>\n" +
        "      <div class=\"layui-input-block\">\n" +
        "        <input type=\"text\" id=\"expectedQuantity-input\" required   style=\"width: 200px;\"  lay-verify=\"required\"  autocomplete=\"off\" class=\"layui-input\">\n" +
        "      </div>\n" +
        "    </div>\n" +
        "    <div class=\"layui-form-item\">\n" +
        "      <label class=\"layui-form-label\">预计碰撞率:</label>\n" +
        "      <div class=\"layui-input-inline\">\n" +
        "        <input  id=\"misjudgmentRate-input\" required lay-verify=\"required\"  autocomplete=\"off\" class=\"layui-input\">\n" +
        "      </div>\n" +
        "    </div>\n" +
        "  </form>\n" +
        "  <button type=\"button\"     onclick=\"reset()\" style=\"margin-left: 20px;\" class=\"layui-btn\">重置</button>";
    mainArea.append(formTemplate);
    form.render();

}


function reset() {
    var expectedQuantityInput = document.getElementById("expectedQuantity-input");
    var misjudgmentRateInput = document.getElementById("misjudgmentRate-input");
    var expectedQuantity = expectedQuantityInput.value;
    var misjudgmentRate = misjudgmentRateInput.value;

    $.ajax({
        url: 'reset',
        type: 'post',
        data: {"expectedQuantity": expectedQuantity, "misjudgmentRate": misjudgmentRate},
        success: function (data) {
            alert(data);
            if (data == "重置成功") {
                showStatusTable();
            }
        },
        error: function (data) {
            alert(data);
        }
    });
}


function showSingleInsertForm() {
    var mainArea = $('#mainArea');
    mainArea.empty();
    var singleInsertForm = "\n" +
        "  <form class=\"layui-form\" action=\"\">\n" +
        "    <div class=\"layui-form-item\" style=\"margin-top: 10px\">\n" +
        "      <label class=\"layui-form-label\">插入内容:</label>\n" +
        "      <div class=\"layui-input-block\">\n" +
        "        <input type=\"text\" id=\"str-input\" required   style=\"width: 200px;display: inline;height: 38px\"   autocomplete=\"off\" class=\"layui-input\">\n" +
        "        <button type=\"button\"    onclick=\"singleInsert()\"  class=\"layui-btn\">插入</button>  \n" +
        "    </div>\n" +
        "    </div>\n" +
        "  </form>";
    mainArea.append(singleInsertForm);
    form.render();

}


function singleInsert() {
    var strInput = document.getElementById("str-input");
    var str = strInput.value;
    $.ajax({
        url: 'singleInsert',
        type: 'post',
        data: {"content": str},
        success: function (data) {
            if (data == 1) {
                alert("插入成功");
            }else{
                alert("插入失败");
            }
        },
        error: function (data) {
            alert(data);
        }
    });


}


function showBatchInsertForm() {
    var mainArea = $('#mainArea');
    mainArea.empty();
    var batchForm = "<div>\n" +
        "<form class=\"layui-form\" action=\"\">\n" +
        "  <div class=\"layui-form-item\"  style=\"margin-top: 10px\">\n" +
        "    <div class=\"layui-input-block\" style=\"display: inline;\"> \n" +
        "          <input id=\"chooseFile\"   placeholder=\"选择文件\"  type=\"file\" style=\"width: 180px;display: inline;height: 38px\"> \n" +
        "          <button type=\"button\"   onclick=\"submitFile()\"  class=\"layui-btn\">插入</button>  \n" +
        "    </div>\n" +
        "  </div>\n" +
        "</form>\n" +
        "</div>\n" +
        " <div id=\"result-div\">\n" +
        " </div>";
    mainArea.append(batchForm);
    form.render();
    getLocalFile();

}


function getLocalFile() {
    var chooseFile = document.getElementById("chooseFile");
    var targetFile = document.getElementById("fileToSee");
    if (typeof FileReader === 'undefined') {
        targetFile.innerHTML = "抱歉，你的浏览器不支持 FileReader";
        chooseFile.setAttribute('disabled', 'disabled');
    } else {
        chooseFile.addEventListener('change', readFile, false);
    }

}


function readFile() {
    var file = this.files[0];
    var reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = function (e) {
        fileToSend = this.result;
    }

}


function submitFile() {

    var formData = new FormData();
    var chooseFile=document.getElementById("chooseFile");
    var files=chooseFile.files;
    formData.append("txtfile", files[files.length-1])
    $.ajax({
        url: 'batchInsert',
        type: 'post',
        data: formData,
        cache: false,
        contentType: false,
        processData: false,
        success: function (data) {
            if (data["result"] == "fail") {
                alert("插入失败");
            } else {
                showResultTable(data);
            }
        },
        error: function (data) {
            alert(data);
        }
    });

}

function showResultTable(data) {
    var resultDiv = $('#result-div');
    resultDiv.empty();
    var resultTable = "\n" +
        " <table class=\"layui-table\" style=\"width: 400px;\">\n" +
        "      <colgroup>\n" +
        "        <col width=\"180\">\n" +
        "        <col width=\"180\">\n" +
        "      </colgroup>\n" +
        "      <thead>\n" +
        "        <tr>\n" +
        "          <th>本次插入数据(条)</th>\n" +
        "          <th>本次插入耗时(毫秒)</th>\n" +
        "        </tr> \n" +
        "      </thead>\n" +
        "      <tbody>\n" +
        "        <tr>\n" +
        "          <td  id=\"count-td\"></td>\n" +
        "          <td  id=\"takeUpTime-td\"></td>\n" +
        "       </tr>\n" +
        "      </tbody>\n" +
        "</table>";
     resultDiv.append(resultTable);
    showResultData(data);

}

function showResultData(data) {
    var  countTd=document.getElementById("count-td");
    var takeUpTimeTd=document.getElementById("takeUpTime-td");
    countTd.innerHTML=data.count;
    takeUpTimeTd.innerHTML=data.takeUpTime;

}


function showSearchStrForm() {
    var mainArea = $('#mainArea');
    mainArea.empty();
    var searchSeachStrForm = "\n" +
        "  <form class=\"layui-form\" action=\"\">\n" +
        "    <div class=\"layui-form-item\" style=\"margin-top: 10px\">\n" +
        "      <label class=\"layui-form-label\">查询关键字:</label>\n" +
        "      <div class=\"layui-input-block\">\n" +
        "        <input type=\"text\" id=\"str-input\" required   style=\"width: 200px;display: inline;height: 38px\"   autocomplete=\"off\" class=\"layui-input\">\n" +
        "        <button type=\"button\"    onclick=\"searchIfContain()\"  class=\"layui-btn\">查询</button>  \n" +
        "    </div>\n" +
        "    </div>\n" +
        "  </form>";
    mainArea.append(searchSeachStrForm);
    form.render();


}

function searchIfContain() {
    var strInput=document.getElementById("str-input");
    var content=strInput.value;
    $.ajax({
        url: 'checkIfContain',
        type: 'post',
        data: {"content": content},
        success: function (data) {
           alert(data);
        },
        error: function (data) {
            alert(data);
        }
    });



}

function showNodeSatatusTable() {
    var mainArea = $('#mainArea');
    mainArea.empty();
    var tableTemplate = "<table class=\"layui-table\" style=\"width: 560px;\">\n" +
        "    <colgroup>\n" +
        "      <col width=\"280\">\n" +
        "      <col width=\"280\">\n" +
        "      <col>\n" +
        "    </colgroup>\n" +
        "    <thead>\n" +
        "      <tr>\n" +
        "        <th>节点名称</th>\n" +
        "        <th>存活状态</th>\n" +
        "      </tr> \n" +
        "    </thead>\n" +
        "    <tbody id=\"nodesTbody\">\n" +
        "    </tbody>\n" +
        "  </table>";
    mainArea.append(tableTemplate);
    getNodeSatatus();


}


function getNodeSatatus() {
    $.ajax({
        url: 'getNodeStatus',
        type: 'get',
        success: function (data) {
            showNodeStatus(data);
        },
        error: function (data) {
            alert(data);
        }

    });



}

function  showNodeStatus(data) {
    var aliveMap = data.aliveMap;
    var leaderId = data.leaderId;
    var nodesTbody = document.getElementById("nodesTbody");
    for(key in aliveMap){
        var tr = document.createElement('tr');
        var td1 = document.createElement('td');
        var td2 = document.createElement('td');
        if(key == leaderId){
           td1.innerText = (key + "(leader)");
        }else{
           td1.innerText = key;
        }
        if(aliveMap[key] == 0){
            td2.innerText = "已下线";
        }else {
            td2.innerText = "存活";
        }
        tr.appendChild(td1);
        tr.appendChild(td2);
        nodesTbody.appendChild(tr);
    }





}