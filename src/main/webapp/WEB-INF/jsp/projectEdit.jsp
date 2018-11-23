<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html>
<html lang="en">
  <!-- Jquery -->
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
  <!-- Bootstrap -->
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap-theme.min.css" integrity="sha384-rHyoN1iRsVXV4nD0JutlnGaslCJuC7uwjduW9SVrLvRYooPp2bWYgmgJQIXwl/Sp" crossorigin="anonymous">
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous"></script>
  <!-- JBox -->
  <script src="https://cdn.jsdelivr.net/gh/StephanWagner/jBox@v0.5.1/dist/jBox.all.min.js"></script>
  <link href="https://cdn.jsdelivr.net/gh/StephanWagner/jBox@v0.5.1/dist/jBox.all.min.css" rel="stylesheet">

  <head>
    <meta http-equiv="Content-Type" content="text/html" charset="utf-8"/>
    <title>Aesel Project</title>
    <!--- Ag-grid-community --->
    <script src="https://unpkg.com/ag-grid-community/dist/ag-grid-community.min.noStyle.js"></script>
    <link rel="stylesheet" href="https://unpkg.com/ag-grid-community/dist/styles/ag-grid.css">
    <link rel="stylesheet" href="https://unpkg.com/ag-grid-community/dist/styles/ag-theme-balham.css">
    <style>
    html, body {
      overflow: hidden;
      width   : 100%;
      height  : 100%;
      margin  : 0;
      padding : 0;
    }
    input {
      width : 100%;
    }
    html {
      overflow-y: hidden;
    }
    </style>
  </head>
  <body>
    <div class="pre-scrollable" style="height:100%;max-height: 100%;">
    <div class="container-fluid" style="height:100%;">
      <div class="row">
        <h1 style="text-align: center;">Project</h1>
      </div>
      <div class="row">
        <div class="col-md-12" class="tooltip" title="The human-readable name of the Project.">
          <input id="keyinp" type="text" name="Key" placeholder="Key"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-md-12" class="tooltip" title="The human-readable name of the Project.">
          <input id="nameinp" type="text" name="Name" placeholder="Name"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-md-12" class="tooltip" title="The human-readable name of the Project.">
          <input id="descinp" type="text" name="Description" placeholder="Description"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-md-12" class="tooltip" title="The human-readable name of the Project.">
          <input id="categoryinp" type="text" name="Category" placeholder="Category"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-md-12" class="tooltip" title="The human-readable name of the Project.">
          <input id="tagsinp" type="text" name="Tags" placeholder="Tags"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-md-12" class="tooltip" title="The human-readable name of the Project.">
          <input id="collectionsinp" type="text" name="Asset Collections" placeholder="Asset Collections"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-md-4" class="tooltip" title="The human-readable name of the Project.">
          <input name="file" type="file" id="uploadThumbnail"></button>
        </div>
        <div class="col-md-4" class="tooltip" title="The human-readable name of the Project.">
          <button id="deleteThumbnail">Delete Thumbnail</button>
        </div>
        <div class="col-md-4" class="tooltip" title="The human-readable name of the Project.">
          <img id="projectThumbnail" alt="No Thumbnail Available" src="https://www.layoutit.com/img/sports-q-c-140-140-3.jpg" class="rounded" />
        </div>
      </div>
      <div class="row">
        <div class="col-md-12" class="tooltip" title="The human-readable name of the Project.">
          <h2 style="text-align: center;">Scene Groups</h2>
        </div>
      </div>
      <div style="height:100%;" class="row">
        <div style="height:100%;" class="col-md-6">
          <div id="myGrid" style="height:100%;" class="ag-theme-balham"></div>
        </div>
        <div class="col-md-6">
          <input id="sgnameinp" type="text" name="Name" placeholder="Name"></input>
          <input id="sgdescinp" type="text" name="Description" placeholder="Description"></input>
          <input id="sgcategoryinp" type="text" name="Category" placeholder="Category"></input>
          <input id="sgscenesinp" type="text" name="Scenes" placeholder="Scenes"></input>
          <button id="saveSg">Save Scene Group</button>
          <button id="clearSg">Clear Scene Group</button>
          <button id="deleteSg">Delete Scene Group</button>
          <button id="viewSg">View Scene Group</button>
        </div>
      </div>
      <div class="row">
        <div class="col-md-6" class="tooltip" title="The human-readable name of the Project.">
          <button id="cancel">Cancel</button>
        </div>
        <div class="col-md-6" class="tooltip" title="The human-readable name of the Project.">
          <button id="save">Save Project</button>
        </div>
      </div>
      <footer class="footer">
          <p> &copy; 2018 AO Labs</p>
      </footer>
    </div>
    </div>
    <script>
    // The Project Key is injected here by the server before
    // it returns the page
    var projectKey = "${projKey}";

    var currentProject = {};
    var isAssetDeleted = false;
    var selectedGroup = "";

    function getFileExtension(filename) {
      var parts = filename.split('.');
      return parts[parts.length - 1];
    }

    // specify the columns for the scene group list
    var columnDefs = [
      {
        headerName: "Name",
        field: "name",
        pinned: 'left',
        filter: "agTextColumnFilter",
        editable: false
      },
      {
        headerName: "Category",
        field: "category",
        filter: "agTextColumnFilter",
        editable: false
      },
      {
        headerName: "Description",
        field: "description",
        filter: "agTextColumnFilter",
        editable: false
      },
      {
        headerName: "Scenes",
        field: "scenes",
        filter: "agTextColumnFilter",
        editable: false
      }
    ];

    // specify the grid options
    var gridOptions = {
      animateRows: true,
      columnDefs: columnDefs,
      rowData: [],
      pagination: true,
      enableFilter: true,
      rowSelection: 'single',
      onRowDoubleClicked: function(event) {
        console.log("Row Double Clicked:");
        var selectedRow = gridOptions.api.getSelectedNodes()[0].data;
        var newDescription = selectedRow.description;
        var newName = selectedRow.name;
        var newCategory = selectedRow.category;
        var newScenes = selectedRow.scenes;
        document.getElementById('sgdescinp').value = newDescription;
        document.getElementById('sgnameinp').value = newName;
        document.getElementById('sgcategoryinp').value = newCategory;
        document.getElementById('sgscenesinp').value = newScenes;
        selectedGroup = newName;
      }
    };

    // lookup the container we want the Grid to use
    var eGridDiv = document.querySelector('#myGrid');

    // create the grid passing in the div to use together with the columns & data we want to use
    new agGrid.Grid(eGridDiv, gridOptions);

    // Save a Project
    function saveProject() {
      console.log("Saving Project");
      // Build the JSON message
      var existingKey = document.getElementById('keyinp').value;
      currentProject["name"] = document.getElementById('nameinp').value;
      currentProject["description"] = document.getElementById('descinp').value;
      currentProject["category"] = document.getElementById('categoryinp').value;
      currentProject["tags"] = [document.getElementById('tagsinp').value];
      currentProject["assetCollectionIds"] = [document.getElementById('collectionsinp').value];

      // Persist the Project
      var url = "v1/project";
      if (existingKey != "") url = url + "/" + existingKey;
      $.ajax({
        url: url,
        method: 'POST',
        data: JSON.stringify(currentProject),
        contentType: "application/json; charset=utf-8",
        success: function(data) {
          console.log("Project Saved");
          // Route back to the previous page after saving the project
          window.history.back();
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
            url: "v1/asset/" + document.getElementById('keyinp').value,
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
          var url = "v1/asset?asset-type=thumbnail&name=AlexTest&file-type=" + getFileExtension(assetFileName);
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
        window.history.back();
      } else if (event.target.id == "saveSg") {
        // Save Scene Group Button
        // See if we are updating an existing group
        var groupExists = false;
        for (var i = 0; i < currentProject.sceneGroups.length; i++) {
          if (currentProject.sceneGroups[i].name == selectedGroup) {
            groupExists = true;
            currentProject.sceneGroups[i].name = document.getElementById('sgnameinp').value;
            currentProject.sceneGroups[i].description = document.getElementById('sgdescinp').value;
            currentProject.sceneGroups[i].category = document.getElementById('sgcategoryinp').value;
            currentProject.sceneGroups[i].scenes = [document.getElementById('sgscenesinp').value]
          }
        }

        // If no group exists, create a new one
        if (!groupExists) {
          currentProject["sceneGroups"].push({
            name: document.getElementById('sgnameinp').value,
            description: document.getElementById('sgdescinp').value,
            category: document.getElementById('sgcategoryinp').value,
            scenes: [document.getElementById('sgscenesinp').value]
          });
        }

        // Update the scene grid with the new data
        gridOptions.api.setRowData(currentProject.sceneGroups);
      } else if (event.target.id == "clearSg") {
        // Clear Scene Group Button
        document.getElementById('sgdescinp').value = "";
        document.getElementById('sgnameinp').value = "";
        document.getElementById('sgcategoryinp').value = "";
        document.getElementById('sgscenesinp').value = "";
        selectedGroup = "";
      } else if (event.target.id == "deleteSg") {
        for (var i = 0; i < currentProject.sceneGroups.length; i++) {
          if (currentProject.sceneGroups[i].name == selectedGroup) {
            // Remove the group
            currentProject.sceneGroups.splice(i, 1);
          }
        }
        gridOptions.api.setRowData(currentProject.sceneGroups);
      } else if (event.target.id == "viewSg") {
        // Redirect to the Scene Browser with the list of scene ids
        //        as a query parameter
        var sceneIdList = "";
        for (var i = 0; i < currentProject.sceneGroups.length; i++) {
          if (currentProject.sceneGroups[i].name == selectedGroup) {
            // Get the Scene ID List
            for (var j = 0; j < currentProject.sceneGroups[i].scenes.length; j++) {
              sceneIdList = sceneIdList.push(currentProject.sceneGroups[i].scenes[j]);
              if (j < currentProject.sceneGroups[i].scenes.length - 1) {
                sceneIdList = sceneIdList.push(",");
              }
            }
          }
        }
        window.location.replace("sceneBrowser?keys=" + sceneIdList);
      } else if (event.target.id == "deleteThumbnail") {
        isAssetDeleted = confirm("Do you want to delete the thumbnail from this Project?");
      }
    }

    // Logic for loading the page from a pre-existing Project
    var query_return = function(data) {
      console.log("Updating Project Data");
      console.log(data);

      // Update the Text inputs
      document.getElementById('keyinp').value = data.id;
      document.getElementById('nameinp').value = data.name;
      document.getElementById('descinp').value = data.description;
      document.getElementById('categoryinp').value = data.category;
      document.getElementById('tagsinp').value = data.tags;
      document.getElementById('collectionsinp').value = data.assetCollectionIds;
      document.getElementById('projectThumbnail').src = "v1/asset/" + data.thumbnail + "/thumbnail.png";

      // Update the current project to the returned values
      currentProject["thumbnail"] = data.thumbnail;
      currentProject["sceneGroups"] = data.sceneGroups;

      // Update the Scene Groups Grid
      gridOptions.api.setRowData(data.sceneGroups);
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

      console.log("Project Key: " + projectKey)
      if (projectKey) {
        $.ajax({url: "v1/project/" + projectKey, data: {}, success: query_return});
      }
    });
    </script>
  </body>
</html>
