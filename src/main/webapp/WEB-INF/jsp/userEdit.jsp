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
    <title>Aesel User</title>
    <!--- Ag-grid-community --->
    <script src="https://unpkg.com/ag-grid-community/dist/ag-grid-community.min.noStyle.js"></script>
    <link rel="stylesheet" href="https://unpkg.com/ag-grid-community/dist/styles/ag-grid.css">
    <link rel="stylesheet" href="https://unpkg.com/ag-grid-community/dist/styles/ag-theme-balham.css">
  </head>
  <body>
    <div class="container-fluid pre-scrollable" style="height:100%;max-height:100%;">
      <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
        <a class="navbar-brand" href="#">Aesel</a>
          <ul class="navbar-nav mr-auto">
            <li class="nav-item">
              <a class="nav-link" href="/portal/home">Home</a>
            </li>
            <li class="nav-item" id="projectBrowser">
              <a class="nav-link" href="/projectBrowser">Projects</a>
            </li>
            <li class="nav-item" id="sceneBrowser">
              <a class="nav-link" href="/sceneBrowser">Scenes</a>
            </li>
            <li class="nav-item" id="assetBrowser">
              <a class="nav-link" href="/assetBrowser">Assets</a>
            </li>
            <li class="nav-item active" id="userBrowser">
              <a class="nav-link" href="#" id="userBrowserLink">Users<span class="sr-only">(current)</span></a>
            </li>
            <li class="nav-item" id="docs">
              <a class="nav-link" href="https://aesel.readthedocs.io/en/latest/index.html">Documentation</a>
            </li>
          </ul>
      </nav>
      <div class="alert alert-success" id="success-alert" style="display:none">User Saved!</div>
      <div class="row">
        <div class="col-md-12">
          <h1 style="text-align: center;">User</h1>
        </div>
      </div>
      <div class="row">
        <div class="col-xs-5 col-sm-4 col-md-2 col-lg-2">
          <p>Key:</p>
        </div>
        <div class="col-xs-7 col-sm-8 col-md-10 col-lg-10" class="tooltip" title="The unique identifier of the User.">
          <input id="keyinp" type="text" class="form-control-plaintext" name="Key" placeholder="Key"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-xs-5 col-sm-4 col-md-2 col-lg-2">
          <p>Username:</p>
        </div>
        <div class="col-xs-7 col-sm-8 col-md-10 col-lg-10" class="tooltip" title="The username for the account.">
          <input id="usernameinp" type="text" class="form-control-plaintext" name="Username" placeholder="Username"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-xs-5 col-sm-4 col-md-2 col-lg-2">
          <p>Password:</p>
        </div>
        <div class="col-xs-7 col-sm-8 col-md-10 col-lg-10" class="tooltip" title="The password to login with this account.">
          <input id="passwordinp" type="password" class="form-control-plaintext" name="Password" placeholder="Password"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-xs-5 col-sm-4 col-md-2 col-lg-2">
          <p>Email:</p>
        </div>
        <div class="col-xs-7 col-sm-8 col-md-10 col-lg-10" class="tooltip" title="The email associated to the account.">
          <input id="emailinp" type="text" class="form-control-plaintext" name="Email" placeholder="Email"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-xs-5 col-sm-4 col-md-2 col-lg-2">
          <p>Administrator:</p>
        </div>
        <div class="col-xs-7 col-sm-8 col-md-10 col-lg-10" class="tooltip" title="Set to 'true' if this user has administrator access.">
          <input id="admininp" type="text" class="form-control-plaintext" name="isAdmin" placeholder="isAdmin"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-xs-5 col-sm-4 col-md-2 col-lg-2">
          <p>Active:</p>
        </div>
        <div class="col-xs-7 col-sm-8 col-md-10 col-lg-10" class="tooltip" title="Set to 'true' if this user has access to the system.">
          <input id="activeinp" type="text" class="form-control-plaintext" name="isActive" placeholder="isActive"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-xs-5 col-sm-4 col-md-2 col-lg-2">
          <p>Favorite Projects:</p>
        </div>
        <div class="col-xs-7 col-sm-8 col-md-10 col-lg-10" class="tooltip" title="A list of Project ID's the user has marked as favorites.">
          <input id="favprojectsinp" type="text" class="form-control-plaintext" name="favProjects" placeholder="Favorite Projects"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-xs-5 col-sm-4 col-md-2 col-lg-2">
          <p>Favorite Scenes:</p>
        </div>
        <div class="col-xs-7 col-sm-8 col-md-10 col-lg-10" class="tooltip" title="A list of Scene ID's the user has marked as favorites.">
          <input id="favscenesinp" type="text" class="form-control-plaintext" name="favScenes" placeholder="Favorite Scenes"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-md-6 col-lg-6" class="tooltip" title="Cancel the changes.">
          <button id="cancel" type="button" class="btn btn-primary">Cancel</button>
        </div>
        <div class="col-md-6 col-lg-6" class="tooltip" title="Save the Scene.">
          <button id="save" type="button" class="btn btn-primary">Save User</button>
        </div>
      </div>
      <footer class="footer">
          <p> &copy; 2018 AO Labs</p>
      </footer>
    </div>
    <script>
    // The User Key is injected here by the server before
    // it returns the page
    var userKey = "${userKey}";
    console.log(userKey);

    function userReturn(data) {
      console.log(data);
      $("#success-alert").show();
      setTimeout(function() { $("#success-alert").hide(); }, 5000);
    }

    // Button click logic
    function onButtonClick(event) {
      if (event.target.id == "save") {
        var newKey = document.getElementById('keyinp').value;
        var newUsername = document.getElementById('usernameinp').value;
        var newPassword = document.getElementById('passwordinp').value;
        var newEmail = document.getElementById('emailinp').value;
        var newAdmin = document.getElementById('admininp').value;
        var newActive = document.getElementById('activeinp').value;
        var newProjectList = document.getElementById('favprojectsinp').value;
        var newSceneList = document.getElementById('favscenesinp').value;
        userData = {}
        if (newUsername) userData["username"] = newUsername;
        if (newPassword) userData["password"] = newPassword;
        if (newEmail) userData["email"] = newEmail;
        if (newAdmin) userData["isAdmin"] = (newAdmin == "true");
        if (newActive) userData["isActive"] = (newActive == "true");
        if (newProjectList) userData["favoriteProjects"] = newProjectList.split(",");
        if (newSceneList) userData["favoriteScenes"] = newSceneList.split(",");
        userMethodType = "POST";
        userUrl = "/users/";
        if (newKey) {
          userMethodType = "PUT";
          userData["key"] = newKey;
          userUrl = userUrl + newKey;
        } else {
          userUrl = userUrl + "sign-up";
        }
        console.log(userData);
        $.ajax({url: userUrl,
                type: userMethodType,
                data: JSON.stringify(userData),
                contentType: "application/json; charset=utf-8",
                success: userReturn});
      } else if (event.target.id == "cancel") {
        window.location.replace("userBrowser");
      }
    }

    // Logic for loading the page from a pre-existing Project
    var query_return = function(data) {
      console.log("Updating User Data");
      console.log(data);
      document.getElementById('keyinp').value = data.id;
      document.getElementById('usernameinp').value = data.username;
      document.getElementById('emailinp').value = data.email;
      document.getElementById('admininp').value = data.isAdmin;
      document.getElementById('activeinp').value = data.isActive;
      document.getElementById('favprojectsinp').value = data.favoriteProjects;
      document.getElementById('favscenesinp').value = data.favoriteScenes;
    }

    window.addEventListener('DOMContentLoaded', function(){
      // The User ID is injected here by the server before
      // it returns the page
      var loggedInUser = "${userName}";
      console.log(loggedInUser);
      // If the user is an admin, then the server will inject 'true' here,
      // otherwise, it will inject 'false'.
      var isUserAdmin = "${isAdmin}";
      var adminLoggedIn = (isUserAdmin == 'true');
      if (!adminLoggedIn) {
        // Disable the user browser link in the navbar if the logged in
        // user does not have admin access
        document.getElementById("userBrowser").setAttribute('class', 'nav-item disabled');
        document.getElementById("userBrowserLink").click(function() {
          return false;
        });
      }

      // Setup the button callbacks into the Javascript
      var buttons = document.getElementsByTagName("button");
      for (let i = 0; i < buttons.length; i++) {
        buttons[i].addEventListener("click", onButtonClick, false);
      };

      // Activate JBox Tooltips
      new jBox('Tooltip', {
        attach: '.tooltip'
      });

      console.log("User Key: " + userKey)
      if (userKey) {
        $.ajax({url: "/users/" + userKey, data: {}, success: query_return});
      }
    });
    </script>
  </body>
</html>
