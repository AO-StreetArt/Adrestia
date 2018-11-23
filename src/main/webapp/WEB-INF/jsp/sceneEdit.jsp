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
    <title>Aesel Scene</title>
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
        <h1 style="text-align: center;">Scene</h1>
      </div>
      <div class="row">
        <div class="col-md-2">
          Key:
        </div>
        <div class="col-md-10" class="tooltip" title="The unique identifier of the Scene.">
          <input id="keyinp" type="text" name="Key" placeholder="Key"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-md-2">
          Name:
        </div>
        <div class="col-md-10" class="tooltip" title="The human-readable name of the Scene.">
          <input id="nameinp" type="text" name="Name" placeholder="Name"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-md-2">
          Region:
        </div>
        <div class="col-md-10" class="tooltip" title="The human-readable name of the Scene.">
          <input id="regioninp" type="text" name="Region" placeholder="Region"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-md-2">
          Latitude:
        </div>
        <div class="col-md-10" class="tooltip" title="The latitude of the Scene for distance queries.">
          <input id="latitudeinp" type="text" name="Latitude" placeholder="Latitude"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-md-2">
          Longitude:
        </div>
        <div class="col-md-10" class="tooltip" title="The longitude of the Scene for distance queries.">
          <input id="longitudeinp" type="text" name="Longitude" placeholder="Longitude"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-md-2">
          Tags:
        </div>
        <div class="col-md-10" class="tooltip" title="The tags associated to the Scene.">
          <input id="tagsinp" type="text" name="Tags" placeholder="Tags"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-md-6" class="tooltip" title="Cancel the changes.">
          <button id="cancel">Cancel</button>
        </div>
        <div class="col-md-6" class="tooltip" title="Save the Scene.">
          <button id="save">Save Scene</button>
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
    var sceneKey = "${sceneKey}";

    function sceneReturn(data) {
      console.log(data);
    }

    // Button click logic
    function onButtonClick(event) {
      if (event.target.id == "save") {
        var newKey = document.getElementById('keyinp').value;
        var newName = document.getElementById('nameinp').value;
        var newRegion = document.getElementById('regioninp').value;
        var newLat = document.getElementById('latitudeinp').value;
        var newLong = document.getElementById('longitudeinp').value;
        var newTags = document.getElementById('tagsinp').value;
        sceneData = {}
        if (newName) sceneData["name"] = newName;
        if (newRegion) sceneData["region"] = newRegion;
        if (newLong) sceneData["longitude"] = newLong;
        if (newLat) sceneData["latitutde"] = newLat;
        if (newTags) sceneData["tags"] = newTags;
        sceneListMethodType = "POST";
        if (newKey) {
          sceneListMethodType = "PUT";
          sceneData["key"] = newKey;
        }
        sceneUrl = "v1/scene/" + newKey;
        sceneListData = JSON.stringify({"scenes": [sceneData]});
        $.ajax({url: sceneUrl,
                type: sceneListMethodType,
                data: sceneListData,
                contentType: "application/json; charset=utf-8",
                success: sceneReturn});
        };
      } else if (event.target.id == "cancel") {
        window.history.back();
      }
    }

    // Logic for loading the page from a pre-existing Project
    var query_return = function(data) {
      console.log("Updating Scene Data");
      console.log(data);
      document.getElementById('keyinp').value = data.scenes[0].key;
      document.getElementById('nameinp').value = data.scenes[0].name;
      document.getElementById('regioninp').value = data.scenes[0].region;
      document.getElementById('latitudeinp').value = data.scenes[0].latitude;
      document.getElementById('longitudeinp').value = data.scenes[0].longitude;
      document.getElementById('tagsinp').value = data.scenes[0].tags;
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

      console.log("Scene Key: " + sceneKey)
      if (sceneKey) {
        $.ajax({url: "v1/scene/" + sceneKey, data: {}, success: query_return});
      }
    });
    </script>
  </body>
</html>
