var cutter = angular.module('app.cut-controller', []);

cutter.controller('CutCtrl', ['$scope', '$http','$window','SplitService', 'ShareTableService', 'ListTableService',
    function($scope, $http, $window, SplitService, ShareTableService, ListTableService) {
        //保存当前微服务数量
        $scope.maxKey = 0;


        //////////////////////初始化，初始共享表/////////////////////////
        //共享表的选择
        $('#shareDropdown').dropdown();

        //根据sharingTableGroups刷新notSharingTables、sharingTables
        $scope.refreshNoSharing = function(){
            $scope.notSharingTables =[];
            $scope.sharingTables = [];
            for(var i = 0; i < $scope.sharingTableGroups.length; i++){
                $scope.sharingTables = $scope.sharingTables.concat($scope.sharingTableGroups[i]);
            }
            for(var j = 0; j < $scope.allTables.length; j++){
                var flag = 0;
                for(var k = 0; k < $scope.sharingTables.length; k++){
                    if( $scope.sharingTables[k].id == $scope.allTables[j].id){
                        flag = 1;
                        break;
                    }
                }
                if(flag == 0){
                    $scope.notSharingTables.push($scope.allTables[j]);
                }
            }
        };

        //加载数据，初始化
        $scope.init = function(){
            ShareTableService.calShare().then(function(data){
                $scope.sharingTableGroups = data;
                //获取所有table列表
                ListTableService.listAll().then(function(data){
                    $scope.allTables = data;
                    $scope.refreshNoSharing();

                    //获取拆分结果
                    SplitService.loadServiceList().then(function(data){
                        $scope.proposalGroups = data.splitProposal;
                        $scope.cost = data.splitCost;
                        // $scope.getAllTables();
                        $scope.hideLoading();
                    });

                });
                //隐藏loading
                $scope.hideLoading();
            });


        };

        $scope.init();

        //////////////////改变共享表//////////////////////////////
        $scope.addSharingGroup = function(){
            //dropdown的值是一个字符串！！！
            if($('#shareDropdown').dropdown('get value') == "") return;
            var newSharingGroupIds = $('#shareDropdown').dropdown('get value').split(',') || [];
            console.log("newSharingGroupIds:");
            console.log(newSharingGroupIds);

            if(newSharingGroupIds.length <= 0) return;

            var newSharingGroup = [];
            var newNotSharingTables = [];
            for(var i = 0; i < $scope.notSharingTables.length; i++){
                var flag = 1;
                for(var j = 0; j < newSharingGroupIds.length; j++){
                    if($scope.notSharingTables[i].id == newSharingGroupIds[j]){
                        newSharingGroup.push($scope.notSharingTables[i]);
                        $scope.sharingTables.push($scope.notSharingTables[i]);
                        flag = 0;
                        break;
                    }
                }
                if(flag == 1){
                    newNotSharingTables.push($scope.notSharingTables[i]);
                }
            }
            $scope.sharingTableGroups.push(newSharingGroup);
            $scope.notSharingTables = newNotSharingTables;

            //清空dropdown
            $('#shareDropdown').dropdown('clear');
        };

        $scope.cancelSharingGroup = function(){
            //清空dropdown
            $('#shareDropdown').dropdown('clear');
        };

        $scope.deleteSharingGroup = function(index){
            //外面还有一层数组!!!
            var deleteTables = $scope.sharingTableGroups.splice(index, 1);
            console.log("deleteTables");
            console.log(deleteTables);
            for(var i = 0; i < deleteTables[0].length; i++){
                $scope.notSharingTables.push(deleteTables[0][i]);
                for(var j = 0; j < $scope.sharingTables.length; j++) {
                    if($scope.sharingTables[j].id == deleteTables[0][i].id){
                        $scope.sharingTables.splice(j, 1);
                        break;
                    }
                }
            }
        };
        //////////////////////获取拆分方案并做相应处理///////////////////////////////////

        $scope.startSplit = function(){
            $scope.showLoading();

            //获取拆分结果
            SplitService.loadServiceList(0, $scope.sharingTableGroups).then(function(data){
                $scope.proposalGroups = data.splitProposal;
                $scope.cost = data.splitCost;
                // $scope.getAllTables();
                $scope.hideLoading();
            });

        };

        // $scope.getAllTables = function(){
        //     $scope.allTables = [];
        //     for(var key in $scope.groups){
        //         $scope.allTables = $scope.allTables.concat($scope.groups[key]);
        //         $scope.maxKey = Math.max($scope.maxKey, key);
        //     }
        //     console.log($scope.allTables);
        // };


        //////////////////拖动改变单个table的归属///////////////////

        //拖动完成，删除原先数组中的元素，将拖动元素添加到目标数组的末尾
        $scope.onDropComplete = function(newGroupNum, obj) {
            //删除原来group中的table
            $scope.deleteTable(obj);
            //将obj加入新的group中
            $scope.proposalGroups[newGroupNum].push(obj);
            //删除空的group
            $scope.clearEmptyGroupList();
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
            $scope.maxKey = newIndex - 1;
            $scope.proposalGroups = newGroups;
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

        // //给一个table单独生成一个微服务
        // $scope.extractService = function(table){
        //     //删除原来group中的table
        //     $scope.deleteTable(table);
        //     //建一个新的group
        //     $scope.groups[++$scope.maxKey] = [table];
        //     //删除空的group
        //     $scope.clearEmptyGroupList();
        // };

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