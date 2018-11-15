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
        <h1 style="text-align: center;">Scene Browser</h1>
      </div>
      <div class="row">
        <div class="col align-self-left">
          <input id="keyinp" type="text" name="ID" style="z-index:265" placeholder="ID"></input>
          <input id="nameinp" type="text" name="Name" style="z-index:265" placeholder="Name"></input>
          <input id="regioninp" type="text" name="Region" style="z-index:265" placeholder="Region"></input>
          <input id="latitudeinp" type="text" name="Latitude" style="z-index:265" placeholder="Latitude"></input>
          <input id="longitudeinp" type="text" name="Longitude" style="z-index:265" placeholder="Longitude"></input>
          <input id="distanceinp" type="text" name="Distance" style="z-index:265" placeholder="Distance"></input>
          <button id="query" style="z-index:265">Query</button>
        </div>
        <div class="col align-self-center">
          <button id="firstPage" style="z-index:265">First Page</button>
          <button id="prevPage" style="z-index:265">Previous Page</button>
          <button id="nextPage" style="z-index:265">Next Page</button>
        </div>
      </div>
      <div class="row" style="height:50%;">
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
        headerName: "Region",
        field: "region",
        filter: "agTextColumnFilter",
        filterParams: { applyButton: true, clearButton:true, filterOptions:["equals"], suppressAndOrCondition:true, caseSensitive:true },
        editable: false
      },
      {
        headerName: "Latitude",
        field: "latitude",
        filter: "agTextColumnFilter",
        filterParams: { applyButton: true, clearButton:true, filterOptions:["equals"], suppressAndOrCondition:true, caseSensitive:true },
        editable: false
      },
      {
        headerName: "Longitude",
        field: "longitude",
        filter: "agTextColumnFilter",
        filterParams: { applyButton: true, clearButton:true, filterOptions:["equals"], suppressAndOrCondition:true, caseSensitive:true },
        editable: false
      }
    ];

    // specify the columns for the object table
    var objColumnDefs = [
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
        headerName: "Type",
        field: "type",
        filter: "agTextColumnFilter",
        filterParams: { applyButton: true, clearButton:true, filterOptions:["equals"], suppressAndOrCondition:true, caseSensitive:true },
        editable: false
      },
      {
        headerName: "Subtype",
        field: "subtype",
        filter: "agTextColumnFilter",
        filterParams: { applyButton: true, clearButton:true, filterOptions:["equals"], suppressAndOrCondition:true, caseSensitive:true },
        editable: false
      },
      {
        headerName: "Owner",
        field: "owner",
        filter: "agTextColumnFilter",
        filterParams: { applyButton: true, clearButton:true, filterOptions:["equals"], suppressAndOrCondition:true, caseSensitive:true },
        editable: false
      },
      {
        headerName: "Frame",
        field: "frame",
        filter: "agTextColumnFilter",
        filterParams: { applyButton: true, clearButton:true, filterOptions:["equals"], suppressAndOrCondition:true, caseSensitive:true },
        editable: false
      }
    ];

    // specify the grid options for the Scene List
    var scnGridOptions = {
      columnDefs: scnColumnDefs,
      animateRows: true,
      rowData: [],
      enableFilter: true,
      rowSelection: 'single',
      pagination: true,
      suppressPaginationPanel: true,
      onRowDoubleClicked: function(event) {
        console.log("Row Double Clicked:");
        // Get selected data from list
        var selectedNodes = scnGridOptions.api.getSelectedNodes()
        var selectedData = selectedNodes.map( function(node) { return node.data })
        if (selectedData.length > 0) {
          var sceneKey = selectedData[0].key;

          // Create the object query data
          objListData = {num_records: 100, objects: [{key: sceneKey}]}

          // Execute an HTTP call to get the available asset metadata
          // and populate it into the object list
          $.ajax({url: "v1/scene/" + sceneKey + "/object/query",
                  type: 'POST',
                  data: JSON.stringify(objListData),
                  contentType: "application/json; charset=utf-8",
                  success: obj_query_return});
        }
      },
      onFilterChanged: updateSceneGridData
    };

    function updateSceneGridData(event) {
      console.log("Filter Updated:");
      var filterModel = scnGridOptions.api.getFilterModel();
      sceneData = {}
      var queryKey = document.getElementById('keyinp').value;
      var queryName = document.getElementById('nameinp').value;
      var queryRegion = document.getElementById('regioninp').value;
      var queryLat = document.getElementById('latitudeinp').value;
      var queryLong = document.getElementById('longitudeinp').value;
      var queryDist = document.getElementById('distanceinp').value;
      if (queryKey) sceneData["key"] = queryKey;
      if (queryName) sceneData["name"] = queryName;
      if (queryRegion) sceneData["region"] = queryRegion;
      if (queryLong) sceneData["longitude"] = queryLong;
      if (queryLat) sceneData["latitutde"] = queryLat;
      if (queryDist) sceneData["distance"] = queryDist;
      if ("key" in filterModel) sceneData["key"] = filterModel.key.filter;
      if ("name" in filterModel) sceneData["name"] = filterModel.name.filter;
      if ("region" in filterModel) sceneData["region"] = filterModel.region.filter;
      if ("latitude" in filterModel) sceneData["latitude"] = filterModel.latitude.filter;
      if ("longitude" in filterModel) sceneData["longitude"] = filterModel.longitude.filter;
      sceneData["num_records"] = scnGridOptions.api.paginationGetPageSize();
      sceneData["start_record"] = (scnGridOptions.api.paginationGetCurrentPage() * scnGridOptions.api.paginationGetPageSize()) + scnGridOptions.api.paginationGetPageSize();

      // Create the scene query data
      sceneListData = {"num_records": 100, "scenes": []}
      sceneListData["scenes"].push(sceneData);

      // Execute an HTTP call to get the available scenes
      // and populate it into the scene list
      $.ajax({url: "v1/scene/query",
              type: 'POST',
              data: JSON.stringify(sceneListData),
              contentType: "application/json; charset=utf-8",
              success: scn_query_return});
    };

    // Button Callback
    function onButtonClick(event) {
      console.log("Detected button click");

      // Logic to update the scene list based on the input query
      if (event.target.id == "firstPage") {
        scnGridOptions.api.paginationGoToPage(0);
      } else if (event.target.id == "prevPage") {
        var currentPage = scnGridOptions.api.paginationGetCurrentPage();
        scnGridOptions.api.paginationGoToPage(currentPage+1);
      } else if (event.target.id == "nextPage") {
        var currentPage = scnGridOptions.api.paginationGetCurrentPage();
        scnGridOptions.api.paginationGoToPage(currentPage-1);
      }
      updateSceneGridData({});
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
      scnGridOptions.api.setRowData(data.scenes);
    }

    // Callback to populate the object table
    var obj_query_return = function(data) {
      console.log("Updating Object Table Data")
      objGridOptions.api.setRowData(data.objects);
    }

    // Main page, added once DOM is loaded into browser
    window.addEventListener('DOMContentLoaded', function(){

      updateSceneGridData({});

      // Setup the button callbacks into the Javascript
      var buttons = document.getElementsByTagName("button");
      for (let i = 0; i < buttons.length; i++) {
        buttons[i].addEventListener("click", onButtonClick, false);
      };
    });
    </script>
  </body>
</html>
