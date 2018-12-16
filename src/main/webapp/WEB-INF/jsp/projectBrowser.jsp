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
  <head>
    <meta http-equiv="Content-Type" content="text/html" charset="utf-8"/>
    <title>Aesel Project</title>
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
            <li class="nav-item active" id="projectBrowser">
              <a class="nav-link" href="#">Projects <span class="sr-only">(current)</span></a>
            </li>
            <li class="nav-item" id="sceneBrowser">
              <a class="nav-link" href="/sceneBrowser">Scenes</a>
            </li>
            <li class="nav-item" id="assetBrowser">
              <a class="nav-link" href="/assetBrowser">Assets</a>
            </li>
            <li class="nav-item" id="docs">
              <a class="nav-link" href="https://aesel.readthedocs.io/en/latest/index.html">Documentation</a>
            </li>
          </ul>
      </nav>
      <div class="row">
        <div class="col-md-12">
          <h1 style="text-align: center;">Projects</h1>
        </div>
      </div>
      <div class="row">
    		<div class="col-md-6 col-lg-6">
    			<p id="projectDescription">
            Double click on a project in the list to view the description and thumbnail.
    			</p>
    		</div>
    		<div class="col-md-6 col-lg-6">
    			<img class="img-fluid rounded mx-auto d-block" id="projectThumbnail" alt="No Thumbnail Available" src="https://www.layoutit.com/img/sports-q-c-140-140-3.jpg" class="rounded" />
    		</div>
    	</div>
      <div class="row">
        <div class="col-md-12">
          <div class="btn-toolbar" role="toolbar" aria-label="Project Toolbar" style="justify-content: center;">
            <div class="btn-group" role="group" aria-label="Project Toolbar">
              <button id="editProj" type="button" class="btn btn-primary" style="z-index:265"><span style="font-size:larger;">Edit Project</span></button>
              <button id="createProj" type="button" class="btn btn-primary" style="z-index:265"><span style="font-size:larger;">Create Project</span></button>
              <button id="delete" type="button" class="btn btn-primary" style="z-index:265"><span style="font-size:larger;">Delete Project</span></button>
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
        headerName: "Name",
        field: "name",
        pinned: 'left',
        filter: "agTextColumnFilter",
        filterParams: { applyButton: true, clearButton:true, filterOptions:["equals"], suppressAndOrCondition: true, caseSensitive:true },
        editable: false
      },
      {
        headerName: "Category",
        field: "category",
        filter: "agTextColumnFilter",
        filterParams: { applyButton: true, clearButton:true, filterOptions:["equals"], suppressAndOrCondition: true, caseSensitive:true },
        editable: false
      },
      {
        headerName: "Thumbnail",
        field: "thumbnail",
        hide: true
      }
    ];

    function updateGridData(event) {
      console.log("Filter Updated:");
      var filterModel = gridOptions.api.getFilterModel();
      if ("id" in filterModel) {
        console.log("Getting Project by ID");
        $.ajax({url: "v1/project/" + filterModel.id.filter, type: 'GET', data: {}, success: query_return});
      } else {
        console.log("Getting Projects by query");
        // Getting filter params
        query_params = {}
        if ("name" in filterModel) query_params["name"] = filterModel.name.filter;
        if ("category" in filterModel) query_params["category"] = filterModel.category.filter;
        query_params["num_records"] = gridOptions.api.paginationGetPageSize();
        query_params["page"] = gridOptions.api.paginationGetCurrentPage();
        // Execute query
        $.ajax({url: "v1/project", type: 'GET', data: query_params, success: query_return});
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
        var newDescription = selectedRow.description;
        var newThumbnail = selectedRow.thumbnail;
        document.getElementById('projectDescription').innerHTML = newDescription;
        document.getElementById('projectThumbnail').src = "v1/asset/" + newThumbnail + "/thumbnail.png";
        console.log(newDescription)
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
        var assetKey = selectedData[0].key;
        $.ajax({url: "v1/project/" + assetKey,
                type: 'DELETE',
                success: function(data) {
                  console.log("Deleted Project");
                }});
      }
    }

    // Button Callback
    function onButtonClick(event) {
      console.log("Detected button click");

      // Logic to update the scene list based on the input query
      if (event.target.id == "firstPage") {
        gridOptions.api.paginationGoToPage(0);
      } else if (event.target.id == "prevPage") {
        var currentPage = gridOptions.api.paginationGetCurrentPage();
        gridOptions.api.paginationGoToPage(currentPage+1);
      } else if (event.target.id == "nextPage") {
        var currentPage = gridOptions.api.paginationGetCurrentPage();
        gridOptions.api.paginationGoToPage(currentPage-1);
      } else if (event.target.id == "createProj") {
        window.location.replace("editProject");
      } else if (event.target.id == "editProj") {
        var selectedRow = gridOptions.api.getSelectedNodes()[0].data;
        window.location.replace("editProject?key=" + selectedRow.id);
      } else if (event.target.id == "delete") {
        delete_selected();
      }
      updateGridData({});
    }

    // Main page, added once DOM is loaded into browser
    window.addEventListener('DOMContentLoaded', function(){

      // Execute an HTTP call to get the available asset metadata
      // and populate it into the list
      $.ajax({url: "v1/project", data: {}, success: query_return});

      // Setup the button callbacks into the Javascript
      var buttons = document.getElementsByTagName("button");
      for (let i = 0; i < buttons.length; i++) {
        buttons[i].addEventListener("click", onButtonClick, false);
      };
    });
    </script>
  </body>
</html>
