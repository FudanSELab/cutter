var cutter = angular.module('app.cut-controller', []);

cutter.controller('CutCtrl', ['$scope', '$http','$window','SplitService', 'ShareTableService', 'ListTableService',
    function($scope, $http, $window, SplitService, ShareTableService, ListTableService) {
        //保存当前微服务数量
        $scope.maxKey = 0;


        //////////////////////初始化，初始共享表/////////////////////////

        //加载数据，初始化
        $scope.init = function(){
            ShareTableService.calShare().then(function(data){
                $scope.sharingTableGroups = data;
                $scope.hideLoading();
            });
            //获取拆分结果
            // SplitService.loadServiceList().then(function(data){
            //     $scope.groups = data.splitProposal;
            //     $scope.cost = data.splitCost;
            //     $scope.getAllTables();
            //     $scope.hideLoading();
            // });
        };

        $scope.init();
        //////////////////////获取拆分方案并做相应处理///////////////////////////////////

        //获取所有table列表
        ListTableService.listAll().then(function(data){
            $scope.allTables = data;
        });
        // $scope.getAllTables = function(){
        //     $scope.allTables = [];
        //     for(var key in $scope.groups){
        //         $scope.allTables = $scope.allTables.concat($scope.groups[key]);
        //         $scope.maxKey = Math.max($scope.maxKey, key);
        //     }
        //     console.log($scope.allTables);
        // };


        /////////////////////////////////////

        //拖动改变单个table的归属
        $scope.onDropComplete = function(newGroupNum, obj) {
            //删除原来group中的table
            $scope.deleteTable(obj);
            //将obj加入新的group中
            $scope.groups[newGroupNum].push(obj);
            //删除空的group
            $scope.clearEmptyGroupList();
        };

        //删除空的group
        $scope.clearEmptyGroupList = function(){
            var newIndex = 1;
            var newGroups = {};
            for(var k in $scope.groups){
                if($scope.groups[k].length > 0){
                    newGroups[newIndex ++] = $scope.groups[k];
                }
            }
            $scope.maxKey = newIndex - 1;
            $scope.groups = newGroups;
        };

        //删除原来group中的table
        $scope.deleteTable = function(obj){
            for(var key in $scope.groups){
                var tables = $scope.groups[key];
                for(var i = 0; i < tables.length; i++) {
                    if (tables[i].id == obj.id) {
                        tables.splice(i, 1);
                        break;
                    }
                }
            }
        };

        //给一个table单独生成一个微服务
        $scope.extractService = function(table){
            //删除原来group中的table
            $scope.deleteTable(table);
            //建一个新的group
            $scope.groups[++$scope.maxKey] = [table];
            //删除空的group
            $scope.clearEmptyGroupList();
        };

        ////////////////////拆分代价////////////////////////////
        $scope.showSplitDetail = function(index){
            if(index == 1){
                $('.split-modal-sql').modal('show');
            } else if(index == 2){
                $('.split-modal-method').modal('show');
            } else {
                $('.split-modal-class').modal('show');
            }
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

    }]);