console.log("hello yarntest")


const fs = require('fs');

fs.readFile('myreport.json', 'utf8', (err, data) => {
  if (err) {
    console.error(err);
    return;
  }
  

  let myreport = JSON.parse(data);
  let output = "";
  output +=JSON.stringify(myreport[0].histogram.json, null, 2);
  output +=JSON.stringify(myreport[1].histogram.json, null, 2);
  console.log(output);
  fs.writeFile("myreportsmall.txt", output, function(err) {
    if(err) {
        return console.log(err);
    }
    console.log("The file was saved!");
}); 
});