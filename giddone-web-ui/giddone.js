function MainCtrl($scope) {
    $scope.mainUrl = 'main.html';
}
function TodoCtrl($scope) {
    $scope.snapshot = snapshot();
    $scope.todos = _.groupBy($scope.snapshot.todo, function(todo) { return todo.file; });
}
// TODO: underscore is only used for the group_by and should be removed
document.write('<link href="giddone.css" media="all" rel="stylesheet" type="text/css"><link href="//netdna.bootstrapcdn.com/font-awesome/3.2.1/css/font-awesome.css" rel="stylesheet"><script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.0.7/angular.min.js"><\/script><script src="https://cdnjs.cloudflare.com/ajax/libs/underscore.js/1.5.2/underscore-min.js"><\/script>');