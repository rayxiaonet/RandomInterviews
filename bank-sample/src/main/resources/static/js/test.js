'use strict'

angular.module("app", [])
    .config(
            function($httpProvider) {
                $httpProvider.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';
            }).controller("home", function($http, $location) {
        var self = this;
        self.amount=0;
        $http.get("/user").success(function(data) {
            if (data.name) {
                self.user = data.name;
                self.authenticated = true;
                $http.get("/balance").success(function(data){
                    console.log('balance data:');
                    console.dir(data);
                    self.balance=data.balance;
                })
                .error(function() {
                    self.balance='N/a';
                });



            } else {
                self.user = "N/A";
                self.authenticated = false;
            }
        }).error(function() {
            self.user = "N/A";
            self.authenticated = false;
        });
        self.all = function() {
            $http.get('allbalance').success(function(data) {
                console.log('all balance success:');
                console.dir(data);
            }).error(function (data) {
                console.log('all balance failed:');
                console.dir(data);
            });
        }
        self.deposit = function() {
            $http.post('deposit',{amount:self.amount,notes:'this is a test'}).success(function(data) {
                console.log('deposit success:');
                self.message="deposit success, the new balance is "+data.balance;
                self.balance=data.balance;
            }).error(function (data) {
                console.log('deposit failed:');
                self.message="deposit failed!"+data.message;
            });
        }
        self.withdraw = function() {
            $http.post('withdrawal',{amount:self.amount,notes:'this is a test'}).success(function(data) {
                console.log('withdraw success:');
                self.message="withdraw success, the new balance is "+data.balance;
                self.balance=data.balance;

            }).error(function (data) {
                console.log('withdraw failed:');
                self.message="withdraw failed!"+data.message;
            });

        }

        self.logout = function() {
            $http.post('logout', {}).success(function() {
                self.authenticated = false;
                $location.path("/");
            }).error(function(data) {
                console.log("Logout failed")
                self.authenticated = false;
            });
        };
    });