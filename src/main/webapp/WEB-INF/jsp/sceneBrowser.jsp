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

  <!-- Custom CSS -->
  <link href="/css/aeselBrowserBaseStyle.css" rel="stylesheet">
  <!-- Custom Javascript -->
  <script src="/js/aeselBrowserUtils.js"></script>
  <script src="/js/aeselSceneGridDefinitions.js"></script>
  <head>
    <meta http-equiv="Content-Type" content="text/html" charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <title>Aesel Scene Browser</title>
    <!--- Ag-grid-community --->
    <script src="https://unpkg.com/ag-grid-community/dist/ag-grid-community.min.noStyle.js"></script>
    <link rel="stylesheet" href="https://unpkg.com/ag-grid-community/dist/styles/ag-grid.css">
    <link rel="stylesheet" href="https://unpkg.com/ag-grid-community/dist/styles/ag-theme-balham.css">
  </head>
  <body>
    <div class="container-fluid pre-scrollable" style="height:100%;max-height:100%;">
      <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
        <a class="navbar-brand" href="#"></a>
          <ul class="navbar-nav mr-auto">
            <li class="nav-item">
              <a class="nav-link" href="/portal/home">Home</a>
            </li>
            <li class="nav-item" id="projectBrowser">
              <a class="nav-link" href="/projectBrowser">Projects</a>
            </li>
            <li class="nav-item" id="sceneBrowser">
              <a class="nav-link active" href="#">Scenes <span class="sr-only">(current)</span></a>
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
      <div class="row">
        <div class="col-md-12">
          <h1 id="pageHeader" style="text-align: center;">Scenes</h1>
        </div>
      </div>
      <div class="alert alert-success" id="success-alert" style="display:none">Success!</div>
      <div class="row">
        <div class="col-sm-6 col-md-3 col-lg-3">
          <input id="keyinp" type="text" name="ID" placeholder="ID"></input>
        </div>
        <div class="col-sm-6 col-md-3 col-lg-3">
          <input id="nameinp" type="text" name="Name" placeholder="Name"></input>
        </div>
        <div class="col-sm-6 col-md-3 col-lg-3">
          <input id="regioninp" type="text" name="Region" placeholder="Region"></input>
        </div>
        <div class="col-sm-6 col-md-3 col-lg-3">
          <input id="latitudeinp" type="text" name="Latitude" placeholder="Latitude"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-sm-6 col-md-3 col-lg-3">
          <input id="longitudeinp" type="text" name="Longitude" placeholder="Longitude"></input>
        </div>
        <div class="col-sm-6 col-md-3 col-lg-3">
          <input id="distanceinp" type="text" name="Distance" placeholder="Distance"></input>
        </div>
        <div class="col-sm-6 col-md-3 col-lg-3">
          <input id="taginp" type="text" name="Tag" placeholder="Tag"></input>
        </div>
        <div class="col-sm-6 col-md-3 col-lg-3 text-center">
          <button id="query" class="btn btn-primary"><span style="font-size:larger;">Query</span></button>
        </div>
      </div>
      <div class="row">
        <div class="col-md-12">
          <div class="btn-toolbar" role="toolbar" aria-label="Scenes Toolbar" style="justify-content: center;">
            <div class="btn-group" role="group" aria-label="Scene Tools">
              <button id="editScene" type="button" class="btn btn-primary"><span style="font-size:larger;">Edit Scene</span></button>
              <button id="createScene" type="button" class="btn btn-primary"><span style="font-size:larger;">Create Scene</span></button>
              <button id="deleteScene" type="button" class="btn btn-primary"><span style="font-size:larger;">Delete Scene</span></button>
              <button id="viewScnProperties" type="button" class="btn btn-primary"><span style="font-size:larger;">View Scene Properties</span></button>
              <button id="favoriteScene" type="button" class="btn btn-primary"><span style="font-size:larger;">Add to Favorites</span></button>
            </div>
          </div>
        </div>
      </div>
      <div class="row">
        <div class="col-md-12">
          <div class="btn-toolbar" role="toolbar" aria-label="Scene Group Toolbar" style="justify-content: center;">
            <div class="btn-group" role="group" aria-label="Scene Group Tools">
              <button style="display:none" id="viewSceneGroup" type="button" class="btn btn-primary"><span style="font-size:larger;">View Scene Group</span></button>
              <button style="display:none" id="viewAllScenes" type="button" class="btn btn-primary"><span style="font-size:larger;">View All Scenes</span></button>
              <button style="display:none" id="addToSceneGroup" type="button" class="btn btn-primary"><span style="font-size:larger;">Add to Scene Group</span></button>
              <button style="display:none" id="removeFromSceneGroup" type="button" class="btn btn-primary"><span style="font-size:larger;">Remove from Scene Group</span></button>
            </div>
          </div>
        </div>
      </div>
      <div class="row" style="height:50%;">
        <div class="col" style="height:100%;">
          <div id="sceneGrid" style="height:100%;" class="ag-theme-balham"></div>
        </div>
      </div>
      <div class="row">
        <div class="col-md-12">
          <div class="btn-toolbar pull-right" role="toolbar" aria-label="Scenes Pagination">
            <div class="btn-group mr-2" role="group" aria-label="Scene Pagination">
              <button id="firstPage" type="button" class="btn btn-secondary btn-sm">1</button>
              <button id="prevPage" type="button" class="btn btn-secondary btn-sm"><<</button>
              <button id="nextPage" type="button" class="btn btn-secondary btn-sm">>></button>
            </div>
          </div>
        </div>
      </div>
      <div class="row">
        <div class="col-md-12">
          <h1 style="text-align: center;">Objects</h1>
        </div>
      </div>
      <div class="row">
        <div class="col-md-12">
          <div class="btn-toolbar" role="toolbar" aria-label="Object Toolbar" style="justify-content: center;">
            <div class="btn-group" role="group" aria-label="Object Toolbar">
              <button id="editObject" type="button" class="btn btn-primary"><span style="font-size:larger;">Edit Object</span></button>
              <button id="createObject" type="button" class="btn btn-primary"><span style="font-size:larger;">Create Object</span></button>
              <button id="deleteObject" type="button" class="btn btn-primary"><span style="font-size:larger;">Delete Object</span></button>
              <button id="viewObjProperties" type="button" class="btn btn-primary"><span style="font-size:larger;">View Object Properties</span></button>
            </div>
          </div>
        </div>
      </div>
      <div class="row" style="height:50%;">
        <div class="col" style="height:100%;">
          <div id="objGrid" style="height:100%;" class="ag-theme-balham"></div>
        </div>
      </div>
      <footer class="footer">
          <p> &copy; 2018 AO Labs</p>
      </footer>
    </div>
    <script>
    // The scene, project, and/or scene group Keys are
    // injected here by the server before it returns the page
    var sceneKeys = "${sceneKeys}";
    var projectKey = "${projectKey}";
    var scnGroupName = "${scnGroupName}";
    var scnGroupDisplay = false;

    // specify the grid options for the Scene List
    var scnGridOptions = {
      columnDefs: scnColumnDefs,
      animateRows: true,
      rowData: [],
      enableFilter: true,
      rowSelection: 'single',
      pagination: true,
      suppressPaginationPanel: true,
      onRowDoubleClicked: updateObjectGridData,
      onFilterChanged: updateSceneGridData
    };

    function updateObjectGridData(event) {
      // Get selected data from list
      var selectedNodes = scnGridOptions.api.getSelectedNodes()
      var selectedData = selectedNodes.map( function(node) { return node.data })
      if (selectedData.length > 0) {
        var sceneKey = selectedData[0].key;

        // Create the object query data
        objListData = {num_records: 100, objects: [{scene: sceneKey}]};

        // Execute an HTTP call to get the available asset metadata
        // and populate it into the object list
        $.ajax({url: "v1/scene/" + sceneKey + "/object/query",
                type: 'POST',
                data: JSON.stringify(objListData),
                contentType: "application/json; charset=utf-8",
                success: obj_query_return});
      }
    };

    // Generate a Scene Query from the varioius input sources
    function generateFullSceneQuery() {
      var filterModel = scnGridOptions.api.getFilterModel();
      var sceneData = {}
      var queryKey = document.getElementById('keyinp').value;
      var queryName = document.getElementById('nameinp').value;
      var queryRegion = document.getElementById('regioninp').value;
      var queryLat = document.getElementById('latitudeinp').value;
      var queryLong = document.getElementById('longitudeinp').value;
      var queryDist = document.getElementById('distanceinp').value;
      var queryTag = document.getElementById('taginp').value;
      if (queryKey) sceneData["key"] = queryKey;
      if (queryName) sceneData["name"] = queryName;
      if (queryRegion) sceneData["region"] = queryRegion;
      if (queryLong) sceneData["longitude"] = queryLong;
      if (queryLat) sceneData["latitude"] = queryLat;
      if (queryDist) sceneData["distance"] = queryDist;
      if (queryTag) sceneData["tags"] = queryTag.split(",");
      if ("key" in filterModel) sceneData["key"] = filterModel.key.filter;
      if ("name" in filterModel) sceneData["name"] = filterModel.name.filter;
      if ("region" in filterModel) sceneData["region"] = filterModel.region.filter;
      if ("latitude" in filterModel) sceneData["latitude"] = filterModel.latitude.filter;
      if ("longitude" in filterModel) sceneData["longitude"] = filterModel.longitude.filter;
      return sceneData;
    }

    function updateSceneGridData(event) {
      console.log("Filter Updated:");

      // Create the scene query data
      sceneListData = {"scenes": []}
      sceneListData["num_records"] = scnGridOptions.api.paginationGetPageSize();
      sceneListData["start_record"] = (scnGridOptions.api.paginationGetCurrentPage() * scnGridOptions.api.paginationGetPageSize());

      if (sceneKeys != "" && scnGroupDisplay) {
        var sceneList = sceneKeys.split(",");
        for (var i = 0; i < sceneList.length; i++) {
          sceneListData["scenes"].push({"key": sceneList[i]});
        }
      } else {
        var sceneData = generateFullSceneQuery();
        sceneListData["scenes"].push(sceneData);
      }
      console.log(sceneListData);

      // Execute an HTTP call to get the available scenes
      // and populate it into the scene list
      $.ajax({url: "v1/scene/query",
              type: 'POST',
              cache: false,
              data: JSON.stringify(sceneListData),
              contentType: "application/json; charset=utf-8",
              success: scn_query_return});
    };

    function deleteSelectedObj() {
      var selectedRow = scnGridOptions.api.getSelectedNodes()[0].data;
      var selectedObj = objGridOptions.api.getSelectedNodes()[0].data;
      var sceneKey = selectedRow.key;
      var objKey = selectedObj.key;
      $.ajax({url: "v1/scene/" + sceneKey + "/object/" + objKey,
              type: 'DELETE',
              success: function(data) {
                console.log("Deleted Object");
                objGridOptions.api.updateRowData({remove: [selectedObj]});
              }});
    }

    function deleteSelectedScene() {
      var selectedNodes = scnGridOptions.api.getSelectedNodes()
      var selectedData = selectedNodes.map( function(node) { return node.data })
      if (selectedData.length > 0) {
        var sceneKey = selectedData[0].key;
        $.ajax({url: "v1/scene/" + sceneKey,
                type: 'DELETE',
                success: function(data) {
                  console.log("Deleted Scene");
                  scnGridOptions.api.updateRowData({remove: [selectedData[0]]});
                }});
      }
    }

    // Button Callback
    function onButtonClick(event) {
      console.log("Detected button click");

      // Logic to update the scene list based on the input query
      if (event.target.id == "firstPage") {
        scnGridOptions.api.paginationGoToPage(0);
        updateSceneGridData({});

      } else if (event.target.id == "prevPage") {
        var currentPage = scnGridOptions.api.paginationGetCurrentPage();
        scnGridOptions.api.paginationGoToPage(currentPage+1);
        updateSceneGridData({});

      } else if (event.target.id == "nextPage") {
        var currentPage = scnGridOptions.api.paginationGetCurrentPage();
        scnGridOptions.api.paginationGoToPage(currentPage-1);
        updateSceneGridData({});

      } else if (event.target.id == "query") {
        updateSceneGridData({});

      // CRUD Operations
      } else if (event.target.id == "createScene") {
        window.location.replace("editScene");

      } else if (event.target.id == "editScene") {
        var selectedRow = scnGridOptions.api.getSelectedNodes()[0].data;
        window.location.replace("editScene?key=" + selectedRow.key);

      } else if (event.target.id == "deleteScene") {
        var r = confirm("Delete the selected Scene?");
        if (r) {
          deleteSelectedScene();
        }

      } else if (event.target.id == "viewScnProperties") {
        var selectedRow = scnGridOptions.api.getSelectedNodes()[0].data;
        window.location.replace("propertyBrowser?scene=" + selectedRow.key);

      } else if (event.target.id == "favoriteScene") {
        var selectedRow = scnGridOptions.api.getSelectedNodes()[0].data;
        $.ajax({url: "/users/" + loggedInKey + "/scenes/" + selectedRow.key,
                type: 'PUT',
                success: function(data) {
                  console.log("Saved Favorite Scene");
                  displayAlert("success-alert");
                }});

      } else if (event.target.id == "createObject") {
        var selectedRow = scnGridOptions.api.getSelectedNodes()[0].data;
        window.location.replace("editObj?sceneKey=" + selectedRow.key);

      } else if (event.target.id == "editObject") {
        var selectedRow = scnGridOptions.api.getSelectedNodes()[0].data;
        var selectedObj = objGridOptions.api.getSelectedNodes()[0].data;
        window.location.replace("editObj?sceneKey=" + selectedRow.key + "&objKey=" + selectedObj.key);

      } else if (event.target.id == "viewObjProperties") {
        var selectedRow = scnGridOptions.api.getSelectedNodes()[0].data;
        var selectedObj = objGridOptions.api.getSelectedNodes()[0].data;
        window.location.replace("propertyBrowser?scene=" + selectedRow.key + "&object=" + selectedObj.key);

      } else if (event.target.id == "deleteObject") {
        var r = confirm("Delete the selected Object?");
        if (r) {
          deleteSelectedObj();
        }
      } else if (event.target.id == "viewSceneGroup") {
        scnGroupDisplay = true;
        updateObjectGridData({});
      } else if (event.target.id == "viewAllScenes") {
        scnGroupDisplay = false;
        updateObjectGridData({});
      } else if (event.target.id == "addToSceneGroup") {
        // Add the selected scene to the scene group
        var selectedRow = scnGridOptions.api.getSelectedNodes()[0].data;
        var url = "v1/project/" + projectKey + "/groups/" + scnGroupName + "/" + selectedRow.key;
        $.ajax({
          url: url,
          method: 'PUT',
          success: function(data) {
            console.log(data);
            displayAlert("success-alert");
          }
        });
      } else if (event.target.id == "removeFromSceneGroup") {
        // Remove the selected scene from the scene group
        // Add the selected scene to the scene group
        var selectedRow = scnGridOptions.api.getSelectedNodes()[0].data;
        var url = "v1/project/" + projectKey + "/groups/" + scnGroupName + "/" + selectedRow.key;
        $.ajax({
          url: url,
          method: 'DELETE',
          success: function(data) {
            console.log(data);
            displayAlert("success-alert");
          }
        });
      }
    }

    // specify the grid options for the Object List
    var objGridOptions = {
      columnDefs: objColumnDefs,
      animateRows: true,
      rowData: [],
      enableSorting: true,
      enableFilter: true,
      rowSelection: 'single'
    };

    // Add the Scene grid to the page
    var scnGridDiv = document.querySelector('#sceneGrid');
    new agGrid.Grid(scnGridDiv, scnGridOptions);

    // Add the Object grid to the page
    var objGridDiv = document.querySelector('#objGrid');
    new agGrid.Grid(objGridDiv, objGridOptions);

    // Callback to populate the scene table
    var scn_query_return = function(data) {
      console.log("Updating Scene Table Data")
      console.log(data.scenes)
      scnGridOptions.api.setRowData(data.scenes);
    }

    // Callback to populate the object table
    var obj_query_return = function(data) {
      console.log("Updating Object Table Data");
      console.log(data.objects)
      objGridOptions.api.setRowData(data.objects);
    }

    // Main page, added once DOM is loaded into browser
    window.addEventListener('DOMContentLoaded', function(){
      // The User ID is injected here by the server before
      // it returns the page
      var loggedInUser = "${userName}";
      var loggedInKey = "${userId}";
      console.log(loggedInUser);
      // If the user is an admin, then the server will inject 'true' here,
      // otherwise, it will inject 'false'.
      var isUserAdmin = "${isAdmin}";
      setUsersLink((isUserAdmin == 'true'), "userBrowserLink", loggedInKey);

      // If we have been given a list of scenes
      // then we want to hide the query inputs
      if (sceneKeys != "") {
        hideHtmlElements([
          "keyinp",
          "nameinp",
          "regioninp",
          "latitudeinp",
          "longitudeinp",
          "distanceinp",
          "taginp",
          "query"
        ]);
        scnGroupDisplay = true;
      }

      // If we have been given a scene group from a project, then
      // we want to show buttons for editing the scene group
      if (projectKey != "" && scnGroupName != "") {
        document.getElementById('pageHeader').innerHTML = scnGroupName;
        showHtmlElements([
          "viewSceneGroup",
          "viewAllScenes",
          "addToSceneGroup",
          "removeFromSceneGroup"
        ]);
      }

      // Fill the Scene Grid
      updateSceneGridData({});

      // Setup the button callbacks
      registerButtonCallback(onButtonClick);
    });
    </script>
  </body>
</html>
