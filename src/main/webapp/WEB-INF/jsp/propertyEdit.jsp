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
  <!-- JBox -->
  <script src="https://cdn.jsdelivr.net/gh/StephanWagner/jBox@v0.5.1/dist/jBox.all.min.js"></script>
  <link href="https://cdn.jsdelivr.net/gh/StephanWagner/jBox@v0.5.1/dist/jBox.all.min.css" rel="stylesheet">

  <!-- Custom CSS -->
  <link href="/css/aeselBrowserBaseStyle.css" rel="stylesheet">
  <!-- Custom Javascript -->
  <script src="/js/aeselBrowserUtils.js"></script>
  <head>
    <meta http-equiv="Content-Type" content="text/html" charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <title>Aesel Property</title>
    <!--- Ag-grid-community --->
    <script src="https://unpkg.com/ag-grid-community/dist/ag-grid-community.min.noStyle.js"></script>
    <link rel="stylesheet" href="https://unpkg.com/ag-grid-community/dist/styles/ag-grid.css">
    <link rel="stylesheet" href="https://unpkg.com/ag-grid-community/dist/styles/ag-theme-balham.css">
  </head>
  <body>
    <div class="container-fluid pre-scrollable" style="height:100%;max-height:100%;">
      <%@include  file="aeselBrowserNavbar.jspf" %>
      <div class="alert alert-success" id="success-alert" style="display:none">Property Saved!</div>
      <div class="row">
        <div class="col-md-12">
          <h1 style="text-align: center;">Property</h1>
        </div>
      </div>
      <div class="row">
        <div class="col-xs-5 col-sm-4 col-md-2 col-lg-2">
          <p>Key:</p>
        </div>
        <div class="col-xs-7 col-sm-8 col-md-10 col-lg-10" class="tooltip" title="The unique identifier of the Property.">
          <input id="keyinp" type="text" class="form-control-plaintext" name="Key" placeholder="Key"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-xs-5 col-sm-4 col-md-2 col-lg-2">
          <p>Name:</p>
        </div>
        <div class="col-xs-7 col-sm-8 col-md-10 col-lg-10" class="tooltip" title="The human-readable name of the Property.">
          <input id="nameinp" type="text" class="form-control-plaintext" name="Name" placeholder="Name"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-xs-5 col-sm-4 col-md-2 col-lg-2">
          <p>Parent:</p>
        </div>
        <div class="col-xs-7 col-sm-8 col-md-10 col-lg-10" class="tooltip" title="The parent Object from which this inherits attributes.">
          <input id="parentinp" type="text" class="form-control-plaintext" name="Parent" placeholder="Parent"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-xs-5 col-sm-4 col-md-2 col-lg-2">
          <p>Asset Sub ID:</p>
        </div>
        <div class="col-xs-7 col-sm-8 col-md-10 col-lg-10" class="tooltip" title="The sub-ID of the property within an asset.">
          <input id="assetsubinp" type="text" class="form-control-plaintext" name="AssetSubId" placeholder="Asset Sub ID"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-xs-5 col-sm-4 col-md-2 col-lg-2">
          <p>Frame:</p>
        </div>
        <div class="col-xs-7 col-sm-8 col-md-10 col-lg-10" class="tooltip" title="The frame the Object.">
          <input id="frameinp" type="text" class="form-control-plaintext" name="Frame" placeholder="Frame"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-xs-5 col-sm-4 col-md-2 col-lg-2">
          <p>Timestamp:</p>
        </div>
        <div class="col-xs-7 col-sm-8 col-md-10 col-lg-10" class="tooltip" title="The timestamp of the Object.">
          <input id="timeinp" type="text" class="form-control-plaintext" name="Timestamp" placeholder="Timestamp"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-xs-5 col-sm-4 col-md-2 col-lg-2">
          <p>Value 1:</p>
        </div>
        <div class="col-xs-7 col-sm-8 col-md-10 col-lg-10" class="tooltip" title="The timestamp of the Object.">
          <input id="value1inp" type="text" class="form-control-plaintext" name="Value1" placeholder="Value 1"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-xs-5 col-sm-4 col-md-2 col-lg-2">
          <p>Value 2:</p>
        </div>
        <div class="col-xs-7 col-sm-8 col-md-10 col-lg-10" class="tooltip" title="The timestamp of the Object.">
          <input id="value2inp" type="text" class="form-control-plaintext" name="Value2" placeholder="Value 2"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-xs-5 col-sm-4 col-md-2 col-lg-2">
          <p>Value 3:</p>
        </div>
        <div class="col-xs-7 col-sm-8 col-md-10 col-lg-10" class="tooltip" title="The timestamp of the Object.">
          <input id="value3inp" type="text" class="form-control-plaintext" name="Value3" placeholder="Value 3"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-xs-5 col-sm-4 col-md-2 col-lg-2">
          <p>Value 4:</p>
        </div>
        <div class="col-xs-7 col-sm-8 col-md-10 col-lg-10" class="tooltip" title="The timestamp of the Object.">
          <input id="value4inp" type="text" class="form-control-plaintext" name="Value4" placeholder="Value 4"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-md-6" class="tooltip" title="Cancel the changes.">
          <button id="cancel" type="button" class="btn btn-primary"><span style="font-size:larger;">Cancel</span></button>
        </div>
        <div class="col-md-6" class="tooltip" title="Save the Object.">
          <button id="save" type="button" class="btn btn-primary"><span style="font-size:larger;">Save Property</span></button>
        </div>
      </div>
      <footer class="footer">
          <p> &copy; 2018 AO Labs</p>
      </footer>
    </div>
    <script>
    // The Scene and Property Keys are injected here by
    // the server before it returns the page
    var sceneKey = "${sceneKey}";
    var propKey = "${propKey}";

    // Button click logic (save and cancel)
    function onButtonClick(event) {
      if (event.target.id == "save") {
        var newKey = document.getElementById('keyinp').value;
        var newName = document.getElementById('nameinp').value;
        var newParent = document.getElementById('parentinp').value;
        var newAssetSubId = document.getElementById('assetsubinp').value;
        var newFrame = document.getElementById('frameinp').value;
        var newTime = document.getElementById('timeinp').value;
        var newValue1 = document.getElementById('value1inp').value;
        var newValue2 = document.getElementById('value2inp').value;
        var newValue3 = document.getElementById('value3inp').value;
        var newValue4 = document.getElementById('value4inp').value;
        propData = {scene: sceneKey}
        if (newName) propData["name"] = newName;
        if (newParent) propData["parent"] = newParent;
        if (newAssetSubId) propData["assetsubinp"] = newAssetSubId;
        if (newFrame) propData["frame"] = parseInt(newFrame, 10);
        if (newTime) propData["time"] = parseInt(newTime, 10);
        if (newValue1) {
          propData["values"] = [{"value": parseFloat(newValue1)}];
          if (newValue2) propData["values"].push({"value": parseFloat(newValue2)});
          if (newValue3) propData["values"].push({"value": parseFloat(newValue3)});
          if (newValue4) propData["values"].push({"value": parseFloat(newValue4)});
        }
        var objUrl = "v1/scene/" + sceneKey + "/property/";
        if (newKey) {
          p["key"] = newKey;
          objUrl = objUrl + newKey;
        }
        propListData = {"properties": [propData]}
        $.ajax({url: objUrl,
                type: 'POST',
                data: JSON.stringify(propListData),
                contentType: "application/json; charset=utf-8",
                success: function(data) {
                  console.log(data);
                  displayAlert("success-alert");
                }});
      } else if (event.target.id == "cancel") {
        window.location.replace("propertyBrowser?scene=" + sceneKey);
      }
    }

    // Logic for loading the page from a pre-existing Project
    var query_return = function(data) {
      console.log("Updating Object Data");
      console.log(data);
      document.getElementById('keyinp').value = data.properties[0].key;
      if (data.properties[0].name) {
        document.getElementById('nameinp').value = data.properties[0].name;
      }
      if (data.properties[0].parent) {
        document.getElementById('parentinp').value = data.properties[0].parent;
      }
      if (data.properties[0].asset_sub_id) {
        document.getElementById('assetsubinp').value = data.properties[0].asset_sub_id;
      }
      if (data.properties[0].frame) {
        document.getElementById('frameinp').value = data.properties[0].frame;
      }
      if (data.properties[0].timestamp) {
        document.getElementById('timeinp').value = data.properties[0].timestamp;
      }
      if (data.properties[0].values.length > 0) {
        document.getElementById('value1inp').value = data.properties[0].values[0].value;
      }
      if (data.properties[0].values.length > 1) {
        document.getElementById('value2inp').value = data.properties[0].values[1].value;
      }
      if (data.properties[0].values.length > 2) {
        document.getElementById('value3inp').value = data.properties[0].values[2].value;
      }
      if (data.properties[0].values.length > 3) {
        document.getElementById('value4inp').value = data.properties[0].values[3].value;
      }
    }

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
      setUsersLink((isUserAdmin == 'true'), "userBrowserLink");

      // Setup the button callbacks into the Javascript
      registerButtonCallback(onButtonClick);

      // Activate JBox Tooltips
      new jBox('Tooltip', {
        attach: '.tooltip'
      });

      console.log("Scene Key: " + sceneKey + " and Property Key: " + propKey);
      if (sceneKey && propKey) {
        $.ajax({url: "v1/scene/" + sceneKey + "/property/" + propKey, data: {}, success: query_return});
      }
    });
    </script>
  </body>
</html>
