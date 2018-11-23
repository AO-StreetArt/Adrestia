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

  <head>
    <meta http-equiv="Content-Type" content="text/html" charset="utf-8"/>
    <title>Aesel Asset Browser</title>
    <!--- Ag-grid-community --->
    <script src="https://unpkg.com/ag-grid-community/dist/ag-grid-community.min.noStyle.js"></script>
    <link rel="stylesheet" href="https://unpkg.com/ag-grid-community/dist/styles/ag-grid.css">
    <link rel="stylesheet" href="https://unpkg.com/ag-grid-community/dist/styles/ag-theme-balham.css">
    <!--- BabylonJS --->
    <script src="https://cdn.babylonjs.com/babylon.js"></script>
    <script src="https://preview.babylonjs.com/loaders/babylonjs.loaders.min.js"></script>
    <style>
    html, body {
      overflow: hidden;
      width   : 100%;
      height  : 100%;
      margin  : 0;
      padding : 0;
    }

    #renderCanvas {
      width   : 100%;
      height  : 100%;
      touch-action: none;
    }
    </style>
  </head>
  <body>
    <div class="container-fluid" style="height:100%;">
      <div class="row">
        <h1 style="text-align: center;">Asset Browser</h1>
      </div>
      <div class="row" style="height:50%;">
        <canvas id="renderCanvas"></canvas>
      </div>
      <div class="row">
        <div class="col align-self-center">
          <button id="firstPage" style="z-index:265">First Page</button>
          <button id="prevPage" style="z-index:265">Previous Page</button>
          <button id="nextPage" style="z-index:265">Next Page</button>
          <button id="download" style="z-index:265">Download</button>
          <button id="edit" style="z-index:265">Edit Asset</button>
          <button id="create" style="z-index:265">Create Asset</button>
          <button id="delete" style="z-index:265">Delete Asset</button>
        </div>
      </div>
      <div class="row" style="height:40%;">
        <div class="col" style="height:100%;">
          <div id="myGrid" style="height:100%;" class="ag-theme-balham"></div>
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
        headerName: "Description",
        field: "description",
        suppressFilter: true,
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
        // Get selected data from list
        var selectedNodes = gridOptions.api.getSelectedNodes()
        var selectedData = selectedNodes.map( function(node) { return node.data })
        if (selectedData.length > 0) {
          var assetId = selectedData[0]["key"]
          var assetFileType = selectedData[0]["fileType"]
          console.log(assetId)
          // Replace the information stored in the Babylon scene
          activeAsset.dispose();
          BABYLON.SceneLoader.Append("/v1/asset/" + assetId + "/", "file." + assetFileType, scene, function (scene) {
            console.log("Asset Loaded")
          });
        }
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
          delete_selected();
        }
        updateGridData({});
      }
    }

    // Main page, added once DOM is loaded into browser
    window.addEventListener('DOMContentLoaded', function(){

      // Execute an HTTP call to get the available asset metadata
      // and populate it into the list
      updateGridData({});

      // Setup the button callbacks into the Javascript
      var buttons = document.getElementsByTagName("button");
      for (let i = 0; i < buttons.length; i++) {
        buttons[i].addEventListener("click", onButtonClick, false);
      };

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
