var cutter = angular.module('app.cut-controller', []);

cutter.controller('CutCtrl', ['$scope', '$http', '$window','$location', 'SplitService', 'ShareTableService',
    'ListTableService','AdjustGranularityService', 'ProposalFactory',
    function($scope, $http, $window, $location,  SplitService, ShareTableService, ListTableService,
             AdjustGranularityService, ProposalFactory) {

        //当前proposalGroups中最大的key，即为proposalGroups的长度
        $scope.maxKey = 0;

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

        //////////////////////获取拆分方案并做相应处理///////////////////////////////////
        $scope.handleSplitResult = function(data){
            $scope.proposalGroups = data.splitProposal;
            $scope.maxKey = Object.keys($scope.proposalGroups).length;
            $scope.cost = data.splitCost;
            $scope.splitGranularity = data.splitGranularity;
            $scope.granularityPercent = $scope.splitGranularity.curServiceNum / $scope.splitGranularity.maxServiceNum;
        };

        $scope.startSplit = function(){
            $scope.showLoading();
            //获取拆分结果
            SplitService.startSplit(0, $scope.sharingTableGroups).then(function(data){
                $scope.handleSplitResult(data);
                $scope.hideLoading();
                $scope.enableProgress();
                $('#adjust-button').removeClass('disabled');
            });
        };
        //////////////////拆分粒度调整/////////////////////////////
        $scope.addService = function(){
            $scope.showLoading();
            AdjustGranularityService.addService($scope.splitGranularity.curServiceNum).then(function(data){
                $scope.handleSplitResult(data);
                $scope.hideLoading();
            });
        };

        $scope.reduceService = function(){
            $scope.showLoading();
            AdjustGranularityService.reduceService($scope.splitGranularity.curServiceNum).then(function(data){
                $scope.handleSplitResult(data);
                $scope.hideLoading();
            });
        };

        //拆分粒度禁用
        $scope.disableProgress = function(){
            $('#granularity-progress').addClass('disabled');
            $('#minus-granularity-button').addClass('disabled');
            $('#add-granularity-button').addClass('disabled');
        };

        //拆分粒度恢复可用
        $scope.enableProgress = function(){
            $('#granularity-progress').removeClass('disabled');
            $('#minus-granularity-button').removeClass('disabled');
            $('#add-granularity-button').removeClass('disabled');
        };

        // //由于手动改变table归属，必须重新计算当前的服务数量
        // $scope.refreshProgress = function(){
        //     //重新计算当前微服务数量和百分比，然后禁用微服务数量的更改
        //     $scope.splitGranularity.curServiceNum = Object.keys($scope.proposalGroups).length;
        //     $scope.granularityPercent = $scope.splitGranularity.curServiceNum / $scope.splitGranularity.maxServiceNum;
        //     $scope.disableProgress();
        // };
        //////////////////////初始化，初始共享表/////////////////////////
        //共享表的选择
        $('#shareDropdown').dropdown();

        //从第二页跳转回来时恢复页面数据
        $scope.recoverPage = function(){
            $scope.proposalGroups = ProposalFactory.getProposalGroups();
            $scope.maxKey = ProposalFactory.getMaxKey();
            $scope.cost = ProposalFactory.getCost();

            $scope.splitGranularity = ProposalFactory.getSplitGranularity();
            $scope.granularityPercent = $scope.splitGranularity.curServiceNum / $scope.splitGranularity.maxServiceNum;

            $scope.sharingTableGroups = ProposalFactory.getSharingTableGroups();
            $scope.allTables = ProposalFactory.getAllTables();
            console.log("$scope.allTables:");
            console.log($scope.allTables);

            $scope.refreshNoSharing();

            $scope.enableProgress();
            $('#adjust-button').removeClass('disabled');

            $scope.hideLoading();
        };

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
            $scope.disableProgress();

            if(ProposalFactory.getMaxKey() != 0){
                console.log("从第二个页面跳转回来了");
                $scope.recoverPage();
                $scope.hideLoading();
                return;
            }

            //先计算共享度高的表
            ShareTableService.calShare().then(function(data){
                $scope.sharingTableGroups = data;
                //获取所有table列表
                ListTableService.listAll().then(function(data){
                    $scope.allTables = data;
                    $scope.refreshNoSharing();
                    $scope.hideLoading();

                    //获取拆分结果,仅测试
                    // SplitService.startSplit().then(function(data){
                    //     $scope.proposalGroups = data.splitProposal;
                    //     $scope.cost = data.splitCost;
                    //     $scope.hideLoading();
                    // });

                });
            });
        };

        $scope.init();

        //////////////////改变共享表//////////////////////////////
        $scope.addSharingGroup = function(){
            //dropdown的值是一个字符串！！！
            if($('#shareDropdown').dropdown('get value') == "") return;
            var newSharingGroupIds = $('#shareDropdown').dropdown('get value').split(',') || [];
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

        /////////////////////////跳转到第二个界面////////////////////////////
        $scope.toAdjustPage = function(){
            if($scope.proposalGroups === undefined) return;

            ProposalFactory.setProposal($scope.proposalGroups, $scope.maxKey, $scope.cost,
                $scope.splitGranularity, $scope.sharingTableGroups, $scope.allTables);

            $location.path('/adjust');
        };


    }]);