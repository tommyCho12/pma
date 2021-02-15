var chartDataStr = decodeHtml(chartData);
var chartJsonArray = JSON.parse(chartDataStr);

var arrayLength = chartJsonArray.length;
var numericData = [];
var labelData = [];

for(var i = 0; i < arrayLength; i++){
    numericData[i] = chartJsonArray[i].value;
    labelData[i] = chartJsonArray[i].label;
}

new Chart(
    document.getElementById("homePieChart"),
    {
        type: 'pie',
        data: {
            labels: labelData,
            datasets: [{
                label: 'Projects by stages',
                backgroundColor: ["#3e95cd", "#8ef4a2", "#3cba9f"],
                borderColor: 'rgb(255, 255, 255)',
                data: numericData
            }]
        },
        // Configuration options go here
        options: {
            title:{
                display: true,
                text: 'Project Stauses'
            }
        }
    }
);

// "[{"value": 1, "label": "COMPLETED"},{"value": 2, "label": "INPROGRESS"},{"value": 1, "label": "NOTSTARTED"}]"
function decodeHtml(html){
    var txt = document.createElement("textarea");
    txt.innerHTML = html;
    return txt.value;
}