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
  <!-- Custom Javascript -->
  <script src="/js/aeselBrowserUtils.js"></script>
  <head>
    <meta http-equiv="Content-Type" content="text/html" charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <title>Aesel Project</title>
    <!--- Ag-grid-community --->
    <script src="https://unpkg.com/ag-grid-community/dist/ag-grid-community.min.noStyle.js"></script>
    <link rel="stylesheet" href="https://unpkg.com/ag-grid-community/dist/styles/ag-grid.css">
    <link rel="stylesheet" href="https://unpkg.com/ag-grid-community/dist/styles/ag-theme-balham.css">
  </head>
  <body>
    <div class="container-fluid pre-scrollable" style="height:100%;max-height:100%;">
      <%@include  file="aeselBrowserNavbar.jspf" %>
      <ul class="nav nav-pills">
        <li class="nav-item">
          <a id="projDetailNav" class="nav-link" href="/editProject">Project Details</a>
        </li>
        <li class="nav-item">
          <a class="nav-link active" href="#">Scene Groups</a>
        </li>
        <li class="nav-item">
          <a id="assetCollNav" class="nav-link" href="/projectCollectionBrowser">Asset Collections</a>
        </li>
      </ul>
      <div class="alert alert-success" id="success-alert" style="display:none">Success!</div>
      <div class="row">
        <div class="col-md-12">
          <h2 style="text-align: center;">Scene Groups</h2>
        </div>
      </div>
      <%@include  file="cardDisplay.jspf" %>
      <div class="row">
        <div class="col-md-4">
          <input id="newGroupNameInp" type="text" class="form-control-plaintext" name="newGroupNameInput" placeholder="Name"></input>
        </div>
        <div class="col-md-4">
          <textarea type="text" id="newGroupDescInp" class="md-textarea form-control" rows="4" placeholder="Description"></textarea>
        </div>
        <div class="col-md-4">
          <input id="newGroupCategoryInp" type="text" class="form-control-plaintext" name="newGroupCategoryInput" placeholder="Category"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-md-6 col-lg-6" class="tooltip" title="Clear the new scene group info.">
          <button id="clearNewSceneGroup" type="button" class="btn btn-primary"><span style="font-size:larger;">Clear</span></button>
        </div>
        <div class="col-md-6 col-lg-6" class="tooltip" title="Add a new Scene Group with the given information.">
          <button id="addSceneGroup" type="button" class="btn btn-primary"><span style="font-size:larger;">Add Scene Group</span></button>
        </div>
      </div>
      <footer class="footer">
          <p> &copy; 2018 AO Labs</p>
      </footer>
    </div>
    <script>
    // The Project Key is injected here by the server before
    // it returns the page
    var projectKey = "${projKey}";

    // Current project and pagination info
    var currentProject = {sceneGroups: []};
    var currentPage = 0;

    // Set the scene group cards based on a set of scene groups
    // and the current pagination settings.
    function updateSceneGroupCards(groups) {
      for (i = 1; i <= 3; i++) {
        var groupListIndex = ((currentPage * 3) + i - 1);
        document.getElementById('group' + i + 'Header').innerHTML = "";
        document.getElementById('group' + i + 'Body').innerHTML = "";
        document.getElementById('group' + i + 'Category').innerHTML = "";
        if (groupListIndex > groups.length-1) break;
        document.getElementById('group' + i + 'Header').innerHTML = groups[groupListIndex].name;
        document.getElementById('group' + i + 'Body').innerHTML = groups[groupListIndex].description;
        document.getElementById('group' + i + 'Category').innerHTML = groups[groupListIndex].category;
      }
    }

    // Callback for HTTP Request to load Scene Groups for display
    function query_return(data) {
      console.log("Updating Scene Group Data");
      console.log(data);

      // Update current project data
      currentProject = data;

      // Load Scene Group Cards
      updateSceneGroupCards(data.sceneGroups);
    }

    // Initiate the HTTP Request to load Scene Groups
    function loadSceneGroupCards(data) {
      $.ajax({
        url: "v1/project/" + projectKey,
        data: {},
        success: query_return
      });
    }

    function deleteSceneGroup(index) {
      $.ajax({
        url: "v1/project/" + projectKey + "/groups/" + document.getElementById('g' + index + 'HeaderInp').value,
        method: 'DELETE',
        data: {},
        success: loadSceneGroupCards
      });
    }

    function saveSceneGroup(index) {
      var sgName = document.getElementById('g' + index + 'HeaderInp').value;
      var sgDesc = document.getElementById('g' + index + 'DescInp').value;
      var sgCat = document.getElementById('g' + index + 'CategoryInp').value;
      $.ajax({
        url: "/v1/project/" + projectKey + "groups/" + sgName,
        method: 'POST',
        data: {"name": sgName, "description": sgDesc, "category": sgCat},
        contentType: "application/json; charset=utf-8",
        success: function(data) {
          console.log(data);
          displayAlert("success-alert");
        }
      });
    }

    // Button click logic
    function onButtonClick(event) {
      if (event.target.id == "viewg1btn") {
        console.log(currentProject.sceneGroups[0]);
        //window.location.replace("sceneBrowser?keys=" + currentProject.sceneGroups[0].scenes);
      } else if (event.target.id == "editg1btn") {
        showHtmlElements([
          "g1BodyInp",
          "g1CategoryInp",
          "saveg1btn",
          "cancelg1btn"
        ]);
        hideHtmlElements([
          "group1Body",
          "group1Category",
          "editg1btn"
        ]);
      } else if (event.target.id == "deleteg1btn") {
        deleteSceneGroup(1);
      } else if (event.target.id == "saveg1btn") {
        saveSceneGroup(1);
      } else if (event.target.id == "cancelg1btn") {
        hideHtmlElements([
          "g1BodyInp",
          "g1CategoryInp",
          "saveg1btn",
          "cancelg1btn"
        ]);
        showHtmlElements([
          "group1Body",
          "group1Category",
          "editg1btn"
        ]);
      } else if (event.target.id == "firstPage") {
        currentPage = 0;
        updateSceneGroupCards(currentProject.sceneGroups);
      } else if (event.target.id == "nextPage") {
        currentPage = currentPage + 1;
        updateSceneGroupCards(currentProject.sceneGroups);
      } else if (event.target.id == "prevPage") {
        if (currentPage > 0) currentPage = currentPage - 1;
        updateSceneGroupCards(currentProject.sceneGroups);
      } else if (event.target.id == "addSceneGroup") {
        // Add a new scene group to the project with
        // the information from the new group inputs.
        var sgName = document.getElementById('newGroupNameInp').value;
        var sgDesc = document.getElementById('newGroupDescInp').value;
        var sgCat = document.getElementById('newGroupCategoryInp').value;
        var newGroupDict = {"name": sgName, "description": sgDesc, "category": sgCat};
        currentProject.sceneGroups.push(newGroupDict);
        updateSceneGroupCards(currentProject.sceneGroups);

        // Send the request to add the scene group to the project
        // in the aesel server.
        $.ajax({
          url: "/v1/project/" + projectKey + "/groups",
          method: 'POST',
          data: newGroupDict,
          contentType: "application/json; charset=utf-8",
          success: function(data) {
            console.log(data);
            displayAlert("success-alert");
          }
        });
      } else if (event.target.id == "clearNewSceneGroup") {
        // Clear the new Scene Group inputs
        document.getElementById('newGroupNameInp').value = "";
        document.getElementById('newGroupDescInp').value = "";
        document.getElementById('newGroupCategoryInp').value = "";
      }
    }

    window.addEventListener('DOMContentLoaded', function(){
      // The User ID is injected here by the server before
      // it returns the page
      var loggedInUser = "${userName}";
      var loggedInKey = "${userId}";
      console.log(loggedInUser);
      // If the user is an admin, then the server will inject 'true' here,
      // otherwise, it will inject 'false'.
      var isUserAdmin = "${isAdmin}";
      var adminLoggedIn = (isUserAdmin == 'true');
      setUsersLink((isUserAdmin == 'true'), "userBrowserLink", loggedInKey);

      // Setup the button callbacks into the Javascript
      registerButtonCallback(onButtonClick);

      // Activate JBox Tooltips
      new jBox('Tooltip', {
        attach: '.tooltip'
      });

      // Load the project, chaining the callbacks to load the scene list
      console.log("Project Key: " + projectKey)
      if (projectKey) {
        loadSceneGroupCards({});
      }
    });
    </script>
  </body>
</html>
