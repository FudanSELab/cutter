var uploader = angular.module('app.upload-controller', []);

uploader.controller('UploadCtrl', ['$scope', '$http', 'UploadFileService', '$location',
    function($scope, $http, UploadFileService, $location){

        $scope.selectFile = function(){
            //效验上传文件类型
            $scope.fileName=$('#logFile').val();
            if(!/\.(dat)$/.test($scope.fileName)){
                alert("文件类型必须是.dat！");
                $('#logFile').val("");
                return false;
            }
            $scope.form = new FormData();
            $scope.file = document.getElementById("logFile").files[0];
            $scope.form.append('file', $scope.file);
            UploadFileService.upload($scope.form).then(function(data){
                $('#logFile').val("");
                $location.path('/cut');
            });
            return true;
        }



    }
]);