# angular-google-auth2

## Table of contents

- [About](#about)
- [Installation](#installation)
- [Documentation](#documentation)
- [License](#license)


## About

This angular js directive plugin helps one to implement the google auth2 sign-in and sign-out features in web apps, mobile apps developed using angular js.


## Installation

This plugin requires [AngularJS](https://angularjs.org/) to be used in your application.

* bower install angular-google-auth2 -  

```
bower install --save angular-google-auth2
```
* npm install angular-google-auth2 -  

```
npm install --save angular-google-auth2
```

## Documentation

Follow the steps below to use the plugin:

* Include the following Google API link, JS and CSS files for the plugin:

```
<script src="https://apis.google.com/js/api:client.js"></script>

<link rel="stylesheet" type="text/css" href="angular-google-auth2/build/css/style.css">

<script type="text/javascript" src="angular.min.js"></script>

<script type="text/javascript" src="angular-route.min.js"></script>

<script type="text/javascript" src="angular-google-auth2/build/js/angular-google-auth2.js"></script>
```

* Add the module dependency in your AngularJS app:

```javascript
var auth2Demo = angular.module('myApp', ['angular-google-auth2','ngRoute']);
```


* This plugin provides 2 directives - 
1) Sign In directive - <b>s-sign-in</b>
2) Sign Out directive - <b>s-sign-out</b>

* Perform the following to use <b>s-sign-in</b> directive inside your login.html page (This will create a sign in button along with it's functionality) - 
  
  ```
  <div class="signInGreenSmall" s-sign-in client="xxxxxxxxxxxxxx" hd="gmail.com,yahoo.com" redirect="home" thirdparty="no" emailvalidate="emails">
  ```
  - <b>class</b> - This plugin provides with a set of custom css classes for sign-in button
  - <b>s-sign-in</b> - Directive name to be used for sign-in  
  - <b>client</b> - The client id that you will get from Google's Developer account when you will create your project credentials there
  - <b>hd</b> - List of the domains that specify which domain users are allowed to use your application. Users that don't belong to the listed domains will have no access to your application
  - <b>redirect</b> - Name of the page that you want your user to redirect on sign-in
  - <b>thirdparty (optional)</b> - This attribute takes two values. 
    - <b>yes</b> - If this value is set to 'yes', it specifies that you are using a third party to '<b>logout</b>' from your app, and so this plugin will provide you a custom method <b>S_GeneralData.s_flush()</b>, which you need to use manditorily along with your own logout procedure
    - <b>no</b> - If this value is set to 'no', plugin will handle the '<b>logout</b>', you don't need to worry
    - <b>emailvalidate (optional)</b> - You need to provide an array of objects containing email ids as value to this attribute. It checks if the user belongs to the list of given emails from the array. It must be array of objects where the key should be 'email' and value must be the email ids. Here, 'emails' is the name of the array of objects.

* To use <b>s-sign-out</b> directive, include the following inside your home.html page[or any page you like the logout button to be] (This will create sign out button along with it's functionality) -

  ```
    <div class="signOutGreen" s-sign-out></div>
    ```
    - <b>class</b> - This plugin provides with a set of custom css classes for sign-out button
    - <b>s-sign-out</b> - Directive name to be used for sign out

* Your login.html will be having corresponding controller, say, 'LoginController'. Include the following line of code in your html page and controller -

login.html

```html
<div ng-controller="LoginController">
  <h3>{{displayLogin}}</h3>
  <div class="signInGreenLarge" s-sign-in client="xxxxxxxxxxxxxxxxxxxxxxxxxxx"      hd="gmail.com,yahoo.com" redirect="home" thirdparty="no" emailvalidate="emails">
</div>
```
LoginController

```javascript
auth2Demo.controller('LoginController', ['$scope','S_PersonalData','$location', function($scope, S_PersonalData, $location){
  if(S_PersonalData.s_getPersonalData().isLogin == 'yes'){
    $location.path("/home");
  }else{
    $scope.displayLogin = "This is login page";
        $scope.emails = [
            {
              'email' : 'kinisreenesh202@gmail.com',
            },
            {
              'email' : 'sreeneshkini202@gmail.com'
            }
    ];
  }
}]);
```
1) This plugin provides a service called <b>S_PersonalData</b>. This service provides you with the <b>s_getPersonalData()</b> method, that in turn provides you with following values -

 * <b>isLogin</b> - Used to check whether the user is logged-in or not. It has two values, namely, yes or no.
 * <b>token</b> - Used to get the token of the logged-in user
 * <b>name</b> - Used to get the name of the logged-in user
 * <b>email</b> - Used to get the email id of the logged-in user
 * <b>profile</b> - Used to get the user profile url of logged-in user

2) For all pages in your app, you need to check whether the user is logged in or not. For this, you should write <b>if (S_PersonalData.s_getPersonalData().isLogin == 'yes')</b> as shown in above demo code. If the user is already logged-in, you can redirect user to required page.


3) List of allowed users can be provided as an array of objects like <b>$scope.emails</b>. However, this is optional

* Your home.html will be having corresponding controller, say, 'HomeController'. Include the following line of code in your html and controller -

home.html

```html
<div ng-controller="HomeController">
  <p>Name : {{name}}</p>
  <p>Email : {{email}}</p>
  <p>Profile Pic: </p><img ng-src="{{profileURL}}">
  <div class="signOutGreen" s-sign-out></div>
</div>
```
HomeController

```javascript
auth2Demo.controller('HomeController', ['$scope', 'S_PersonalData', '$location', function($scope, S_PersonalData, $location){
  if(S_PersonalData.s_getPersonalData().isLogin == 'yes'){
    $scope.name = S_PersonalData.s_getPersonalData().name;
    $scope.email = S_PersonalData.s_getPersonalData().email;
    $scope.profileURL = S_PersonalData.s_getPersonalData().profile;
  }else{
    $location.path("/");
  }
}]);
```
1) home.html page will be the page that will be accessible after successful login. The controller checks the same whether the user is logged-in or not. If user is logged-in, then you can fetch the required values to display or store in database.

* As mentioned before, if you are using any third party for handling the logout, then you need to manditorily use plugin's <b>S_GeneralData.s_flush()</b> method, which will flush out the plugin's initializations on sign-out. For this you need to use <b>S_GeneralData</b> service in your controller as follows -

HomeController

```javascript
auth2Demo.controller('HomeController', ['$scope', 'S_PersonalData','S_GeneralData', '$location', function($scope, S_PersonalData, S_GeneralData, $location){
  if(S_PersonalData.s_getPersonalData().isLogin == 'yes'){
        $scope.name = S_PersonalData.s_getPersonalData().name;
        $scope.email = S_PersonalData.s_getPersonalData().email;
        $scope.profileURL = S_PersonalData.s_getPersonalData().profile;
    var your_logout_method = function(){
          // your code if any
          S_GeneralData.s_flush();
          // your code if any
        };
  }else{
    $location.path("/");
  }
}]);
```

* This plugin provides following custom CSS classes that you can use to change the color and size of sign-in and sign-out buttons - 

   - signInBlueSmall
   - signInBlueMedium
   - signInBlueLarge
   - signInGreenSmall
   - signInGreenMedium
   - signInGreenLarge
   - signInRedSmall
   - signInRedMedium
   - signInRedLarge
   - signInLightBlueSmall
   - signInLightBlueMedium
   - signInLightBlueLarge
   - signInLightGreenSmall
   - signInLightGreenMedium
   - signInLightGreenLarge
   - signInLightRedSmall
   - signInLightRedMedium
   - signInLightRedLarge
   - signOutBlue
   - signOutGreen
   - signOutRed
   - signOutLightBlue
   - signOutLightGreen
   - signOutLightRed

* <b>Login Page Demo</b>

![Image of Login Page](https://raw.githubusercontent.com/sreeneshkini/angular-google-auth2/master/build/demo_images/login_page.PNG)

* <b>Home Page Demo</b>

![Image of Login Page](https://raw.githubusercontent.com/sreeneshkini/angular-google-auth2/master/build/demo_images/home_page.PNG)

## License

The MIT License


