'use strict';
console.log('mvvm version:18.2.5');
//支持指令：
//1.nc-value  只能写变量，不可以写表达式，双向绑定，凡是有value属性的元素，都可以使用。
//4.nc-src	  例 nc-src="{{basePictureUrl+item.picture}}"
//5.nc-text   列 <span nc-text="{{item.name}}"></span>
//2.nc-for	  只能写命令，row in ary 这样的形式，任何元素都可以使用，有一点注意，只能使用基本属性，不能传递entity，比如onclick="delete({{row}})"，这是不行的，但是可以onclick="delete({{$index}})"。
//3.nc-if	  支持表达式
//（已放弃nc-class）4.nc-class  支持指令，express?class1:class2这样的形式，express可以是表达式
//{{}}		  可以是表达式，可以接|过滤器，{{}}可以用在任何文本或者节点的属性里。
//$watch(proPathAry,function)，proPathAry参数是需要监控的变量名数组，function是回调函数
//
//注意事项：
//1.变量必须先声明，否则不在托管范围
//2.变量只能使用a-zA-Z0-9_这些

//暴露给外部的全局对象
//注意nice-mvvm.js要放在第一个引入
var $NICE_MVVM = function(mvvmElementId,excludeIds){
	var mvvmElement = document.getElementById(mvvmElementId);

	//内存参数mvvm中的model对象
	var $nc = new Object();
	//参数监听队列
	var $WATCH_QUEE = {};
	//监听
	var $watch = function(proPathAry,fun){
		if(typeof proPathAry === 'string'){
			var proPath = proPathAry;
			proPathAry = [];
			proPathAry.push(proPath);
		}

		for(var i=0;i<proPathAry.length;i++){
			if(!$WATCH_QUEE[proPathAry[i]]){
				$WATCH_QUEE[proPathAry[i]] = [];
			}
			$WATCH_QUEE[proPathAry[i]].push({
				'proPathAry':proPathAry,
				'fun':fun
			});
		}
	};
	//节点渲染执行完毕后的回调
	var $AFTER_RENDER = null;
	var $onload = function(fun){
		$AFTER_RENDER = fun;
	};
	//在nice-mvvm刷新周期内，会被主动调用一次。
	var $AFTER_FLUSH = null;
	var $onflush = function(fun){
		$AFTER_FLUSH = fun;
	};


	var $INITED = false;
	var $init = function(){
		if(!$INITED){
			$INITED = true;
		}else{
			//把缓存放回去
			var newElement = document.getElementById(mvvmElementId);
			newElement.innerHTML="";
			while(mvvmElement.childNodes && mvvmElement.childNodes.length > 0){
				newElement.appendChild(mvvmElement.firstChild);
			}
			mvvmElement = newElement;

			return false;
		}

		//*********************** $nc如果冲突，以上代码可以修改****************
		var $SCOPE = {
			'$DATA': $nc
		};

		var $SCOPE_DATA_ = new Object();//副本，用于脏值检测和同步
		
		$SCOPE.$NODE_ID_POINT = 1;//节点id指针
		$SCOPE.$UNREFRESH_NODE_ID = -1;//排除在外，不需要同步的节点id
		$SCOPE.$V2M_NODE_MAP = new Object();//存放VM渲染的节点对象

		var INDEX = 1;
		$SCOPE.$ADD_V2M_NODE_MAP = function(expression,nodePack){

			//转换数组的表达形式
			expression = expression.replace(/\[/g,'.');
			expression = expression.replace(/\]/g,'');
			
/*			
			//TODO:因为后续改了expression的分析方式，所以不需要这样僵化的替换原生方法
			//还有别的原生方法，如果没写全，要补充
			expression = expression.replace(/\.indexOf\(/g,' ');//不能让原生方法影响参数解析
			expression = expression.replace(/\.substring\(/g,' ');//不能让原生方法影响参数解析
			expression = expression.replace(/\.substr\(/g,' ');//不能让原生方法影响参数解析
			expression = expression.replace(/\.subStr\(/g,' ');//不能让原生方法影响参数解析
			expression = expression.replace(/\.charAt\(/g,' ');//不能让原生方法影响参数解析
			expression = expression.replace(/\.lastIndexOf\(/g,' ');//不能让原生方法影响参数解析
			expression = expression.replace(/\.match\(/g,' ');//不能让原生方法影响参数解析
			expression = expression.replace(/\.search\(/g,' ');//不能让原生方法影响参数解析
			expression = expression.replace(/\.slice\(/g,' ');//不能让原生方法影响参数解析
			expression = expression.replace(/\.split\(/g,' ');//不能让原生方法影响参数解析
			expression = expression.replace(/\.split\(/g,' ');//不能让原生方法影响参数解析
			expression = expression.replace(/\.length\(/g,' ');//不能让原生方法影响参数解析
			expression = expression.replace(/\.toLowerCase\(/g,' ');//不能让原生方法影响参数解析
			expression = expression.replace(/\.toUpperCase\(/g,' ');//不能让原生方法影响参数解析
			expression = expression.replace(/\.replace\(/g,' ');//不能让原生方法影响参数解析
			expression = expression.replace(/\.length/g,' ');//不能让原生方法影响参数解析
*/
			var find = false;
			for(var pro in $SCOPE_DATA_){
				var expression_ = $SCOPE.$REPLACE_PROPATH(expression,pro,'');
				if(expression_ != expression){
					//说明pro在expression里存在
					if(!$SCOPE.$V2M_NODE_MAP[pro]){
						$SCOPE.$V2M_NODE_MAP[pro] = [];
					}
					find = true;
					var dublicNode = false;
					for(var arryIndex=0;arryIndex<$SCOPE.$V2M_NODE_MAP[pro].length;arryIndex++){
						if($SCOPE.$V2M_NODE_MAP[pro][arryIndex].id == nodePack.id){
							dublicNode = true;
						}
					}
					if(!dublicNode){
						$SCOPE.$V2M_NODE_MAP[pro].push($SCOPE.$COPY_NODE_PACK(nodePack));
					}
				}
			}

			if(!find){
				//尝试是否是数组形式的
				var proReg = new RegExp("^"+expression+'\\.[0-9]+\\..*');
				for(var pro in $SCOPE_DATA_){
					if(proReg.test(pro)){
						if(!$SCOPE.$V2M_NODE_MAP[expression]){
							$SCOPE.$V2M_NODE_MAP[expression] = [];
						}
						var dublicNode = false;
						for(var arryIndex=0;arryIndex<$SCOPE.$V2M_NODE_MAP[expression].length;arryIndex++){
							if($SCOPE.$V2M_NODE_MAP[expression][arryIndex].node == nodePack.node){
								dublicNode = true;
							}
						}
						if(!dublicNode){
							$SCOPE.$V2M_NODE_MAP[expression].push($SCOPE.$COPY_NODE_PACK(nodePack));
						}
						
						find = true;
						break;
					}
				}
			}

			if(!find){
				//尝试常量表达式解析，如果能解析，那就直接作为常量值
				try{
					var val = eval(expression);
					nodePack.render(expression,val);
				}catch(err){}
			}
			

			//每次有新的节点push进来的时候，需要讲对应key的数据副本清空重新渲染
			// delete $SCOPE_DATA_['.'+proPath];
		};

		$SCOPE.$DEL_V2M_NODE_MAP = function(nodePackIds){var num=0;
			/*var num1=0;
			for(var key in $SCOPE.$V2M_NODE_MAP){
				num1 = num1+$SCOPE.$V2M_NODE_MAP[key].length;
			}*/
			var delNodePackIds = nodePackIds;
			var nodePackIdAry = nodePackIds.split(',');
			for(var i=0;i<nodePackIdAry.length;i++){
				if(nodePackIdAry[i]){
					delNodePackIds = delNodePackIds+$SCOPE.$GET_DEL_NODE_ARY(nodePackIdAry[i]);
				}
			}
			var tmp_map = {};
			for(var key in $SCOPE.$V2M_NODE_MAP){
				var tmp_ary = [];
				for(var i=0;$SCOPE.$V2M_NODE_MAP[key] && i<$SCOPE.$V2M_NODE_MAP[key].length;i++){
					if(delNodePackIds.indexOf(','+$SCOPE.$V2M_NODE_MAP[key][i].id+',')<0){
						//不删除的需要留下
						tmp_ary.push($SCOPE.$V2M_NODE_MAP[key][i]);
					}
				}
				if(tmp_ary.length > 0){
					tmp_map[key] = tmp_ary;
				}
			}

			$SCOPE.$V2M_NODE_MAP = tmp_map;
			/*var num2=0;
			for(var key in $SCOPE.$V2M_NODE_MAP){
				num2 = num2+$SCOPE.$V2M_NODE_MAP[key].length;
			}
			console.log('['+mvvmElementId+']原:'+num1+'删:'+delNodePackIds.split(',,').length+'剩:'+num2);*/
		}

		$SCOPE.$GET_DEL_NODE_ARY = function(nodeParentId){
			var delNodePackIds = '';
			for(var key in $SCOPE.$V2M_NODE_MAP){
				for(var i=0;i<$SCOPE.$V2M_NODE_MAP[key].length;i++){
					var nodePack = $SCOPE.$V2M_NODE_MAP[key][i];
					if(nodePack.parentNodePackIds.indexOf(nodeParentId)>=0){
						if(!delNodePackIds || (','+delNodePackIds).indexOf(','+nodePack.id+',')<0){
							delNodePackIds = delNodePackIds+nodePack.id+',';
						}
						delNodePackIds = delNodePackIds+$SCOPE.$GET_DEL_NODE_ARY(nodePack.id);
					}
				}
			}
			return delNodePackIds;
		}

		$SCOPE.$REPLACE_PROPATH = function(expression,pro,newPro){
			var stepLen = pro.length;
			if(expression.indexOf(pro) >= 0){
				var stepIndexAry = [];
				for(var step=0;step<expression.length && (step+stepLen)<=expression.length;step++){
					var stepStr = expression.substring(step,step+stepLen);
					if(stepStr === pro){
						var preStr = step>0?expression.substring(step-1,step):'';
						var flagStrReg = /[_0-9a-zA-Z]/;
						//直接判断到下一个位置
						step = step+stepLen;
						//判断前一个字，是不是变量包含
						if(!flagStrReg.test(preStr)){
							if(step == expression.length){
								//如果到底了，那么这个词，就是要替换的
								stepIndexAry.push(step);
							}else{
								//如果还没到底
								//判断是否后面跟着的，是_0-9az-A-Z
								var nextStr = expression.substring(step,step+1);
								if(!flagStrReg.test(nextStr)){
									//这就说明确实是一个单词
									stepIndexAry.push(step);
								}
							}
						}
					}
				}
				if(stepIndexAry.length > 0){
					var expression_ = '';
					var start = 0;
					for(var step=0;step<stepIndexAry.length;step++){
						var end = stepIndexAry[step]-stepLen;
						expression_ = expression_+expression.substring(start,end);
						expression_ = expression_+newPro;
						start = stepIndexAry[step];
					}
					expression_ = expression_+expression.substring(start);
					expression = expression_;
				}
			}
			return expression;
		}

		$SCOPE.$COPY_NODE_PACK = function(nodePack){
			//把所有的内容都复制出来，除了node
			//因为node要统一渲染，所以要共用，但是其余部分，比如version，是不公用的
			var newNodePack = new Object();
			for(var key in nodePack){
				newNodePack[key] = nodePack[key];
			}
			//node因为不是基础类型，所以，自动会被拷贝引用的
			return newNodePack;
		};


		//proPath表示单一的参数，不是表达式
		//express表示表达式，只有nc-if、{{}}有
		//command表示指令，nc-for和nc-class都是
		$SCOPE.$NICE_COMMAND = {
			'nc-value':{
				'commandName':'nc-value',//双向绑定
				'initFunc':function(node,proPath,parentNodePackIds){
					var node_nc_id = $SCOPE.$NODE_ID_POINT++;

					var onchangeFun = node.onchange;
					node.onchange=function(){
						//保存值到内存
						if(node.type.toLowerCase() == 'checkbox'){
							//checkbox的值会组成数组
							var proPathVal = $SCOPE.$GET_VAL(proPath);
							if(!proPathVal){
								proPathVal = [];
							}
							if(node.checked){
								proPathVal.push(node.value);
							}else{
								var spliceIndex = -1;
								for(var i=0;i<proPathVal.length;i++){
									if(proPathVal[i]==node.value){
										spliceIndex = i;
										break;
									}
								}
								if(spliceIndex >= 0){
									proPathVal.splice(spliceIndex,1);
								}
							}
							$SCOPE.$SET_VAL(proPath,proPathVal);
						}else{
							//其他的都是替换
							$SCOPE.$SET_VAL(proPath,node.value);
						}

						//调用用户原生方法
						if(onchangeFun){
							onchangeFun();
						}
					};
					
					var onfocusFun = node.onfocus;
					node.onfocus=function(){
						//将自己设为不需要dom更新
						if(node.type.toLowerCase() == 'checkbox'){
							//不要排除掉本身
						}else if(node.type.toLowerCase() == 'radio'){
							//不要排除掉本身
						}else{
							//排除掉本身
							$SCOPE.$UNREFRESH_NODE_ID = node_nc_id;
						}
						//调用用户原生方法
						if(onfocusFun){
							onfocusFun();
						}
					};

					var onblurFun = node.onblur;
					node.onblur=function(){
						//将自己设为需要dom更新
						$SCOPE.$UNREFRESH_NODE_ID = -1;
						//调用用户原生方法
						if(onblurFun){
							onblurFun();
						}
					};

					//加入到V2M_大Map里
					$SCOPE.$ADD_V2M_NODE_MAP(proPath,{
						'id':node_nc_id,
						'parentNodePackIds':parentNodePackIds,
						'node':node,
						'expression':proPath,
						'render':function(proPath,val){
							if(this.node.type.toLowerCase() == 'checkbox'){
								//需要判断勾选
								var proPathVal = $SCOPE.$GET_VAL(proPath);
								this.node.checked = false;
								if(proPathVal){
									for(var i=0;i<proPathVal.length;i++){
										if(proPathVal[i] == this.node.value){
											this.node.checked = true;
											break;
										}
									}
								}
							}else if(this.node.type.toLowerCase() == 'radio'){
								//需要判断选中
								var proPathVal = $SCOPE.$GET_VAL(proPath);
								this.node.checked = false;
								if(proPathVal == this.node.value){
									this.node.checked = true;
								}
							}else{
								this.node.value=val;
							}
						}
					});
					return node_nc_id;
				}
			},
            'nc-src':{
                'commandName':'nc-src',//绑定src属性，必须要求有nc-src值，可以避免原生html的src因为表达式出现网络404的问题。
                'initFunc':function(node,proPath,parentNodePackIds){
                    return $SCOPE.$BIND_TXT(node,'src',parentNodePackIds);
                }
            },
            'nc-text':{
                'commandName':'nc-text',//可以直接渲染元素的文本
                'initFunc':function(node,proPath,parentNodePackIds){
                    return $SCOPE.$BIND_TXT(node,'innerHTML',parentNodePackIds);
                }
            },
            'nc-for':{
				'commandName':'nc-for',//for循环
				'initFunc':function(node,command,parentNodePackIds){
					var node_nc_id = $SCOPE.$NODE_ID_POINT++;
					//比如row in records，records是数组，这里在V2M_MAP里的键，是recrods，而不是records[0]这样。
					var flag = command.substring(0,command.indexOf(' in '));
					flag = flag.replace(/ +/g,'');
					var proPath = command.substring(command.indexOf(' in ')+4);
					proPath = proPath.replace(/ +/g,'');

					var indexReg =  new RegExp(flag+'\\.\\$index','g');
					var NEW_NODE_MAP = {
						'id':node_nc_id,
						'parentNodePackIds':parentNodePackIds,
						'node':node,
						'expression':proPath,
						'render':function(proPath,val){
							//如果新的val的长度，和当前的dom节点列表已经不一致，那么需要重新加载节点，否则不需要加载新的节点
							
							if(val.length > this.newNodeAry.length){

								//有下一个兄弟节点，就在这个兄弟节点前使劲插入
								for(var i=this.newNodeAry.length;i<val.length;i++){

									//替换nc-for指令
									var newHtml = this.nodeHtml;
									// var newHtml = this.nodeHtml.replace(/nc-for='[^']+'/g,'');
									// newHtml = newHtml.replace(/nc-for="[^"]+"/g,'');

									//替换$index
									newHtml = newHtml.replace(this.indexReg,i);
									//替换row.
									newHtml = $SCOPE.$REPLACE_PROPATH(newHtml,this.flag,proPath+'['+i+']');
									// newHtml = newHtml.replace(flagReg,proPath+'['+i+']');

									var lowerNewHtml = newHtml.toLowerCase();
									//newHtml在拼接和处理前，需要补全
									//tr需要补充到table
									//option需要补充到select
									var level = 1;
									if(lowerNewHtml.indexOf('<tr') == 0){
										newHtml = '<table><tbody>'+newHtml+'</tbody></table>';
										level = 2;
									}else if(lowerNewHtml.indexOf('<option') == 0){
										newHtml = '<select>'+newHtml+'</select>';
										level=1;
									}else if(lowerNewHtml.indexOf('<ul') == 0){
										newHtml = '<ul>'+newHtml+'</ul>';
										level=1;
									}else{
										level=0;
									}
									//补全之后的newHtml，知道层级，添加到临时div后可以获取
									var tmpDiv =  document.createElement('div');
									tmpDiv.innerHTML = newHtml;
									var newNode = tmpDiv.childNodes[0];
									for(var j=0;j<level;j++){
										newNode = newNode.childNodes[0];
									}
									

									if(this.nextSibling){
										this.parentNode.insertBefore(newNode,this.nextSibling);
									}else{
										this.parentNode.appendChild(newNode);
									}
									//初始化新加的节点
									var nodePackIds = $SCOPE.$INIT_MVVM(newNode,','+this.id+',');

									this.newNodeAry.push({
										'node':newNode,
										'nodePackIds':nodePackIds//留着用来删除用
									});
								}
							} else if(val.length < this.newNodeAry.length){
								var removeNum = this.newNodeAry.length-val.length;
								for(var i=0;i<removeNum;i++){
									var removeNode = this.newNodeAry.pop();
									this.parentNode.removeChild(removeNode.node);

									//清理
									$SCOPE.$DEL_V2M_NODE_MAP(removeNode.nodePackIds);
								}
							}
						},
						'parentNode':node.parentNode,
						'nextSibling':node.nextSibling,//下一个兄弟节点，用来循环插标签
						'newNodeAry':[],
						'flag':flag,
						'indexReg':indexReg
					};


					//初始化的时候，就隐藏掉这个需要遍历的节点
					node.parentNode.removeChild(node);

					//原始节点html副本
					var tmpDiv = document.createElement('div');
					node.removeAttribute('nc-for');
					tmpDiv.appendChild(node);
					var cloneHtml = tmpDiv.innerHTML;
					NEW_NODE_MAP['nodeHtml'] = cloneHtml;

					//加入到V2M_大Map里
					$SCOPE.$ADD_V2M_NODE_MAP(proPath,NEW_NODE_MAP);
					return node_nc_id;
				}
			},
			'nc-if':{
				'commandName':'nc-if',//双向绑定
				'initFunc':function(node,expression,parentNodePackIds){
					if(node.getAttribute('nc-for')){
						//nc-for指令与nc-if指令不重复渲染
						return false;
					}

					var node_nc_id = $SCOPE.$NODE_ID_POINT++;

					//加入到V2M_大Map里
					$SCOPE.$ADD_V2M_NODE_MAP(expression,{
						'id':node_nc_id,
						'parentNodePackIds':parentNodePackIds,
						'node':node,
						'expression':expression,
						'render':function(expression,val){
							if(val){
								if(!this.node.parentNode){
									if(this.nextSibling){
										this.parentNode.insertBefore(this.node,this.nextSibling);
									}else{
										this.parentNode.appendChild(this.node);
									}
								}
							}else{
								if(this.node.parentNode){
									this.node.parentNode.removeChild(this.node);
								}
							}
						},
						'parentNode':node.parentNode,
						'nextSibling':node.nextSibling//下一个兄弟节点，用来循环插标签
					});
					return node_nc_id;
				}
			}
		};


		$SCOPE.$BIND_NODE = function(node,parentNodePackIds){
			var attributes = node.attributes;
			var nodePackIds = '';
			if(attributes && attributes.length > 0){
				for(var i=0;i<attributes.length;i++){
					var nodeName = attributes[i].nodeName;
					var nodeValue = attributes[i].nodeValue;
					if($SCOPE.$NICE_COMMAND[nodeName]){
						//作为指令解析
						var nodePackId = $SCOPE.$NICE_COMMAND[nodeName].initFunc(node,nodeValue,parentNodePackIds);
						if(nodePackId){
							if(nodePackIds.length > 0){
								nodePackIds = nodePackIds+',';
							}
							nodePackIds = nodePackIds+nodePackId;
						}
					}else{
						//将普通属性也作为节点，尝试纯文本解析{{}}
						var nodePackId = $SCOPE.$BIND_TXT(attributes[i],'nodeValue',parentNodePackIds);
						if(nodePackId){
							if(nodePackIds.length > 0){
								nodePackIds = nodePackIds+',';
							}
							nodePackIds = nodePackIds+nodePackId;
						}
					}
				}
			}
			return nodePackIds;
		};

		$SCOPE.$BIND_TXT = function(node,renderType,parentNodePackIds){
			var content = '';
			if(renderType=='nodeValue'){
				content = node.nodeValue;
			}else if(renderType=='src'){
				content = node.getAttribute('nc-src');
			}else if(renderType=='innerHTML'){
				content = node.getAttribute('nc-text');
			}
			if(!content){
				return false;
			}else{
				content = content+'';//转成String
			}
			var start = content.indexOf('{{');
			var end = content.indexOf('}}');
			if(start <0 || end <= 0){
				return false;
			}

			var nodeTxtAry = [];
			var expressionAry = [];
			var stop = false;
			for(;!stop;){
				var first = content.substring(0,start);//常量部分
				//##########前面常量部分的保存
				nodeTxtAry.push({
					'name':first,
					'value':first
				});

				//##########中间变量部分表达式的解析
				var second = content.substring(start+2,end);//{{}}内的部分
				//转换数组的表达形式
				second = second.replace(/\[/g,'.');
				second = second.replace(/\]/g,'');
				var filter = '';
				if(second.indexOf('|')>0){
					filter = second.substring(second.indexOf('|')+1);
					second = second.substring(0,second.indexOf('|'));
				}

				filter = filter.replace(/,/g,';');//所有的,号都换成分号，下面会对filter带参统一解析;
				expressionAry.push({
					'expression':second,
					'mainExpression':second,
					'filter':filter
				});

				//如果filter带参数，那么需要补充映射关系
				var filterParamAry = [];
				if(filter.indexOf(':') > 0){
					filterParamAry = filter.split(':')[1].split(';');
					for(var j=0;j<filterParamAry.length;j++){
						if(filterParamAry[j]){
							expressionAry.push({
								'expression':filterParamAry[j],
								'mainExpression':second,
								'filter':filter
							});
						}
					}
				}

				nodeTxtAry.push({
					'name':second,
					'value':'',
					'filterParamAry':filterParamAry
				});

				//##########后面常量部分的保存
				var content = content.substring(end+2);
				start = content.indexOf('{{');
				end = content.indexOf('}}');
				if(start <0 || end <= 0){
					//如果下面没有需要解析的{{}}了，就结束，把卒后一个content拼接上
					nodeTxtAry.push({
						'name':content,
						'value':content
					});
					stop = true;
				}
			}

			var node_nc_ids = '';
			for(var i=0;i<expressionAry.length;i++){
				var node_nc_id = $SCOPE.$NODE_ID_POINT++;
				if(node_nc_ids.length > 0){
					node_nc_ids = node_nc_ids+",";
				}
				node_nc_ids = node_nc_ids+node_nc_id;
				$SCOPE.$ADD_V2M_NODE_MAP(expressionAry[i].expression,{
					'id':node_nc_id,
					'parentNodePackIds':parentNodePackIds,
					'node':node,
					'expression':expressionAry[i].expression,
					'render':function(expression,val){
						//判断是否是主表达式，如果不是，那么val要改成主表达式的值，val的值，是filter里的参数的
						if(expression != this.mainExpression){
							val = $SCOPE.$GET_VAL(this.mainExpression);
						}
						if(this.expressionFilter){
							var filterExpressionAry = this.expressionFilter.split(':');
							var filterExpression = filterExpressionAry[0]+'(val';
							var filterParamValAry = [];
							if(filterExpressionAry.length > 1){
								//如果this.expressionFilter是带额外参数的，那么需要继续添加绑定关系
								var filterParamAry = filterExpressionAry[1].split(';');
								for(var h=0;h<filterParamAry.length;h++){
									if(filterParamAry[h]){
										var filterParamVal = $SCOPE.$GET_VAL(filterParamAry[h]);
										filterParamValAry.push(filterParamVal);
										filterExpression = filterExpression+',filterParamValAry['+h+']';
									}
								}
							}
							filterExpression = filterExpression+',$SCOPE.$DATA)';
							try{
								val = eval(filterExpression);
							}catch(e){
								console.error('过滤器失败 '+filterExpression);
								console.error(e);
							}
						}

						var renderVal = '';
						for(var j=0;j<this.nodeTxtAry.length;j++){
							if(this.nodeTxtAry[j].name == expression){
								this.nodeTxtAry[j].value = val;
							}else{
								if(this.nodeTxtAry[j].filterParamAry && this.nodeTxtAry[j].filterParamAry.length > 0){
									for(var q=0;q<this.nodeTxtAry[j].filterParamAry.length;q++){
										if(this.nodeTxtAry[j].filterParamAry[q] == expression){
											this.nodeTxtAry[j].value = val;
											break;
										}
									}
								}
							}
							
							renderVal = renderVal+this.nodeTxtAry[j].value;
						}
						this.node[this.renderType] = renderVal;
					},
					'mainExpression':expressionAry[i].mainExpression,//这是主表达式
					'expressionFilter':expressionAry[i].filter,
					'nodeTxtAry':nodeTxtAry,//节点中文本的组成
					'renderType':renderType
				});
			}
			return node_nc_ids;
		};

		//设置参数
		$SCOPE.$SET_VAL = function(proPath,val){
			proPath = proPath.replace(/\[/g,'.');
			proPath = proPath.replace(/\]/g,'');
			var pros = proPath.split('.');
			var obj = $SCOPE.$DATA;
			for(var i=0;i<pros.length;i++){
				if(i<pros.length-1){
					if(!obj[pros[i]]){
						obj[pros[i]] = new Object();
					}
				}else{
					obj[pros[i]] = val;
				}
				obj = obj[pros[i]];
			}
		};

		$SCOPE.PRO_PATH_CACHE = {};
		$SCOPE.$GET_VAL = function(proPath){
			/*var pros = proPath.split('.');
			var obj = $SCOPE.$DATA;
			for(var i=0;i<pros.length;i++){
				if(i == pros.length-1){
					//到底了
					return obj[pros[i]];
				}else{
					if( obj[pros[i]]){
						obj = obj[pros[i]];
					}else{
						return null;
					}
				}
			}*/
			try{
				if($SCOPE.PRO_PATH_CACHE[proPath] === undefined){
					var key = proPath;
					//proPath其实是指令里的具体参数值
					//有可能是 name
					//有可能是 !name
					//还有可能是 name.something 或者 age-1这样
					for(var pro in $SCOPE_DATA_){
						if(proPath.indexOf(pro) >= 0){
							var words = proPath.split(pro);
							//比如 ' user.name' 按照'user.name'分解会有一个空格和一个'user.name'
							if(words.length>1){
								var newProPath = '';
								for(var i=0;i<words.length-1;i++){
									if(words[i].length > 0){
										if(words[i].substring(words[i].length-1)=="."){
											newProPath = newProPath+words[i]+pro;
										}else{
											newProPath = newProPath+words[i]+'$SCOPE.$DATA.'+pro;
										}
									}else{
										newProPath = newProPath+words[i]+'$SCOPE.$DATA.'+pro;
									}
								}
								proPath = newProPath+words[words.length-1];
							}
						}else{
							//cotinue;
						}
					}	

					//如果里面含有数组的成分，比如$SCOPE.$DATA.ary.0.name，应该改成...ary[0].name
					var words = proPath.split('.');
					proPath = '';
					for(var i=0;i<words.length;i++){
						if(proPath.length > 0){
							if(isNaN(words[i])){
								proPath = proPath+'.';
							}else{
								//如果是纯数字，改成数组方式取
								proPath = proPath+'[';
							}
						}

						proPath = proPath+words[i];

						if(!isNaN(words[i])){
							//如果是纯数字，改成数组方式取
							proPath = proPath+']';
						}
					}
					$SCOPE.PRO_PATH_CACHE[key] = proPath;
				}else{
					proPath = $SCOPE.PRO_PATH_CACHE[proPath];
				}
				

				var result = eval(proPath);
				return result;
			}catch(err){
			  	return undefined;
			}
		};

		//得到单层次展开的参数->值的映射
		$SCOPE.$GET_PRO_SOLID_MAP = function(pKey,DATA,emptyProSolidMap){
			if(pKey){
				emptyProSolidMap[pKey]=DATA;
				pKey = pKey+'.';
			}else{
				pKey = '';
			}
			if(DATA instanceof Object){
				for(var key in DATA){
					$SCOPE.$GET_PRO_SOLID_MAP(pKey+key,DATA[key],emptyProSolidMap);
				}
			}
		};

		//同步值到副本总，并得到与副本中不一致的值，以此基准来更新dom
		$SCOPE.$SYNC_SCOPE_DATA_ = function(proSolidMap){
			var keys = {};
			
			// 数据版本不一致，需要同步的字段
			for(var proPath in proSolidMap){
				do{
					if($SCOPE_DATA_[proPath] === undefined && proSolidMap[proPath] === undefined){
						//如果两端都是undefined，那么没有继续比较的意义，因为js中如果值是undefined，那么会存储不成功
						break;
					}
					if($SCOPE_DATA_[proPath] !== undefined && $SCOPE_DATA_[proPath].value === proSolidMap[proPath]){
						//如果存在并且已经最新，不需要同步
						break;
					}

					var version = 1;
					if($SCOPE_DATA_[proPath] !== undefined){
						version = $SCOPE_DATA_[proPath]['version']+1;
					}
					$SCOPE_DATA_[proPath] = {
						'version': version,
						'value': proSolidMap[proPath]
					};

					keys[proPath]=$SCOPE_DATA_[proPath]['version'];

				}while(false);
			}

			//数据到dom节点版本不一致，需要同步的
			for(var proPath in $SCOPE_DATA_){
				var version = $SCOPE_DATA_[proPath]['version'];
				do{
					if(keys[proPath] !== undefined) {
						//已经存在的要同步字段，就不需要重复添加到等待同步了
						break;
					}

					//如果值已经删除了，同样需要更新dom，但是版本还是要一致的
					if(proSolidMap[proPath] === undefined){
						//清除副本
						delete $SCOPE_DATA_[proPath];
						delete $SCOPE.$V2M_NODE_MAP[proPath];
						keys[proPath] = version;
						break;
					}

					for(var i=0;$SCOPE.$V2M_NODE_MAP[proPath] && i<$SCOPE.$V2M_NODE_MAP[proPath].length;i++){
						if($SCOPE.$V2M_NODE_MAP[proPath][i]['version'] !== version){
							keys[proPath] = version;
							break;
						}
						//select元素，很有可能在nc-for对option进行渲染后，会自动改变select的值，自动选中最后一个。
						//这是不行的，所以必须把值调整回来，调整到正确值。
						if($SCOPE.$V2M_NODE_MAP[proPath][i]['node']['nodeName'].toLowerCase() == 'select'){
							if($SCOPE_DATA_[proPath]){
								if($SCOPE.$V2M_NODE_MAP[proPath][i]['node'].value != $SCOPE_DATA_[proPath]['value']){
									$SCOPE.$V2M_NODE_MAP[proPath][i]['version'] = 0;//降低版本，等待下次同步
								}
							}
						}
					}

				}while(false);
			}

			//根据keys，向上追溯，所有这条线的，都需要渲染
			var needSyncProPath = new Object();
			for(var proPath in keys){
				needSyncProPath[proPath] = keys[proPath];

				var end = proPath.lastIndexOf('.');
				while(end > 0){
					proPath = proPath.substring(0,end);
					needSyncProPath[proPath]=keys[proPath];
					end = proPath.lastIndexOf('.');
				}
			}

			return needSyncProPath;
		};

		//是否需要在全部dom渲染完后，执行下回调
		$SCOPE.$NEED_AFTER_RENDER = true;
		$SCOPE.$FLUSH = function(){

			//计算的出，需要进行同步的proPath
			//深度优先遍历
			var proSolidMap = {};
			$SCOPE.$GET_PRO_SOLID_MAP(null,$SCOPE.$DATA,proSolidMap);

			var needSyncProPath =  $SCOPE.$SYNC_SCOPE_DATA_(proSolidMap);

			var needSyncProPathSize = 0;
			for(var proPath in needSyncProPath){
				needSyncProPathSize++;
				for(var i=0;$SCOPE.$V2M_NODE_MAP[proPath] !== undefined && i<$SCOPE.$V2M_NODE_MAP[proPath].length;i++){
					var nodePack = $SCOPE.$V2M_NODE_MAP[proPath][i];
					nodePack['version'] = needSyncProPath[proPath];
					if(nodePack.id == $SCOPE.$UNREFRESH_NODE_ID) continue;

					//flush dom
					var val = $SCOPE.$GET_VAL(nodePack.expression);

					if(val === undefined){
						val = '';
					}
					nodePack.render(nodePack.expression,val);
				}

				//flush $watch data
				if($WATCH_QUEE[proPath] && $WATCH_QUEE[proPath].length > 0){
					for(var j=0;j<$WATCH_QUEE[proPath].length;j++){
						var $watchObj = $WATCH_QUEE[proPath][j];
						
						var execStatement = '$watchObj.fun(';
						for(var k=0;k<$watchObj.proPathAry.length;k++){
							execStatement = execStatement+'$SCOPE.$DATA.'+$watchObj.proPathAry[k]+'';
							if(k<$watchObj.proPathAry.length-1){
								execStatement = execStatement+',';
							}
						}
						execStatement = execStatement+')';
						eval(execStatement);
					}
				}
			}
			
			if(needSyncProPathSize > 0){
				//需要渲染，前面的for已经做了渲染，等到下次列表为空，渲染结束，那么进入到else里。
				$SCOPE.$NEED_AFTER_RENDER = true;
			}else{
				//#渲染结束回调，周期是每次批量渲染完页面后，有且只执行一次。
				if($SCOPE.$NEED_AFTER_RENDER){
					//不需要渲染了，那么就执行一次回调，然后等待下次需要渲染的时候再次触发。
					$SCOPE.$NEED_AFTER_RENDER = false;
					if($AFTER_RENDER){
						$AFTER_RENDER();
					}

					var num=0;
					for(var key in $SCOPE.$V2M_NODE_MAP){
						num = num+$SCOPE.$V2M_NODE_MAP[key].length;
						/*for(var ai=0;ai<$SCOPE.$V2M_NODE_MAP[key].length;ai++){
							console.log('['+mvvmElementId+']'+$SCOPE.$V2M_NODE_MAP[key][ai].parentNodePackIds);
						}*/
					}
					console.log('['+mvvmElementId+']总计:'+num);
				}
				//#flush周期回调，次数是，每次当flush空闲刷新的时候，都会被回调。
				if($AFTER_FLUSH){
					$AFTER_FLUSH();
				}
				
			}
			
		};

		//parentNodePackIds形如 ,1,2,
		$SCOPE.$INIT_MVVM = function(node,parentNodePackIds){
		    ///Attribute  nodeType值为2，表示节点属性
		    ///Comment    nodeType值为8，表示注释文本
		    ///Document   nodeType值为9，表示Document
		    ///DocumentFragment   nodeType值为11，表示Document片段
		    ///Element            nodeType值为1，表示元素节点
		    ///Text               nodeType值为3，表示文本节点
		    //1代表节点的类型为Element

		    //查看node的id，如果在排除的范围内，那么跳过
		    if(excludeIds && excludeIds.length > 0 && (node.id || node.id+'#'=='0#')){
		    	var exclude = false;
		    	for(var i=0;i<excludeIds.length;i++){
		    		if(node.id == excludeIds[i]){
		    			exclude = true;
		    			break;
		    		}
		    	}
		    	if(exclude){
		    		return '';
		    	}
		    }
		    var nodePackIds = null;
		    if(node.nodeType==1) {
		    	//初始化节点
		    	nodePackIds = $SCOPE.$BIND_NODE(node,parentNodePackIds);
		    }
		    //3代表节点为文本
		    if(node.nodeType==3){
		    	
		    	nodePackIds = $SCOPE.$BIND_TXT(node,'nodeValue',parentNodePackIds);
		    }

		    var nodePackIds_4_return = '';
		    if(!nodePackIds){
		    	//如果不存在，说明没有需要挂接V2M_NODEL_MAP的节点，那么新起始一个分支
		    	nodePackIds = $SCOPE.$NODE_ID_POINT++;
		    	
		    }

		    nodePackIds_4_return = ','+nodePackIds+',';
	    	//但是因为是新起的，所以子节点必须以上级也作为上级，否则删除时候删不着或者错删
	    	if(parentNodePackIds){
	    		nodePackIds = parentNodePackIds+nodePackIds+',';
	    	}else{
	    		nodePackIds = ','+nodePackIds+',';
	    	}
	    	
	    	var childrens=node.childNodes;
		    for(var i=0;childrens !== undefined && i<childrens.length;i++) {
		    	$SCOPE.$INIT_MVVM(childrens[i],nodePackIds);
		    }
		    return nodePackIds_4_return;
		};

		$SCOPE.$FLUSH();
		$SCOPE.$INIT_MVVM(mvvmElement,'');

		$SCOPE.$INTERVAL = setInterval(function(){
			var startTime = new Date().getTime();
			$SCOPE.$FLUSH();
			var endTime = new Date().getTime();
			if(endTime-startTime > 100){
				console.log('['+mvvmElementId+']耗时：'+(endTime-startTime)+'ms');
			}
		},1);
		
	};
	
	return {
		'$scope':$nc,
		'$watch':$watch,
		'$onload':$onload,
		'$onflush':$onflush,
		'$init':$init
	};
};

