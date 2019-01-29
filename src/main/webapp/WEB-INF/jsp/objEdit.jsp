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
    <title>Aesel Object</title>
    <!--- Ag-grid-community --->
    <script src="https://unpkg.com/ag-grid-community/dist/ag-grid-community.min.noStyle.js"></script>
    <link rel="stylesheet" href="https://unpkg.com/ag-grid-community/dist/styles/ag-grid.css">
    <link rel="stylesheet" href="https://unpkg.com/ag-grid-community/dist/styles/ag-theme-balham.css">
  </head>
  <body>
    <div class="container-fluid pre-scrollable" style="height:100%;max-height:100%;">
      <%@include  file="aeselBrowserNavbar.jspf" %>
      <div class="alert alert-success" id="success-alert" style="display:none">Object Saved!</div>
      <div class="row">
        <div class="col-md-12">
          <h1 style="text-align: center;">Object</h1>
        </div>
      </div>
      <div class="row">
        <div class="col-xs-5 col-sm-4 col-md-2 col-lg-2">
          <p>Key:</p>
        </div>
        <div class="col-xs-7 col-sm-8 col-md-10 col-lg-10" class="tooltip" title="The unique identifier of the Object.">
          <input id="keyinp" type="text" class="form-control-plaintext" name="Key" placeholder="Key"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-xs-5 col-sm-4 col-md-2 col-lg-2">
          <p>Name:</p>
        </div>
        <div class="col-xs-7 col-sm-8 col-md-10 col-lg-10" class="tooltip" title="The human-readable name of the Object.">
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
          <p>Type:</p>
        </div>
        <div class="col-xs-7 col-sm-8 col-md-10 col-lg-10" class="tooltip" title="The human-readable name of the Object.">
          <input id="typeinp" type="text" class="form-control-plaintext" name="Type" placeholder="Type"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-xs-5 col-sm-4 col-md-2 col-lg-2">
          <p>Subtype:</p>
        </div>
        <div class="col-xs-7 col-sm-8 col-md-10 col-lg-10" class="tooltip" title="The latitude of the Object for distance queries.">
          <input id="subtypeinp" type="text" class="form-control-plaintext" name="Subtype" placeholder="Subtype"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-xs-5 col-sm-4 col-md-2 col-lg-2">
          <p>Owner:</p>
        </div>
        <div class="col-xs-7 col-sm-8 col-md-10 col-lg-10" class="tooltip" title="The longitude of the Object for distance queries.">
          <input id="ownerinp" type="text" class="form-control-plaintext" name="Owner" placeholder="Owner"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-xs-5 col-sm-4 col-md-2 col-lg-2">
          <p>Frame:</p>
        </div>
        <div class="col-xs-7 col-sm-8 col-md-10 col-lg-10" class="tooltip" title="The tags associated to the Object.">
          <input id="frameinp" type="text" class="form-control-plaintext" name="Frame" placeholder="Frame"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-xs-5 col-sm-4 col-md-2 col-lg-2">
          <p>Timestamp:</p>
        </div>
        <div class="col-xs-7 col-sm-8 col-md-10 col-lg-10" class="tooltip" title="The tags associated to the Object.">
          <input id="timeinp" type="text" class="form-control-plaintext" name="Timestamp" placeholder="Timestamp"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-xs-5 col-sm-4 col-md-2 col-lg-2">
          <p>Translation:</p>
        </div>
        <div class="col-xs-7 col-sm-8 col-md-10 col-lg-10" class="tooltip" title="The tags associated to the Object.">
          <input id="translationinp" type="text" class="form-control-plaintext" name="Translation" placeholder="Translation"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-xs-5 col-sm-4 col-md-2 col-lg-2">
          <p>Euler Rotation:</p>
        </div>
        <div class="col-xs-7 col-sm-8 col-md-10 col-lg-10" class="tooltip" title="The tags associated to the Object.">
          <input id="erotinp" type="text" class="form-control-plaintext" name="ERotation" placeholder="Euler Rotation"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-xs-5 col-sm-4 col-md-2 col-lg-2">
          <p>Quaternion Rotation:</p>
        </div>
        <div class="col-xs-7 col-sm-8 col-md-10 col-lg-10" class="tooltip" title="The tags associated to the Object.">
          <input id="qrotinp" type="text" class="form-control-plaintext" name="QRotation" placeholder="Quaternion Rotation"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-xs-5 col-sm-4 col-md-2 col-lg-2">
          <p>Scale:</p>
        </div>
        <div class="col-xs-7 col-sm-8 col-md-10 col-lg-10" class="tooltip" title="The tags associated to the Object.">
          <input id="scaleinp" type="text" class="form-control-plaintext" name="Scale" placeholder="Scale"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-xs-5 col-sm-4 col-md-2 col-lg-2">
          <p>Transform:</p>
        </div>
        <div class="col-xs-7 col-sm-8 col-md-10 col-lg-10" class="tooltip" title="The tags associated to the Object.">
          <input id="transforminp" type="text" class="form-control-plaintext" name="Transform" placeholder="Transform"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-md-6" class="tooltip" title="Cancel the changes.">
          <button id="cancel" type="button" class="btn btn-primary"><span style="font-size:larger;">Cancel</span></button>
        </div>
        <div class="col-md-6" class="tooltip" title="Save the Object.">
          <button id="save" type="button" class="btn btn-primary"><span style="font-size:larger;">Save Object</span></button>
        </div>
      </div>
      <footer class="footer">
          <p> &copy; 2018 AO Labs</p>
      </footer>
    </div>
    <script>
    // The Scene and Object Keys are injected here by
    // the server before it returns the page
    var sceneKey = "${sceneKey}";
    var objKey = "${objKey}";

    function stringToFloatArray(str) {
      var stringList = str.split(",");
      var returnList = [];
      for (var i = 0; i < stringList.length; i++) {
        returnList.push(parseFloat(stringList[i]));
      }
      return returnList;
    }

    // Button click logic (save and cancel)
    function onButtonClick(event) {
      if (event.target.id == "save") {
        var newKey = document.getElementById('keyinp').value;
        var newName = document.getElementById('nameinp').value;
        var newParent = document.getElementById('parentinp').value;
        var newType = document.getElementById('typeinp').value;
        var newSubtype = document.getElementById('subtypeinp').value;
        var newOwner = document.getElementById('ownerinp').value;
        var newFrame = document.getElementById('frameinp').value;
        var newTime = document.getElementById('timeinp').value;
        var newTranslation = document.getElementById('translationinp').value;
        var newERotation = document.getElementById('erotinp').value;
        var newQRotation = document.getElementById('qrotinp').value;
        var newScale = document.getElementById('scaleinp').value;
        var newTransform = document.getElementById('transforminp').value;
        objData = {scene: sceneKey}
        if (newName) objData["name"] = newName;
        if (newParent) objData["parent"] = newParent;
        if (newType) objData["type"] = newType;
        if (newSubtype) objData["subtype"] = newSubtype;
        if (newOwner) objData["owner"] = newOwner;
        if (newFrame) objData["frame"] = parseInt(newFrame, 10);
        if (newTime) objData["time"] = parseInt(newTime, 10);
        if (newTranslation) objData["translation"] = stringToFloatArray(newTranslation);
        if (newERotation) objData["euler_rotation"] = stringToFloatArray(newERotation);
        if (newQRotation) objData["quaternion_rotation"] = stringToFloatArray(newQRotation);
        if (newScale) objData["scale"] = stringToFloatArray(newScale);
        if (newTransform) objData["transform"] = stringToFloatArray(newTransform);
        var objUrl = "v1/scene/" + sceneKey + "/object/";
        if (newKey) {
          objData["key"] = newKey;
          objUrl = objUrl + newKey;
        }
        objListData = {"objects": [objData]}
        $.ajax({url: objUrl,
                type: 'POST',
                data: JSON.stringify(objListData),
                contentType: "application/json; charset=utf-8",
                success: function(data) {
                  console.log(data);
                  displayAlert("success-alert");
                }});
      } else if (event.target.id == "cancel") {
        window.location.replace("sceneBrowser");
      }
    }

    // Logic for loading the page from a pre-existing Project
    var query_return = function(data) {
      console.log("Updating Object Data");
      console.log(data);
      document.getElementById('keyinp').value = data.objects[0].key;
      document.getElementById('nameinp').value = data.objects[0].name;
      document.getElementById('parentinp').value = data.objects[0].parent;
      document.getElementById('typeinp').value = data.objects[0].type;
      document.getElementById('subtypeinp').value = data.objects[0].subtype;
      document.getElementById('ownerinp').value = data.objects[0].owner;
      document.getElementById('frameinp').value = data.objects[0].frame;
      document.getElementById('timeinp').value = data.objects[0].timestamp;
      document.getElementById('translationinp').value = data.objects[0].translation;
      document.getElementById('erotinp').value = data.objects[0].euler_rotation;
      document.getElementById('qrotinp').value = data.objects[0].quaternion_rotation;
      document.getElementById('scaleinp').value = data.objects[0].scale;
      document.getElementById('transforminp').value = data.objects[0].transform;
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
      setUsersLink((isUserAdmin == 'true'), "userBrowserLink", loggedInKey);

      // Setup the button callbacks into the Javascript
      registerButtonCallback(onButtonClick);

      // Activate JBox Tooltips
      new jBox('Tooltip', {
        attach: '.tooltip'
      });

      console.log("Object Key: " + sceneKey)
      if (sceneKey && objKey) {
        $.ajax({url: "v1/scene/" + sceneKey + "/object/" + objKey, data: {}, success: query_return});
      }
    });
    </script>
  </body>
</html>
