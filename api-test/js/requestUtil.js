    /**
     * 获取数据ajax-get请求
     * @author laixm
     */
    $.get = function (url,data,headers,successCallback,errorCallback){
        $.ajax({
            url:url,
            type:"GET",
            contentType:"application/json",
            dataType:"json",
            timeout:10000,
            data:data,
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

    /**
     * 提交json数据的post请求
     * @author laixm
     */
    $.post = function(url,data,headers,successCallback,errorCallback){
        $.ajax({
            url:url,
            type:"POST",
            dataType:"json",
            data:data,
            timeout:60000,
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

    /**
     * 修改数据的ajax-put请求
     * @author laixm
     */
    $.put = function(url,data,headers,successCallback,errorCallback){
        $.ajax({
            url:url,
            type:"PUT",
            contentType:'application/json;charset=utf-8',
            dataType:"json",
            data:JSON.stringify(data),
            timeout:20000,
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
    /**
     * 删除数据的ajax-delete请求
     * @author laixm
     */
    $.delete = function(url,data,headers,successCallback,errorCallback){
        $.ajax({
            url:url,
            type:"DELETE",
            contentType:"application/json",
            dataType:"json",
            data:JSON.stringify(data),
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