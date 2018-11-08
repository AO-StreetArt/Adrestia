<!DOCTYPE html>
<html>
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
      <div class="row" style="height:40%;">
        <div class="col">
          <input id="keyinp" type="text" name="ID" style="z-index:265" placeholder="ID"></input>
          <input id="contenttypeinp" type="text" name="Content Type" style="z-index:265" placeholder="Content Type"></input>
          <input id="filetypeinp" type="text" name="File Type" style="z-index:265" placeholder="File Type"></input>
          <input id="assettypeinp" type="text" name="Asset Type" style="z-index:265" placeholder="Asset Type"></input>
          <button id="query" style="z-index:265">Query</button>
          <button id="load" style="z-index:265">View in Browser</button>
          <button id="download" style="z-index:265">Download</button>
        </div>
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
      {headerName: "ID", field: "key", pinned: 'left'},
      {headerName: "Name", field: "name", pinned: 'left'},
      {headerName: "Description", field: "description"},
      {headerName: "Content Type", field: "contentType"},
      {headerName: "File Type", field: "fileType"},
      {headerName: "Asset Type", field: "assetType"},
      {headerName: "Created", field: "createdTimestamp"}
    ];

    // specify the grid options
    var gridOptions = {
      columnDefs: columnDefs,
      rowData: [],
      enableSorting: true,
      enableFilter: true,
      rowSelection: 'multiple',
      pagination: true
    };

    // lookup the container we want the Grid to use
    var eGridDiv = document.querySelector('#myGrid');

    // create the grid passing in the div to use together with the columns & data we want to use
    new agGrid.Grid(eGridDiv, gridOptions);

    // let the grid know which columns and what data to use
    var query_return = function(data) {
      console.log("Updating Table Data")
      gridOptions.api.setRowData(data);
    }

    // The Active Asset in the browser
    var activeAsset = null;

    // Button Callback
    function onButtonClick(event) {
      console.log("Detected button click");

      // Logic to update the list based on the input query
      if (event.target.id == "query") {
        var queryKey = document.getElementById('keyinp').value;
        var queryCt = document.getElementById('contenttypeinp').value;
        var queryFt = document.getElementById('filetypeinp').value;
        var queryAt = document.getElementById('assettypeinp').value;
        // Execute an HTTP call to get the available asset metadata
        // and populate it into the list
        query_params = {}
        if (queryKey) query_params["key"] = queryKey;
        if (queryCt) query_params["content-type"] = queryCt;
        if (queryFt) query_params["file-type"] = queryFt;
        if (queryAt) query_params["asset-type"] = queryAt;
        $.ajax({url: "v1/asset", data: query_params, success: query_return});

      // Logic based on the selected element in list
      } else {
        // Get selected data from list
        var selectedNodes = gridOptions.api.getSelectedNodes()
        var selectedData = selectedNodes.map( function(node) { return node.data })
        if (selectedData.length > 0) {
          var assetId = selectedData[0]["key"]
          var assetFileType = selectedData[0]["fileType"]
          console.log(assetId)

          // Logic to load the asset into the viewer
          if (event.target.id == "load") {
            // Replace the information stored in the Babylon scene
            activeAsset.dispose();
            BABYLON.SceneLoader.Append("/v1/asset/" + assetId + "/", "file." + assetFileType, scene, function (scene) {
              console.log("Asset Loaded")
            });

          // Logic to download the asset
          } else if (event.target.id == "download") {
            window.open("/v1/asset/" + assetId, "_blank");
          }
        }
      }

    }

    // Main page, added once DOM is loaded into browser
    window.addEventListener('DOMContentLoaded', function(){

      // Execute an HTTP call to get the available asset metadata
      // and populate it into the list
      $.ajax({url: "v1/asset", data: {}, success: query_return});

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
