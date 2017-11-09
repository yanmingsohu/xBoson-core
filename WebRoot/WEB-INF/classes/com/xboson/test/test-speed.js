var a = Date.now();  
var b = 0;  
var c = 0;  
var avg = 0;  

  
var fn = function() {  
    return Math.sin(Math.random());
};


while (c<10) {  
    fn();  
    b++;  
    if (Date.now() - a >= 1000) {  
        a = Date.now();  
        console.log('1s Math.sqrt do', b);  
        avg += b;  
        ++c;  
        b = 0;  
    }  
}  
  
console.log('avg 1s use', avg/c, 'counts');  
