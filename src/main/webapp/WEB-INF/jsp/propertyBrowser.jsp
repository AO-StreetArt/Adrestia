<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html>
<html lang="en">

  <!-- Custom CSS -->
  <link href="/css/aeselBrowserBaseStyle.css" rel="stylesheet">
  <!-- Custom Javascript -->
  <script src="/js/aeselBrowserUtils.js"></script>
  <head>
    <meta http-equiv="Content-Type" content="text/html" charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <!-- Jquery -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
    <!-- Popper -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js" integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q" crossorigin="anonymous"></script>
    <!-- Bootstrap -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootswatch/4.1.3/sandstone/bootstrap.min.css">
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous"></script>
    <!--- Ag-grid-community --->
    <script src="https://unpkg.com/ag-grid-community/dist/ag-grid-community.min.noStyle.js"></script>
    <link rel="stylesheet" href="https://unpkg.com/ag-grid-community/dist/styles/ag-grid.css">
    <link rel="stylesheet" href="https://unpkg.com/ag-grid-community/dist/styles/ag-theme-balham.css">
    <title>Aesel Properties</title>
  </head>
  <body>
    <div class="container-fluid pre-scrollable" style="height:100%;max-height:100%;">
      <%@include  file="aeselBrowserNavbar.jspf" %>
      <div class="row">
        <div class="col-md-12">
          <h1 style="text-align: center;">Properties</h1>
        </div>
      </div>
      <div class="row">
        <div class="col-md-12">
          <div class="btn-toolbar" role="toolbar" aria-label="Properties Toolbar" style="justify-content: center;">
            <div class="btn-group" role="group" aria-label="Properties Toolbar">
              <button id="editProperty" type="button" class="btn btn-primary" style="z-index:265"><span style="font-size:larger;">Edit Property</span></button>
              <button id="createProperty" type="button" class="btn btn-primary" style="z-index:265"><span style="font-size:larger;">Create Property</span></button>
              <button id="deleteProperty" type="button" class="btn btn-primary" style="z-index:265"><span style="font-size:larger;">Delete Property</span></button>
            </div>
          </div>
        </div>
      </div>
      <div class="row" style="height:60%;">
        <div class="col" style="height:100%;">
          <div id="myGrid" style="height:100%;" class="ag-theme-balham"></div>
        </div>
      </div>
      <div class="row">
        <div class="col-xs-6 col-sm-6 col-md-6 col-lg-6">
          <div class="btn-toolbar pull-left" role="toolbar" aria-label="Scenes Pagination">
            <div class="btn-group mr-2" role="group" aria-label="Scene Pagination">
              <input id="frameinp" type="text" name="Frame" placeholder="frame"></input>
              <button id="setFrame" type="button" class="btn btn-secondary btn-sm">View Frame</button>
              <button id="noFrame" type="button" class="btn btn-secondary btn-sm">No Frame</button>
            </div>
          </div>
        </div>
        <div class="col-xs-6 col-sm-6 col-md-6 col-lg-6">
          <div class="btn-toolbar pull-right" role="toolbar" aria-label="Asset Toolbar">
            <div class="btn-group" role="group" aria-label="Asset Toolbar">
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
    // Scene and potentially Object ID are injected here
    var sceneId = "${sceneKey}";
    var objId = "${objKey}";
    // The User ID is injected here by the server before
    var loggedInUser = "${userName}";
    var loggedInKey = "${userId}";
    // If the user is an admin, then the server will inject 'true' here,
    // otherwise, it will inject 'false'.
    var isUserAdmin = "${isAdmin}";

    console.log("Creating Table");

    // specify the columns
    var columnDefs = [
      {
        headerName: "Key",
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
        filterParams: { applyButton: true, clearButton:true, filterOptions:["equals"], suppressAndOrCondition: true, caseSensitive:true },
        editable: false
      },
      {
        headerName: "Parent",
        field: "parent",
        filter: "agTextColumnFilter",
        filterParams: { applyButton: true, clearButton:true, filterOptions:["equals"], suppressAndOrCondition: true, caseSensitive:true },
        editable: false
      },
      {
        headerName: "Asset Sub ID",
        field: "asset_sub_id",
        filter: "agTextColumnFilter",
        filterParams: { applyButton: true, clearButton:true, filterOptions:["equals"], suppressAndOrCondition: true, caseSensitive:true },
        editable: false
      }
    ];

    function updateGridData(event) {
      console.log("Filter Updated:");
      var filterModel = gridOptions.api.getFilterModel();

      if ("key" in filterModel) {
        console.log("Getting User by ID");
        $.ajax({url: "v1/scene/" + sceneId + "/property/" + filterModel.key.filter, type: 'GET', data: {}, success: query_return});
      } else {
        console.log("Getting Users by query");
        query_data = {}
        query_data["num_records"] = gridOptions.api.paginationGetPageSize();
        query_data["page"] = gridOptions.api.paginationGetCurrentPage();
        query_data["properties"] = [{}]
        if (sceneId) {
          query_data["properties"][0]["scene"] = sceneId;
        }
        if (objId) {
          query_data["properties"][0]["parent"] = objId;
        }
        if ("name" in filterModel) {
          query_data["properties"][0]["name"] = filterModel.name.filter;
        }
        if ("parent" in filterModel) {
          query_data["properties"][0]["parent"] = filterModel.parent.filter;
        }
        if ("asset_sub_id" in filterModel) {
          query_data["properties"][0]["asset_sub_id"] = filterModel.asset_sub_id.filter;
        }
        var queryFrame = document.getElementById('frameinp').value;
        if (queryFrame) query_data["properties"][0]["frame"] = queryFrame;

        // Execute an HTTP call to get the available properties
        // and populate them into the list
        $.ajax({url: "v1/scene/" + sceneId + "/property/query",
                type: 'POST',
                cache: false,
                data: JSON.stringify(query_data),
                contentType: "application/json; charset=utf-8",
                success: query_return});
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
        var selectedRow = gridOptions.api.getSelectedNodes()[0].data;
        window.location.replace("editProperty?scene=" + sceneId + "&property=" + selectedRow.id);
      },
      onFilterChanged: updateGridData
    };

    // lookup the container we want the Grid to use
    var eGridDiv = document.querySelector('#myGrid');

    // create the grid passing in the div to use together with the columns & data we want to use
    new agGrid.Grid(eGridDiv, gridOptions);

    // let the grid know which columns and what data to use
    var query_return = function(data) {
      console.log("Updating Table Data")
      gridOptions.api.setRowData(data.properties);
    }

    function delete_selected() {
      var selectedNodes = gridOptions.api.getSelectedNodes();
      var selectedData = selectedNodes.map( function(node) { return node.data })
      if (selectedData.length > 0) {
        var propKey = selectedData[0].key;
        $.ajax({url: "/v1/scene/" + sceneId + "/property/" + propKey,
                type: 'DELETE',
                success: function(data) {
                  console.log("Deleted Property");
                  gridOptions.api.updateRowData({remove: [selectedNodes[0].data]});
                }});
      }
    }

    // Button Callback
    function onButtonClick(event) {
      console.log("Detected button click");

      // Logic to update the scene list based on the input query
      if (event.target.id == "deleteProperty") {
        var r = confirm("Delete the selected Property?");
        if (r) {
          delete_selected();
        }
      } else {
        if (event.target.id == "firstPage") {
          gridOptions.api.paginationGoToPage(0);
        } else if (event.target.id == "prevPage") {
          var currentPage = gridOptions.api.paginationGetCurrentPage();
          gridOptions.api.paginationGoToPage(currentPage+1);
        } else if (event.target.id == "nextPage") {
          var currentPage = gridOptions.api.paginationGetCurrentPage();
          gridOptions.api.paginationGoToPage(currentPage-1);
        } else if (event.target.id == "createProperty") {
          window.location.replace("editProperty?scene=" + sceneId);
        } else if (event.target.id == "editProperty") {
          var selectedRow = gridOptions.api.getSelectedNodes()[0].data;
          window.location.replace("editProperty?scene=" + sceneId + "&property=" + selectedRow.key);
        } else if (event.target.id == "setFrame") {
          updateGridData({});
        } else if (event.target.id == "noFrame") {
          document.getElementById('frameinp').value = "";
          updateGridData({});
        }
        updateGridData({});
      }
    }

    // Main page, added once DOM is loaded into browser
    window.addEventListener('DOMContentLoaded', function(){
      console.log(loggedInUser);
      var adminLoggedIn = (isUserAdmin == 'true');
      setUsersLink((isUserAdmin == 'true'), "userBrowserLink");

      updateGridData({});

      // Setup the button callbacks into the Javascript
      registerButtonCallback(onButtonClick);
    });
    </script>
  </body>
</html>
