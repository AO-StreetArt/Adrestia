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
    <title>Aesel Scene</title>
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
            <li class="nav-item active" id="sceneBrowser">
              <a class="nav-link" href="#">Scenes <span class="sr-only">(current)</span></a>
            </li>
            <li class="nav-item" id="assetBrowser">
              <a class="nav-link" href="/assetBrowser">Assets</a>
            </li>
            <li class="nav-item" id="userBrowser">
              <a class="nav-link" href="/userBrowser" id="userBrowserLink">Users</a>
            </li>
            <li class="nav-item" id="docs">
              <a class="nav-link" href="https://aesel.readthedocs.io/en/latest/index.html">Documentation</a>
            </li>
          </ul>
      </nav>
      <div class="alert alert-success" id="success-alert" style="display:none">Scene Saved!</div>
      <div class="row">
        <div class="col-md-12">
          <h1 style="text-align: center;">Scene</h1>
        </div>
      </div>
      <div class="row">
        <div class="col-xs-5 col-sm-4 col-md-2 col-lg-2">
          <p>Key:</p>
        </div>
        <div class="col-xs-7 col-sm-8 col-md-10 col-lg-10" class="tooltip" title="The unique identifier of the Scene.">
          <input id="keyinp" type="text" class="form-control-plaintext" name="Key" placeholder="Key"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-xs-5 col-sm-4 col-md-2 col-lg-2">
          <p>Name:</p>
        </div>
        <div class="col-xs-7 col-sm-8 col-md-10 col-lg-10" class="tooltip" title="The human-readable name of the Scene.">
          <input id="nameinp" type="text" class="form-control-plaintext" name="Name" placeholder="Name"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-xs-5 col-sm-4 col-md-2 col-lg-2">
          <p>Region:</p>
        </div>
        <div class="col-xs-7 col-sm-8 col-md-10 col-lg-10" class="tooltip" title="The human-readable name of the Scene.">
          <input id="regioninp" type="text" class="form-control-plaintext" name="Region" placeholder="Region"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-xs-5 col-sm-4 col-md-2 col-lg-2">
          <p>Latitude:</p>
        </div>
        <div class="col-xs-7 col-sm-8 col-md-10 col-lg-10" class="tooltip" title="The latitude of the Scene for distance queries.">
          <input id="latitudeinp" type="text" class="form-control-plaintext" name="Latitude" placeholder="Latitude"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-xs-5 col-sm-4 col-md-2 col-lg-2">
          <p>Longitude:</p>
        </div>
        <div class="col-xs-7 col-sm-8 col-md-10 col-lg-10" class="tooltip" title="The longitude of the Scene for distance queries.">
          <input id="longitudeinp" type="text" class="form-control-plaintext" name="Longitude" placeholder="Longitude"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-xs-5 col-sm-4 col-md-2 col-lg-2">
          <p>Tags:</p>
        </div>
        <div class="col-xs-7 col-sm-8 col-md-10 col-lg-10" class="tooltip" title="The tags associated to the Scene.">
          <input id="tagsinp" type="text" class="form-control-plaintext" name="Tags" placeholder="Tags"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-md-6 col-lg-6" class="tooltip" title="Cancel the changes.">
          <button id="cancel" type="button" class="btn btn-primary">Cancel</button>
        </div>
        <div class="col-md-6 col-lg-6" class="tooltip" title="Save the Scene.">
          <button id="save" type="button" class="btn btn-primary">Save Scene</button>
        </div>
      </div>
      <footer class="footer">
          <p> &copy; 2018 AO Labs</p>
      </footer>
    </div>
    <script>
    // The Scene Key is injected here by the server before
    // it returns the page
    var sceneKey = "${sceneKey}";

    function sceneReturn(data) {
      console.log(data);
      $("#success-alert").show();
      setTimeout(function() { $("#success-alert").hide(); }, 5000);
    }

    // Button click logic
    function onButtonClick(event) {
      if (event.target.id == "save") {
        var newKey = document.getElementById('keyinp').value;
        var newName = document.getElementById('nameinp').value;
        var newRegion = document.getElementById('regioninp').value;
        var newLat = document.getElementById('latitudeinp').value;
        var newLong = document.getElementById('longitudeinp').value;
        var newTags = document.getElementById('tagsinp').value;
        sceneData = {}
        if (newName) sceneData["name"] = newName;
        if (newRegion) sceneData["region"] = newRegion;
        if (newLong) sceneData["longitude"] = parseFloat(newLong);
        if (newLat) sceneData["latitude"] = parseFloat(newLat);
        if (newTags) sceneData["tags"] = newTags.split(",");
        sceneListMethodType = "POST";
        if (newKey) {
          sceneListMethodType = "PUT";
          sceneData["key"] = newKey;
        }
        sceneUrl = "v1/scene/" + newKey;
        sceneListData = JSON.stringify({"scenes": [sceneData]});
        console.log(sceneListData);
        $.ajax({url: sceneUrl,
                type: sceneListMethodType,
                data: sceneListData,
                contentType: "application/json; charset=utf-8",
                success: sceneReturn});
      } else if (event.target.id == "cancel") {
        window.location.replace("sceneBrowser");
      }
    }

    // Logic for loading the page from a pre-existing Project
    var query_return = function(data) {
      console.log("Updating Scene Data");
      console.log(data);
      document.getElementById('keyinp').value = data.scenes[0].key;
      document.getElementById('nameinp').value = data.scenes[0].name;
      document.getElementById('regioninp').value = data.scenes[0].region;
      document.getElementById('latitudeinp').value = data.scenes[0].latitude;
      document.getElementById('longitudeinp').value = data.scenes[0].longitude;
      document.getElementById('tagsinp').value = data.scenes[0].tags;
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
        document.getElementById("userBrowserLink").href = "#";
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

      console.log("Scene Key: " + sceneKey)
      if (sceneKey) {
        $.ajax({url: "v1/scene/" + sceneKey, data: {}, success: query_return});
      }
    });
    </script>
  </body>
</html>
