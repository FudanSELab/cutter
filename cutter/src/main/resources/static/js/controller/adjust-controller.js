var adjuster = angular.module('app.adjust-controller', []);

adjuster.controller('AdjustCtrl', ['$scope', '$http','$window', '$location', 'CostService','ProposalFactory', 'SplitService',
    function($scope, $http, $window, $location, CostService, ProposalFactory, SplitService) {

        ////////////////////////////////////////////////////////
        //回到上一步
        $scope.toSplitPage = function(){
            $location.path('/cut');
        };

        ///////////////////判断是否被拆分////////////////////////////////////////////
        $scope.isSplitClass = function(s){
            for(var key in $scope.cost.classToSplitResult){
                if(key == s){
                    if($scope.cost.classToSplitResult[key].length > 1){
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        };

        $scope.isSplitMethod = function(s){
            for(var key in $scope.cost.methodToSplitResult){
                if(key == s){
                    if($scope.cost.methodToSplitResult[key].length > 1){
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        };

        $scope.isSplitSql = function(s){
            for(var key in $scope.cost.sqlToSplitResult){
                if(key == s){
                    if($scope.cost.sqlToSplitResult[key].length > 1){
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        };

        ///////////////////////////右侧tab///////////////////////////////////////
        //点击左边的service，切换右边的detail内容
        $scope.changeTabDetail = function(key){
            $scope.selectedServiceKey = key;
            if(key == 0){
                //没有表的方法和类
                SplitService.getNoTableTree().then(function(data){
                    $scope.fillTree(data);
                    $('#split-detail-tab').css('display','none');
                });
            } else {
                $('#split-detail-tab').css('display','block');
                $scope.fillTree($scope.detailTreeMap[$scope.selectedServiceKey]);
                $scope.fillTab();
            }
        };

        //获取当前拆分方案的idList
        $scope.getProposalIdList = function(){
            var idLists = [];
            for(var key in $scope.proposalGroups){
                var idList = [];
                var group = $scope.proposalGroups[key];
                for(var i = 0; i < group.length; i++){
                    idList.push(group[i].id);
                }
                idLists.push(idList);
            }
            return idLists;
        };

        //获取代码拆分总方案
        $scope.getSplitDetail = function(){
            //每组微服务包含的class/method/sql列表
            SplitService.getSplitDetail().then(function(data){
                $scope.groupBySql = data.groupBySql;
                $scope.groupByMethod = data.groupByMethod;
                $scope.groupByClass = data.groupByClass;
                $scope.selectedServiceKey = 1;
                $scope.fillTab();
                //每组微服务的树形结构
                SplitService.getSplitDetailTree().then(function(data){
                    $scope.detailTreeMap = data;
                    $scope.fillTree($scope.detailTreeMap[$scope.selectedServiceKey]);

                });
            });
        };


        $scope.treeSetting = {
            view:{
                showIcon:true
            }
        };

        //填充单个微服务项目所包含的包目录结构
        $scope.fillTree = function(data){
            // $("#detail-tree").html("");
            // $("#detail-tree").tagTree({
            //     id: "",
            //     data: data,
            //     fold: true,
            //     multiple: true
            // });

            if($scope.tree != undefined){
                $scope.tree.destroy();
            }
            $scope.tree = $.fn.zTree.init($("#detail-tree"), $scope.treeSetting, data);

        };

        //填充右侧的tab，显示单个微服务包含的class/method/sql
        $scope.fillTab = function(){
            $scope.sqls = $scope.groupBySql[$scope.selectedServiceKey];
            $scope.methods = $scope.groupByMethod[$scope.selectedServiceKey];
            $scope.classes = $scope.groupByClass[$scope.selectedServiceKey];
        };

        ///////////////////////loading//////////////////////////////
        //加载数据中，隐藏滚动条
        $('body').css({'overflow-y':'hidden'});
        //显示加载动画
        $scope.showLoading = function(){
            //加载数据中，隐藏滚动条
            $('body').css({'overflow-y':'hidden'});
            $('.ui.active.dimmer').css({'display': 'block'});
        };
        //隐藏加载动画
        $scope.hideLoading = function(){
            //加载数据中，隐藏滚动条
            $('body').css({'overflow-y':'scroll'});
            $('.ui.active.dimmer').css({'display': 'none'});
        };

        //////////////////改变单个table的归属///////////////////
        $scope.$on('ngRepeatFinished', function (ngRepeatFinishedEvent) {
            $('.popuplabel').popup({
                position:'right center',
                on:'click'
            });

            $('.tab-menu .item').tab();

            $scope.hideLoading();
        });

        $scope.extractTable = function(obj){
            $scope.showLoading();
            //删除原来group中的table
            $scope.deleteTable(obj);
            //删除空的group
            $scope.clearEmptyGroupList();
            //将obj单独变成一个新的group
            $scope.proposalGroups[++$scope.maxKey]= [];
            $scope.proposalGroups[$scope.maxKey].push(obj);
            $scope.splitGranularity.curServiceNum = $scope.maxKey;
            //计算新的拆分方案的拆分代价
            $scope.recalculateCost();
        };

        //拖动完成，删除原先数组中的元素，将拖动元素添加到目标数组的末尾
        $scope.onDropComplete = function(newGroupNum, obj) {
            $scope.showLoading();
            //删除原来group中的table
            $scope.deleteTable(obj);
            //将obj加入新的group中
            $scope.proposalGroups[newGroupNum].push(obj);
            //删除空的group
            $scope.clearEmptyGroupList();

            //计算新的拆分方案的拆分代价
            $scope.recalculateCost();
        };

        //删除空的group
        $scope.clearEmptyGroupList = function(){
            var newIndex = 1;
            var newGroups = {};
            for(var k in $scope.proposalGroups){
                if($scope.proposalGroups[k].length > 0){
                    newGroups[newIndex ++] = $scope.proposalGroups[k];
                }
            }
            $scope.proposalGroups = newGroups;
            $scope.maxKey = Object.keys($scope.proposalGroups).length;
            $scope.splitGranularity.curServiceNum = $scope.maxKey;
        };

        //删除原来group中的table
        $scope.deleteTable = function(obj){
            for(var key in $scope.proposalGroups){
                var tables = $scope.proposalGroups[key];
                for(var i = 0; i < tables.length; i++) {
                    if (tables[i].id == obj.id) {
                        tables.splice(i, 1);
                        break;
                    }
                }
            }
        };

        ////////////////////拆分代价////////////////////////////
        $scope.showSplitModal = function(index){
            if(index == 1){
                $('.split-modal-sql').modal('show');
            } else if(index == 2){
                $('.split-modal-method').modal('show');
            } else {
                $('.split-modal-class').modal('show');
            }
        };

        //重新根据当前的拆分方案计算拆分代价
        $scope.recalculateCost = function(){
            var idLists = $scope.getProposalIdList();
            CostService.calCost(idLists).then(function(data){
                $scope.cost = data;
                //获取代码拆分总方案
                $scope.getSplitDetail(idLists);
            });
        };


        $scope.selectedServiceKey = 1;
        //判断当前service是否被选中查看detail
        $scope.isSelected = function(key){
            return key == $scope.selectedServiceKey;
        };

        ////////////////////////init//////////////////////////////////////
        $scope.init = function(){
            $scope.showLoading();
            $scope.proposalGroups = ProposalFactory.getProposalGroups();
            $scope.maxKey = ProposalFactory.getMaxKey();
            $scope.cost = ProposalFactory.getCost();
            $scope.splitGranularity = ProposalFactory.getSplitGranularity();

            $scope.getSplitDetail();
            $('.tab-menu .item').tab();

            //for test
            // $scope.getSplitDetail();
        };

        $scope.init();

    }]);