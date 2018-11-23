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
  <!-- JBox -->
  <script src="https://cdn.jsdelivr.net/gh/StephanWagner/jBox@v0.5.1/dist/jBox.all.min.js"></script>
  <link href="https://cdn.jsdelivr.net/gh/StephanWagner/jBox@v0.5.1/dist/jBox.all.min.css" rel="stylesheet">

  <head>
    <meta http-equiv="Content-Type" content="text/html" charset="utf-8"/>
    <title>Aesel Object</title>
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
    input {
      width : 100%;
    }
    html {
      overflow-y: hidden;
    }
    </style>
  </head>
  <body>
    <div class="pre-scrollable" style="height:100%;max-height: 100%;">
    <div class="container-fluid" style="height:100%;">
      <div class="row">
        <h1 style="text-align: center;">Object</h1>
      </div>
      <div class="row">
        <div class="col-md-2">
          Key:
        </div>
        <div class="col-md-10" class="tooltip" title="The unique identifier of the Object.">
          <input id="keyinp" type="text" name="Key" placeholder="Key"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-md-2">
          Name:
        </div>
        <div class="col-md-10" class="tooltip" title="The human-readable name of the Object.">
          <input id="nameinp" type="text" name="Name" placeholder="Name"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-md-2">
          Type:
        </div>
        <div class="col-md-10" class="tooltip" title="The human-readable name of the Object.">
          <input id="typeinp" type="text" name="Type" placeholder="Type"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-md-2">
          Subtype:
        </div>
        <div class="col-md-10" class="tooltip" title="The latitude of the Object for distance queries.">
          <input id="subtypeinp" type="text" name="Subtype" placeholder="Subtype"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-md-2">
          Owner:
        </div>
        <div class="col-md-10" class="tooltip" title="The longitude of the Object for distance queries.">
          <input id="ownerinp" type="text" name="Owner" placeholder="Owner"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-md-2">
          Frame:
        </div>
        <div class="col-md-10" class="tooltip" title="The tags associated to the Object.">
          <input id="frameinp" type="text" name="Frame" placeholder="Frame"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-md-2">
          Timestamp:
        </div>
        <div class="col-md-10" class="tooltip" title="The tags associated to the Object.">
          <input id="timeinp" type="text" name="Timestamp" placeholder="Timestamp"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-md-2">
          Translation:
        </div>
        <div class="col-md-10" class="tooltip" title="The tags associated to the Object.">
          <input id="translationinp" type="text" name="Translation" placeholder="Translation"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-md-2">
          Euler Rotation:
        </div>
        <div class="col-md-10" class="tooltip" title="The tags associated to the Object.">
          <input id="erotinp" type="text" name="ERotation" placeholder="Euler Rotation"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-md-2">
          Quaternion Rotation:
        </div>
        <div class="col-md-10" class="tooltip" title="The tags associated to the Object.">
          <input id="qrotinp" type="text" name="QRotation" placeholder="Quaternion Rotation"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-md-2">
          Scale:
        </div>
        <div class="col-md-10" class="tooltip" title="The tags associated to the Object.">
          <input id="scaleinp" type="text" name="Scale" placeholder="Scale"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-md-2">
          Transform:
        </div>
        <div class="col-md-10" class="tooltip" title="The tags associated to the Object.">
          <input id="transforminp" type="text" name="Transform" placeholder="Transform"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-md-6" class="tooltip" title="Cancel the changes.">
          <button id="cancel">Cancel</button>
        </div>
        <div class="col-md-6" class="tooltip" title="Save the Object.">
          <button id="save">Save Object</button>
        </div>
      </div>
      <footer class="footer">
          <p> &copy; 2018 AO Labs</p>
      </footer>
    </div>
    </div>
    <script>
    // The Project Key is injected here by the server before
    // it returns the page
    var sceneKey = "${sceneKey}";
    var objKey = "${objKey}";

    // Button click logic (save and cancel)
    function onButtonClick(event) {
      if (event.target.id == "save") {
        var newKey = document.getElementById('keyinp').value;
        var newName = document.getElementById('nameinp').value;
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
        objData = {}
        if (newName) objData["name"] = newName;
        if (newType) objData["type"] = newType;
        if (newSubtype) objData["subtype"] = newSubtype;
        if (newOwner) objData["owner"] = newOwner;
        if (newFrame) objData["frame"] = newFrame;
        if (newTime) objData["time"] = newTime;
        if (newTranslation) objData["translation"] = newTranslation;
        if (newERotation) objData["euler_rotation"] = newERotation;
        if (newQRotation) objData["quaternion_rotation"] = newQRotation;
        if (newScale) objData["scale"] = newScale;
        if (newTransform) objData["transform"] = newTransform;
        var objUrl = "v1/object/";
        if (newKey) {
          objData["key"] = newKey;
          objUrl = objUrl + newKey;
        }
        objListData = {"objects": [objData]}
        $.ajax({url: "v1/scene/" + sceneKey + "/object/" + newKey,
                type: 'POST',
                data: JSON.stringify(objListData),
                contentType: "application/json; charset=utf-8",
                success: function(data) {
                  console.log(data);
                }});
        };
      } else if (event.target.id == "cancel") {
        window.history.back();
      }
    }

    // Logic for loading the page from a pre-existing Project
    var query_return = function(data) {
      console.log("Updating Object Data");
      console.log(data);
      document.getElementById('keyinp').value = data.objects[0].key;
      document.getElementById('nameinp').value = data.objects[0].name;
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

      // Setup the button callbacks into the Javascript
      var buttons = document.getElementsByTagName("button");
      for (let i = 0; i < buttons.length; i++) {
        buttons[i].addEventListener("click", onButtonClick, false);
      };

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
