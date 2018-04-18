/*
 * MIT License
 * 
 * Copyright (c) 2017 CaiDongyu
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */ 

$.submitForm = function(formId,url,headers,successCallback,errorCallback){
    $(formId).ajaxSubmit({
        url:url,
        timeout:10000,
        beforeSend:function(request) {
            for(var key in headers){
                request.setRequestHeader(key, headers[key]);
            }
        },
        success:function(data){
            if(successCallback){
                successCallback(data);
            }
        },
        error:function(XMLHttpRequest, textStatus, errorThrown){
            if(errorCallback){
                errorCallback(XMLHttpRequest, textStatus, errorThrown);
            }
        }
    });
};

$.get = function (url,data,headers,successCallback,errorCallback){
    $.ajax({
        url:url,
        type:"GET",
        contentType:"application/json",
        dataType:"text",
        timeout:10000,
        data:data,
        beforeSend:function(request) {
            for(var key in headers){
                request.setRequestHeader(key, headers[key]);
            }
        },
        success:function(data){
            if(successCallback){
                try{
                    data = JSON.parse(data);
                }catch(e){};
                successCallback(data);
            }
        },
        error:function(XMLHttpRequest, textStatus, errorThrown){
            if(errorCallback){
                errorCallback(XMLHttpRequest, textStatus, errorThrown);
            }
        }
    });
};


$.post = function(url,data,headers,successCallback,errorCallback){
    $.ajax({
        url:url,
        type:"POST",
        dataType:"text",
        traditional:true,
        data:data,
        timeout:60000,
        beforeSend:function(request) {
            for(var key in headers){
                request.setRequestHeader(key, headers[key]);
            }
        },
        success:function(data){
            if(successCallback){
                try{
                    data = JSON.parse(data);
                }catch(e){};
                successCallback(data);
            }
        },
        error:function(XMLHttpRequest, textStatus, errorThrown){
            if(errorCallback){
                errorCallback(XMLHttpRequest, textStatus, errorThrown);
            }
        }
    });
};

$.put = function(url,data,headers,successCallback,errorCallback){
    $.ajax({
        url:url,
        type:"PUT",
        contentType:'application/json;charset=utf-8',
        dataType:"text",
        data:JSON.stringify(data),
        timeout:20000,
        beforeSend:function(request) {
            for(var key in headers){
                request.setRequestHeader(key, headers[key]);
            }
        },
        success:function(data){
            if(successCallback){
                try{
                    data = JSON.parse(data);
                }catch(e){};
                successCallback(data);
            }
        },
        error:function(XMLHttpRequest, textStatus, errorThrown){
            if(errorCallback){
                errorCallback(XMLHttpRequest, textStatus, errorThrown);
            }
        }
    });
};

$.delete = function(url,data,headers,successCallback,errorCallback){
    $.ajax({
        url:url,
        type:"DELETE",
        contentType:"application/json",
        dataType:"text",
        data:JSON.stringify(data),
        beforeSend:function(request) {
            for(var key in headers){
                request.setRequestHeader(key, headers[key]);
            }
        },
        success:function(data){
            if(successCallback){
                try{
                    data = JSON.parse(data);
                }catch(e){};
                successCallback(data);
            }
        },
        error:function(XMLHttpRequest, textStatus, errorThrown){
            if(errorCallback){
                errorCallback(XMLHttpRequest, textStatus, errorThrown);
            }
        }
    });
};