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
  <head>
    <meta http-equiv="Content-Type" content="text/html" charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <title>Aesel Asset Browser</title>
    <!--- Ag-grid-community --->
    <script src="https://unpkg.com/ag-grid-community/dist/ag-grid-community.min.noStyle.js"></script>
    <link rel="stylesheet" href="https://unpkg.com/ag-grid-community/dist/styles/ag-grid.css">
    <link rel="stylesheet" href="https://unpkg.com/ag-grid-community/dist/styles/ag-theme-balham.css">
    <!--- BabylonJS --->
    <script src="https://cdn.babylonjs.com/babylon.js"></script>
    <script src="https://preview.babylonjs.com/loaders/babylonjs.loaders.min.js"></script>
    <style>
    #renderCanvas {
      width   : 100%;
      height  : 100%;
      touch-action: none;
    }
    </style>
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
              <a class="nav-link" href="/sceneBrowser">Scenes</a>
            </li>
            <li class="nav-item" id="assetBrowser">
              <a class="nav-link active" href="#">Assets <span class="sr-only">(current)</span></a>
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
          <h1 style="text-align: center;">Asset Browser</h1>
        </div>
      </div>
      <div class="row" style="height:50%;">
        <canvas id="renderCanvas"></canvas>
      </div>
      <div class="row">
        <div class="col-md-12">
          <div class="btn-toolbar" role="toolbar" aria-label="Asset Toolbar" style="justify-content: center;">
            <div class="btn-group" role="group" aria-label="Asset Toolbar">
              <button id="view" type="button" class="btn btn-primary" style="z-index:265"><span style="font-size:larger;">View</span></button>
              <button id="download" type="button" class="btn btn-primary" style="z-index:265"><span style="font-size:larger;">Download</span></button>
              <button id="edit" type="button" class="btn btn-primary" style="z-index:265"><span style="font-size:larger;">Edit Asset</span></button>
              <button id="create" type="button" class="btn btn-primary" style="z-index:265"><span style="font-size:larger;">Create Asset</span></button>
              <button id="delete" type="button" class="btn btn-primary" style="z-index:265"><span style="font-size:larger;">Delete Asset</span></button>
            </div>
          </div>
        </div>
      </div>
      <div class="row" style="height:40%;">
        <div class="col" style="height:100%;">
          <div id="myGrid" style="height:100%;" class="ag-theme-balham"></div>
        </div>
      </div>
      <div class="row">
        <div class="col-md-12">
          <div class="btn-toolbar pull-right" role="toolbar" aria-label="Asset Toolbar">
            <div class="btn-group" role="group" aria-label="Asset Pagination">
              <button id="firstPage" type="button" class="btn btn-secondary btn-sm" style="z-index:265">1</button>
              <button id="prevPage" type="button" class="btn btn-secondary btn-sm" style="z-index:265"><<</button>
              <button id="nextPage" type="button" class="btn btn-secondary btn-sm" style="z-index:265">>></button>
            </div>
          </div>
        </div>
      </div>
      <footer class="footer">
          <p> &copy; 2018 AO Labs</p>
      </footer>
    </div>
    <script>
    // Global WegGL variables to access in callbacks
    var canvas = null;
    var engine = null;
    var scene = null;
    var activeSprite = null;
    var activeSpriteManager = null;

    console.log("Creating Table");

    // specify the columns
    var columnDefs = [
      {
        headerName: "ID",
        field: "key",
        pinned: 'left',
        filter: "agTextColumnFilter",
        filterParams: { applyButton: true, clearButton:true, filterOptions:["equals"], suppressAndOrCondition:true, caseSensitive:true },
        editable: false
      },
      {
        headerName: "Name",
        field: "name",
        pinned: 'left',
        filter: "agTextColumnFilter",
        filterParams: { applyButton: true, clearButton:true, filterOptions:["equals"], suppressAndOrCondition:true, caseSensitive:true },
        editable: false
      },
      {
        headerName: "Content Type",
        field: "contentType",
        filter: "agTextColumnFilter",
        filterParams: { applyButton: true, clearButton:true, filterOptions:["equals"], suppressAndOrCondition:true, caseSensitive:true },
        editable: false
      },
      {
        headerName: "File Type",
        field: "fileType",
        filter: "agTextColumnFilter",
        filterParams: { applyButton: true, clearButton:true, filterOptions:["equals"], suppressAndOrCondition:true, caseSensitive:true },
        editable: false
      },
      {
        headerName: "Asset Type",
        field: "assetType",
        filter: "agTextColumnFilter",
        filterParams: { applyButton: true, clearButton:true, filterOptions:["equals"], suppressAndOrCondition:true, caseSensitive:true },
        editable: false
      },
      {
        headerName: "Created",
        field: "createdTimestamp",
        suppressFilter: true,
        editable: false
      }
    ];

    function viewSelectedAsset() {
      // Get selected data from list
      var selectedNodes = gridOptions.api.getSelectedNodes()
      var selectedData = selectedNodes.map( function(node) { return node.data })
      if (selectedData.length > 0) {
        var assetId = selectedData[0]["key"]
        var assetFileType = selectedData[0]["fileType"]
        console.log(assetId)
        // Replace the information stored in the Babylon scene
        for (let i = scene.meshes.length - 1; i >= 0; i--) {
          scene.meshes[i].dispose();
        }
        if (activeSprite != null) {
          activeSprite.dispose();
          activeSprite = null;
          activeSpriteManager.dispose();
          activeSpriteManager = null;
        }
        if (assetFileType == "obj") {
          BABYLON.SceneLoader.Append("/v1/asset/" + assetId + "/", "file." + assetFileType, scene, function (scene) {
            console.log("Asset Loaded")
          });
        } else if (assetFileType == "png" || assetFileType == "jpg" || assetFileType == "jpeg") {
          activeSpriteManager = new BABYLON.SpriteManager("imageAssetManager", "/v1/asset/" + assetId + "/file" + assetFileType, 1, 800, scene);
          activeSprite = new BABYLON.Sprite("imageAsset", activeSpriteManager);
          activeSprite.position.x = 0
          activeSprite.position.y = 0
          activeSprite.position.z = 0
          activeSprite.width = 5;
          activeSprite.height = 5;
        }
      }
    }

    // specify the grid options
    var gridOptions = {
      animateRows: true,
      columnDefs: columnDefs,
      rowData: [],
      enableFilter: true,
      rowSelection: 'single',
      pagination: true,
      suppressPaginationPanel: true,
      onRowDoubleClicked: function(event) {
        console.log("Row Double Clicked:");
        viewSelectedAsset();
      },
      onFilterChanged: updateGridData
    };

    function updateGridData(event) {
      var filterModel = gridOptions.api.getFilterModel();
      query_params = {}
      if ("key" in filterModel) query_params["key"] = filterModel.key.filter;
      if ("name" in filterModel) query_params["name"] = filterModel.name.filter;
      if ("contentType" in filterModel) query_params["contentType"] = filterModel.contentType.filter;
      if ("fileType" in filterModel) query_params["fileType"] = filterModel.fileType.filter;
      if ("assetType" in filterModel) query_params["assetType"] = filterModel.assetType.filter;
      query_params["limit"] = gridOptions.api.paginationGetPageSize();
      query_params["offset"] = gridOptions.api.paginationGetCurrentPage() * gridOptions.api.paginationGetPageSize();
      // Execute an HTTP call to get the available asset metadata
      // and populate it into the list
      console.log("Sending Query with parameters:");
      console.log(query_params);
      $.ajax({url: "v1/asset", data: query_params, success: query_return});
    }

    // lookup the container we want the Grid to use
    var eGridDiv = document.querySelector('#myGrid');

    // create the grid passing in the div to use together with the columns & data we want to use
    new agGrid.Grid(eGridDiv, gridOptions);

    // let the grid know which columns and what data to use
    var query_return = function(data) {
      console.log("Updating Table Data")
      console.log(data);
      gridOptions.api.setRowData(data);
    }

    function delete_selected() {
      var selectedNodes = gridOptions.api.getSelectedNodes()
      var selectedData = selectedNodes.map( function(node) { return node.data })
      if (selectedData.length > 0) {
        var assetKey = selectedData[0].key;
        $.ajax({url: "v1/asset/" + assetKey,
                type: 'DELETE',
                success: function(data) {
                  console.log("Deleted Asset");
                  gridOptions.api.updateRowData({remove: [selectedData[0]]});
                }});
      }
    }

    // The Active Asset in the browser
    var activeAsset = null;

    // Button Callback
    function onButtonClick(event) {
      console.log("Detected button click");
      if (event.target.id == "download") {
        // Get selected data from list
        var selectedNodes = gridOptions.api.getSelectedNodes()
        var selectedData = selectedNodes.map( function(node) { return node.data })
        if (selectedData.length > 0) {
          var assetId = selectedData[0]["key"]
          var assetFileType = selectedData[0]["fileType"]
          console.log(assetId)
            window.open("/v1/asset/" + assetId, "_blank");
        }
      } else if (event.target.id == "create") {
        window.location.replace("editAsset");
      } else if (event.target.id == "edit") {
        var selectedRow = gridOptions.api.getSelectedNodes()[0].data;
        window.location.replace("editAsset?key=" + selectedRow.key);
      } else if (event.target.id == "view") {
        viewSelectedAsset();
      } else {
        // Logic to update the scene list based on the input query
        if (event.target.id == "firstPage") {
          gridOptions.api.paginationGoToPage(0);
        } else if (event.target.id == "prevPage") {
          var currentPage = gridOptions.api.paginationGetCurrentPage();
          gridOptions.api.paginationGoToPage(currentPage+1);
        } else if (event.target.id == "nextPage") {
          var currentPage = gridOptions.api.paginationGetCurrentPage();
          gridOptions.api.paginationGoToPage(currentPage-1);
        } else if (event.target.id == "delete") {
          var r = confirm("Delete the selected asset?");
          if (r) {
            delete_selected();
          }
        }
        updateGridData({});
      }
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
      var adminLoggedIn = (isUserAdmin == 'true');
      setUsersLink((isUserAdmin == 'true'), "userBrowserLink", loggedInKey)

      // Execute an HTTP call to get the available asset metadata
      // and populate it into the list
      updateGridData({});

      // Setup the button callbacks into the Javascript
      registerButtonCallback(onButtonClick);

      // get the canvas DOM element
      canvas = document.getElementById('renderCanvas');

      // load the 3D engine
      engine = new BABYLON.Engine(canvas, true);

      // create a basic BJS Scene object
      scene = new BABYLON.Scene(engine);

      // create a FreeCamera, and set its position to (x:0, y:5, z:-10)
      var camera = new BABYLON.FreeCamera('camera1', new BABYLON.Vector3(0, 5,-10), scene);

      // target the camera to scene origin
      camera.setTarget(BABYLON.Vector3.Zero());

      // attach the camera to the canvas
      camera.attachControl(canvas, false);

      // create a basic light, aiming 0,1,0 - meaning, to the sky
      var light = new BABYLON.HemisphericLight('light1', new BABYLON.Vector3(0,1,0), scene);

      // create a built-in "sphere" shape; its constructor takes 6 params: name, segment, diameter, scene, updatable, sideOrientation
      activeAsset = BABYLON.Mesh.CreateSphere('sphere1', 16, 2, scene);

      // move the sphere upward 1/2 of its height
      activeAsset.position.y = 1;

      // run the render loop
      engine.runRenderLoop(function(){
        scene.render();
      });

      // the canvas/window resize event handler
      window.addEventListener('resize', function(){
        engine.resize();
      });
    });
    </script>
  </body>
</html>
