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
    <title>Aesel Project Browser</title>
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
        <h1 style="text-align: center;">Project Browser</h1>
      </div>
      <div class="row">
    		<div class="col-md-8">
    			<p id="projectDescription">
            Double click on a project in the list to view the description and thumbnail.
    			</p>
    		</div>
    		<div class="col-md-4">
    			<img id="projectThumbnail" alt="No Thumbnail Available" src="https://www.layoutit.com/img/sports-q-c-140-140-3.jpg" class="rounded" />
    		</div>
    	</div>
      <div class="row">
        <div class="col align-self-center">
          <button id="firstPage" style="z-index:265">First Page</button>
          <button id="prevPage" style="z-index:265">Previous Page</button>
          <button id="nextPage" style="z-index:265">Next Page</button>
        </div>
      </div>
      <div class="row" style="height:60%;">
        <div class="col" style="height:100%;">
          <div id="myGrid" style="height:100%;" class="ag-theme-balham"></div>
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
