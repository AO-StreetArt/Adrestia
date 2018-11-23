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
    <title>Aesel Scene Browser</title>
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
    <div class="pre-scrollable" style="height:100%;max-height: 100%;">
    <div class="container-fluid" style="height:100%;">
      <div class="row">
        <h1 style="text-align: center;">Scene Browser</h1>
      </div>
      <div class="row">
        <div class="col align-self-left">
          <input id="keyinp" type="text" name="ID" placeholder="ID"></input>
          <input id="nameinp" type="text" name="Name" placeholder="Name"></input>
          <input id="regioninp" type="text" name="Region" placeholder="Region"></input>
          <input id="latitudeinp" type="text" name="Latitude" placeholder="Latitude"></input>
          <input id="longitudeinp" type="text" name="Longitude" placeholder="Longitude"></input>
          <input id="distanceinp" type="text" name="Distance" placeholder="Distance"></input>
          <input id="taginp" type="text" name="Tag" placeholder="Tag"></input>
          <button id="query">Query</button>
        </div>
        <div class="col align-self-center">
          <button id="firstPage">First Page</button>
          <button id="prevPage">Previous Page</button>
          <button id="nextPage">Next Page</button>
        </div>
        <div class="col align-self-right">
          <button id="editScene">Edit Scene</button>
          <button id="createScene">Create Scene</button>
          <button id="deleteScene">Delete Scene</button>
          <button id="editObject">Edit Object</button>
          <button id="createObject">Create Object</button>
          <button id="deleteObject">Delete Object</button>
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
    </div>
    <script>
    // The Scene Key is injected here by the server before
    // it returns the page
    var sceneKeys = "${sceneKeys}";

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
      var queryTag = document.getElementById('taginp').value;
      if (queryKey) sceneData["key"] = queryKey;
      if (queryName) sceneData["name"] = queryName;
      if (queryRegion) sceneData["region"] = queryRegion;
      if (queryLong) sceneData["longitude"] = queryLong;
      if (queryLat) sceneData["latitutde"] = queryLat;
      if (queryDist) sceneData["distance"] = queryDist;
      if (queryTag) sceneData["tags"] = [queryTag];
      if ("key" in filterModel) sceneData["key"] = filterModel.key.filter;
      if ("name" in filterModel) sceneData["name"] = filterModel.name.filter;
      if ("region" in filterModel) sceneData["region"] = filterModel.region.filter;
      if ("latitude" in filterModel) sceneData["latitude"] = filterModel.latitude.filter;
      if ("longitude" in filterModel) sceneData["longitude"] = filterModel.longitude.filter;

      // Create the scene query data
      sceneListData = {"scenes": []}
      sceneListData["num_records"] = scnGridOptions.api.paginationGetPageSize();
      sceneListData["start_record"] = (scnGridOptions.api.paginationGetCurrentPage() * scnGridOptions.api.paginationGetPageSize()) + scnGridOptions.api.paginationGetPageSize();

      if (sceneKeys == "") {
        sceneListData["scenes"].push(sceneData);
      } else {
        var sceneList = sceneKeys.split(",");
        for (var i = 0; i < sceneList.length; i++) {
          sceneListData["scenes"].push({"key": sceneList[i]});
        }
      }

      // Execute an HTTP call to get the available scenes
      // and populate it into the scene list
      $.ajax({url: "v1/scene/query",
              type: 'POST',
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
                }});
      }
    }

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
      } else if (event.target.id == "createScene") {
        window.location.replace("editScene");
      } else if (event.target.id == "editScene") {
        var selectedRow = scnGridOptions.api.getSelectedNodes()[0].data;
        window.location.replace("editScene?key=" + selectedRow.key);
      } else if (event.target.id == "createObject") {
        var selectedRow = scnGridOptions.api.getSelectedNodes()[0].data;
        window.location.replace("editObject?sceneKey=" + selectedRow.key);
      } else if (event.target.id == "editObject") {
        var selectedRow = scnGridOptions.api.getSelectedNodes()[0].data;
        var selectedObj = objGridOptions.api.getSelectedNodes()[0].data;
        window.location.replace("editObject?sceneKey=" + selectedRow.key + "&objKey=" + selectedObj.key);
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
