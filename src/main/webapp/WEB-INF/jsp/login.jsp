<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html>
<html lang="en">
  <!-- Jquery -->
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
  <!-- Popper -->
  <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js" integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q" crossorigin="anonymous"></script>
  <!-- Bootstrap -->
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
  <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootswatch/4.1.3/sandstone/bootstrap.min.css">
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous"></script>
  <!-- JBox -->
  <script src="https://cdn.jsdelivr.net/gh/StephanWagner/jBox@v0.5.1/dist/jBox.all.min.js"></script>
  <link href="https://cdn.jsdelivr.net/gh/StephanWagner/jBox@v0.5.1/dist/jBox.all.min.css" rel="stylesheet">

  <!-- Custom CSS -->
  <link href="/css/aeselBrowserBaseStyle.css" rel="stylesheet">
  <head>
    <meta http-equiv="Content-Type" content="text/html" charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <title>Aesel Login</title>
  </head>
  <body>
    <div class="container-fluid pre-scrollable" style="height:100%;max-height:100%;">
      <div class="row">
        <div class="col-md-12">
          <h1 style="text-align: center;">Login</h1>
        </div>
      </div>
      <div class="row">
        <div class="col-xs-5 col-sm-4 col-md-2 col-lg-2">
          <p>Username:</p>
        </div>
        <div class="col-xs-7 col-sm-8 col-md-10 col-lg-10" class="tooltip" title="Your username.">
          <input id="usernameinp" type="text" class="form-control-plaintext" name="Username" placeholder="Username"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-xs-5 col-sm-4 col-md-2 col-lg-2">
          <p>Password:</p>
        </div>
        <div class="col-xs-7 col-sm-8 col-md-10 col-lg-10" class="tooltip" title="Your secret password.">
          <input id="passwordinp" type="password" class="form-control-plaintext" name="Password" placeholder="Password"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-md-6 col-lg-6" class="tooltip" title="Cancel the login.">
          <button id="cancel" type="button" class="btn btn-primary"><span style="font-size:larger;">Cancel</span></button>
        </div>
        <div class="col-md-6 col-lg-6" class="tooltip" title="Login to the Web Browser.">
          <button id="login" type="button" class="btn btn-primary"><span style="font-size:larger;">Login</span></button>
        </div>
      </div>
      <footer class="footer">
          <p> &copy; 2018 AO Labs</p>
      </footer>
    </div>
    <script>

    function loginReturn(data, textStatus, xhr) {
      console.log("Redirecting to home page");
      console.log(xhr.getResponseHeader('Authorization'));
      window.location.replace("home");
    }

    // Button click logic
    function onButtonClick(event) {
      if (event.target.id == "login") {
        var newUser = document.getElementById('usernameinp').value;
        var newPass = document.getElementById('passwordinp').value;
        var loginData = {}
        if (newUser) loginData["username"] = newUser;
        if (newPass) loginData["password"] = newPass;
        var loginMethodType = "POST";
        var loginUrl = "/login";
        var loginDataString = JSON.stringify(loginData);
        console.log(loginDataString);
        $.ajax({url: loginUrl,
                type: loginMethodType,
                data: loginDataString,
                contentType: "application/json; charset=utf-8",
                success: loginReturn});
      } else if (event.target.id == "cancel") {
        window.history.back();
      }
    }

    window.addEventListener('DOMContentLoaded', function(){

      // Setup the button callbacks into the Javascript
      var buttons = document.getElementsByTagName("button");
      for (let i = 0; i < buttons.length; i++) {
        buttons[i].addEventListener("click", onButtonClick, false);
      };

      // Activate JBox Tooltips
      new jBox('Tooltip', {
        attach: '.tooltip'
      });
    });
    </script>
  </body>
</html>
