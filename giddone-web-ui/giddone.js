function TodoCtrl($scope) {
    $scope.snapshot = snapshot();
    $scope.todos = _.groupBy($scope.snapshot.todo, function(todo) { return todo.file; });
}
document.write('<body ng-app ng-include src="\'main.html\'"><\/body><script src="http://ajax.googleapis.com/ajax/libs/angularjs/1.0.7/angular.min.js"><\/script><script src="http://cdnjs.cloudflare.com/ajax/libs/underscore.js/1.5.2/underscore-min.js"><\/script>');