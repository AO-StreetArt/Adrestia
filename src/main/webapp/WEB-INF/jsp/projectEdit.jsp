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
          <a class="nav-link active" href="#">Project Details</a>
        </li>
        <li class="nav-item">
          <a id="scnGroupNav" class="nav-link" href="/sceneGroupBrowser">Scene Groups</a>
        </li>
        <li class="nav-item">
          <a id="assetCollNav" class="nav-link" href="/projectCollectionBrowser">Asset Collections</a>
        </li>
      </ul>
      <div class="alert alert-success" id="success-alert" style="display:none">Project Saved!</div>
      <div class="row" onmouseenter='showEditButton("editnamebtn")' onmouseleave='hideEditButton("editnamebtn")'>
        <div class="col-md-12">
          <h1 id="nameheader" style="text-align: center;"></h1>
          <input id="nameinp" type="text" class="form-control-plaintext" name="Name" placeholder="Name" style="display:none"></input>
          <div class="btn-toolbar pull-right" role="toolbar" aria-label="Asset Toolbar">
            <div class="btn-group" role="group" aria-label="Asset Toolbar">
              <button style="display:none" id="editnamebtn" type="button" class="btn btn-secondary btn-sm" style="z-index:265">Edit</button>
              <button style="display:none" id="savenamebtn" type="button" class="btn btn-secondary btn-sm" style="z-index:265">Save</button>
              <button style="display:none" id="cancelnamebtn" type="button" class="btn btn-secondary btn-sm" style="z-index:265">Cancel</button>
            </div>
          </div>
        </div>
      </div>
      <div class="row">
        <div class="col-xs-6 col-sm-6 col-md-7 col-lg-7" class="tooltip" title="A Description of the Project." onmouseenter='showEditButton("editdescbtn")' onmouseleave='hideEditButton("editdescbtn")'>
          <p id="descdisplay"></p>
          <textarea type="text" id="descinp" class="md-textarea form-control" rows="4" style="display:none"></textarea>
          <div class="btn-toolbar pull-right" role="toolbar" aria-label="Asset Toolbar">
            <div class="btn-group" role="group" aria-label="Asset Toolbar">
              <button style="display:none" id="editdescbtn" type="button" class="btn btn-secondary btn-sm" style="z-index:265">Edit</button>
              <button style="display:none" id="savedescbtn" type="button" class="btn btn-secondary btn-sm" style="z-index:265">Save</button>
              <button style="display:none" id="canceldescbtn" type="button" class="btn btn-secondary btn-sm" style="z-index:265">Cancel</button>
            </div>
          </div>
        </div>
        <div class="col-xs-6 col-sm-6 col-md-5 col-lg-5" class="tooltip" title="Project Thumbnail." onmouseenter='showEditButton("uploadThumbnail");showEditButton("deleteThumbnail")' onmouseleave='hideEditButton("uploadThumbnail");hideEditButton("deleteThumbnail")'>
          <div class="row">
            <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
              <img class="img-fluid rounded mx-auto d-block" id="projectThumbnail" alt="No Thumbnail Available" src="https://www.layoutit.com/img/sports-q-c-140-140-3.jpg" class="rounded center" />
            </div>
          </div>
          <div class="row">
            <div class="col-md-6 col-lg-6">
              <input style="display:none" name="file" type="file" id="uploadThumbnail" class="form-control-file"></input>
            </div>
            <div class="col-md-6 col-lg-6">
              <button style="display:none" id="deleteThumbnail" type="button" class="btn btn-primary btn-sm">Delete Thumbnail</button>
            </div>
          </div>
        </div>
      </div>
      <div class="row">
        <div class="col-xs-5 col-sm-4 col-md-2 col-lg-2">
          <p>Category:</p>
        </div>
        <div class="col-xs-7 col-sm-8 col-md-10 col-lg-10" class="tooltip" title="The category of the Project.">
          <input id="categoryinp" type="text" class="form-control-plaintext" name="Category" placeholder="Category"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-xs-5 col-sm-4 col-md-2 col-lg-2">
          <p>Tags:</p>
        </div>
        <div class="col-xs-7 col-sm-8 col-md-10 col-lg-10" class="tooltip" title="Searchable tags on the Project.">
          <input id="tagsinp" type="text" class="form-control-plaintext" name="Tags" placeholder="Tags"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-md-6" class="tooltip" title="The human-readable name of the Project.">
          <button id="cancel" type="button" class="btn btn-primary"><span style="font-size:larger;">Cancel</span></button>
        </div>
        <div class="col-md-6" class="tooltip" title="The human-readable name of the Project.">
          <button id="save" type="button" class="btn btn-primary"><span style="font-size:larger;">Save Project</span></button>
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

    // The User ID is injected here by the server before
    // it returns the page
    var loggedInUser = "${userName}";
    var loggedInKey = "${userId}";
    console.log(loggedInUser);

    // If the user is an admin, then the server will inject 'true' here,
    // otherwise, it will inject 'false'.
    var isUserAdmin = "${isAdmin}";

    var currentProject = {sceneGroups: []};
    var isAssetDeleted = false;
    var selectedGroup = "";

    // Save a Project
    function saveProject() {
      console.log("Saving Project");
      // Build the JSON message
      currentProject["name"] = document.getElementById('nameinp').value;
      currentProject["description"] = document.getElementById('descinp').value;
      currentProject["category"] = document.getElementById('categoryinp').value;
      currentProject["tags"] = document.getElementById('tagsinp').value.split(",");
      currentProject["assetCollectionIds"] = [];
      currentProject["sceneGroups"] = [];

      // Persist the Project
      var url = "v1/project";
      if (projectKey != "") url = url + "/" + projectKey;
      $.ajax({
        url: url,
        method: 'POST',
        data: JSON.stringify(currentProject),
        contentType: "application/json; charset=utf-8",
        success: function(data) {
          console.log(data);
          document.getElementById('scnGroupNav').href = "/sceneGroupBrowser?key=" + data.id;
          document.getElementById('assetCollNav').href = "/assetCollectionBrowser?key=" + data.id;
          displayAlert("success-alert");
        }
      });
    }

    // Callback after saving asset during page save
    function onAssetCreate(data) {
      console.log(data);
      // Add the thumbnail to the project
      currentProject["thumbnail"] = data;
      saveProject();
    }

    // Button click logic
    function onButtonClick(event) {
      // Save Button
      if (event.target.id == "save") {
        if (isAssetDeleted) {

          // Delete the existing asset
          $.ajax({
            url: "v1/asset/" + currentProject["thumbnail"],
            method: 'DELETE',
            success: function(data) {
              console.log("Asset Deleted");
              // Update the project to have no thumbnail
              currentProject["thumbnail"] = "";
              saveProject();
            }
          });
        } else if (document.getElementById('uploadThumbnail').value != "") {
          var assetFile = $("input[name=file]")[0].files[0];
          var assetFileName = assetFile.name;

          // Setup the creation request
          var url = "v1/asset?asset-type=thumbnail&file-type=" + getFileExtension(assetFileName);
          var formData = new FormData();
          console.log(assetFile);
          formData.append("file", assetFile);

          // Create a new asset
          $.ajax({
            url: url,
            method: 'POST',
            cache: false,
            contentType: false,
            data: formData,
            processData: false,
            success: onAssetCreate
          });
        } else {
          saveProject();
        }
      } else if (event.target.id == "cancel") {
        // Cancel Button
        window.location.replace("projectBrowser");
      } else if (event.target.id == "deleteThumbnail") {
        isAssetDeleted = confirm("Do you want to delete the thumbnail from this Project?");
      } else if (event.target.id == "editnamebtn") {
        $("#nameheader").hide();
        $("#nameinp").show();
        $("#savenamebtn").show();
        $("#cancelnamebtn").show();
      } else if (event.target.id == "savenamebtn") {
        document.getElementById('nameheader').innerHTML = document.getElementById('nameinp').value;
        $("#nameheader").show();
        $("#nameinp").hide();
        $("#savenamebtn").hide();
        $("#cancelnamebtn").hide();
      } else if (event.target.id == "cancelnamebtn") {
        $("#nameheader").show();
        $("#nameinp").hide();
        $("#savenamebtn").hide();
        $("#cancelnamebtn").hide();
      } else if (event.target.id == "editdescbtn") {
        $("#descdisplay").hide();
        $("#descinp").show();
        $("#savedescbtn").show();
        $("#canceldescbtn").show();
      } else if (event.target.id == "savedescbtn") {
        document.getElementById('descdisplay').innerHTML = document.getElementById('descinp').value;
        $("#descdisplay").show();
        $("#descinp").hide();
        $("#savedescbtn").hide();
        $("#canceldescbtn").hide();
      } else if (event.target.id == "canceldescbtn") {
        $("#descdisplay").show();
        $("#descinp").hide();
        $("#savedescbtn").hide();
        $("#canceldescbtn").hide();
      }
    }

    // Logic for loading the page from a pre-existing Project
    var query_return = function(data) {
      console.log("Updating Project Data");
      console.log(data);

      // Update the Text inputs
      document.getElementById('nameinp').value = data.name;
      document.getElementById('nameheader').innerHTML = data.name;
      document.getElementById('descdisplay').innerHTML = data.description;
      document.getElementById('descinp').value = data.description;
      document.getElementById('categoryinp').value = data.category;
      document.getElementById('tagsinp').value = data.tags;
      document.getElementById('projectThumbnail').src = "v1/asset/" + data.thumbnail + "/thumbnail.png";

      // Update the current project to the returned values
      currentProject["thumbnail"] = data.thumbnail;
      currentProject["sceneGroups"] = data.sceneGroups;
    }

    window.addEventListener('DOMContentLoaded', function(){
      setUsersLink((isUserAdmin == 'true'), "userBrowserLink", loggedInKey)

      // Setup the button callbacks into the Javascript
      registerButtonCallback(onButtonClick);

      // Activate JBox Tooltips
      new jBox('Tooltip', {
        attach: '.tooltip'
      });

      console.log("Project Key: " + projectKey)
      if (projectKey) {
        $.ajax({url: "v1/project/" + projectKey, data: {}, success: query_return});
        document.getElementById('scnGroupNav').href = "/sceneGroupBrowser?key=" + projectKey;
        document.getElementById('assetCollNav').href = "/assetCollectionBrowser?key=" + projectKey;
      } else {
        // Show inputs by default if we aren't loading an existing project
        showEditButton("editnamebtn");
        showEditButton("editdescbtn");
      }
    });
    </script>
  </body>
</html>
