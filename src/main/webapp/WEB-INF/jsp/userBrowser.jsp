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
    <title>Aesel Users</title>
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
              <a class="nav-link" href="/sceneBrowser">Scenes</a>
            </li>
            <li class="nav-item" id="assetBrowser">
              <a class="nav-link" href="/assetBrowser">Assets</a>
            </li>
            <li class="nav-item" id="userBrowser">
              <a class="nav-link active" href="#" id="userBrowserLink">Users <span class="sr-only">(current)</span></a>
            </li>
            <li class="nav-item" id="docs">
              <a class="nav-link" href="https://aesel.readthedocs.io/en/latest/index.html">Documentation</a>
            </li>
          </ul>
      </nav>
      <div class="row">
        <div class="col-md-12">
          <h1 style="text-align: center;">Users</h1>
        </div>
      </div>
      <div class="row">
        <div class="col-md-12">
          <div class="btn-toolbar" role="toolbar" aria-label="User Toolbar" style="justify-content: center;">
            <div class="btn-group" role="group" aria-label="User Toolbar">
              <button id="editUser" type="button" class="btn btn-primary" style="z-index:265"><span style="font-size:larger;">Edit User</span></button>
              <button id="createUser" type="button" class="btn btn-primary" style="z-index:265"><span style="font-size:larger;">Create User</span></button>
              <button id="deleteUser" type="button" class="btn btn-primary" style="z-index:265"><span style="font-size:larger;">Delete User</span></button>
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
        <div class="col-md-12">
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
    console.log("Creating Table");

    // specify the columns
    var columnDefs = [
      {
        headerName: "ID",
        field: "id",
        pinned: 'left',
        filter: "agTextColumnFilter",
        filterParams: { applyButton: true, clearButton:true, filterOptions:["equals"], suppressAndOrCondition:true, caseSensitive:true },
        editable: false
      },
      {
        headerName: "Username",
        field: "username",
        pinned: 'left',
        filter: "agTextColumnFilter",
        filterParams: { applyButton: true, clearButton:true, filterOptions:["equals"], suppressAndOrCondition: true, caseSensitive:true },
        editable: false
      },
      {
        headerName: "Email",
        field: "email",
        filter: "agTextColumnFilter",
        filterParams: { applyButton: true, clearButton:true, filterOptions:["equals"], suppressAndOrCondition: true, caseSensitive:true },
        editable: false
      }
    ];

    function updateGridData(event) {
      console.log("Filter Updated:");
      var filterModel = gridOptions.api.getFilterModel();
      if ("id" in filterModel) {
        console.log("Getting User by ID");
        $.ajax({url: "/users/" + filterModel.id.filter, type: 'GET', data: {}, success: query_return});
      } else {
        console.log("Getting Users by query");
        // Getting filter params
        query_params = {}
        if ("username" in filterModel) query_params["username"] = filterModel.username.filter;
        if ("email" in filterModel) query_params["email"] = filterModel.email.filter;
        query_params["num_records"] = gridOptions.api.paginationGetPageSize();
        query_params["page"] = gridOptions.api.paginationGetCurrentPage();
        // Execute query
        $.ajax({url: "/users/", type: 'GET', data: query_params, success: query_return});
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
        window.location.replace("editUser?key=" + selectedRow.id);
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
      gridOptions.api.setRowData(data);
    }

    function delete_selected() {
      var selectedNodes = gridOptions.api.getSelectedNodes()
      var selectedData = selectedNodes.map( function(node) { return node.data })
      if (selectedData.length > 0) {
        var userKey = selectedData[0].id;
        $.ajax({url: "/users/" + userKey,
                type: 'DELETE',
                success: function(data) {
                  console.log("Deleted User");
                  updateGridData({});
                }});
      }
    }

    // Button Callback
    function onButtonClick(event) {
      console.log("Detected button click");

      // Logic to update the scene list based on the input query
      if (event.target.id == "deleteUser") {
        var r = confirm("Delete the selected User?");
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
        } else if (event.target.id == "createUser") {
          window.location.replace("editUser");
        } else if (event.target.id == "editUser") {
          var selectedRow = gridOptions.api.getSelectedNodes()[0].data;
          window.location.replace("editUser?key=" + selectedRow.id);
        }
        updateGridData({});
      }
    }

    // Main page, added once DOM is loaded into browser
    window.addEventListener('DOMContentLoaded', function(){

      // Execute an HTTP call to get the available users
      // and populate them into the list
      query_params = {}
      query_params["num_records"] = gridOptions.api.paginationGetPageSize();
      query_params["page"] = gridOptions.api.paginationGetCurrentPage();
      $.ajax({url: "/users/", data: query_params, success: query_return});

      // Setup the button callbacks into the Javascript
      registerButtonCallback(onButtonClick);
    });
    </script>
  </body>
</html>
