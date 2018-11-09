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
    <style>
    html, body {
      overflow: hidden;
      width   : 100%;
      height  : 100%;
      margin  : 0;
      padding : 0;
    }
    </style>
  </head>
  <body>
    <div class="container-fluid" style="height:100%;">
      <div class="row">
        <h1 style="text-align: center;">Asset Browser</h1>
      </div>
      <!-- TO-DO: Add another table, so we have one for scenes and one for objects -->
      <div class="row" style="height:50%;">
        <div class="col">
          <input id="keyinp" type="text" name="ID" style="z-index:265" placeholder="ID"></input>
          <input id="nameinp" type="text" name="Name" style="z-index:265" placeholder="Name"></input>
          <input id="regioninp" type="text" name="Region" style="z-index:265" placeholder="Region"></input>
          <input id="latitudeinp" type="text" name="Latitude" style="z-index:265" placeholder="Latitude"></input>
          <input id="longitudeinp" type="text" name="Longitude" style="z-index:265" placeholder="Longitude"></input>
          <input id="distanceinp" type="text" name="Distance" style="z-index:265" placeholder="Distance"></input>
          <button id="query" style="z-index:265">Query</button>
          <button id="load" style="z-index:265">View Objects</button>
        </div>
        <div class="col" style="height:100%;">
          <div id="sceneGrid" style="height:100%;" class="ag-theme-balham"></div>
        </div>
      </div>
      <div class="row" style="height:40%;">
        <div class="col" style="height:100%;">
          <div id="objGrid" style="height:100%;" class="ag-theme-balham"></div>
        </div>
      </div>
      <footer class="footer">
          <p> &copy; 2018 AO Labs</p>
      </footer>
    </div>
    <script>
    console.log("Creating Tables");

    // specify the columns for the scene table
    var scnColumnDefs = [
      {headerName: "ID", field: "key", pinned: 'left'},
      {headerName: "Name", field: "name", pinned: 'left'},
      {headerName: "Region", field: "region"},
      {headerName: "Latitude", field: "latitude"},
      {headerName: "Longitude", field: "longitude"}
    ];

    // specify the columns for the object table
    var objColumnDefs = [
      {headerName: "ID", field: "key", pinned: 'left'},
      {headerName: "Name", field: "name", pinned: 'left'},
      {headerName: "Type", field: "type"},
      {headerName: "Subtype", field: "subtype"},
      {headerName: "Owner", field: "owner"},
      {headerName: "Frame", field: "frame"}
    ];

    // specify the grid options for the Scene List
    var scnGridOptions = {
      columnDefs: scnColumnDefs,
      rowData: [],
      enableSorting: true,
      enableFilter: true,
      rowSelection: 'multiple',
      pagination: true
    };

    // specify the grid options for the Object List
    var objGridOptions = {
      columnDefs: objColumnDefs,
      rowData: [],
      enableSorting: true,
      enableFilter: true,
      rowSelection: 'multiple',
      pagination: true
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
      scnGridOptions.api.setRowData(data.scenes);
    }

    // Callback to populate the object table
    var obj_query_return = function(data) {
      console.log("Updating Object Table Data")
      scnGridOptions.api.setRowData(data.objects);
    }

    // Button Callback
    function onButtonClick(event) {
      console.log("Detected button click");

      // Logic to update the scene list based on the input query
      if (event.target.id == "query") {
        var queryKey = document.getElementById('keyinp').value;
        var queryName = document.getElementById('nameinp').value;
        var queryRegion = document.getElementById('regioninp').value;
        var queryLat = document.getElementById('latitudeinp').value;
        var queryLong = document.getElementById('longitudeinp').value;
        var queryDist = document.getElementById('distanceinp').value;

        // Create the scene query data
        sceneListData = {num_records: 100, scenes: []}
        sceneData = {}
        if (queryKey) query_params["key"] = queryKey;
        if (queryName) query_params["name"] = queryName;
        if (queryRegion) query_params["region"] = queryRegion;
        if (queryLong) query_params["longitude"] = queryLong;
        if (queryLat) query_params["latitutde"] = queryLat;
        if (queryDist) query_params["distance"] = queryDist;
        sceneListData["scenes"].push(sceneData);

        // Execute an HTTP call to get the available asset metadata
        // and populate it into the scene list
        $.ajax({url: "v1/scene/query", type: 'POST', data: sceneListData, success: scn_query_return});

      // Load the objects in the selected scene into the object list
      } else if (event.target.id == "load") {
        // Get selected data from list
        var selectedNodes = gridOptions.api.getSelectedNodes()
        var selectedData = selectedNodes.map( function(node) { return node.data })
        if (selectedData.length > 0) {
          var sceneKey = selectedData[0].key;

          // Create the object query data
          objListData = {num_records: 100, objects: [{key: sceneKey}]}

          // Execute an HTTP call to get the available asset metadata
          // and populate it into the object list
          $.ajax({url: "v1/object/query", type: 'POST', data: objListData, success: obj_query_return});
        }
      }
    }

    // Main page, added once DOM is loaded into browser
    window.addEventListener('DOMContentLoaded', function(){

      // Create the scene query data
      sceneListData = {num_records: 100, scenes: []}
      sceneData = {}
      sceneListData["scenes"].push(sceneData);

      // Execute an HTTP call to get the available asset metadata
      // and populate it into the list
      $.ajax({url: "v1/scene/query", type: 'POST', data: query_params, success: query_return});

      // Setup the button callbacks into the Javascript
      var buttons = document.getElementsByTagName("button");
      for (let i = 0; i < buttons.length; i++) {
        buttons[i].addEventListener("click", onButtonClick, false);
      };
    });
    </script>
  </body>
</html>
